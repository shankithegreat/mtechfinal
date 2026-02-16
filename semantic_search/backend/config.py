"""
Configuration management for Semantic Repository Index System
"""
import os
from pathlib import Path

class Config:
    # Paths
    BASE_DIR = Path(__file__).parent
    CODE_SCAN_DIR = r"C:\development\ms_code"
    FEATURE_FLAGS_PATH = BASE_DIR.parent / "config" / "featureflags.json"
    VECTOR_DB_DIR = BASE_DIR / "vector_db"
    
    # Ensure vector DB directory exists
    VECTOR_DB_DIR.mkdir(exist_ok=True)
    
    # Embedding Model Configuration
    EMBEDDING_MODEL = "sentence-transformers/all-MiniLM-L6-v2"  # Lightweight and fast
    # Alternative: "sentence-transformers/all-mpnet-base-v2"  # Better quality but slower
    
    # File Extensions to Scan
    SUPPORTED_EXTENSIONS = [
        '.py',   # Python
        '.js',   # JavaScript
        '.java', # Java
        '.go',   # Go
        '.ts',   # TypeScript
        '.jsx',  # React JSX
        '.tsx',  # TypeScript JSX
        '.cs',   # C#
        '.rb',   # Ruby
        '.php',  # PHP
        '.cpp',  # C++
        '.c',    # C
        '.h',    # C/C++ Header
    ]
    
    # Feature Flag Detection Patterns
    FLAG_PATTERNS = [
        # General/JS/Python patterns
        r'featureFlag[s]?\[[\"\']([^\"\']+)[\"\']\]',  # featureFlags['flag_name']
        r'featureFlag[s]?\.get\([\"\']([\w_]+)[\"\']\)',  # featureFlags.get('flag_name')
        r'isEnabled\([\"\']([\w_]+)[\"\']\)',  # isEnabled('flag_name')
        r'checkFlag\([\"\']([\w_]+)[\"\']\)',  # checkFlag('flag_name')
        r'getFlag\([\"\']([\w_]+)[\"\']\)',    # getFlag('flag_name')
        r'flag[s]?\.[\"\']([\w_]+)[\"\']\]',   # flags['flag_name']
        r'FF_([A-Z_]+)',  # FF_FLAG_NAME constant pattern
        
        # Java/Backend specific patterns
        r'isFeatureEnabled\([\"\']([\w_]+)[\"\']\)',  # isFeatureEnabled('flag_name')
        r'FeatureFlagConstants\.([A-Z_]+)',           # Constants usage
        r'([A-Z_]+_ENABLE_[A-Z_]+)',                 # CONSTANT_NAMING_CONVENTION
        
        # String literal catch-all for known prefixes (based on featureflags.json)
        r'[\"\'](auth_enable_[\w_]+)[\"\']',
        r'[\"\'](billing_enable_[\w_]+)[\"\']',
        r'[\"\'](customer_enable_[\w_]+)[\"\']',
    ]
    
    # FAISS Configuration
    FAISS_INDEX_TYPE = "Flat"  # Use IndexFlatL2 for exact search
    VECTOR_DIMENSION = 384  # For all-MiniLM-L6-v2 model
    
    # API Configuration
    API_HOST = "0.0.0.0"
    API_PORT = 5000
    DEBUG = True
    CORS_ORIGINS = "*"
    
    # Search Configuration
    MAX_SEARCH_RESULTS = 50
    SIMILARITY_THRESHOLD = 0.3  # Cosine similarity threshold
    
    # Indexing Configuration
    BATCH_SIZE = 32  # Batch size for embedding generation
    MAX_SNIPPET_LENGTH = 500  # Max characters per code snippet
    CONTEXT_LINES = 5  # Lines of context around flag usage
