#!/usr/bin/env python3
"""Test FeatureFlagReader parsing logic"""
import json
import re

# Read the manifest file
with open('featureflags.json', 'r') as f:
    content = f.read()

# Simulate what FeatureFlagReader.loadFlags() does
# 1. Remove inline comments (lines with //)
lines = content.split('\n')
cleaned_lines = [line if '//' not in line else line[:line.index('//')] for line in lines]
cleaned_content = '\n'.join(cleaned_lines)

# 2. Remove fence markers (```)
cleaned_content = re.sub(r'```', '', cleaned_content)

# 3. Parse JSON
flags_array = json.loads(cleaned_content)

print(f'✓ Successfully loaded {len(flags_array)} feature flags')
print(f'✓ Services: {sorted(set(f["serviceName"] for f in flags_array))}')

# Test flag lookup
flag_map = {f['featureFlagName']: f['featureFlagState'] == 'enabled' for f in flags_array}

# Test some flags
test_flags = [
    'auth_enable_registration',
    'billing_enable_invoice_generation',
    'cart_product_management_device_products',
    'catalog_enable_product_creation',
    'customer_enable_registration',
]

print(f'\nTesting flag lookups:')
for flag_name in test_flags:
    enabled = flag_map.get(flag_name, False)
    print(f'  {flag_name}: {"enabled" if enabled else "disabled"}')

print(f'\n✓ FeatureFlagReader would successfully parse and load this manifest')
print(f'\nFlag coverage by service:')
for service in sorted(set(f["serviceName"] for f in flags_array)):
    count = len([f for f in flags_array if f["serviceName"] == service])
    print(f'  {service}: {count} flags')
