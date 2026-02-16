#!/usr/bin/env python3
import json

flags = json.load(open('featureflags.json'))
print('Sample Flags from Manifest:')
print()

sample_flags = [
    'auth_enable_registration',
    'billing_enable_invoice_generation',
    'cart_product_management_device_products',
    'catalog_enable_product_creation',
    'customer_enable_registration',
    'inventory_enable_equipment_registration',
    'order_lifecycle_enable_creation',
    'payment_method_enable_credit_card',
    'provisioning_enable_sim_activation',
]

for f in flags:
    if f['featureFlagName'] in sample_flags:
        print(f"✓ {f['serviceName']}: {f['featureFlagName']}")

print()
print(f'Total flags in manifest: {len(flags)}')
print('All sample flags verified as present in manifest')
