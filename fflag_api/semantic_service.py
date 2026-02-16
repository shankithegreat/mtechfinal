"""
Semantic Service Module
Handles embedding generation for code snippets, feature flags, and their relationships.
Provides semantic search and similarity detection capabilities.
"""

import os
import json
import re
from typing import List, Dict, Any, Tuple, Optional
from sentence_transformers import SentenceTransformer
import numpy as np


class CodeEmbedder:
    """Generates embeddings for code snippets."""
    
    def __init__(self, model_name: str = "sentence-transformers/all-MiniLM-L6-v2"):
        """
        Initialize the code embedder with a pre-trained model.
        
        Args:
            model_name: HuggingFace model identifier
        """
        self.model = SentenceTransformer(model_name)
        self.embedding_dim = self.model.get_sentence_embedding_dimension()
    
    def embed_code_snippet(self, code: str, metadata: Optional[Dict[str, Any]] = None) -> np.ndarray:
        """
        Generate embedding for a code snippet.
        
        Args:
            code: Source code string
            metadata: Optional metadata (method name, class, file path)
        
        Returns:
            Normalized embedding vector
        """
        # Preprocess code: remove extra whitespace, normalize
        code_normalized = self._normalize_code(code)
        
        # Optionally include metadata in the embedding context
        if metadata:
            context = self._build_code_context(code_normalized, metadata)
        else:
            context = code_normalized
        
        # Generate embedding
        embedding = self.model.encode(context, convert_to_numpy=True)
        
        # Normalize for cosine similarity
        return self._normalize_embedding(embedding)
    
    def embed_batch(self, code_snippets: List[str], metadata_list: Optional[List[Dict[str, Any]]] = None) -> np.ndarray:
        """
        Generate embeddings for multiple code snippets efficiently.
        
        Args:
            code_snippets: List of source code strings
            metadata_list: Optional list of metadata dicts
        
        Returns:
            Array of normalized embeddings (n_snippets x embedding_dim)
        """
        if metadata_list:
            contexts = [
                self._build_code_context(self._normalize_code(code), meta)
                for code, meta in zip(code_snippets, metadata_list)
            ]
        else:
            contexts = [self._normalize_code(code) for code in code_snippets]
        
        embeddings = self.model.encode(contexts, convert_to_numpy=True, show_progress_bar=True)
        
        # Normalize all embeddings
        return np.array([self._normalize_embedding(emb) for emb in embeddings])
    
    def _normalize_code(self, code: str) -> str:
        """Normalize code by removing extra whitespace and comments."""
        # Remove single-line comments (// style)
        code = re.sub(r'//.*?$', '', code, flags=re.MULTILINE)
        
        # Remove multi-line comments (/* */ style)
        code = re.sub(r'/\*.*?\*/', '', code, flags=re.DOTALL)
        
        # Normalize whitespace
        code = re.sub(r'\s+', ' ', code)
        
        return code.strip()
    
    def _build_code_context(self, code: str, metadata: Dict[str, Any]) -> str:
        """Build context string by combining code with metadata."""
        parts = []
        
        if 'method_name' in metadata:
            parts.append(f"Method: {metadata['method_name']}")
        
        if 'class_name' in metadata:
            parts.append(f"Class: {metadata['class_name']}")
        
        if 'file_path' in metadata:
            # Extract just the filename
            filename = os.path.basename(metadata['file_path'])
            parts.append(f"File: {filename}")
        
        parts.append(code)
        
        return " | ".join(parts)
    
    def _normalize_embedding(self, embedding: np.ndarray) -> np.ndarray:
        """Normalize embedding to unit length for cosine similarity."""
        norm = np.linalg.norm(embedding)
        if norm > 0:
            return embedding / norm
        return embedding


class FlagEmbedder:
    """Generates embeddings for feature flag definitions."""
    
    def __init__(self, model_name: str = "sentence-transformers/all-MiniLM-L6-v2"):
        """
        Initialize the flag embedder with a pre-trained model.
        
        Args:
            model_name: HuggingFace model identifier
        """
        self.model = SentenceTransformer(model_name)
        self.embedding_dim = self.model.get_sentence_embedding_dimension()
    
    def embed_flag(self, flag_name: str, flag_data: Optional[Dict[str, Any]] = None) -> np.ndarray:
        """
        Generate embedding for a feature flag.
        
        Args:
            flag_name: Feature flag identifier
            flag_data: Optional flag metadata (state, description, service)
        
        Returns:
            Normalized embedding vector
        """
        context = self._build_flag_context(flag_name, flag_data)
        
        # Generate embedding
        embedding = self.model.encode(context, convert_to_numpy=True)
        
        # Normalize for cosine similarity
        return self._normalize_embedding(embedding)
    
    def embed_batch(self, flag_names: List[str], flag_data_list: Optional[List[Dict[str, Any]]] = None) -> np.ndarray:
        """
        Generate embeddings for multiple flags efficiently.
        
        Args:
            flag_names: List of flag names
            flag_data_list: Optional list of flag metadata dicts
        
        Returns:
            Array of normalized embeddings (n_flags x embedding_dim)
        """
        if flag_data_list:
            contexts = [
                self._build_flag_context(name, data)
                for name, data in zip(flag_names, flag_data_list)
            ]
        else:
            contexts = [self._build_flag_context(name, None) for name in flag_names]
        
        embeddings = self.model.encode(contexts, convert_to_numpy=True, show_progress_bar=True)
        
        # Normalize all embeddings
        return np.array([self._normalize_embedding(emb) for emb in embeddings])
    
    def _build_flag_context(self, flag_name: str, flag_data: Optional[Dict[str, Any]]) -> str:
        """Build context string from flag name and metadata."""
        parts = []
        
        # Parse semantic meaning from flag name
        # e.g., "auth_enable_2fa" -> "auth enable 2fa"
        readable_name = flag_name.replace('_', ' ')
        parts.append(f"Feature Flag: {readable_name}")
        
        if flag_data:
            if 'state' in flag_data:
                parts.append(f"State: {flag_data['state']}")
            
            if 'description' in flag_data:
                parts.append(f"Description: {flag_data['description']}")
            
            if 'service' in flag_data:
                parts.append(f"Service: {flag_data['service']}")
            
            if 'category' in flag_data:
                parts.append(f"Category: {flag_data['category']}")
        
        return " | ".join(parts)
    
    def _normalize_embedding(self, embedding: np.ndarray) -> np.ndarray:
        """Normalize embedding to unit length for cosine similarity."""
        norm = np.linalg.norm(embedding)
        if norm > 0:
            return embedding / norm
        return embedding


class RelationshipMapper:
    """Maps relationships between code and feature flags."""
    
    def __init__(self, code_embedder: CodeEmbedder, flag_embedder: FlagEmbedder):
        """
        Initialize relationship mapper.
        
        Args:
            code_embedder: Instance of CodeEmbedder
            flag_embedder: Instance of FlagEmbedder
        """
        self.code_embedder = code_embedder
        self.flag_embedder = flag_embedder
    
    def create_relationship_embedding(
        self,
        code: str,
        flag_name: str,
        code_metadata: Optional[Dict[str, Any]] = None,
        flag_metadata: Optional[Dict[str, Any]] = None
    ) -> np.ndarray:
        """
        Create an embedding representing the code-flag relationship.
        
        Args:
            code: Source code snippet
            flag_name: Feature flag name
            code_metadata: Optional code metadata
            flag_metadata: Optional flag metadata
        
        Returns:
            Normalized relationship embedding
        """
        # Get individual embeddings
        code_emb = self.code_embedder.embed_code_snippet(code, code_metadata)
        flag_emb = self.flag_embedder.embed_flag(flag_name, flag_metadata)
        
        # Combine embeddings (average)
        relationship_emb = (code_emb + flag_emb) / 2.0
        
        # Normalize
        norm = np.linalg.norm(relationship_emb)
        if norm > 0:
            return relationship_emb / norm
        return relationship_emb
    
    def extract_flag_usage_from_code(self, code: str) -> List[str]:
        """
        Extract feature flag names used in code.
        
        Args:
            code: Source code string
        
        Returns:
            List of flag names found in the code
        """
        flags = []
        
        # Pattern for FeatureFlagReader.isFeatureEnabled(...)
        pattern = r'FeatureFlagReader\.isFeatureEnabled\s*\(\s*["\']?([a-zA-Z_][a-zA-Z0-9_]*)["\']?\s*\)'
        matches = re.findall(pattern, code)
        flags.extend(matches)
        
        # Pattern for constant references (e.g., AuthServiceFeatureFlagConstants.AUTH_ENABLE_2FA)
        const_pattern = r'[A-Z][a-zA-Z]*FeatureFlagConstants\.([A-Z_]+)'
        const_matches = re.findall(const_pattern, code)
        # Convert to lowercase with underscores convention
        flags.extend([match.lower() for match in const_matches])
        
        return list(set(flags))  # Remove duplicates


class SemanticService:
    """
    Main semantic service orchestrator.
    Coordinates embedding generation, indexing, and search operations.
    """
    
    def __init__(self, model_name: str = "sentence-transformers/all-MiniLM-L6-v2"):
        """
        Initialize the semantic service.
        
        Args:
            model_name: HuggingFace model identifier for embeddings
        """
        self.code_embedder = CodeEmbedder(model_name)
        self.flag_embedder = FlagEmbedder(model_name)
        self.relationship_mapper = RelationshipMapper(self.code_embedder, self.flag_embedder)
        self.embedding_dim = self.code_embedder.embedding_dim
    
    def get_embedding_dimension(self) -> int:
        """Get the dimension of embeddings produced by this service."""
        return self.embedding_dim
    
    def process_code_snippet(
        self,
        code: str,
        metadata: Optional[Dict[str, Any]] = None
    ) -> Tuple[np.ndarray, List[str]]:
        """
        Process a code snippet: generate embedding and extract flag usage.
        
        Args:
            code: Source code string
            metadata: Optional metadata
        
        Returns:
            Tuple of (embedding, list of flags used)
        """
        embedding = self.code_embedder.embed_code_snippet(code, metadata)
        flags_used = self.relationship_mapper.extract_flag_usage_from_code(code)
        
        return embedding, flags_used
    
    def process_flag(
        self,
        flag_name: str,
        flag_data: Optional[Dict[str, Any]] = None
    ) -> np.ndarray:
        """
        Process a feature flag: generate embedding.
        
        Args:
            flag_name: Feature flag identifier
            flag_data: Optional flag metadata
        
        Returns:
            Flag embedding vector
        """
        return self.flag_embedder.embed_flag(flag_name, flag_data)
    
    def process_relationship(
        self,
        code: str,
        flag_name: str,
        code_metadata: Optional[Dict[str, Any]] = None,
        flag_metadata: Optional[Dict[str, Any]] = None
    ) -> np.ndarray:
        """
        Process a code-flag relationship: generate combined embedding.
        
        Args:
            code: Source code snippet
            flag_name: Feature flag name
            code_metadata: Optional code metadata
            flag_metadata: Optional flag metadata
        
        Returns:
            Relationship embedding vector
        """
        return self.relationship_mapper.create_relationship_embedding(
            code, flag_name, code_metadata, flag_metadata
        )
