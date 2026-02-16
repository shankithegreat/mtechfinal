"""
Embedding Service for generating semantic embeddings
Uses sentence-transformers to convert text/code to vector representations
"""
import numpy as np
from sentence_transformers import SentenceTransformer
from typing import List, Union
from config import Config


class EmbeddingService:
    """Service for generating semantic embeddings"""
    
    def __init__(self, model_name: str = None):
        self.model_name = model_name or Config.EMBEDDING_MODEL
        print(f"Loading embedding model: {self.model_name}")
        self.model = SentenceTransformer(self.model_name)
        print(f"Model loaded successfully. Embedding dimension: {self.model.get_sentence_embedding_dimension()}")
    
    def generate_embedding(self, text: str) -> np.ndarray:
        """
        Generate embedding for a single text
        
        Args:
            text: Input text or code snippet
            
        Returns:
            numpy array of embeddings (normalized)
        """
        if not text or not isinstance(text, str):
            raise ValueError("Text must be a non-empty string")
        
        embedding = self.model.encode(text, normalize_embeddings=True)
        return embedding
    
    def batch_generate(self, texts: List[str], batch_size: int = None) -> np.ndarray:
        """
        Generate embeddings for multiple texts efficiently
        
        Args:
            texts: List of text strings
            batch_size: Batch size for processing
            
        Returns:
            2D numpy array of embeddings
        """
        if not texts:
            return np.array([])
        
        batch_size = batch_size or Config.BATCH_SIZE
        
        print(f"Generating embeddings for {len(texts)} texts...")
        embeddings = self.model.encode(
            texts,
            batch_size=batch_size,
            normalize_embeddings=True,
            show_progress_bar=True
        )
        
        return embeddings
    
    def get_embedding_dimension(self) -> int:
        """Get the dimension of embeddings produced by this model"""
        return self.model.get_sentence_embedding_dimension()
    
    def compute_similarity(self, embedding1: np.ndarray, 
                          embedding2: np.ndarray) -> float:
        """
        Compute cosine similarity between two embeddings
        
        Args:
            embedding1: First embedding vector
            embedding2: Second embedding vector
            
        Returns:
            Similarity score (0-1, higher is more similar)
        """
        # Assuming embeddings are already normalized
        similarity = np.dot(embedding1, embedding2)
        return float(similarity)
    
    def prepare_code_for_embedding(self, code: str) -> str:
        """
        Preprocess code snippet for better embedding quality
        
        Args:
            code: Raw code snippet
            
        Returns:
            Preprocessed code
        """
        # Remove excessive whitespace
        code = ' '.join(code.split())
        
        # Truncate if too long (model has token limits)
        max_chars = 2000  # Safe limit for most models
        if len(code) > max_chars:
            code = code[:max_chars]
        
        return code


if __name__ == "__main__":
    # Test the embedding service
    service = EmbeddingService()
    
    # Test single embedding
    sample_code = """
    if (featureFlags.isEnabled('auth_enable_2fa')) {
        return enable2FA();
    }
    """
    
    embedding = service.generate_embedding(sample_code)
    print(f"\nSingle embedding shape: {embedding.shape}")
    print(f"Sample values: {embedding[:5]}")
    
    # Test batch embedding
    samples = [
        "if (flag.enabled('feature1')) { doSomething(); }",
        "featureFlags.get('feature2')",
        "checkFlag('feature3')"
    ]
    
    batch_embeddings = service.batch_generate(samples)
    print(f"\nBatch embeddings shape: {batch_embeddings.shape}")
    
    # Test similarity
    sim = service.compute_similarity(batch_embeddings[0], batch_embeddings[1])
    print(f"\nSimilarity between first two: {sim:.4f}")
