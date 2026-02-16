"""
Flask API for Semantic Search Service
Provides REST endpoints for searching and indexing
"""
from flask import Flask, request, jsonify, send_from_directory
from flask_cors import CORS
from config import Config
from semantic_indexer import SemanticIndexer
import traceback
from pathlib import Path

app = Flask(__name__)
CORS(app, resources={r"/api/*": {"origins": Config.CORS_ORIGINS}})

# Frontend directory path
FRONTEND_DIR = Path(__file__).parent.parent / 'frontend'

# Initialize indexer
print("Initializing Semantic Indexer...")
indexer = SemanticIndexer()
print("Indexer ready!")


@app.route('/api/search/flag', methods=['POST'])
def search_by_flag():
    """
    Search for code snippets by flag name
    
    Request body:
        {
            "flagName": "auth_enable_2fa"
        }
    
    Response:
        {
            "success": true,
            "results": [
                {
                    "service_name": "auth-service",
                    "file_path": "C:\\path\\to\\file.py",
                    "line_number": 42,
                    "code": "if (flag.enabled('auth_enable_2fa')) {...}",
                    "match_type": "exact"
                }
            ],
            "count": 5
        }
    """
    try:
        data = request.get_json()
        
        if not data or 'flagName' not in data:
            return jsonify({
                'success': False,
                'error': 'Missing required field: flagName'
            }), 400
        
        flag_name = data['flagName']
        results = indexer.search_by_flag_name(flag_name)
        
        return jsonify({
            'success': True,
            'results': results,
            'count': len(results)
        })
    
    except Exception as e:
        print(f"Error in search_by_flag: {e}")
        traceback.print_exc()
        return jsonify({
            'success': False,
            'error': str(e)
        }), 500


@app.route('/api/search/similarity', methods=['POST'])
def search_by_similarity():
    """
    Find similar code patterns
    
    Request body:
        {
            "codeFragment": "if (featureFlag.isEnabled(...)) {...}",
            "limit": 20
        }
    
    Response:
        {
            "success": true,
            "results": [
                {
                    "service_name": "billing-service",
                    "file_path": "C:\\path\\to\\file.js",
                    "line_number": 156,
                    "code": "...",
                    "detected_flags": ["billing_enable_invoice"],
                    "similarity_score": 0.87
                }
            ],
            "count": 12
        }
    """
    try:
        data = request.get_json()
        
        if not data or 'codeFragment' not in data:
            return jsonify({
                'success': False,
                'error': 'Missing required field: codeFragment'
            }), 400
        
        code_fragment = data['codeFragment']
        limit = data.get('limit', Config.MAX_SEARCH_RESULTS)
        
        results = indexer.search_by_code_similarity(code_fragment, k=limit)
        
        return jsonify({
            'success': True,
            'results': results,
            'count': len(results)
        })
    
    except Exception as e:
        print(f"Error in search_by_similarity: {e}")
        traceback.print_exc()
        return jsonify({
            'success': False,
            'error': str(e)
        }), 500


@app.route('/api/search/natural', methods=['POST'])
def search_natural_language():
    """
    Natural language search
    
    Request body:
        {
            "query": "authentication with two factor",
            "limit": 20
        }
    
    Response:
        {
            "success": true,
            "flags": [...],
            "code": [...],
            "flag_count": 5,
            "code_count": 8
        }
    """
    try:
        data = request.get_json()
        
        if not data or 'query' not in data:
            return jsonify({
                'success': False,
                'error': 'Missing required field: query'
            }), 400
        
        query = data['query']
        limit = data.get('limit', Config.MAX_SEARCH_RESULTS)
        
        results = indexer.search_natural_language(query, k=limit)
        
        return jsonify({
            'success': True,
            'flags': results['flags'],
            'code': results['code'],
            'flag_count': len(results['flags']),
            'code_count': len(results['code'])
        })
    
    except Exception as e:
        print(f"Error in search_natural_language: {e}")
        traceback.print_exc()
        return jsonify({
            'success': False,
            'error': str(e)
        }), 500


@app.route('/api/index/rebuild', methods=['POST'])
def rebuild_index():
    """
    Rebuild the entire index
    
    Request body:
        {
            "confirm": true
        }
    
    Response:
        {
            "success": true,
            "stats": {...}
        }
    """
    try:
        data = request.get_json() or {}
        
        if not data.get('confirm'):
            return jsonify({
                'success': False,
                'error': 'Must confirm rebuild with "confirm": true'
            }), 400
        
        print("Rebuilding index...")
        stats = indexer.index_codebase(rebuild=True)
        
        return jsonify({
            'success': True,
            'stats': stats,
            'message': 'Index rebuilt successfully'
        })
    
    except Exception as e:
        print(f"Error in rebuild_index: {e}")
        traceback.print_exc()
        return jsonify({
            'success': False,
            'error': str(e)
        }), 500


@app.route('/api/stats', methods=['GET'])
def get_stats():
    """
    Get index statistics
    
    Response:
        {
            "success": true,
            "stats": {
                "total_vectors": 1234,
                "code_snippets": 1000,
                "feature_flags": 234,
                "services": 5
            }
        }
    """
    try:
        stats = indexer.get_stats()
        
        return jsonify({
            'success': True,
            'stats': stats
        })
    
    except Exception as e:
        print(f"Error in get_stats: {e}")
        traceback.print_exc()
        return jsonify({
            'success': False,
            'error': str(e)
        }), 500


@app.route('/api/health', methods=['GET'])
def health_check():
    """Health check endpoint"""
    return jsonify({
        'success': True,
        'status': 'healthy',
        'service': 'semantic-search-api'
    })


# Frontend Routes
@app.route('/')
def index():
    """Serve the main HTML page"""
    return send_from_directory(FRONTEND_DIR, 'index.html')


@app.route('/<path:path>')
def serve_static(path):
    """Serve static frontend files (CSS, JS, fonts, etc.)"""
    return send_from_directory(FRONTEND_DIR, path)


@app.errorhandler(404)
def not_found(e):
    return jsonify({
        'success': False,
        'error': 'Endpoint not found'
    }), 404


@app.errorhandler(500)
def internal_error(e):
    return jsonify({
        'success': False,
        'error': 'Internal server error'
    }), 500


if __name__ == '__main__':
    print(f"\nStarting Flask API server on {Config.API_HOST}:{Config.API_PORT}")
    print(f"Debug mode: {Config.DEBUG}")
    print("\n" + "="*60)
    print("🚀 Semantic Search System Ready!")
    print("="*60)
    print("\n📍 Web Interface:")
    print(f"   http://localhost:{Config.API_PORT}")
    print("\n📡 API Endpoints:")
    print("  POST /api/search/flag - Search by flag name")
    print("  POST /api/search/similarity - Find similar code")
    print("  POST /api/search/natural - Natural language search")
    print("  POST /api/index/rebuild - Rebuild index")
    print("  GET  /api/stats - Get statistics")
    print("  GET  /api/health - Health check")
    print("\n" + "="*60)
    print("💡 Tip: Open http://localhost:5000 in your browser")
    print("="*60 + "\n")
    
    app.run(
        host=Config.API_HOST,
        port=Config.API_PORT,
        debug=Config.DEBUG
    )
