# Semantic Repository Index - Implementation Plan

## Goal Description

Build a **Semantic Repository Index** system that maintains embeddings of code snippets, flag definitions, and their relationships to enable fast semantic search and similarity detection. This will allow developers to:

- Find similar code patterns across the microservices codebase
- Discover related feature flags based on semantic meaning
- Identify code-flag relationships and dependencies
- Search for code or flags using natural language queries

The system will use pre-trained language models to generate embeddings and FAISS (Facebook AI Similarity Search) for efficient vector similarity search.

## User Review Required

> [!IMPORTANT]
> **Technology Stack Selection**
> - **Embedding Model**: Using `sentence-transformers/all-MiniLM-L6-v2` (lightweight, 384 dimensions, good for code and text)
> - **Vector Database**: FAISS (in-memory with optional persistence)
> - **Alternative Option**: If you prefer ChromaDB or Pinecone for persistence, please specify

> [!WARNING]
> **Performance Considerations**
> - Initial indexing of the entire codebase may take several minutes
> - Vector index will consume memory (estimated ~100MB for 10K code snippets)
> - Consider implementing async/background indexing for production use

> [!IMPORTANT]
> **Scope Clarification**
> The implementation will index:
> 1. Java service methods from all microservices
> 2. Feature flag definitions from JSON manifests
> 3. Flag-code relationships (where flags are used)
> 
> Should we also index:
> - Configuration files?
> - Documentation/README files?
> - Git commit messages?

## Proposed Changes

### Embedding & Semantic Search Module

#### [NEW] [semantic_service.py](file:///c:/development/mtech_final_project/fflag_api/semantic_service.py)

Create a new service module that handles:
- **Code embedding generation**: Extract methods from Java files, generate embeddings
- **Flag embedding generation**: Process flag definitions with metadata
- **Relationship embedding**: Combine code and flag context for relationship mapping
- **Vector index management**: FAISS index creation, updates, persistence

Key classes:
- `SemanticService`: Main service orchestrator
- `CodeEmbedder`: Handles code snippet embeddings
- `FlagEmbedder`: Handles feature flag embeddings
- `RelationshipMapper`: Maps flag usage to code locations

---

### Vector Storage Layer

#### [NEW] [vector_store.py](file:///c:/development/mtech_final_project/fflag_api/vector_store.py)

Implements vector storage operations:
- **Index creation**: Initialize FAISS index with appropriate dimensions
- **CRUD operations**: Add, update, remove vectors
- **Search operations**: K-nearest neighbor search, similarity threshold filtering
- **Persistence**: Save/load index to/from disk
- **Metadata storage**: Store code snippets, flag metadata alongside vectors

The store will maintain:
- Vector index (FAISS)
- Metadata index (Python dict/JSON) mapping vector IDs to original content
- Reverse lookup for efficient retrieval

---

### Indexing Pipeline

#### [NEW] [code_indexer.py](file:///c:/development/mtech_final_project/fflag_api/code_indexer.py)

Automated indexing pipeline:
- **Repository scanner**: Walk through microservices directories
- **Java parser integration**: Use existing `MethodParser` to extract methods
- **Flag manifest parser**: Parse feature flag JSON files
- **Batch processing**: Efficient bulk indexing
- **Incremental updates**: Detect and index only changed files

---

### API Controllers

#### [MODIFY] [fflag_controller.py](file:///c:/development/mtech_final_project/fflag_api/fflag_controller.py)

Add new REST API endpoints for semantic operations:

**Indexing Endpoints:**
- `POST /semantic/index/rebuild` - Full reindex of codebase
- `POST /semantic/index/incremental` - Index only changed files
- `GET /semantic/index/status` - Get indexing status and statistics

**Search Endpoints:**
- `POST /semantic/search` - Semantic search across code and flags
  - Request: `{"query": "authentication logic", "type": "code|flag|all", "limit": 10}`
  - Response: Ranked results with similarity scores
  
- `POST /semantic/similar` - Find similar items
  - Request: `{"item_id": "...", "item_type": "code|flag", "limit": 5}`
  - Response: Similar code snippets or flags

- `POST /semantic/related-flags` - Find flags related to code
  - Request: `{"code_snippet": "...", "limit": 10}`
  - Response: Related feature flags with context

- `POST /semantic/related-code` - Find code using specific flags
  - Request: `{"flag_name": "auth_enable_2fa", "limit": 10}`
  - Response: Code locations and snippets

---

### Configuration

#### [MODIFY] [config/fflag.config](file:///c:/development/mtech_final_project/fflag_api/config/fflag.config)

Add semantic search configuration section:

```ini
[semantic]
# Embedding model from HuggingFace
embedding_model = sentence-transformers/all-MiniLM-L6-v2
# Vector index storage path
index_path = ./vector_index
# Search result limit
default_limit = 10
# Similarity threshold (0-1)
similarity_threshold = 0.7
# Enable auto-indexing on startup
auto_index = false
```

---

### Dependencies

#### [MODIFY] [requirements.txt](file:///c:/development/mtech_final_project/fflag_api/requirements.txt) (if exists) or create new

Add required packages:
- `sentence-transformers` - For generating embeddings
- `faiss-cpu` - Vector similarity search (or `faiss-gpu` for GPU support)
- `numpy` - Numerical operations
- `torch` - PyTorch backend for transformers

---

### Data Storage

#### [NEW] Directory structure for vector storage

Create `fflag_api/vector_index/` directory with:
- `code_vectors.index` - FAISS index for code embeddings
- `flag_vectors.index` - FAISS index for flag embeddings
- `metadata.json` - Metadata for all indexed items
- `mappings.json` - Code-to-flag relationship mappings

## Verification Plan

### Automated Tests

#### 1. Unit Tests for Embedding Generation

**File**: Create `test_semantic_service.py`

```bash
# Run from fflag_api directory
python -m pytest test_semantic_service.py -v
```

Tests will verify:
- Embedding generation produces correct dimensions (384d)
- Embeddings are normalized
- Similar code produces similar embeddings
- Different code produces different embeddings

#### 2. Vector Store Tests

**File**: Create `test_vector_store.py`

```bash
python -m pytest test_vector_store.py -v
```

Tests will verify:
- Index creation and persistence
- Add/search/delete operations
- K-NN search accuracy
- Metadata retrieval

#### 3. Integration Tests

**File**: Create `test_semantic_integration.py`

```bash
python -m pytest test_semantic_integration.py -v
```

Tests will verify:
- End-to-end indexing pipeline
- API endpoint responses
- Search result relevance

### Manual Verification

#### 1. Index the Codebase

```bash
# Start the Flask app
cd c:\development\mtech_final_project\fflag_api
python fflag_controller.py
```

Then in another terminal:
```bash
# Trigger full indexing
curl -X POST http://127.0.0.1:1212/semantic/index/rebuild
```

Expected: Should index all Java files and flag definitions without errors.

#### 2. Test Semantic Search

```bash
# Search for authentication-related code
curl -X POST http://127.0.0.1:1212/semantic/search \
  -H "Content-Type: application/json" \
  -d '{"query": "user authentication with 2FA", "type": "code", "limit": 5}'
```

Expected result: Should return code snippets from `AuthServiceService.java` mentioning 2FA/MFA features with high similarity scores (>0.7).

#### 3. Test Flag Similarity

```bash
# Find flags similar to auth_enable_2fa
curl -X POST http://127.0.0.1:1212/semantic/similar \
  -H "Content-Type: application/json" \
  -d '{"item_id": "auth_enable_2fa", "item_type": "flag", "limit": 5}'
```

Expected result: Should return related auth flags like `auth_enable_mfa`, `auth_enable_email_verification`, etc.

#### 4. Test Code-Flag Relationships

```bash
# Find code using a specific flag
curl -X POST http://127.0.0.1:1212/semantic/related-code \
  -H "Content-Type: application/json" \
  -d '{"flag_name": "auth_enable_email_verification", "limit": 5}'
```

Expected result: Should return methods from `AuthServiceService.java` that check this flag (lines 46-50).

#### 5. Performance Test

Check index status and verify reasonable performance:

```bash
curl -X GET http://127.0.0.1:1212/semantic/index/status
```

Expected response should include:
- Total indexed items (code snippets + flags)
- Index size in MB
- Average search latency (<100ms for most queries)
