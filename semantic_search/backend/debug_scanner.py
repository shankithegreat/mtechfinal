"""
Debug script to understand why code scanner finds 0 snippets
"""
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent))

from config import Config
from code_scanner import CodeScanner

def debug_scanner():
    print("="*60)
    print("Code Scanner Debug")
    print("="*60)
    
    scanner = CodeScanner()
    
    # Check directory
    print(f"\n1. Scan directory: {scanner.scan_dir}")
    print(f"   Exists: {scanner.scan_dir.exists()}")
    
    if scanner.scan_dir.exists():
        # Count files by extension
        print(f"\n2. Files by extension:")
        for ext in scanner.extensions:
            files = list(scanner.scan_dir.rglob(f'*{ext}'))
            print(f"   {ext}: {len(files)} files")
        
        # Get all code files
        all_files = scanner._get_code_files()
        print(f"\n3. Total code files found: {len(all_files)}")
        
        if all_files:
            # Show first few
            print(f"\n4. Sample files (first 5):")
            for f in all_files[:5]:
                print(f"   - {f}")
            
            # Test flag detection on first file
            print(f"\n5. Testing flag detection on first file...")
            try:
                test_file = all_files[0]
                snippets = scanner._scan_file(test_file)
                print(f"   File: {test_file}")
                print(f"   Snippets found: {len(snippets)}")
                
                if snippets:
                    print(f"\n   Sample snippet:")
                    s = snippets[0]
                    print(f"   - Service: {s.service_name}")
                    print(f"   - Flags: {s.detected_flags}")
                    print(f"   - Code: {s.code[:100]}...")
            except Exception as e:
                print(f"   Error: {e}")
                import traceback
                traceback.print_exc()
        else:
            print("\n⚠️ No code files found!")
            print("   Check if files exist in the directory")
            print(f"   Directory contents: {list(scanner.scan_dir.iterdir())[:10]}")
    else:
        print("\n❌ Scan directory does not exist!")
    
    # Test flag patterns
    print(f"\n6. Testing flag patterns:")
    print(f"   Number of patterns: {len(scanner.flag_patterns)}")
    
    # Test sample lines
    test_lines = [
        "featureFlags['auth_enable_registration']",
        "featureFlags.get('auth_enable_registration')",
        "if (featureFlags['auth_enable_registration']) {",
        "isEnabled('auth_enable_registration')",
        "checkFlag('auth_enable_registration')",
        "getFlag('auth_enable_registration')",
        'config.get("auth_enable_registration")',
        "FF_AUTH_ENABLE_REGISTRATION",
    ]
    
    print(f"\n7. Testing patterns against sample lines:")
    for line in test_lines:
        detected = scanner._detect_flags_in_line(line)
        print(f"   '{line[:50]}...' → {detected}")

if __name__ == "__main__":
    debug_scanner()
