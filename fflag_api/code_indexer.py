"""
Code Indexer Module
Automated indexing pipeline for Java code and feature flags.
"""

import os
import json
from typing import List, Dict, Any, Tuple, Optional
from pathlib import Path

from semantic_service import SemanticService
from vector_store import VectorStore
from methodparser import MethodParser


class CodeIndexer:
    """
    Automated indexing pipeline for code and feature flags.
    Scans repositories, extracts methods, generates embeddings, and stores in vector index.
    """
    
    def __init__(
        self,
        semantic_service: SemanticService,
        vector_store: VectorStore,
        repo_folder_path: str
    ):
        """
        Initialize the code indexer.
        
        Args:
            semantic_service: Instance of SemanticService for embedding generation
            vector_store: Instance of VectorStore for storing embeddings
            repo_folder_path: Root path to microservices repositories
        """
        self.semantic_service = semantic_service
        self.vector_store = vector_store
        self.repo_folder_path = repo_folder_path
        self.method_parser = MethodParser()
        
        # Track indexing progress
        self.indexed_files = set()
        self.indexed_flags = set()
    
    def index_all_repositories(self, service_dirs: Optional[List[str]] = None) -> Dict[str, Any]:
        """
        Index all microservice repositories.
        
        Args:
            service_dirs: Optional list of specific service directories to index.
                         If None, indexes all directories in repo_folder_path.
        
        Returns:
            Dictionary with indexing statistics
        """
        stats = {
            "code_snippets_indexed": 0,
            "flags_indexed": 0,
            "relationships_indexed": 0,
            "errors": []
        }
        
        # Get list of service directories
        if service_dirs is None:
            service_dirs = self._discover_service_directories()
        
        print(f"Indexing {len(service_dirs)} service directories...")
        
        # Index each service
        for service_dir in service_dirs:
            service_path = os.path.join(self.repo_folder_path, service_dir)
            if os.path.isdir(service_path):
                try:
                    service_stats = self._index_service(service_path, service_dir)
                    stats["code_snippets_indexed"] += service_stats["code_snippets"]
                    stats["relationships_indexed"] += service_stats["relationships"]
                except Exception as e:
                    error_msg = f"Error indexing {service_dir}: {str(e)}"
                    print(error_msg)
                    stats["errors"].append(error_msg)
        
        # Index feature flags
        try:
            flags_count = self._index_feature_flags()
            stats["flags_indexed"] = flags_count
        except Exception as e:
            error_msg = f"Error indexing flags: {str(e)}"
            print(error_msg)
            stats["errors"].append(error_msg)
        
        # Save the vector store
        self.vector_store.save()
        
        return stats
    
    def _discover_service_directories(self) -> List[str]:
        """Discover all service directories in the repo folder."""
        service_dirs = []
        
        if not os.path.exists(self.repo_folder_path):
            print(f"Repository path does not exist: {self.repo_folder_path}")
            return service_dirs
        
        for item in os.listdir(self.repo_folder_path):
            item_path = os.path.join(self.repo_folder_path, item)
            # Skip hidden directories, common files, and venv
            if os.path.isdir(item_path) and not item.startswith('.') and item not in ['venv', '__pycache__', 'common']:
                service_dirs.append(item)
        
        return service_dirs
    
    def _index_service(self, service_path: str, service_name: str) -> Dict[str, int]:
        """
        Index a single microservice.
        
        Args:
            service_path: Path to the service directory
            service_name: Name of the service
        
        Returns:
            Dictionary with count of indexed items
        """
        stats = {
            "code_snippets": 0,
            "relationships": 0
        }
        
        print(f"Indexing service: {service_name}")
        
        # Find all Java files
        java_files = self._find_java_files(service_path)
        
        print(f"Found {len(java_files)} Java files in {service_name}")
        
        # Index each Java file
        for java_file in java_files:
            try:
                file_stats = self._index_java_file(java_file, service_name)
                stats["code_snippets"] += file_stats["code_snippets"]
                stats["relationships"] += file_stats["relationships"]
            except Exception as e:
                print(f"Error indexing {java_file}: {str(e)}")
        
        return stats
    
    def _find_java_files(self, directory: str) -> List[str]:
        """Find all Java files in a directory recursively."""
        java_files = []
        
        for root, dirs, files in os.walk(directory):
            # Skip test directories and build outputs
            dirs[:] = [d for d in dirs if d not in ['test', 'tests', 'target', 'build', '.git']]
            
            for file in files:
                if file.endswith('.java') and not file.startswith('.'):
                    java_files.append(os.path.join(root, file))
        
        return java_files
    
    def _index_java_file(self, file_path: str, service_name: str) -> Dict[str, int]:
        """
        Index a single Java file.
        
        Args:
            file_path: Path to the Java file
            service_name: Name of the service
        
        Returns:
            Dictionary with count of indexed items
        """
        stats = {
            "code_snippets": 0,
            "relationships": 0
        }
        
        # Extract methods from the file
        method_list, source_lines = self.method_parser.extract_methods(file_path)
        
        if not method_list:
            return stats
        
        # Get class name from file path
        class_name = self._extract_class_name(file_path)
        
        # Process each method
        code_snippets = []
        metadata_list = []
        
        for method in method_list:
            # Get method code
            method_code = self._get_method_code(method, source_lines)
            
            if not method_code or len(method_code.strip()) < 20:
                continue
            
            # Create metadata
            metadata = {
                "code": method_code,
                "method_name": method.name,
                "class_name": class_name,
                "file_path": file_path,
                "service": service_name,
                "line_start": method.position.line if method.position else 0
            }
            
            code_snippets.append(method_code)
            metadata_list.append(metadata)
        
        if not code_snippets:
            return stats
        
        # Generate embeddings in batch
        embeddings = self.semantic_service.code_embedder.embed_batch(code_snippets, metadata_list)
        
        # Add to vector store
        vector_ids = self.vector_store.add_code_batch(embeddings, metadata_list)
        stats["code_snippets"] = len(vector_ids)
        
        # Process relationships (flag usage)
        for i, (code, metadata) in enumerate(zip(code_snippets, metadata_list)):
            flags_used = self.semantic_service.relationship_mapper.extract_flag_usage_from_code(code)
            
            if flags_used:
                # Store this information in metadata for later relationship indexing
                metadata["flags_used"] = flags_used
                stats["relationships"] += len(flags_used)
        
        self.indexed_files.add(file_path)
        
        return stats
    
    def _extract_class_name(self, file_path: str) -> str:
        """Extract class name from Java file path."""
        filename = os.path.basename(file_path)
        class_name = os.path.splitext(filename)[0]
        return class_name
    
    def _get_method_code(self, method_node, source_lines: List[str]) -> str:
        """Extract method code from source lines."""
        start_line = method_node.position.line - 1 if method_node.position else None
        if start_line is None or start_line >= len(source_lines):
            return ""
        
        brace_count = 0
        method_started = False
        method_end = start_line
        
        for i in range(start_line, len(source_lines)):
            line = source_lines[i]
            
            if not method_started and "{" in line:
                method_started = True
            
            if method_started:
                brace_count += line.count("{") - line.count("}")
            
            if method_started and brace_count == 0:
                method_end = i
                break
        
        return "".join(source_lines[start_line:method_end + 1])
    
    def _index_feature_flags(self) -> int:
        """
        Index feature flags from JSON manifests.
        
        Returns:
            Number of flags indexed
        """
        print("Indexing feature flags...")
        
        # Try to find feature flag files
        flag_files = [
            os.path.join(self.repo_folder_path, "feature-flags.json"),
            os.path.join(self.repo_folder_path, "featureflags.json"),
            os.path.join(os.path.dirname(self.repo_folder_path), "fflag_api", "config", "featureflags.json")
        ]
        
        flags_indexed = 0
        
        for flag_file in flag_files:
            if os.path.exists(flag_file):
                try:
                    with open(flag_file, 'r', encoding='utf-8') as f:
                        data = json.load(f)
                    
                    # Handle different JSON structures
                    if isinstance(data, list):
                        # Array format with featureFlagName and featureFlagState
                        flags_indexed += self._index_flags_from_array(data)
                    elif isinstance(data, dict) and "featureFlags" in data:
                        # Object format with featureFlags key
                        flags_indexed += self._index_flags_from_object(data["featureFlags"])
                    elif isinstance(data, dict):
                        # Direct object format
                        flags_indexed += self._index_flags_from_object(data)
                    
                    print(f"Indexed {flags_indexed} flags from {flag_file}")
                    break  # Only use the first file found
                    
                except Exception as e:
                    print(f"Error reading {flag_file}: {str(e)}")
        
        return flags_indexed
    
    def _index_flags_from_array(self, flags_array: List[Dict[str, Any]]) -> int:
        """Index flags from array format."""
        flag_names = []
        metadata_list = []
        
        for flag_data in flags_array:
            if "featureFlagName" in flag_data:
                flag_name = flag_data["featureFlagName"]
                
                metadata = {
                    "flag_name": flag_name,
                    "state": flag_data.get("featureFlagState", "unknown"),
                    "service": flag_data.get("service", "unknown"),
                    "description": flag_data.get("description", "")
                }
                
                flag_names.append(flag_name)
                metadata_list.append(metadata)
        
        if flag_names:
            # Generate embeddings in batch
            embeddings = self.semantic_service.flag_embedder.embed_batch(
                flag_names,
                metadata_list
            )
            
            # Add to vector store
            self.vector_store.add_flag_batch(embeddings, metadata_list)
            
            for flag_name in flag_names:
                self.indexed_flags.add(flag_name)
        
        return len(flag_names)
    
    def _index_flags_from_object(self, flags_object: Dict[str, Any]) -> int:
        """Index flags from object format (flag_name: state)."""
        flag_names = []
        metadata_list = []
        
        for flag_name, flag_state in flags_object.items():
            # Extract service name from flag prefix
            service = flag_name.split('_')[0] if '_' in flag_name else "unknown"
            
            metadata = {
                "flag_name": flag_name,
                "state": "enabled" if flag_state else "disabled",
                "service": service,
                "description": ""
            }
            
            flag_names.append(flag_name)
            metadata_list.append(metadata)
        
        if flag_names:
            # Generate embeddings in batch
            embeddings = self.semantic_service.flag_embedder.embed_batch(
                flag_names,
                metadata_list
            )
            
            # Add to vector store
            self.vector_store.add_flag_batch(embeddings, metadata_list)
            
            for flag_name in flag_names:
                self.indexed_flags.add(flag_name)
        
        return len(flag_names)
    
    def get_indexing_status(self) -> Dict[str, Any]:
        """Get current indexing status and statistics."""
        return {
            "indexed_files_count": len(self.indexed_files),
            "indexed_flags_count": len(self.indexed_flags),
            "vector_store_stats": self.vector_store.get_stats()
        }
