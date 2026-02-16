"""
Vector Store Module
Implements vector storage and retrieval using FAISS for efficient similarity search.
"""

import os
import json
import pickle
from typing import List, Dict, Any, Tuple, Optional
import numpy as np
import faiss


class VectorStore:
    """
    Vector storage and retrieval using FAISS.
    Maintains separate indices for code, flags, and relationships.
    """
    
    def __init__(self, embedding_dim: int, index_path: str = "./vector_index"):
        """
        Initialize the vector store.
        
        Args:
            embedding_dim: Dimension of embedding vectors
            index_path: Directory path for storing indices
        """
        self.embedding_dim = embedding_dim
        self.index_path = index_path
        
        # Create index directory if it doesn't exist
        os.makedirs(index_path, exist_ok=True)
        
        # Initialize FAISS indices
        self.code_index = faiss.IndexFlatIP(embedding_dim)  # Inner Product (cosine similarity for normalized vectors)
        self.flag_index = faiss.IndexFlatIP(embedding_dim)
        self.relationship_index = faiss.IndexFlatIP(embedding_dim)
        
        # Metadata storage (maps vector ID to original content)
        self.code_metadata: Dict[int, Dict[str, Any]] = {}
        self.flag_metadata: Dict[int, Dict[str, Any]] = {}
        self.relationship_metadata: Dict[int, Dict[str, Any]] = {}
        
        # Counter for generating unique IDs
        self.code_counter = 0
        self.flag_counter = 0
        self.relationship_counter = 0
    
    def add_code_vector(self, embedding: np.ndarray, metadata: Dict[str, Any]) -> int:
        """
        Add a code embedding to the index.
        
        Args:
            embedding: Normalized embedding vector
            metadata: Metadata including code snippet, file path, method name, etc.
        
        Returns:
            ID of the added vector
        """
        # Ensure embedding is 2D array
        if embedding.ndim == 1:
            embedding = embedding.reshape(1, -1)
        
        # Add to FAISS index
        self.code_index.add(embedding.astype('float32'))
        
        # Store metadata
        vector_id = self.code_counter
        self.code_metadata[vector_id] = metadata
        self.code_counter += 1
        
        return vector_id
    
    def add_flag_vector(self, embedding: np.ndarray, metadata: Dict[str, Any]) -> int:
        """
        Add a flag embedding to the index.
        
        Args:
            embedding: Normalized embedding vector
            metadata: Metadata including flag name, state, description, etc.
        
        Returns:
            ID of the added vector
        """
        if embedding.ndim == 1:
            embedding = embedding.reshape(1, -1)
        
        self.flag_index.add(embedding.astype('float32'))
        
        vector_id = self.flag_counter
        self.flag_metadata[vector_id] = metadata
        self.flag_counter += 1
        
        return vector_id
    
    def add_relationship_vector(self, embedding: np.ndarray, metadata: Dict[str, Any]) -> int:
        """
        Add a relationship embedding to the index.
        
        Args:
            embedding: Normalized embedding vector
            metadata: Metadata including code ID, flag ID, relationship type
        
        Returns:
            ID of the added vector
        """
        if embedding.ndim == 1:
            embedding = embedding.reshape(1, -1)
        
        self.relationship_index.add(embedding.astype('float32'))
        
        vector_id = self.relationship_counter
        self.relationship_metadata[vector_id] = metadata
        self.relationship_counter += 1
        
        return vector_id
    
    def add_code_batch(self, embeddings: np.ndarray, metadata_list: List[Dict[str, Any]]) -> List[int]:
        """
        Add multiple code embeddings efficiently.
        
        Args:
            embeddings: Array of normalized embeddings (n x embedding_dim)
            metadata_list: List of metadata dicts
        
        Returns:
            List of vector IDs
        """
        start_id = self.code_counter
        
        # Add to FAISS index
        self.code_index.add(embeddings.astype('float32'))
        
        # Store metadata
        vector_ids = []
        for i, metadata in enumerate(metadata_list):
            vector_id = start_id + i
            self.code_metadata[vector_id] = metadata
            vector_ids.append(vector_id)
        
        self.code_counter += len(metadata_list)
        
        return vector_ids
    
    def add_flag_batch(self, embeddings: np.ndarray, metadata_list: List[Dict[str, Any]]) -> List[int]:
        """
        Add multiple flag embeddings efficiently.
        
        Args:
            embeddings: Array of normalized embeddings (n x embedding_dim)
            metadata_list: List of metadata dicts
        
        Returns:
            List of vector IDs
        """
        start_id = self.flag_counter
        
        self.flag_index.add(embeddings.astype('float32'))
        
        vector_ids = []
        for i, metadata in enumerate(metadata_list):
            vector_id = start_id + i
            self.flag_metadata[vector_id] = metadata
            vector_ids.append(vector_id)
        
        self.flag_counter += len(metadata_list)
        
        return vector_ids
    
    def search_code(
        self,
        query_embedding: np.ndarray,
        k: int = 10,
        threshold: float = 0.0
    ) -> List[Tuple[int, float, Dict[str, Any]]]:
        """
        Search for similar code snippets.
        
        Args:
            query_embedding: Query embedding vector
            k: Number of results to return
            threshold: Minimum similarity score (0-1)
        
        Returns:
            List of (vector_id, similarity_score, metadata) tuples
        """
        if self.code_index.ntotal == 0:
            return []
        
        if query_embedding.ndim == 1:
            query_embedding = query_embedding.reshape(1, -1)
        
        # Search
        similarities, indices = self.code_index.search(query_embedding.astype('float32'), k)
        
        # Filter by threshold and return results
        results = []
        for i, (idx, score) in enumerate(zip(indices[0], similarities[0])):
            if idx != -1 and score >= threshold:
                results.append((int(idx), float(score), self.code_metadata.get(int(idx), {})))
        
        return results
    
    def search_flags(
        self,
        query_embedding: np.ndarray,
        k: int = 10,
        threshold: float = 0.0
    ) -> List[Tuple[int, float, Dict[str, Any]]]:
        """
        Search for similar feature flags.
        
        Args:
            query_embedding: Query embedding vector
            k: Number of results to return
            threshold: Minimum similarity score (0-1)
        
        Returns:
            List of (vector_id, similarity_score, metadata) tuples
        """
        if self.flag_index.ntotal == 0:
            return []
        
        if query_embedding.ndim == 1:
            query_embedding = query_embedding.reshape(1, -1)
        
        similarities, indices = self.flag_index.search(query_embedding.astype('float32'), k)
        
        results = []
        for i, (idx, score) in enumerate(zip(indices[0], similarities[0])):
            if idx != -1 and score >= threshold:
                results.append((int(idx), float(score), self.flag_metadata.get(int(idx), {})))
        
        return results
    
    def search_relationships(
        self,
        query_embedding: np.ndarray,
        k: int = 10,
        threshold: float = 0.0
    ) -> List[Tuple[int, float, Dict[str, Any]]]:
        """
        Search for similar code-flag relationships.
        
        Args:
            query_embedding: Query embedding vector
            k: Number of results to return
            threshold: Minimum similarity score (0-1)
        
        Returns:
            List of (vector_id, similarity_score, metadata) tuples
        """
        if self.relationship_index.ntotal == 0:
            return []
        
        if query_embedding.ndim == 1:
            query_embedding = query_embedding.reshape(1, -1)
        
        similarities, indices = self.relationship_index.search(query_embedding.astype('float32'), k)
        
        results = []
        for i, (idx, score) in enumerate(zip(indices[0], similarities[0])):
            if idx != -1 and score >= threshold:
                results.append((int(idx), float(score), self.relationship_metadata.get(int(idx), {})))
        
        return results
    
    def get_code_by_id(self, vector_id: int) -> Optional[Dict[str, Any]]:
        """Get code metadata by vector ID."""
        return self.code_metadata.get(vector_id)
    
    def get_flag_by_id(self, vector_id: int) -> Optional[Dict[str, Any]]:
        """Get flag metadata by vector ID."""
        return self.flag_metadata.get(vector_id)
    
    def get_stats(self) -> Dict[str, Any]:
        """Get statistics about the vector store."""
        return {
            "code_vectors": self.code_index.ntotal,
            "flag_vectors": self.flag_index.ntotal,
            "relationship_vectors": self.relationship_index.ntotal,
            "total_vectors": self.code_index.ntotal + self.flag_index.ntotal + self.relationship_index.ntotal,
            "embedding_dimension": self.embedding_dim,
            "index_path": self.index_path
        }
    
    def save(self) -> None:
        """Save indices and metadata to disk."""
        # Save FAISS indices
        faiss.write_index(self.code_index, os.path.join(self.index_path, "code_vectors.index"))
        faiss.write_index(self.flag_index, os.path.join(self.index_path, "flag_vectors.index"))
        faiss.write_index(self.relationship_index, os.path.join(self.index_path, "relationship_vectors.index"))
        
        # Save metadata as JSON
        metadata = {
            "code": self.code_metadata,
            "flags": self.flag_metadata,
            "relationships": self.relationship_metadata,
            "counters": {
                "code": self.code_counter,
                "flag": self.flag_counter,
                "relationship": self.relationship_counter
            }
        }
        
        with open(os.path.join(self.index_path, "metadata.json"), "w", encoding="utf-8") as f:
            json.dump(metadata, f, indent=2)
        
        print(f"Vector store saved to {self.index_path}")
    
    def load(self) -> bool:
        """
        Load indices and metadata from disk.
        
        Returns:
            True if successful, False otherwise
        """
        try:
            # Load FAISS indices
            code_index_path = os.path.join(self.index_path, "code_vectors.index")
            flag_index_path = os.path.join(self.index_path, "flag_vectors.index")
            relationship_index_path = os.path.join(self.index_path, "relationship_vectors.index")
            
            if os.path.exists(code_index_path):
                self.code_index = faiss.read_index(code_index_path)
            
            if os.path.exists(flag_index_path):
                self.flag_index = faiss.read_index(flag_index_path)
            
            if os.path.exists(relationship_index_path):
                self.relationship_index = faiss.read_index(relationship_index_path)
            
            # Load metadata
            metadata_path = os.path.join(self.index_path, "metadata.json")
            if os.path.exists(metadata_path):
                with open(metadata_path, "r", encoding="utf-8") as f:
                    metadata = json.load(f)
                
                # Convert string keys back to integers
                self.code_metadata = {int(k): v for k, v in metadata.get("code", {}).items()}
                self.flag_metadata = {int(k): v for k, v in metadata.get("flags", {}).items()}
                self.relationship_metadata = {int(k): v for k, v in metadata.get("relationships", {}).items()}
                
                # Restore counters
                counters = metadata.get("counters", {})
                self.code_counter = counters.get("code", 0)
                self.flag_counter = counters.get("flag", 0)
                self.relationship_counter = counters.get("relationship", 0)
            
            print(f"Vector store loaded from {self.index_path}")
            return True
        
        except Exception as e:
            print(f"Error loading vector store: {e}")
            return False
    
    def clear(self) -> None:
        """Clear all indices and metadata."""
        self.code_index = faiss.IndexFlatIP(self.embedding_dim)
        self.flag_index = faiss.IndexFlatIP(self.embedding_dim)
        self.relationship_index = faiss.IndexFlatIP(self.embedding_dim)
        
        self.code_metadata.clear()
        self.flag_metadata.clear()
        self.relationship_metadata.clear()
        
        self.code_counter = 0
        self.flag_counter = 0
        self.relationship_counter = 0
        
        print("Vector store cleared")
