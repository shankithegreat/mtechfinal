"""
Semantic Indexer - Orchestrates the indexing workflow
Coordinates code scanning, embedding generation, and vector storage
"""
import json
from pathlib import Path
from typing import List, Dict
from config import Config
from code_scanner import CodeScanner, CodeSnippet
from embedding_service import EmbeddingService
from vector_store import VectorStore


class SemanticIndexer:
    """Main indexing orchestrator for the semantic search system"""
    
    def __init__(self):
        self.scanner = CodeScanner()
        self.embedding_service = EmbeddingService()
        self.vector_store = VectorStore(
            dimension=self.embedding_service.get_embedding_dimension()
        )
        self.feature_flags = self._load_feature_flags()
    
    def _load_feature_flags(self) -> List[Dict]:
        """Load feature flags from JSON file"""
        try:
            with open(Config.FEATURE_FLAGS_PATH, 'r', encoding='utf-8') as f:
                flags = json.load(f)
            print(f"Loaded {len(flags)} feature flags")
            return flags
        except Exception as e:
            print(f"Error loading feature flags: {e}")
            return []
    
    def index_codebase(self, rebuild: bool = False) -> Dict:
        """
        Index the entire codebase
        
        Args:
            rebuild: If True, clear existing index and rebuild
            
        Returns:
            Statistics about the indexing process
        """
        if rebuild:
            print("Rebuilding index from scratch...")
            self.vector_store.clear()
        
        stats = {
            'code_snippets_found': 0,
            'flags_indexed': 0,
            'total_vectors': 0,
            'services_scanned': set()
        }
        
        # Step 1: Scan codebase for flag usage
        print("\n=== Step 1: Scanning codebase ===")
        code_snippets = self.scanner.scan_directory()
        stats['code_snippets_found'] = len(code_snippets)
        
        if code_snippets:
            # Track services
            for snippet in code_snippets:
                stats['services_scanned'].add(snippet.service_name)
            
            # Step 2: Generate embeddings for code snippets
            print("\n=== Step 2: Generating code embeddings ===")
            self._index_code_snippets(code_snippets)
        
        # Step 3: Index feature flag definitions
        print("\n=== Step 3: Indexing feature flags ===")
        if self.feature_flags:
            self._index_feature_flags()
            stats['flags_indexed'] = len(self.feature_flags)
        
        # Step 4: Save index
        print("\n=== Step 4: Saving index ===")
        self.vector_store.save_index()
        
        # Update stats
        stats['total_vectors'] = self.vector_store.index.ntotal
        stats['services_scanned'] = list(stats['services_scanned'])
        
        print(f"\n=== Indexing Complete ===")
        print(f"Code snippets: {stats['code_snippets_found']}")
        print(f"Feature flags: {stats['flags_indexed']}")
        print(f"Total vectors: {stats['total_vectors']}")
        print(f"Services: {len(stats['services_scanned'])}")
        
        return stats
    
    def _index_code_snippets(self, snippets: List[CodeSnippet]):
        """Index code snippets into vector store"""
        if not snippets:
            return
        
        # Prepare texts for embedding
        texts = [self.embedding_service.prepare_code_for_embedding(s.code) 
                for s in snippets]
        
        # Generate embeddings
        embeddings = self.embedding_service.batch_generate(texts)
        
        # Prepare metadata
        metadata = []
        for snippet in snippets:
            meta = {
                'type': 'code_snippet',
                'service_name': snippet.service_name,
                'file_path': snippet.file_path,
                'line_number': snippet.line_number,
                'code': snippet.code,
                'detected_flags': snippet.detected_flags
            }
            metadata.append(meta)
        
        # Add to vector store
        self.vector_store.add_vectors(embeddings, metadata)
        print(f"Indexed {len(snippets)} code snippets")
    
    def _index_feature_flags(self):
        """Index feature flag definitions"""
        if not self.feature_flags:
            return
        
        # Create searchable text from flag definitions
        texts = []
        metadata = []
        
        for flag in self.feature_flags:
            # Combine flag name and service for better semantic search
            text = f"{flag['featureFlagName']} {flag['serviceName']} feature flag"
            texts.append(text)
            
            meta = {
                'type': 'feature_flag',
                'flag_name': flag['featureFlagName'],
                'service_name': flag['serviceName'],
                'state': flag['featureFlagState'],
                'created_date': flag['flagCreatedDate'],
                'deprecation_date': flag.get('flagDeprecationDate', '')
            }
            metadata.append(meta)
        
        # Generate embeddings
        embeddings = self.embedding_service.batch_generate(texts)
        
        # Add to vector store
        self.vector_store.add_vectors(embeddings, metadata)
        print(f"Indexed {len(self.feature_flags)} feature flags")
    
    def search_by_flag_name(self, flag_name: str) -> List[Dict]:
        """
        Search for code snippets using a specific flag
        
        Args:
            flag_name: Feature flag name to search for
            
        Returns:
            List of matching code snippets with metadata
        """
        results = []
        
        # Search in metadata for exact flag matches
        for i, meta in enumerate(self.vector_store.metadata):
            if meta.get('type') == 'code_snippet':
                if flag_name in meta.get('detected_flags', []):
                    results.append({
                        'service_name': meta['service_name'],
                        'file_path': meta['file_path'],
                        'line_number': meta['line_number'],
                        'code': meta['code'],
                        'match_type': 'exact'
                    })
        
        # Also do semantic search for similar flag names
        flag_embedding = self.embedding_service.generate_embedding(flag_name)
        semantic_results = self.vector_store.search_similar(flag_embedding, k=20)
        
        for result in semantic_results:
            if result.get('type') == 'code_snippet':
                # Avoid duplicates
                file_path = result['file_path']
                line_num = result['line_number']
                
                if not any(r['file_path'] == file_path and r['line_number'] == line_num 
                          for r in results):
                    results.append({
                        'service_name': result['service_name'],
                        'file_path': result['file_path'],
                        'line_number': result['line_number'],
                        'code': result['code'],
                        'match_type': 'semantic',
                        'score': result['score']
                    })
        
        return results
    
    def search_by_code_similarity(self, code_fragment: str, k: int = None) -> List[Dict]:
        """
        Find similar code patterns
        
        Args:
            code_fragment: Code snippet to find similar patterns for
            k: Number of results to return
            
        Returns:
            List of similar code snippets
        """
        k = k or Config.MAX_SEARCH_RESULTS
        
        # Generate embedding for query
        query_text = self.embedding_service.prepare_code_for_embedding(code_fragment)
        query_embedding = self.embedding_service.generate_embedding(query_text)
        
        # Search
        results = self.vector_store.search_similar(query_embedding, k=k)
        
        # Filter for code snippets only
        code_results = []
        for result in results:
            if result.get('type') == 'code_snippet':
                code_results.append({
                    'service_name': result['service_name'],
                    'file_path': result['file_path'],
                    'line_number': result['line_number'],
                    'code': result['code'],
                    'detected_flags': result.get('detected_flags', []),
                    'similarity_score': result['score']
                })
        
        return code_results
    
    def search_natural_language(self, query: str, k: int = None) -> Dict:
        """
        Natural language search across flags and code
        
        Args:
            query: Natural language query
            k: Number of results to return
            
        Returns:
            Dict with 'flags' and 'code' results
        """
        k = k or Config.MAX_SEARCH_RESULTS
        
        # Generate embedding
        query_embedding = self.embedding_service.generate_embedding(query)
        
        # Search
        all_results = self.vector_store.search_similar(query_embedding, k=k*2)
        
        # Separate by type
        flags = []
        code = []
        
        for result in all_results:
            if result.get('type') == 'feature_flag':
                flags.append({
                    'flag_name': result['flag_name'],
                    'service_name': result['service_name'],
                    'state': result['state'],
                    'score': result['score']
                })
            elif result.get('type') == 'code_snippet':
                code.append({
                    'service_name': result['service_name'],
                    'file_path': result['file_path'],
                    'line_number': result['line_number'],
                    'code': result['code'],
                    'score': result['score']
                })
        
        return {
            'flags': flags[:k],
            'code': code[:k]
        }
    
    def get_stats(self) -> Dict:
        """Get indexing statistics"""
        total_snippets = sum(1 for m in self.vector_store.metadata 
                           if m.get('type') == 'code_snippet')
        total_flags = sum(1 for m in self.vector_store.metadata 
                         if m.get('type') == 'feature_flag')
        
        services = set()
        for meta in self.vector_store.metadata:
            if 'service_name' in meta:
                services.add(meta['service_name'])
        
        return {
            'total_vectors': self.vector_store.index.ntotal,
            'code_snippets': total_snippets,
            'feature_flags': total_flags,
            'services': len(services),
            'service_list': sorted(list(services))
        }


if __name__ == "__main__":
    # Test the indexer
    indexer = SemanticIndexer()
    
    # Build index
    stats = indexer.index_codebase(rebuild=True)
    print(f"\nIndexing stats: {stats}")
    
    # Test searches
    print("\n=== Testing flag search ===")
    results = indexer.search_by_flag_name("auth_enable_2fa")
    print(f"Found {len(results)} results for 'auth_enable_2fa'")
    
    print("\n=== Testing natural language search ===")
    nl_results = indexer.search_natural_language("authentication and login")
    print(f"Flags: {len(nl_results['flags'])}, Code: {len(nl_results['code'])}")
