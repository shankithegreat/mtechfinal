package com.telecom.customermanagement.config;

/**
 * Centralized feature flag constants for Customer Management service.
 * All feature flags used in this microservice are declared here to provide
 * a single source of truth and improve maintainability.
 */
public class FeatureFlagConstants {

    // Customer Lifecycle Management Flags
    public static final String ENABLE_CUSTOMER_REGISTRATION = "customer_enable_registration";
    public static final String ENABLE_CUSTOMER_VERIFICATION = "customer_enable_verification";
    public static final String ENABLE_CUSTOMER_ACTIVATION = "customer_enable_activation";
    public static final String ENABLE_CUSTOMER_SUSPENSION = "customer_enable_suspension";
    public static final String ENABLE_CUSTOMER_TERMINATION = "customer_enable_termination";
    
    // Profile Management Flags
    public static final String ENABLE_PROFILE_EDIT = "customer_enable_profile_edit";
    public static final String ENABLE_PROFILE_VALIDATION = "customer_enable_profile_validation";
    public static final String ENABLE_ADVANCED_PROFILE_SYNC = "customer_enable_advanced_profile_sync";
    
    // Billing & Subscription Flags
    public static final String ENABLE_BILLING_ACCOUNT = "customer_enable_billing_account";
    public static final String ENABLE_SUBSCRIPTION_MANAGEMENT = "customer_enable_subscription_management";
    public static final String ENABLE_AUTO_RENEWAL = "customer_enable_auto_renewal";
    public static final String ENABLE_USAGE_TRACKING = "customer_enable_usage_tracking";
    public static final String ENABLE_BILLING_SYNC = "customer_enable_billing_sync";
    
    // Contract Management Flags
    public static final String ENABLE_CONTRACT_MANAGEMENT = "customer_enable_contract_management";
    public static final String ENABLE_CONTRACT_RENEWAL = "customer_enable_contract_renewal";
    public static final String ENABLE_EARLY_TERMINATION = "customer_enable_early_termination";
    
    // Service Quality & Communication Flags
    public static final String ENABLE_PREFERENCE_MANAGEMENT = "customer_enable_preference_management";
    public static final String ENABLE_NOTIFICATION_PREFERENCES = "customer_enable_notification_preferences";
    public static final String ENABLE_LOYALTY_PROGRAM = "customer_enable_loyalty_program";
    public static final String ENABLE_CUSTOMER_SEGMENTATION = "customer_enable_customer_segmentation";
    
    // Analytics & Reporting Flags
    public static final String ENABLE_CUSTOMER_ANALYTICS = "customer_enable_customer_analytics";
    public static final String ENABLE_CHURN_PREDICTION = "customer_enable_churn_prediction";
    public static final String ENABLE_LIFETIME_VALUE_CALCULATION = "customer_enable_lifetime_value_calculation";
    
    // Data Management Flags
    public static final String ENABLE_DATA_EXPORT = "customer_enable_data_export";
    public static final String ENABLE_GDPR_COMPLIANCE = "customer_enable_gdpr_compliance";
    public static final String ENABLE_DUPLICATE_DETECTION = "customer_enable_duplicate_detection";
    
    // Validation & Compliance Flags
    public static final String ENABLE_KYC_VALIDATION = "customer_enable_kyc_validation";
    public static final String ENABLE_CREDIT_CHECK = "customer_enable_credit_check";
    public static final String ENABLE_FRAUD_DETECTION = "customer_enable_fraud_detection";
}
