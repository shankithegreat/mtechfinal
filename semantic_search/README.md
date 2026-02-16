# Semantic Repository Index System

A powerful semantic search system for finding code patterns and feature flags across microservices using machine learning embeddings and vector similarity search.

## 🌟 Features

- **Semantic Code Search**: Find similar code patterns using ML embeddings
- **Flag Discovery**: Search for feature flag usage across all services
- **Natural Language Queries**: Search using plain English descriptions
- **Interactive Visualization**: Mind map dependency graphs showing relationships
- **Fast Vector Search**: FAISS-powered similarity search
- **Modern UI**: Dark-themed, responsive interface

## 📋 Prerequisites

- Python 3.8+
- Node.js (optional, for serving frontend)
- 2GB+ RAM for model loading
- ~500MB disk space for models and index

## 🚀 Quick Start

### 1. Install Backend Dependencies

```bash
cd backend
pip install -r requirements.txt
```

**Note**: First run will download the sentence-transformers model (~90MB).

### 2. Build the Index

Run this once to scan your codebase and build the vector index:

```bash
cd backend
python semantic_indexer.py
```

This will:
- Scan `C:\development\ms_code` for code files
- Detect feature flag usage patterns
- Generate embeddings
- Build FAISS vector index
- Save to `backend/vector_db/`

### 3. Start the API Server

```bash
cd backend
python search_api.py
```

The API will run on `http://localhost:5000`

### 4. Open the Frontend

Simply open `frontend/index.html` in your web browser, or use a local server:

```bash
cd frontend
python -m http.server 8080
```

Then navigate to `http://localhost:8080`

## 🔧 Configuration

Edit `backend/config.py` to customize:

```python
# Code scanning directory
CODE_SCAN_DIR = r"C:\development\ms_code"

# Embedding model (change for better quality or speed)
EMBEDDING_MODEL = "sentence-transformers/all-MiniLM-L6-v2"

# Supported file extensions
SUPPORTED_EXTENSIONS = ['.py', '.js', '.java', '.go', ...]

# Feature flag detection patterns
FLAG_PATTERNS = [...]
```

## 📊 API Endpoints

### Search by Flag Name
```http
POST /api/search/flag
Content-Type: application/json

{
  "flagName": "auth_enable_2fa"
}
```

### Search by Code Similarity
```http
POST /api/search/similarity
Content-Type: application/json

{
  "codeFragment": "if (featureFlags.isEnabled('...')) { ... }",
  "limit": 20
}
```

### Natural Language Search
```http
POST /api/search/natural
Content-Type: application/json

{
  "query": "authentication with two factor security",
  "limit": 20
}
```

### Rebuild Index
```http
POST /api/index/rebuild
Content-Type: application/json

{
  "confirm": true
}
```

### Get Statistics
```http
GET /api/stats
```

## 🎨 Frontend Features

- **Dual-Mode Search**: Toggle between flag name, code fragment, and natural language search
- **Grid View**: Tabular display with service, file, line number, and code snippet
- **Mind Map View**: Interactive dependency graph showing flag-to-service-to-file relationships
- **Real-time Stats**: Dashboard showing total vectors, snippets, flags, and services indexed

## 🧠 How It Works

1. **Code Scanning**: Recursively scans codebase for supported file types
2. **Pattern Detection**: Uses regex to identify feature flag usage patterns
3. **Embedding Generation**: sentence-transformers converts code to 384-dim vectors
4. **Vector Storage**: FAISS stores embeddings for fast similarity search
5. **Semantic Search**: Finds similar code using cosine similarity
6. **Visualization**: Mermaid.js renders dependency graphs

## 📁 Project Structure

```
semantic_search/
├── backend/
│   ├── config.py              # Configuration
│   ├── code_scanner.py        # Code scanning logic
│   ├── embedding_service.py   # Embedding generation
│   ├── vector_store.py        # FAISS vector database
│   ├── semantic_indexer.py    # Main indexing orchestrator
│   ├── search_api.py          # Flask REST API
│   ├── requirements.txt       # Python dependencies
│   └── vector_db/             # Generated index files
├── frontend/
│   ├── index.html             # Main UI
│   ├── app.js                 # Search logic
│   ├── visualization.js       # Mind map rendering
│   ├── search.css             # UI styles
│   └── ...
└── config/
    └── featureflags.json      # Feature flag definitions
```

## 🛠️ Troubleshooting

### Model Download Fails
- Ensure internet connection
- Manually download from Hugging Face: https://huggingface.co/sentence-transformers

### API Connection Error
- Check that `search_api.py` is running
- Verify API URL in `frontend/app.js` matches your setup
- Check CORS settings in `config.py`

### No Results Found
- Ensure index has been built (`python semantic_indexer.py`)
- Check that feature flags exist in `config/featureflags.json`
- Verify code scan directory contains target files

### High Memory Usage
- Use smaller embedding model
- Reduce `BATCH_SIZE` in config
- Process fewer files at once

## 📈 Performance

- **Index Build Time**: ~30s per 1000 files (depends on CPU)
- **Search Latency**: <100ms for typical queries
- **Memory**: ~1GB for model + index
- **Storage**: ~1MB per 1000 code snippets

## 🔮 Future Enhancements

- [ ] Incremental index updates
- [ ] D3.js interactive graphs
- [ ] Code clone detection
- [ ] Elasticsearch integration
- [ ] Multi-language support improvements
- [ ] Export results to CSV/JSON

## 📝 License

MIT License - feel free to modify and distribute

## 🤝 Contributing

Contributions welcome! Areas to improve:
- Additional flag detection patterns
- Better code preprocessing
- UI/UX enhancements
- Performance optimizations
