package com.telecom.ordermanagement.config;

/**
 * Centralized feature flag constants for Order Management Service.
 * All feature flags are organized by business domain for easy identification and management.
 * Each flag follows the naming convention: order_<category>_<feature>
 */
public class OrderManagementFeatureFlagConstants {

    // ==================== ORDER LIFECYCLE MANAGEMENT ====================
    /** Feature flag for enabling order creation with advanced validation and routing */
    public static final String ORDER_ENABLE_CREATION = "order_lifecycle_enable_creation";
    
    /** Feature flag for enabling order validation with complex business rules */
    public static final String ORDER_ENABLE_VALIDATION = "order_lifecycle_enable_validation";
    
    /** Feature flag for enabling order approval workflow */
    public static final String ORDER_ENABLE_APPROVAL_WORKFLOW = "order_lifecycle_enable_approval_workflow";
    
    /** Feature flag for enabling order status tracking and updates */
    public static final String ORDER_ENABLE_STATUS_TRACKING = "order_lifecycle_enable_status_tracking";
    
    /** Feature flag for enabling order cancellation with fee calculations */
    public static final String ORDER_ENABLE_CANCELLATION = "order_lifecycle_enable_cancellation";
    
    // ==================== FULFILLMENT & DELIVERY ====================
    /** Feature flag for enabling fulfillment processing */
    public static final String ORDER_ENABLE_FULFILLMENT = "order_fulfillment_enable_fulfillment";
    
    /** Feature flag for enabling inventory allocation and reservation */
    public static final String ORDER_ENABLE_INVENTORY_ALLOCATION = "order_fulfillment_enable_inventory_allocation";
    
    /** Feature flag for enabling delivery scheduling and tracking */
    public static final String ORDER_ENABLE_DELIVERY_TRACKING = "order_fulfillment_enable_delivery_tracking";
    
    /** Feature flag for enabling backorder management */
    public static final String ORDER_ENABLE_BACKORDER = "order_fulfillment_enable_backorder";
    
    /** Feature flag for enabling batch order processing */
    public static final String ORDER_ENABLE_BATCH_PROCESSING = "order_fulfillment_enable_batch_processing";
    
    // ==================== SERVICE PROVISIONING ====================
    /** Feature flag for enabling automatic service provisioning on order fulfillment */
    public static final String ORDER_ENABLE_SERVICE_PROVISIONING = "order_provisioning_enable_service_provisioning";
    
    /** Feature flag for enabling SIM card activation provisioning */
    public static final String ORDER_ENABLE_SIM_ACTIVATION = "order_provisioning_enable_sim_activation";
    
    /** Feature flag for enabling network configuration provisioning */
    public static final String ORDER_ENABLE_NETWORK_CONFIG = "order_provisioning_enable_network_config";
    
    /** Feature flag for enabling device provisioning and handover */
    public static final String ORDER_ENABLE_DEVICE_PROVISIONING = "order_provisioning_enable_device_provisioning";
    
    // ==================== PRICING & BILLING ====================
    /** Feature flag for enabling dynamic pricing calculations */
    public static final String ORDER_ENABLE_DYNAMIC_PRICING = "order_pricing_enable_dynamic_pricing";
    
    /** Feature flag for enabling promotional discount application */
    public static final String ORDER_ENABLE_PROMOTIONS = "order_pricing_enable_promotions";
    
    /** Feature flag for enabling tax calculation based on jurisdiction */
    public static final String ORDER_ENABLE_TAX_CALCULATION = "order_pricing_enable_tax_calculation";
    
    /** Feature flag for enabling payment processing and verification */
    public static final String ORDER_ENABLE_PAYMENT_PROCESSING = "order_pricing_enable_payment_processing";
    
    /** Feature flag for enabling billing account creation for order */
    public static final String ORDER_ENABLE_BILLING_ACCOUNT = "order_pricing_enable_billing_account";
    
    // ==================== CUSTOMER & PRODUCT MANAGEMENT ====================
    /** Feature flag for enabling bundled service orders */
    public static final String ORDER_ENABLE_BUNDLE_ORDERS = "order_products_enable_bundle_orders";
    
    /** Feature flag for enabling multi-line order handling */
    public static final String ORDER_ENABLE_MULTILINE_ORDERS = "order_products_enable_multiline_orders";
    
    /** Feature flag for enabling family plan order processing */
    public static final String ORDER_ENABLE_FAMILY_PLANS = "order_products_enable_family_plans";
    
    /** Feature flag for enabling contract generation for orders */
    public static final String ORDER_ENABLE_CONTRACT_GENERATION = "order_products_enable_contract_generation";
    
    // ==================== INVENTORY & WAREHOUSE ====================
    /** Feature flag for enabling multi-warehouse fulfillment */
    public static final String ORDER_ENABLE_MULTIWAREHOUSE_FULFILLMENT = "order_inventory_enable_multiwarehouse_fulfillment";
    
    /** Feature flag for enabling warehouse optimization */
    public static final String ORDER_ENABLE_WAREHOUSE_OPTIMIZATION = "order_inventory_enable_warehouse_optimization";
    
    /** Feature flag for enabling stock reservation management */
    public static final String ORDER_ENABLE_STOCK_RESERVATION = "order_inventory_enable_stock_reservation";
    
    // ==================== NOTIFICATIONS & COMMUNICATIONS ====================
    /** Feature flag for enabling customer notification on order events */
    public static final String ORDER_ENABLE_NOTIFICATIONS = "order_notifications_enable_notifications";
    
    /** Feature flag for enabling SMS notifications for order updates */
    public static final String ORDER_ENABLE_SMS_NOTIFICATIONS = "order_notifications_enable_sms_notifications";
    
    /** Feature flag for enabling email notifications for order updates */
    public static final String ORDER_ENABLE_EMAIL_NOTIFICATIONS = "order_notifications_enable_email_notifications";
    
    // ==================== ANALYTICS & REPORTING ====================
    /** Feature flag for enabling order analytics and insights */
    public static final String ORDER_ENABLE_ANALYTICS = "order_analytics_enable_analytics";
    
    /** Feature flag for enabling order performance metrics */
    public static final String ORDER_ENABLE_PERFORMANCE_METRICS = "order_analytics_enable_performance_metrics";
    
    /** Feature flag for enabling order forecasting */
    public static final String ORDER_ENABLE_FORECASTING = "order_analytics_enable_forecasting";
    
    // ==================== COMPLIANCE & QUALITY ====================
    /** Feature flag for enabling compliance checks and validations */
    public static final String ORDER_ENABLE_COMPLIANCE_CHECKS = "order_compliance_enable_compliance_checks";
    
    /** Feature flag for enabling quality assurance workflows */
    public static final String ORDER_ENABLE_QA_WORKFLOW = "order_compliance_enable_qa_workflow";
    
    /** Feature flag for enabling regulatory requirement validation */
    public static final String ORDER_ENABLE_REGULATORY_VALIDATION = "order_compliance_enable_regulatory_validation";
    
    // ==================== ADVANCED FEATURES ====================
    /** Feature flag for enabling machine learning based order recommendations */
    public static final String ORDER_ENABLE_ML_RECOMMENDATIONS = "order_advanced_enable_ml_recommendations";
    
    /** Feature flag for enabling order fraud detection */
    public static final String ORDER_ENABLE_FRAUD_DETECTION = "order_advanced_enable_fraud_detection";
    
    /** Feature flag for enabling order priority queue management */
    public static final String ORDER_ENABLE_PRIORITY_QUEUE = "order_advanced_enable_priority_queue";
}
