"""
Test script to verify the semantic search system
"""
import sys
import json
from pathlib import Path

# Add backend to path
sys.path.insert(0, str(Path(__file__).parent))

from config import Config
from code_scanner import CodeScanner
from embedding_service import EmbeddingService
from vector_store import VectorStore
from semantic_indexer import SemanticIndexer

def test_code_scanner():
    """Test code scanner"""
    print("\n=== Testing Code Scanner ===")
    scanner = CodeScanner()
    
    # Check if scan directory exists
    if not scanner.scan_dir.exists():
        print(f"⚠️  Warning: Scan directory not found: {scanner.scan_dir}")
        print("   Scanner will return empty results")
        return False
    
    print(f"✓ Scan directory exists: {scanner.scan_dir}")
    print(f"✓ Supported extensions: {len(scanner.extensions)}")
    print(f"✓ Flag patterns: {len(scanner.flag_patterns)}")
    return True

def test_embedding_service():
    """Test embedding service"""
    print("\n=== Testing Embedding Service ===")
    try:
        service = EmbeddingService()
        
        # Test single embedding
        test_text = "if (featureFlag.isEnabled('test')) { return true; }"
        embedding = service.generate_embedding(test_text)
        
        print(f"✓ Model loaded: {service.model_name}")
        print(f"✓ Embedding dimension: {embedding.shape[0]}")
        print(f"✓ Single embedding test passed")
        
        # Test batch embedding
        batch = ["test 1", "test 2", "test 3"]
        batch_embeddings = service.batch_generate(batch)
        print(f"✓ Batch embedding test passed: {batch_embeddings.shape}")
        
        return True
    except Exception as e:
        print(f"✗ Error: {e}")
        return False

def test_vector_store():
    """Test vector store"""
    print("\n=== Testing Vector Store ===")
    try:
        from embedding_service import EmbeddingService
        
        service = EmbeddingService()
        store = VectorStore(dimension=service.get_embedding_dimension())
        
        # Create test data
        test_codes = [
            "featureFlags.get('flag1')",
            "if (flag.enabled('flag2')) {}",
            "checkFlag('flag3')"
        ]
        
        embeddings = service.batch_generate(test_codes)
        metadata = [{'code': code, 'test': True} for code in test_codes]
        
        # Add to store
        store.add_vectors(embeddings, metadata)
        print(f"✓ Added {len(test_codes)} vectors to store")
        
        # Test search
        query_embedding = service.generate_embedding("feature flag check")
        results = store.search_similar(query_embedding, k=2)
        print(f"✓ Search returned {len(results)} results")
        
        # Test save/load
        store.save_index()
        print(f"✓ Index saved successfully")
        
        store2 = VectorStore()
        print(f"✓ Index loaded successfully: {store2.get_stats()}")
        
        return True
    except Exception as e:
        print(f"✗ Error: {e}")
        import traceback
        traceback.print_exc()
        return False

def test_feature_flags():
    """Test feature flags loading"""
    print("\n=== Testing Feature Flags ===")
    try:
        with open(Config.FEATURE_FLAGS_PATH, 'r', encoding='utf-8') as f:
            flags = json.load(f)
        
        print(f"✓ Loaded {len(flags)} feature flags")
        
        # Show sample
        if flags:
            sample = flags[0]
            print(f"✓ Sample flag: {sample['featureFlagName']} ({sample['serviceName']})")
        
        return True
    except Exception as e:
        print(f"✗ Error loading flags: {e}")
        return False

def test_full_indexer():
    """Test full indexing pipeline"""
    print("\n=== Testing Full Indexer ===")
    try:
        indexer = SemanticIndexer()
        
        print("✓ Indexer initialized")
        print(f"✓ Feature flags loaded: {len(indexer.feature_flags)}")
        
        # Get current stats (if index exists)
        stats = indexer.get_stats()
        print(f"✓ Current index stats:")
        print(f"  - Total vectors: {stats['total_vectors']}")
        print(f"  - Code snippets: {stats['code_snippets']}")
        print(f"  - Feature flags: {stats['feature_flags']}")
        print(f"  - Services: {stats['services']}")
        
        return True
    except Exception as e:
        print(f"✗ Error: {e}")
        import traceback
        traceback.print_exc()
        return False

def main():
    """Run all tests"""
    print("=" * 50)
    print("Semantic Search System - Test Suite")
    print("=" * 50)
    
    results = {
        'Code Scanner': test_code_scanner(),
        'Embedding Service': test_embedding_service(),
        'Vector Store': test_vector_store(),
        'Feature Flags': test_feature_flags(),
        'Full Indexer': test_full_indexer()
    }
    
    print("\n" + "=" * 50)
    print("Test Results Summary")
    print("=" * 50)
    
    for test_name, passed in results.items():
        status = "✓ PASS" if passed else "✗ FAIL"
        print(f"{status} - {test_name}")
    
    all_passed = all(results.values())
    
    print("\n" + "=" * 50)
    if all_passed:
        print("✓ All tests passed!")
        print("\nSystem is ready to use. Run:")
        print("  python semantic_indexer.py  - to build/rebuild index")
        print("  python search_api.py        - to start API server")
    else:
        print("✗ Some tests failed. Please check the errors above.")
    print("=" * 50)
    
    return 0 if all_passed else 1

if __name__ == "__main__":
    sys.exit(main())
