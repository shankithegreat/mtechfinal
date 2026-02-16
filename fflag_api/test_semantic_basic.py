"""
Simple test script to verify semantic service installation and basic functionality.
Run this after installing dependencies with: pip install -r requirements.txt
"""

import sys
import os

def test_imports():
    """Test that all required modules can be imported."""
    print("Testing imports...")
    
    try:
        import sentence_transformers
        print("✓ sentence-transformers imported successfully")
    except ImportError as e:
        print(f"✗ Failed to import sentence-transformers: {e}")
        return False
    
    try:
        import faiss
        print("✓ faiss imported successfully")
    except ImportError as e:
        print(f"✗ Failed to import faiss: {e}")
        return False
    
    try:
        import torch
        print("✓ torch imported successfully")
    except ImportError as e:
        print(f"✗ Failed to import torch: {e}")
        return False
    
    try:
        from semantic_service import SemanticService
        print("✓ SemanticService imported successfully")
    except ImportError as e:
        print(f"✗ Failed to import SemanticService: {e}")
        return False
    
    try:
        from vector_store import VectorStore
        print("✓ VectorStore imported successfully")
    except ImportError as e:
        print(f"✗ Failed to import VectorStore: {e}")
        return False
    
    try:
        from code_indexer import CodeIndexer
        print("✓ CodeIndexer imported successfully")
    except ImportError as e:
        print(f"✗ Failed to import CodeIndexer: {e}")
        return False
    
    return True


def test_embedding_generation():
    """Test basic embedding generation."""
    print("\nTesting embedding generation...")
    
    try:
        from semantic_service import SemanticService
        import numpy as np
        
        # Initialize service
        service = SemanticService()
        print(f"✓ SemanticService initialized (embedding dim: {service.embedding_dim})")
        
        # Test code embedding
        code_sample = """
        public void authenticateUser(String username, String password) {
            if (FeatureFlagReader.isFeatureEnabled("auth_enable_2fa")) {
                // Two-factor authentication logic
                verify2FA(username);
            }
        }
        """
        
        embedding, flags_used = service.process_code_snippet(code_sample)
        print(f"✓ Code embedding generated: shape={embedding.shape}")
        print(f"✓ Flags detected in code: {flags_used}")
        
        # Test flag embedding
        flag_embedding = service.process_flag("auth_enable_2fa", {
            "state": "enabled",
            "service": "auth"
        })
        print(f"✓ Flag embedding generated: shape={flag_embedding.shape}")
        
        # Test similarity
        similarity = np.dot(embedding, flag_embedding)
        print(f"✓ Code-Flag similarity score: {similarity:.3f}")
        
        return True
        
    except Exception as e:
        print(f"✗ Embedding generation test failed: {e}")
        import traceback
        traceback.print_exc()
        return False


def test_vector_store():
    """Test vector store operations."""
    print("\nTesting vector store...")
    
    try:
        from semantic_service import SemanticService
        from vector_store import VectorStore
        import numpy as np
        
        service = SemanticService()
        store = VectorStore(service.embedding_dim, "./test_vector_index")
        
        # Add some test vectors
        test_code_1 = "public void enableTwoFactorAuth() { }"
        test_code_2 = "public void sendEmailVerification() { }"
        
        emb1, _ = service.process_code_snippet(test_code_1)
        emb2, _ = service.process_code_snippet(test_code_2)
        
        vid1 = store.add_code_vector(emb1, {"code": test_code_1, "method": "enableTwoFactorAuth"})
        vid2 = store.add_code_vector(emb2, {"code": test_code_2, "method": "sendEmailVerification"})
        
        print(f"✓ Added 2 code vectors (IDs: {vid1}, {vid2})")
        
        # Search
        results = store.search_code(emb1, k=2)
        print(f"✓ Search returned {len(results)} results")
        
        # Save and load
        store.save()
        print("✓ Vector store saved")
        
        new_store = VectorStore(service.embedding_dim, "./test_vector_index")
        if new_store.load():
            print("✓ Vector store loaded successfully")
            stats = new_store.get_stats()
            print(f"✓ Loaded stats: {stats}")
        
        # Cleanup
        import shutil
        if os.path.exists("./test_vector_index"):
            shutil.rmtree("./test_vector_index")
            print("✓ Test index cleaned up")
        
        return True
        
    except Exception as e:
        print(f"✗ Vector store test failed: {e}")
        import traceback
        traceback.print_exc()
        return False


if __name__ == "__main__":
    print("=" * 60)
    print("Semantic Repository Index - Basic Tests")
    print("=" * 60)
    
    # Test imports
    if not test_imports():
        print("\n❌ Import test failed. Please install dependencies:")
        print("   pip install -r requirements.txt")
        sys.exit(1)
    
    print("\n✅ All imports successful!")
    
    # Test embedding generation
    if not test_embedding_generation():
        print("\n❌ Embedding generation test failed.")
        sys.exit(1)
    
    print("\n✅ Embedding generation test passed!")
    
    # Test vector store
    if not test_vector_store():
        print("\n❌ Vector store test failed.")
        sys.exit(1)
    
    print("\n✅ Vector store test passed!")
    
    print("\n" + "=" * 60)
    print("🎉 All tests passed successfully!")
    print("=" * 60)
    print("\nNext steps:")
    print("1. Start the Flask server: python fflag_controller.py")
    print("2. Index the codebase: POST to http://127.0.0.1:1212/semantic/index/rebuild")
    print("3. Try semantic search: POST to http://127.0.0.1:1212/semantic/search")
