"""
Vector Store using FAISS for similarity search
Manages vector index and metadata storage
"""
import json
import pickle
import numpy as np
import faiss
from pathlib import Path
from typing import List, Dict, Tuple
from config import Config


class VectorStore:
    """FAISS-based vector database for semantic search"""
    
    def __init__(self, dimension: int = None, index_dir: Path = None):
        self.dimension = dimension or Config.VECTOR_DIMENSION
        self.index_dir = Path(index_dir or Config.VECTOR_DB_DIR)
        self.index_path = self.index_dir / "faiss.index"
        self.metadata_path = self.index_dir / "metadata.json"
        
        # Initialize FAISS index
        self.index = None
        self.metadata = []  # List of metadata dicts corresponding to vectors
        
        # Try to load existing index
        if self.index_path.exists():
            self.load_index()
        else:
            self._create_new_index()
    
    def _create_new_index(self):
        """Create a new FAISS index"""
        print(f"Creating new FAISS index with dimension {self.dimension}")
        # Using IndexFlatL2 for exact search
        # For cosine similarity with normalized vectors, L2 distance works well
        self.index = faiss.IndexFlatL2(self.dimension)
        self.metadata = []
    
    def add_vectors(self, embeddings: np.ndarray, metadata_list: List[Dict]):
        """
        Add vectors and their metadata to the index
        
        Args:
            embeddings: 2D numpy array of embeddings (n_vectors x dimension)
            metadata_list: List of metadata dicts for each vector
        """
        if embeddings.shape[0] != len(metadata_list):
            raise ValueError("Number of embeddings must match number of metadata entries")
        
        if embeddings.shape[1] != self.dimension:
            raise ValueError(f"Embedding dimension {embeddings.shape[1]} doesn't match index dimension {self.dimension}")
        
        # Ensure float32 for FAISS
        embeddings = embeddings.astype('float32')
        
        # Add to index
        self.index.add(embeddings)
        
        # Add metadata
        self.metadata.extend(metadata_list)
        
        print(f"Added {len(metadata_list)} vectors. Total: {self.index.ntotal}")
    
    def search_similar(self, query_embedding: np.ndarray, 
                      k: int = None) -> List[Dict]:
        """
        Search for k most similar vectors
        
        Args:
            query_embedding: Query vector (1D array)
            k: Number of results to return
            
        Returns:
            List of dicts with 'score' and metadata
        """
        k = k or Config.MAX_SEARCH_RESULTS
        
        if self.index.ntotal == 0:
            print("Warning: Index is empty")
            return []
        
        # Reshape query to 2D array
        if query_embedding.ndim == 1:
            query_embedding = query_embedding.reshape(1, -1)
        
        # Ensure float32
        query_embedding = query_embedding.astype('float32')
        
        # Search
        k = min(k, self.index.ntotal)  # Don't request more than available
        distances, indices = self.index.search(query_embedding, k)
        
        # Convert L2 distances to similarity scores (0-1 range)
        # For normalized vectors: similarity = 1 - (L2_distance^2 / 2)
        similarities = 1 - (distances[0] ** 2 / 2)
        
        # Build results
        results = []
        for idx, score in zip(indices[0], similarities):
            if idx < len(self.metadata):  # Valid index
                result = {
                    'score': float(score),
                    **self.metadata[idx]
                }
                
                # Filter by threshold
                if score >= Config.SIMILARITY_THRESHOLD:
                    results.append(result)
        
        return results
    
    def search_by_text(self, texts: List[str], k: int = None) -> Dict[str, List[Dict]]:
        """
        Search by multiple text queries
        
        Args:
            texts: List of query texts
            k: Number of results per query
            
        Returns:
            Dict mapping text to list of results
        """
        from embedding_service import EmbeddingService
        
        embedding_service = EmbeddingService()
        results = {}
        
        for text in texts:
            embedding = embedding_service.generate_embedding(text)
            results[text] = self.search_similar(embedding, k)
        
        return results
    
    def save_index(self):
        """Save FAISS index and metadata to disk"""
        self.index_dir.mkdir(parents=True, exist_ok=True)
        
        # Save FAISS index
        faiss.write_index(self.index, str(self.index_path))
        
        # Save metadata
        with open(self.metadata_path, 'w', encoding='utf-8') as f:
            json.dump(self.metadata, f, indent=2)
        
        print(f"Saved index with {self.index.ntotal} vectors to {self.index_dir}")
    
    def load_index(self):
        """Load FAISS index and metadata from disk"""
        if not self.index_path.exists():
            print("No existing index found")
            self._create_new_index()
            return
        
        try:
            # Load FAISS index
            self.index = faiss.read_index(str(self.index_path))
            
            # Load metadata
            with open(self.metadata_path, 'r', encoding='utf-8') as f:
                self.metadata = json.load(f)
            
            print(f"Loaded index with {self.index.ntotal} vectors from {self.index_dir}")
        except Exception as e:
            print(f"Error loading index: {e}")
            self._create_new_index()
    
    def clear(self):
        """Clear the index and metadata"""
        self._create_new_index()
        print("Index cleared")
    
    def get_stats(self) -> Dict:
        """Get index statistics"""
        return {
            'total_vectors': self.index.ntotal,
            'dimension': self.dimension,
            'total_metadata': len(self.metadata)
        }


if __name__ == "__main__":
    # Test the vector store
    from embedding_service import EmbeddingService
    
    # Create test data
    embedding_service = EmbeddingService()
    
    test_codes = [
        "if (featureFlags['auth_2fa']) { enable2FA(); }",
        "featureFlags.get('billing_enabled')",
        "checkFlag('customer_profile')"
    ]
    
    # Generate embeddings
    embeddings = embedding_service.batch_generate(test_codes)
    
    # Create metadata
    metadata = [
        {'code': code, 'file': f'test{i}.py', 'line': i*10}
        for i, code in enumerate(test_codes)
    ]
    
    # Create vector store
    store = VectorStore()
    store.clear()  # Start fresh
    
    # Add vectors
    store.add_vectors(embeddings, metadata)
    
    # Search
    query = "feature flag for authentication"
    query_embedding = embedding_service.generate_embedding(query)
    results = store.search_similar(query_embedding, k=3)
    
    print(f"\nSearch results for: '{query}'")
    for result in results:
        print(f"Score: {result['score']:.4f} | Code: {result['code']}")
    
    # Save
    store.save_index()
    
    # Test reload
    store2 = VectorStore()
    print(f"\nReloaded stats: {store2.get_stats()}")
