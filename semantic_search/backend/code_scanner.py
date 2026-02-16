"""
Code Scanner for Feature Flag Detection and Full Code Indexing
Recursively scans code directories and extracts snippets
"""
import re
from pathlib import Path
from typing import List, Dict, Tuple
from config import Config


class CodeSnippet:
    """Represents a code snippet with metadata"""
    def __init__(self, file_path: str, line_number: int, code: str, 
                 service_name: str, detected_flags: List[str]):
        self.file_path = file_path
        self.line_number = line_number
        self.code = code
        self.service_name = service_name
        self.detected_flags = detected_flags
    
    def to_dict(self) -> Dict:
        return {
            'file_path': self.file_path,
            'line_number': self.line_number,
            'code': self.code,
            'service_name': self.service_name,
            'detected_flags': self.detected_flags
        }


class CodeScanner:
    """Scans codebase and extracts code snippets"""
    
    def __init__(self, scan_dir: str = None, extensions: List[str] = None):
        self.scan_dir = Path(scan_dir or Config.CODE_SCAN_DIR)
        self.extensions = extensions or Config.SUPPORTED_EXTENSIONS
        self.flag_patterns = [re.compile(pattern, re.IGNORECASE) 
                             for pattern in Config.FLAG_PATTERNS]
    
    def scan_directory(self) -> List[CodeSnippet]:
        """Recursively scan directory and extract code snippets"""
        snippets = []
        
        if not self.scan_dir.exists():
            print(f"Warning: Directory {self.scan_dir} does not exist")
            return snippets
        
        print(f"Scanning directory: {self.scan_dir}")
        
        for file_path in self._get_code_files():
            try:
                # Use chunking strategy to cover full codebase
                file_snippets = self._scan_file_in_chunks(file_path)
                snippets.extend(file_snippets)
            except Exception as e:
                # Be robust against read errors
                print(f"Error scanning {file_path}: {e}")
        
        print(f"Found {len(snippets)} total code snippets")
        return snippets
    
    def _get_code_files(self) -> List[Path]:
        """Get all code files with supported extensions"""
        code_files = []
        for ext in self.extensions:
            code_files.extend(self.scan_dir.rglob(f'*{ext}'))
        return code_files
    
    def _scan_file_in_chunks(self, file_path: Path) -> List[CodeSnippet]:
        """Scan a file and break it into overlapping chunks for indexing"""
        snippets = []
        
        try:
            with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
                lines = f.readlines()
        except Exception as e:
            print(f"Could not read {file_path}: {e}")
            return snippets
        
        # Extract service name from path
        service_name = self._extract_service_name(file_path)
        
        # Chunking configuration
        CHUNK_SIZE = 50   # Lines per chunk
        OVERLAP = 10      # Overlap between chunks
        
        total_lines = len(lines)
        if total_lines == 0:
            return []
            
        # Process file in chunks
        for i in range(0, total_lines, CHUNK_SIZE - OVERLAP):
            end_idx = min(i + CHUNK_SIZE, total_lines)
            chunk_lines = lines[i:end_idx]
            
            # Skip empty chunks
            if not chunk_lines or all(l.strip() == '' for l in chunk_lines):
                continue
                
            chunk_code = ''.join(chunk_lines)
            
            # Detect flags in this chunk
            detected_flags = self._detect_flags_in_chunk(chunk_code)
            
            snippet = CodeSnippet(
                file_path=str(file_path),
                line_number=i + 1,  # 1-indexed start line
                code=chunk_code.strip(),
                service_name=service_name,
                detected_flags=detected_flags
            )
            snippets.append(snippet)
            
            # Optimization: If file is very large, maybe skip parts?
            # For now, we index everything as requested.
            
        return snippets
    
    def _detect_flags_in_chunk(self, text: str) -> List[str]:
        """Detect feature flags in a block of text"""
        flags = []
        for pattern in self.flag_patterns:
            matches = pattern.findall(text)
            for match in matches:
                # Handle regex groups if present
                if isinstance(match, tuple):
                    # Filter empty matches from groups
                    valid_matches = [m for m in match if m]
                    flags.extend(valid_matches)
                else:
                    flags.append(match)
        
        # Clean up flags (remove 'FF_', uppercase differences) and deduplicate
        cleaned_flags = []
        for f in flags:
            # Normalize: if it looks like a constant AUTH_ENABLE_X, convert to lowercase auth_enable_x
            normalized = f.lower()
            if normalized not in cleaned_flags:
                cleaned_flags.append(normalized)
                
        return cleaned_flags

    def _extract_service_name(self, file_path: Path) -> str:
        """Extract service name from file path"""
        try:
            # Try to make relative to scan dir
            rel_path = file_path.relative_to(self.scan_dir)
            parts = rel_path.parts
            if parts:
                return parts[0]
        except ValueError:
            pass
            
        # Fallback: look for common patterns in path
        path_str = str(file_path).replace('\\', '/')
        parts = path_str.split('/')
        for i, part in enumerate(parts):
            if part == 'ms_code' and i + 1 < len(parts):
                return parts[i+1]
        
        return "unknown"


if __name__ == "__main__":
    # Test the scanner
    scanner = CodeScanner()
    snippets = scanner.scan_directory()
    
    print(f"\nTotal snippets found: {len(snippets)}")
    
    # Count snippets with flags
    flagged = [s for s in snippets if s.detected_flags]
    print(f"Snippets with flags: {len(flagged)}")
    
    if flagged:
        print("\nSample snippet with flags:")
        sample = flagged[0]
        print(f"Service: {sample.service_name}")
        print(f"File: {sample.file_path}")
        print(f"Line: {sample.line_number}")
        print(f"Flags: {sample.detected_flags}")
