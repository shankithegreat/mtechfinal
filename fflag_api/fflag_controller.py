import sys
import json
import os
import configparser

from flask import Flask, request, jsonify
from flask_cors import CORS

from process_cleanup import ProcessCleanup
from anomaly_detection_isolation_forest import run_anomaly_detection
from staleness_prediction_xgboost import run_staleness_prediction
from risk_scoring_catboost import run_risk_scoring

# Semantic search imports
from semantic_service import SemanticService
from vector_store import VectorStore
from code_indexer import CodeIndexer

pc = ProcessCleanup()

# Environment setup
os.environ["KMP_DUPLICATE_LIB_OK"] = "TRUE"
current_working_directory = os.getcwd()

# Read config
config = configparser.RawConfigParser()
config_file_path = os.path.join(current_working_directory, "config", "fflag.config")
config.read(config_file_path)

DEBUG = config.getboolean('fflagsection', 'DEBUG')
print(DEBUG)


def log(message):
    if DEBUG:
        print(message)


# Create Flask app
app = Flask(__name__, static_url_path='', static_folder='frontend')
CORS(app)

# Initialize semantic search components
log("Initializing semantic search components...")
try:
    semantic_service = SemanticService()
    embedding_dim = semantic_service.get_embedding_dimension()
    index_path = config.get('semantic', 'index_path', fallback='./vector_index')
    vector_store = VectorStore(embedding_dim, index_path)
    
    # Try to load existing index
    if vector_store.load():
        log("Loaded existing vector index")
    else:
        log("No existing vector index found, will create new on first indexing")
    
    repo_path = config.get('flagmapping', 'repo_folder_path', fallback='../telecom_microservices')
    code_indexer = CodeIndexer(semantic_service, vector_store, repo_path)
    
    log("Semantic search components initialized successfully")
except Exception as e:
    log(f"Warning: Could not initialize semantic search: {e}")
    semantic_service = None
    vector_store = None
    code_indexer = None


@app.route('/')
def home():
    return app.send_static_file('index.html')


@app.route('/ml')
def ml_page():
    return app.send_static_file('ml_analysis.html')


@app.route('/cleanup')
def cleanup_page():
    return app.send_static_file('cleanup.html')


@app.route('/semantic')
def semantic_page():
    return app.send_static_file('semantic.html')



@app.route('/ml/analyze', methods=['POST'])
def analyze_ml():
    data = request.get_json()
    model_type = data.get('model_type')
    if model_type == 'anomaly':
        result = run_anomaly_detection()
    elif model_type == 'staleness':
        result = run_staleness_prediction()
    elif model_type == 'risk':
        result = run_risk_scoring()
    else:
        return jsonify({"error": "Invalid model type"}), 400
    return jsonify(result)


@app.route('/fflag/getimpactedmethods', methods=['POST'])
def get_impacted_methods():
    data = request.get_json()
    flag_name = data.get('flagName')
    repo_name = data.get('repoName')
    methods = pc.get_impacted_methods(flag_name, repo_name)
    return jsonify(methods)


@app.route('/fflag/searchallflags', methods=['POST'])
def get_all_deprecate_flags():
    with open(os.path.join(current_working_directory, "config", "fflag_data.json"), 'r') as f:
        data = json.load(f)
    return data


@app.route('/fflag/repoforflag', methods=['POST'])
def get_all_deprecate_flag_repo_path():
    search_flag_input = request.get_json()
    flag_name = search_flag_input.get("flagname")

    with open(os.path.join(current_working_directory, "config", "fflag_data_mapping.json"), 'r') as f:
        data = json.load(f)

    movers_flag_paths = data.get('moversflag', [])
    print(movers_flag_paths)

    return movers_flag_paths

@app.route('/fflag/identifyflag', methods=['POST'])
def identify_flag_to_remove():
    with open(os.path.join(current_working_directory, "config", "deprecated_flag_response.json"), 'r') as f:
        data = json.load(f)

    print(data['list'][0]['gitRepoName'])
    print(data['list'][0]['gitRepoURL'])
    print(data['list'][0]['deprecatedFeatureFlags'][0]['featureFlagName'])

    return (
        "Flag identified...<br>"
        "1. " + data['list'][0]['gitRepoName'] + "<br>"
        "2. " + data['list'][0]['gitRepoURL'] + "<br>"
        "3. " + data['list'][0]['deprecatedFeatureFlags'][0]['featureFlagName']
    )


@app.route('/fflag/getallflags', methods=['GET', 'POST'])
def get_all_flags_from_config():
    with open(os.path.join(current_working_directory, "config", "featureflags.json"), 'r') as f:
        data = json.load(f)
    return data


@app.route('/fflag/processcleanup', methods=['POST'])
def process_each_repo_flag_removal():
    # Expecting JSON payload with 'repoName' and 'flagName'
    data = request.get_json()
    
    if not data or 'repoName' not in data or 'flagName' not in data:
         # Fallback to old behavior for backward compatibility or testing if needed, 
         # but per requirements we need to use input. 
         # Let's support the new requirement strictly first.
         return "Invalid input", 400

    repo_name = data['repoName']
    flag_name = data['flagName']

    repo_list = [repo_name]

    process_cleanup = ProcessCleanup()
    process_cleanup.process_deprecated_flag_cleanup(flag_name, repo_list)

    return "Flag removal process completed..."


# ============================================================================
# SEMANTIC SEARCH API ENDPOINTS
# ============================================================================

@app.route('/semantic/index/rebuild', methods=['POST'])
def semantic_index_rebuild():
    """Rebuild the entire semantic index from scratch."""
    if not code_indexer:
        return jsonify({"error": "Semantic search not initialized"}), 500
    
    try:
        # Clear existing index
        vector_store.clear()
        
        # Rebuild index
        log("Starting full rebuild of semantic index...")
        stats = code_indexer.index_all_repositories()
        
        return jsonify({
            "status": "success",
            "message": "Index rebuilt successfully",
            "statistics": stats
        })
    except Exception as e:
        log(f"Error rebuilding index: {str(e)}")
        return jsonify({"error": str(e)}), 500


@app.route('/semantic/index/incremental', methods=['POST'])
def semantic_index_incremental():
    """Incrementally index new or changed files."""
    if not code_indexer:
        return jsonify({"error": "Semantic search not initialized"}), 500
    
    try:
        data = request.get_json() or {}
        service_dirs = data.get('services', None)  # Optional: specific services to index
        
        log("Starting incremental indexing...")
        stats = code_indexer.index_all_repositories(service_dirs)
        
        return jsonify({
            "status": "success",
            "message": "Incremental indexing completed",
            "statistics": stats
        })
    except Exception as e:
        log(f"Error during incremental indexing: {str(e)}")
        return jsonify({"error": str(e)}), 500


@app.route('/semantic/index/status', methods=['GET'])
def semantic_index_status():
    """Get current indexing status and statistics."""
    if not code_indexer:
        return jsonify({"error": "Semantic search not initialized"}), 500
    
    try:
        status = code_indexer.get_indexing_status()
        return jsonify({
            "status": "success",
            "data": status
        })
    except Exception as e:
        return jsonify({"error": str(e)}), 500


@app.route('/semantic/search', methods=['POST'])
def semantic_search():
    """
    Semantic search across code and flags.
    Request body: {
        "query": "search query text",
        "type": "code|flag|all",  // optional, default: "all"
        "limit": 10,              // optional, default: 10
        "threshold": 0.7          // optional, similarity threshold 0-1
    }
    """
    if not semantic_service or not vector_store:
        return jsonify({"error": "Semantic search not initialized"}), 500
    
    try:
        data = request.get_json()
        
        if not data or 'query' not in data:
            return jsonify({"error": "Missing 'query' parameter"}), 400
        
        query = data['query']
        search_type = data.get('type', 'all')
        limit = data.get('limit', 10)
        threshold = data.get('threshold', 0.7)
        
        results = {
            "query": query,
            "results": []
        }
        
        # Generate query embedding
        query_embedding = semantic_service.code_embedder.embed_code_snippet(query)
        
        # Search based on type
        if search_type in ['code', 'all']:
            code_results = vector_store.search_code(query_embedding, limit, threshold)
            for vid, score, metadata in code_results:
                results["results"].append({
                    "type": "code",
                    "score": score,
                    "method_name": metadata.get("method_name", ""),
                    "class_name": metadata.get("class_name", ""),
                    "file_path": metadata.get("file_path", ""),
                    "service": metadata.get("service", ""),
                    "code_snippet": metadata.get("code", "")[:500]  # Truncate for response
                })
        
        if search_type in ['flag', 'all']:
            flag_results = vector_store.search_flags(query_embedding, limit, threshold)
            for vid, score, metadata in flag_results:
                results["results"].append({
                    "type": "flag",
                    "score": score,
                    "flag_name": metadata.get("flag_name", ""),
                    "state": metadata.get("state", ""),
                    "service": metadata.get("service", ""),
                    "description": metadata.get("description", "")
                })
        
        # Sort by score
        results["results"].sort(key=lambda x: x["score"], reverse=True)
        results["results"] = results["results"][:limit]  # Limit total results
        
        return jsonify({
            "status": "success",
            "data": results
        })
    
    except Exception as e:
        log(f"Error during semantic search: {str(e)}")
        return jsonify({"error": str(e)}), 500


@app.route('/semantic/similar', methods=['POST'])
def semantic_similar():
    """
    Find similar items to a given code or flag.
    Request body: {
        "item_id": "vector_id or flag_name",
        "item_type": "code|flag",
        "limit": 5,
        "threshold": 0.7
    }
    """
    if not semantic_service or not vector_store:
        return jsonify({"error": "Semantic search not initialized"}), 500
    
    try:
        data = request.get_json()
        
        if not data or 'item_type' not in data:
            return jsonify({"error": "Missing required parameters"}), 400
        
        item_type = data['item_type']
        limit = data.get('limit', 5)
        threshold = data.get('threshold', 0.7)
        
        # For code: use vector_id, for flag: use flag_name
        if item_type == 'code':
            item_id = data.get('item_id')
            if item_id is None:
                return jsonify({"error": "Missing 'item_id' parameter"}), 400
            
            # Get the code metadata
            metadata = vector_store.get_code_by_id(int(item_id))
            if not metadata:
                return jsonify({"error": "Code item not found"}), 404
            
            # Generate embedding for this code
            code = metadata.get('code', '')
            embedding = semantic_service.code_embedder.embed_code_snippet(code, metadata)
            
            # Search for similar code
            results = vector_store.search_code(embedding, limit + 1, threshold)
            
            # Filter out the original item
            similar_items = [
                {
                    "score": score,
                    "method_name": meta.get("method_name", ""),
                    "class_name": meta.get("class_name", ""),
                    "file_path": meta.get("file_path", ""),
                    "service": meta.get("service", ""),
                    "code_snippet": meta.get("code", "")[:500]
                }
                for vid, score, meta in results
                if vid != int(item_id)
            ][:limit]
        
        elif item_type == 'flag':
            flag_name = data.get('flag_name')
            if not flag_name:
                return jsonify({"error": "Missing 'flag_name' parameter"}), 400
            
            # Generate embedding for this flag
            embedding = semantic_service.flag_embedder.embed_flag(flag_name)
            
            # Search for similar flags
            results = vector_store.search_flags(embedding, limit, threshold)
            
            similar_items = [
                {
                    "score": score,
                    "flag_name": meta.get("flag_name", ""),
                    "state": meta.get("state", ""),
                    "service": meta.get("service", ""),
                    "description": meta.get("description", "")
                }
                for vid, score, meta in results
                if meta.get("flag_name") != flag_name
            ][:limit]
        
        else:
            return jsonify({"error": "Invalid item_type. Must be 'code' or 'flag'"}), 400
        
        return jsonify({
            "status": "success",
            "data": {
                "similar_items": similar_items
            }
        })
    
    except Exception as e:
        log(f"Error finding similar items: {str(e)}")
        return jsonify({"error": str(e)}), 500


@app.route('/semantic/related-flags', methods=['POST'])
def semantic_related_flags():
    """
    Find flags related to a code snippet.
    Request body: {
        "code_snippet": "code text",
        "limit": 10,
        "threshold": 0.7
    }
    """
    if not semantic_service or not vector_store:
        return jsonify({"error": "Semantic search not initialized"}), 500
    
    try:
        data = request.get_json()
        
        if not data or 'code_snippet' not in data:
            return jsonify({"error": "Missing 'code_snippet' parameter"}), 400
        
        code_snippet = data['code_snippet']
        limit = data.get('limit', 10)
        threshold = data.get('threshold', 0.7)
        
        # Extract flags directly mentioned in code
        flags_in_code = semantic_service.relationship_mapper.extract_flag_usage_from_code(code_snippet)
        
        # Generate embedding for code
        code_embedding = semantic_service.code_embedder.embed_code_snippet(code_snippet)
        
        # Search for semantically related flags
        flag_results = vector_store.search_flags(code_embedding, limit, threshold)
        
        related_flags = []
        for vid, score, metadata in flag_results:
            flag_name = metadata.get("flag_name", "")
            related_flags.append({
                "flag_name": flag_name,
                "score": score,
                "state": metadata.get("state", ""),
                "service": metadata.get("service", ""),
                "directly_used": flag_name in flags_in_code
            })
        
        return jsonify({
            "status": "success",
            "data": {
                "related_flags": related_flags,
                "flags_directly_used": flags_in_code
            }
        })
    
    except Exception as e:
        log(f"Error finding related flags: {str(e)}")
        return jsonify({"error": str(e)}), 500


@app.route('/semantic/related-code', methods=['POST'])
def semantic_related_code():
    """
    Find code that uses a specific flag.
    Request body: {
        "flag_name": "flag_name",
        "limit": 10,
        "threshold": 0.6
    }
    """
    if not semantic_service or not vector_store:
        return jsonify({"error": "Semantic search not initialized"}), 500
    
    try:
        data = request.get_json()
        
        if not data or 'flag_name' not in data:
            return jsonify({"error": "Missing 'flag_name' parameter"}), 400
        
        flag_name = data['flag_name']
        limit = data.get('limit', 10)
        threshold = data.get('threshold', 0.6)
        
        # Generate embedding for flag
        flag_embedding = semantic_service.flag_embedder.embed_flag(flag_name)
        
        # Search for semantically related code
        code_results = vector_store.search_code(flag_embedding, limit * 2, threshold)
        
        # Filter results to prioritize code that actually uses the flag
        related_code = []
        for vid, score, metadata in code_results:
            flags_used = metadata.get("flags_used", [])
            code_text = metadata.get("code", "")
            
            # Check if flag is actually used in the code
            directly_uses = flag_name in flags_used or flag_name in code_text.lower()
            
            related_code.append({
                "score": score,
                "method_name": metadata.get("method_name", ""),
                "class_name": metadata.get("class_name", ""),
                "file_path": metadata.get("file_path", ""),
                "service": metadata.get("service", ""),
                "code_snippet": code_text[:500],
                "directly_uses_flag": directly_uses,
                "flags_used": flags_used
            })
        
        # Sort: direct usage first, then by score
        related_code.sort(key=lambda x: (not x["directly_uses_flag"], -x["score"]))
        related_code = related_code[:limit]
        
        return jsonify({
            "status": "success",
            "data": {
                "flag_name": flag_name,
                "related_code": related_code
            }
        })
    
    except Exception as e:
        log(f"Error finding related code: {str(e)}")
        return jsonify({"error": str(e)}), 500


# ============================================================================
# END OF SEMANTIC SEARCH API ENDPOINTS
# ============================================================================


if __name__ == '__main__':
    app.run(host="127.0.0.1", port=1212, debug=True)
