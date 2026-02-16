#!/usr/bin/env python3
"""
Comprehensive Feature Flags Manifest Generator
Extracts ALL feature flags from all services and generates final featureflags.json
"""

import json
from datetime import datetime

# All flags extracted from every FeatureFlagConstants file in the codebase
# Organized by service

flags_by_service = {
    "auth-service": [
        "auth_enable_registration",
        "auth_enable_2fa",
        "auth_enable_sso",
        "auth_enable_passwordless",
        "auth_enable_mfa",
        "auth_enable_email_verification",
        "auth_enable_account_lock",
        "auth_enable_audit_logging",
        "auth_enable_session_timeout",
        "auth_enable_user_deactivation",
        "auth_enable_profile_edit",
    ],
    "billing-invoicing": [
        "billing_enable_invoice_generation",
        "billing_enable_scheduled_invoices",
        "billing_enable_invoice_numbering",
        "billing_enable_invoice_tracking",
        "billing_enable_invoice_reminders",
        "billing_tax_by_region",
        "billing_tax_calculation",
        "billing_tax_compliance",
        "billing_tax_reporting",
        "billing_enable_discount_application",
        "billing_enable_coupon_management",
        "billing_enable_promotional_pricing",
        "billing_enable_bundle_discounts",
        "billing_enable_subscription_discount",
        "billing_enable_reconciliation_automation",
        "billing_enable_payment_reconciliation",
        "billing_enable_transaction_matching",
        "billing_enable_discrepancy_detection",
        "billing_enable_multi_currency",
        "billing_enable_currency_conversion",
        "billing_enable_international_billing",
        "billing_enable_currency_rate_updates",
        "billing_enable_history_tracking",
        "billing_enable_invoice_history",
        "billing_enable_payment_history",
        "billing_enable_transaction_history",
        "billing_enable_audit_trail",
        "billing_enable_unmatched_invoice_handling",
        "billing_handle_orphaned_invoices",
        "billing_enable_invoice_aging_analysis",
        "billing_enable_manual_reconciliation",
        "billing_enable_write_off_management",
        "billing_enable_reversal_processing",
        "billing_enable_credit_memo_creation",
        "billing_enable_debit_memo_creation",
        "billing_enable_partial_payment_tracking",
        "billing_enable_advance_payment_tracking",
        "billing_enable_payment_plan_creation",
        "billing_enable_installment_management",
        "billing_enable_late_fee_calculation",
        "billing_enable_interest_accrual",
        "billing_enable_penalty_assessment",
        "billing_enable_debt_collection_integration",
        "billing_enable_write_off_tracking",
    ],
    "shopping-cart": [
        # Product Management (12)
        "cart_product_management_device_products",
        "cart_product_management_service_products",
        "cart_product_management_addon_products",
        "cart_product_management_bundled_products",
        "cart_product_management_product_search",
        "cart_product_management_product_sorting",
        "cart_product_management_product_filtering",
        "cart_product_management_product_comparison",
        "cart_product_management_wishlist",
        "cart_product_management_recently_viewed",
        "cart_product_management_recommendations",
        "cart_product_management_personalization",
        # Inventory (8)
        "cart_inventory_stock_checking",
        "cart_inventory_availability_alerts",
        "cart_inventory_reserve_items",
        "cart_inventory_hold_duration",
        "cart_inventory_backorder_support",
        "cart_inventory_pre_order_support",
        "cart_inventory_warehouse_selection",
        "cart_inventory_real_time_sync",
        # Pricing (9)
        "cart_pricing_dynamic_pricing",
        "cart_pricing_bundle_discounts",
        "cart_pricing_promotional_pricing",
        "cart_pricing_volume_discounts",
        "cart_pricing_loyalty_discounts",
        "cart_pricing_payment_method_discounts",
        "cart_pricing_time_based_pricing",
        "cart_pricing_region_based_pricing",
        "cart_pricing_segment_based_pricing",
        # Cart Operations (10)
        "cart_operations_persistent_cart",
        "cart_operations_anonymous_cart",
        "cart_operations_cart_sharing",
        "cart_operations_saved_for_later",
        "cart_operations_cart_merge",
        "cart_operations_cart_notifications",
        "cart_operations_cart_recovery",
        "cart_operations_quick_order",
        "cart_operations_bulk_order",
        "cart_operations_purchase_order",
        # Bundling (8)
        "cart_bundling_create_bundles",
        "cart_bundling_dynamic_bundles",
        "cart_bundling_bundle_pricing",
        "cart_bundling_bundle_recommendations",
        "cart_bundling_bundle_conflicts",
        "cart_bundling_plan_bundling",
        "cart_bundling_device_plan_bundles",
        "cart_bundling_service_bundles",
        # Financing (6)
        "cart_financing_installment_plans",
        "cart_financing_financing_options",
        "cart_financing_buy_now_pay_later",
        "cart_financing_lease_options",
        "cart_financing_device_financing",
        "cart_financing_financing_rates",
        # Compatibility (6)
        "cart_compatibility_device_plan_matching",
        "cart_compatibility_device_service_compatibility",
        "cart_compatibility_network_compatibility",
        "cart_compatibility_sim_card_compatibility",
        "cart_compatibility_international_compatibility",
        "cart_compatibility_feature_compatibility",
        # Tax and Fees (8)
        "cart_tax_calculation",
        "cart_tax_by_region",
        "cart_tax_compliance",
        "cart_fees_processing_fees",
        "cart_fees_shipping_fees",
        "cart_fees_service_fees",
        "cart_fees_activation_fees",
        "cart_fees_environmental_fees",
        # Checkout (9)
        "cart_checkout_one_step_checkout",
        "cart_checkout_express_checkout",
        "cart_checkout_guest_checkout",
        "cart_checkout_registered_checkout",
        "cart_checkout_payment_gateway_integration",
        "cart_checkout_multiple_payment_methods",
        "cart_checkout_payment_plan_selection",
        "cart_checkout_billing_address_validation",
        "cart_checkout_shipping_address_validation",
        # Analytics (7)
        "cart_analytics_cart_abandonment_tracking",
        "cart_analytics_conversion_tracking",
        "cart_analytics_funnel_analysis",
        "cart_analytics_user_behavior_tracking",
        "cart_analytics_session_tracking",
        "cart_analytics_product_performance",
        "cart_analytics_revenue_tracking",
        # Notifications (6)
        "cart_notifications_stock_alerts",
        "cart_notifications_price_drop_alerts",
        "cart_notifications_deal_notifications",
        "cart_notifications_abandoned_cart_notifications",
        "cart_notifications_order_updates",
        "cart_notifications_promotional_notifications",
        # Special Offers (7)
        "cart_offers_flash_sales",
        "cart_offers_limited_time_offers",
        "cart_offers_seasonal_offers",
        "cart_offers_referral_offers",
        "cart_offers_loyalty_rewards",
        "cart_offers_bundle_offers",
        "cart_offers_free_shipping_offers",
    ],
    "product-catalog": [
        # Product Management (7)
        "catalog_enable_product_creation",
        "catalog_enable_product_modification",
        "catalog_enable_product_deletion",
        "catalog_enable_product_versioning",
        "catalog_enable_product_lifecycle",
        "catalog_enable_bulk_upload",
        "catalog_enable_product_import",
        # Device Catalog (6)
        "catalog_enable_device_catalog",
        "catalog_enable_device_specifications",
        "catalog_enable_device_pricing",
        "catalog_enable_device_availability",
        "catalog_enable_device_images",
        "catalog_enable_device_reviews",
        # Service Plans (6)
        "catalog_enable_service_plans",
        "catalog_enable_plan_pricing",
        "catalog_enable_plan_comparisons",
        "catalog_enable_plan_customization",
        "catalog_enable_plan_bundling",
        "catalog_enable_plan_recommendations",
        # Bundling (6)
        "catalog_enable_bundle_creation",
        "catalog_enable_bundle_management",
        "catalog_enable_bundle_pricing",
        "catalog_enable_bundle_recommendations",
        "catalog_enable_bundle_conflicts",
        "catalog_enable_bundle_publishing",
        # Pricing (6)
        "catalog_enable_pricing_rules",
        "catalog_enable_discount_rules",
        "catalog_enable_promotional_pricing",
        "catalog_enable_volume_pricing",
        "catalog_enable_regional_pricing",
        "catalog_enable_dynamic_pricing",
        # Inventory (5)
        "catalog_enable_inventory_sync",
        "catalog_enable_stock_levels",
        "catalog_enable_warehouse_management",
        "catalog_enable_allocation_rules",
        "catalog_enable_fulfillment_options",
        # Reviews (4)
        "catalog_enable_product_reviews",
        "catalog_enable_review_moderation",
        "catalog_enable_review_ratings",
        "catalog_enable_review_images",
        # Categories (4)
        "catalog_enable_category_management",
        "catalog_enable_category_hierarchy",
        "catalog_enable_product_categorization",
        "catalog_enable_category_recommendations",
        # Availability (4)
        "catalog_enable_availability_check",
        "catalog_enable_preorder",
        "catalog_enable_backorder",
        "catalog_enable_regional_availability",
        # Analytics (4)
        "catalog_enable_product_analytics",
        "catalog_enable_search_analytics",
        "catalog_enable_view_analytics",
        "catalog_enable_purchase_analytics",
        # Advanced (3)
        "catalog_enable_ml_recommendations",
        "catalog_enable_ai_pricing",
        "catalog_enable_predictive_inventory",
    ],
    "customer-management": [
        # Registration & Activation (7)
        "customer_enable_registration",
        "customer_enable_email_verification",
        "customer_enable_phone_verification",
        "customer_enable_kyc_validation",
        "customer_enable_auto_activation",
        "customer_enable_manual_approval",
        "customer_enable_activation_delay",
        # Lifecycle Management (8)
        "customer_enable_suspension",
        "customer_enable_reactivation",
        "customer_enable_termination",
        "customer_enable_dormant_account_management",
        "customer_enable_account_migration",
        "customer_enable_bulk_operations",
        "customer_enable_account_linking",
        "customer_enable_account_hierarchy",
        # Profile Management (9)
        "customer_enable_profile_update",
        "customer_enable_communication_preferences",
        "customer_enable_language_preferences",
        "customer_enable_address_management",
        "customer_enable_contact_management",
        "customer_enable_document_upload",
        "customer_enable_identity_verification",
        "customer_enable_profile_image",
        "customer_enable_profile_completion_tracking",
        # Billing & Subscription (9)
        "customer_enable_billing_address",
        "customer_enable_payment_method_management",
        "customer_enable_subscription_management",
        "customer_enable_plan_change",
        "customer_enable_addon_management",
        "customer_enable_auto_renewal",
        "customer_enable_manual_renewal",
        "customer_enable_billing_notification",
        "customer_enable_invoice_access",
        # Contracts & Agreements (4)
        "customer_enable_contract_management",
        "customer_enable_contract_templates",
        "customer_enable_signature_capture",
        "customer_enable_contract_renewal",
        # Preferences & Consents (6)
        "customer_enable_privacy_preferences",
        "customer_enable_marketing_opt_in",
        "customer_enable_consent_tracking",
        "customer_enable_cookie_preferences",
        "customer_enable_communication_channel_preference",
        "customer_enable_notification_preferences",
        # Analytics & Reporting (6)
        "customer_enable_churn_prediction",
        "customer_enable_lifetime_value_tracking",
        "customer_enable_customer_segmentation",
        "customer_enable_behavior_analytics",
        "customer_enable_engagement_tracking",
        "customer_enable_customer_journey_tracking",
        # Data Management (5)
        "customer_enable_data_export",
        "customer_enable_data_deletion",
        "customer_enable_data_anonymization",
        "customer_enable_gdpr_compliance",
        "customer_enable_data_retention_policy",
        # Compliance & Validation (6)
        "customer_enable_age_verification",
        "customer_enable_address_validation",
        "customer_enable_fraud_detection",
        "customer_enable_aml_check",
        "customer_enable_sanctions_screening",
        "customer_enable_account_linking_validation",
    ],
    "inventory-management": [
        # Equipment Management (4)
        "inventory_enable_equipment_registration",
        "inventory_enable_equipment_tracking",
        "inventory_enable_equipment_maintenance",
        "inventory_enable_equipment_depreciation",
        # Stock Management (5)
        "inventory_enable_stock_level_management",
        "inventory_enable_inventory_adjustment",
        "inventory_enable_stock_transfer",
        "inventory_enable_stock_counting",
        "inventory_enable_lot_tracking",
        # Warehouse Management (4)
        "inventory_enable_warehouse_setup",
        "inventory_enable_warehouse_location_management",
        "inventory_enable_warehouse_assignment",
        "inventory_enable_cross_warehouse_transfer",
        # Order Management (4)
        "inventory_enable_order_allocation",
        "inventory_enable_order_fulfillment",
        "inventory_enable_order_packing",
        "inventory_enable_order_shipping",
        # Supplier Management (4)
        "inventory_enable_supplier_management",
        "inventory_enable_supplier_pricing",
        "inventory_enable_supplier_orders",
        "inventory_enable_supplier_performance_tracking",
        # Lifecycle Management (4)
        "inventory_enable_product_lifecycle_management",
        "inventory_enable_obsolescence_tracking",
        "inventory_enable_end_of_life_management",
        "inventory_enable_disposal_tracking",
        # Analytics (4)
        "inventory_enable_inventory_analytics",
        "inventory_enable_turnover_analysis",
        "inventory_enable_forecasting",
        "inventory_enable_demand_planning",
        # Cost & Pricing (4)
        "inventory_enable_cost_tracking",
        "inventory_enable_valuation_methods",
        "inventory_enable_price_management",
        "inventory_enable_cost_allocation",
    ],
    "order-management": [
        # Order Lifecycle (5)
        "order_lifecycle_enable_creation",
        "order_lifecycle_enable_modification",
        "order_lifecycle_enable_cancellation",
        "order_lifecycle_enable_order_tracking",
        "order_lifecycle_enable_order_status_history",
        # Fulfillment (5)
        "order_fulfillment_enable_order_allocation",
        "order_fulfillment_enable_picking",
        "order_fulfillment_enable_packing",
        "order_fulfillment_enable_shipping",
        "order_fulfillment_enable_delivery_tracking",
        # Provisioning (4)
        "order_provisioning_enable_service_provisioning",
        "order_provisioning_enable_sim_activation",
        "order_provisioning_enable_device_shipping",
        "order_provisioning_enable_activation_delay",
        # Pricing & Billing (5)
        "order_pricing_enable_dynamic_pricing",
        "order_pricing_enable_discount_application",
        "order_pricing_enable_tax_calculation",
        "order_pricing_enable_order_billing",
        "order_pricing_enable_billing_sync",
        # Product Management (4)
        "order_products_enable_product_variants",
        "order_products_enable_addon_products",
        "order_products_enable_bundle_products",
        "order_products_enable_service_products",
        # Inventory (3)
        "order_inventory_enable_stock_checking",
        "order_inventory_enable_reserve_items",
        "order_inventory_enable_backorder",
        # Notifications (3)
        "order_notifications_enable_order_confirmation",
        "order_notifications_enable_order_updates",
        "order_notifications_enable_delivery_updates",
        # Analytics (3)
        "order_analytics_enable_order_analytics",
        "order_analytics_enable_fulfillment_analytics",
        "order_analytics_enable_shipping_analytics",
        # Compliance (3)
        "order_compliance_enable_audit_logging",
        "order_compliance_enable_order_retention",
        "order_compliance_enable_compliance_reporting",
        # Advanced (3)
        "order_advanced_enable_order_recommendations",
        "order_advanced_enable_ml_demand_forecasting",
        "order_advanced_enable_predictive_inventory",
    ],
    "payment-processing": [
        # Payment Methods (6)
        "payment_method_enable_credit_card",
        "payment_method_enable_debit_card",
        "payment_method_enable_digital_wallet",
        "payment_method_enable_bank_transfer",
        "payment_method_enable_cryptocurrency",
        "payment_method_enable_buy_now_pay_later",
        # Transactions (5)
        "payment_transaction_enable_single_payment",
        "payment_transaction_enable_installment_payment",
        "payment_transaction_enable_payment_authorization",
        "payment_transaction_enable_payment_settlement",
        "payment_transaction_enable_payment_settlement_webhook",
        # Refunds (4)
        "payment_refund_enable_full_refund",
        "payment_refund_enable_partial_refund",
        "payment_refund_enable_refund_reversal",
        "payment_refund_enable_refund_automation",
        # Recurring (4)
        "payment_recurring_enable_recurring_charges",
        "payment_recurring_enable_subscription_billing",
        "payment_recurring_enable_auto_renew",
        "payment_recurring_enable_billing_schedule",
        # Billing (5)
        "payment_billing_enable_bill_generation",
        "payment_billing_enable_invoice_billing",
        "payment_billing_enable_billing_cycle_management",
        "payment_billing_enable_billing_notifications",
        "payment_billing_enable_usage_based_billing",
        # Reconciliation (3)
        "payment_reconciliation_enable_transaction_matching",
        "payment_reconciliation_enable_payment_reconciliation",
        "payment_reconciliation_enable_discrepancy_handling",
        # Compliance (4)
        "payment_compliance_enable_pci_dss_compliance",
        "payment_compliance_enable_fraud_detection",
        "payment_compliance_enable_aml_check",
        "payment_compliance_enable_transaction_monitoring",
        # Analytics (4)
        "payment_analytics_enable_payment_analytics",
        "payment_analytics_enable_revenue_analytics",
        "payment_analytics_enable_customer_payment_analytics",
        "payment_analytics_enable_payment_method_analytics",
        # Currency (3)
        "payment_currency_enable_multi_currency",
        "payment_currency_enable_currency_conversion",
        "payment_currency_enable_currency_rate_management",
        # Notifications (4)
        "payment_notification_enable_payment_confirmation",
        "payment_notification_enable_payment_failure_notification",
        "payment_notification_enable_refund_notification",
        "payment_notification_enable_invoice_notification",
        # Disputes (3)
        "payment_dispute_enable_chargeback_handling",
        "payment_dispute_enable_dispute_management",
        "payment_dispute_enable_dispute_tracking",
        # Advanced (4)
        "payment_advanced_enable_ml_fraud_detection",
        "payment_advanced_enable_predictive_churn",
        "payment_advanced_enable_dynamic_pricing",
        "payment_advanced_enable_payment_optimization",
    ],
    "service-provisioning": [
        # Service Activation (8)
        "provisioning_enable_sim_activation",
        "provisioning_enable_device_activation",
        "provisioning_enable_network_config",
        "provisioning_enable_msisdn_allocation",
        "provisioning_enable_imei_assignment",
        "provisioning_enable_apn_config",
        "provisioning_enable_5g_network",
        "provisioning_enable_vpn_config",
        # Service Configuration (7)
        "provisioning_enable_voice_config",
        "provisioning_enable_data_config",
        "provisioning_enable_sms_config",
        "provisioning_enable_roaming_config",
        "provisioning_enable_call_forwarding",
        "provisioning_enable_voicemail",
        "provisioning_enable_supplementary_services",
        # Billing Integration (6)
        "provisioning_enable_billing_sync",
        "provisioning_enable_recurring_charges",
        "provisioning_enable_usage_tracking",
        "provisioning_enable_quota_management",
        "provisioning_enable_throttling",
        "provisioning_enable_overage_charges",
        # Service Management (6)
        "provisioning_enable_service_upgrade",
        "provisioning_enable_service_downgrade",
        "provisioning_enable_plan_change",
        "provisioning_enable_add_on_management",
        "provisioning_enable_service_suspension",
        "provisioning_enable_service_termination",
        # Quality of Service (5)
        "provisioning_enable_qos_management",
        "provisioning_enable_bandwidth_allocation",
        "provisioning_enable_priority_levels",
        "provisioning_enable_latency_control",
        "provisioning_enable_traffic_shaping",
        # Authentication & Security (5)
        "provisioning_enable_authentication",
        "provisioning_enable_encryption",
        "provisioning_enable_security_groups",
        "provisioning_enable_firewall_config",
        "provisioning_enable_dlp",
        # Multi-Carrier Support (4)
        "provisioning_enable_multi_carrier",
        "provisioning_enable_carrier_routing",
        "provisioning_enable_inter_carrier_communication",
        "provisioning_enable_carrier_specific_config",
        # Automation & Orchestration (4)
        "provisioning_enable_workflow_automation",
        "provisioning_enable_orchestration",
        "provisioning_enable_auto_scaling",
        "provisioning_enable_smart_provisioning",
        # Reporting & Analytics (4)
        "provisioning_enable_usage_analytics",
        "provisioning_enable_performance_metrics",
        "provisioning_enable_audit_logs",
        "provisioning_enable_provisioning_reports",
        # Notifications & Alerts (3)
        "provisioning_enable_notifications",
        "provisioning_enable_alerts",
        "provisioning_enable_service_status_updates",
        # Network Slicing (3)
        "provisioning_enable_network_slicing",
        "provisioning_enable_slice_management",
        "provisioning_enable_slice_isolation",
    ],
}

# Create the final manifest with all unique flags
manifest = []
created_date = "2025-12-14"

# Process all flags from all services
for service_name, flags_list in flags_by_service.items():
    for flag_name in sorted(set(flags_list)):  # Remove duplicates with set()
        manifest.append({
            "serviceName": service_name,
            "featureFlagName": flag_name,
            "featureFlagState": "disabled",
            "flagCreatedDate": created_date,
            "flagDeprecationDate": ""
        })

# Sort by service name and then by flag name for consistency
manifest.sort(key=lambda x: (x["serviceName"], x["featureFlagName"]))

# Generate JSON with proper formatting
json_output = json.dumps(manifest, indent=2)

# Print summary
print(f"Total unique flags: {len(manifest)}")
print(f"Services: {len(flags_by_service)}")
for service, flags in sorted(flags_by_service.items()):
    print(f"  {service}: {len(set(flags))} flags")

print(f"\nTotal JSON size: {len(json_output)} bytes")
print(f"Total lines: {len(json_output.splitlines())}")

# Write to file
output_path = "c:\\Users\\ivars\\Downloads\\telecom_microservices\\featureflags.json"
with open(output_path, 'w') as f:
    f.write(json_output)

print(f"\n✓ Successfully generated comprehensive featureflags.json at {output_path}")
print(f"✓ All {len(manifest)} feature flags included across {len(flags_by_service)} services")
