"""
Script to analyze feature flag usage across services
"""
import json
import sys
from pathlib import Path
from collections import defaultdict

def analyze_cross_service_flags():
    metadata_path = Path("backend/vector_db/metadata.json")
    
    if not metadata_path.exists():
        print(f"Error: {metadata_path} not found")
        return

    try:
        with open(metadata_path, 'r', encoding='utf-8') as f:
            metadata = json.load(f)
            
        print(f"Loaded {len(metadata)} items from metadata.json")
        
        # Dictionary to track {flag_name: set(service_names)}
        flag_usage = defaultdict(set)
        
        for item in metadata:
            # Check if it's a code snippet (has 'detected_flags')
            if 'detected_flags' in item and item['detected_flags']:
                service = item.get('service_name', 'unknown')
                for flag in item['detected_flags']:
                    flag_usage[flag].add(service)
                    
        # Filter for flags used in > 1 service
        cross_service_flags = {
            flag: services 
            for flag, services in flag_usage.items() 
            if len(services) > 1
        }
        
        print("\n" + "="*50)
        print(f"Found {len(cross_service_flags)} flags used across multiple services")
        print("="*50)
        
        if cross_service_flags:
            print(f"{'Flag Name':<40} | {'Count':<5} | {'Services'}")
            print("-" * 80)
            for flag, services in sorted(cross_service_flags.items(), key=lambda x: len(x[1]), reverse=True):
                print(f"{flag:<40} | {len(services):<5} | {', '.join(sorted(services))}")
        else:
            print("No flags found being used in multiple services.")
            
        # Also list top used flags overall
        print("\n" + "="*50)
        print("Top 10 Most Used Flags (by Service Count)")
        print("="*50)
        top_flags = sorted(flag_usage.items(), key=lambda x: len(x[1]), reverse=True)[:10]
        for flag, services in top_flags:
            print(f"{flag:<40} | {len(services)} services: {', '.join(sorted(services))}")

    except Exception as e:
        print(f"Error analyzing metadata: {e}")

if __name__ == "__main__":
    analyze_cross_service_flags()
