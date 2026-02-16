package com.telecom.paymentprocessing.config;

/**
 * Centralized feature flag constants for Payment Processing Service.
 * All feature flags are organized by business domain for easy identification and management.
 * Each flag follows the naming convention: payment_<category>_<feature>
 */
public class PaymentProcessingFeatureFlagConstants {

    // ==================== PAYMENT METHOD MANAGEMENT ====================
    /** Feature flag for enabling credit card payment processing */
    public static final String PAYMENT_ENABLE_CREDIT_CARD = "payment_method_enable_credit_card";
    
    /** Feature flag for enabling debit card payment processing */
    public static final String PAYMENT_ENABLE_DEBIT_CARD = "payment_method_enable_debit_card";
    
    /** Feature flag for enabling bank transfer/wire payment */
    public static final String PAYMENT_ENABLE_BANK_TRANSFER = "payment_method_enable_bank_transfer";
    
    /** Feature flag for enabling digital wallet payment (mobile money, e-wallet) */
    public static final String PAYMENT_ENABLE_DIGITAL_WALLET = "payment_method_enable_digital_wallet";
    
    /** Feature flag for enabling USSD/SMS-based payment */
    public static final String PAYMENT_ENABLE_USSD_PAYMENT = "payment_method_enable_ussd_payment";
    
    /** Feature flag for enabling cash/on-account payment */
    public static final String PAYMENT_ENABLE_CASH_PAYMENT = "payment_method_enable_cash_payment";
    
    // ==================== TRANSACTION PROCESSING ====================
    /** Feature flag for enabling payment transaction processing and routing */
    public static final String PAYMENT_ENABLE_TRANSACTION_PROCESSING = "payment_transaction_enable_processing";
    
    /** Feature flag for enabling 3D Secure authentication for card payments */
    public static final String PAYMENT_ENABLE_3D_SECURE = "payment_transaction_enable_3d_secure";
    
    /** Feature flag for enabling real-time transaction validation */
    public static final String PAYMENT_ENABLE_VALIDATION = "payment_transaction_enable_validation";
    
    /** Feature flag for enabling fraud detection for transactions */
    public static final String PAYMENT_ENABLE_FRAUD_DETECTION = "payment_transaction_enable_fraud_detection";
    
    /** Feature flag for enabling transaction settlement and reconciliation */
    public static final String PAYMENT_ENABLE_SETTLEMENT = "payment_transaction_enable_settlement";
    
    // ==================== REFUND MANAGEMENT ====================
    /** Feature flag for enabling partial refunds */
    public static final String PAYMENT_ENABLE_PARTIAL_REFUND = "payment_refund_enable_partial_refund";
    
    /** Feature flag for enabling full refunds */
    public static final String PAYMENT_ENABLE_FULL_REFUND = "payment_refund_enable_full_refund";
    
    /** Feature flag for enabling automatic refund processing */
    public static final String PAYMENT_ENABLE_AUTO_REFUND = "payment_refund_enable_auto_refund";
    
    /** Feature flag for enabling refund reason tracking */
    public static final String PAYMENT_ENABLE_REFUND_TRACKING = "payment_refund_enable_refund_tracking";
    
    // ==================== RECURRING PAYMENT & SUBSCRIPTIONS ====================
    /** Feature flag for enabling recurring payment setup */
    public static final String PAYMENT_ENABLE_RECURRING_PAYMENT = "payment_recurring_enable_recurring_payment";
    
    /** Feature flag for enabling subscription auto-billing */
    public static final String PAYMENT_ENABLE_AUTO_BILLING = "payment_recurring_enable_auto_billing";
    
    /** Feature flag for enabling payment plan management */
    public static final String PAYMENT_ENABLE_PAYMENT_PLANS = "payment_recurring_enable_payment_plans";
    
    /** Feature flag for enabling dunning management for failed payments */
    public static final String PAYMENT_ENABLE_DUNNING = "payment_recurring_enable_dunning";
    
    // ==================== BILLING & INVOICING ====================
    /** Feature flag for enabling invoice generation */
    public static final String PAYMENT_ENABLE_INVOICE_GENERATION = "payment_billing_enable_invoice_generation";
    
    /** Feature flag for enabling automatic billing cycle management */
    public static final String PAYMENT_ENABLE_BILLING_CYCLES = "payment_billing_enable_billing_cycles";
    
    /** Feature flag for enabling overdue bill tracking and management */
    public static final String PAYMENT_ENABLE_OVERDUE_TRACKING = "payment_billing_enable_overdue_tracking";
    
    /** Feature flag for enabling late payment fee calculations */
    public static final String PAYMENT_ENABLE_LATE_FEE = "payment_billing_enable_late_fee";
    
    /** Feature flag for enabling credit memo management */
    public static final String PAYMENT_ENABLE_CREDIT_MEMO = "payment_billing_enable_credit_memo";
    
    // ==================== PAYMENT RECONCILIATION ====================
    /** Feature flag for enabling automated reconciliation */
    public static final String PAYMENT_ENABLE_RECONCILIATION = "payment_reconciliation_enable_reconciliation";
    
    /** Feature flag for enabling discrepancy detection and reporting */
    public static final String PAYMENT_ENABLE_DISCREPANCY_DETECTION = "payment_reconciliation_enable_discrepancy_detection";
    
    /** Feature flag for enabling payment matching with invoices */
    public static final String PAYMENT_ENABLE_PAYMENT_MATCHING = "payment_reconciliation_enable_payment_matching";
    
    // ==================== COMPLIANCE & SECURITY ====================
    /** Feature flag for enabling PCI-DSS compliance checks */
    public static final String PAYMENT_ENABLE_PCI_COMPLIANCE = "payment_compliance_enable_pci_compliance";
    
    /** Feature flag for enabling AML (Anti-Money Laundering) checks */
    public static final String PAYMENT_ENABLE_AML_CHECKS = "payment_compliance_enable_aml_checks";
    
    /** Feature flag for enabling KYC (Know Your Customer) validation */
    public static final String PAYMENT_ENABLE_KYC_VALIDATION = "payment_compliance_enable_kyc_validation";
    
    /** Feature flag for enabling encryption and tokenization */
    public static final String PAYMENT_ENABLE_TOKENIZATION = "payment_compliance_enable_tokenization";
    
    // ==================== PAYMENT ANALYTICS & REPORTING ====================
    /** Feature flag for enabling payment analytics and reporting */
    public static final String PAYMENT_ENABLE_ANALYTICS = "payment_analytics_enable_analytics";
    
    /** Feature flag for enabling payment performance metrics */
    public static final String PAYMENT_ENABLE_METRICS = "payment_analytics_enable_metrics";
    
    /** Feature flag for enabling chargeback tracking and management */
    public static final String PAYMENT_ENABLE_CHARGEBACK_TRACKING = "payment_analytics_enable_chargeback_tracking";
    
    /** Feature flag for enabling payment gateway performance monitoring */
    public static final String PAYMENT_ENABLE_GATEWAY_MONITORING = "payment_analytics_enable_gateway_monitoring";
    
    // ==================== CURRENCY & EXCHANGE ====================
    /** Feature flag for enabling multi-currency payment support */
    public static final String PAYMENT_ENABLE_MULTI_CURRENCY = "payment_exchange_enable_multi_currency";
    
    /** Feature flag for enabling dynamic currency conversion */
    public static final String PAYMENT_ENABLE_DCC = "payment_exchange_enable_dcc";
    
    /** Feature flag for enabling real-time exchange rate management */
    public static final String PAYMENT_ENABLE_EXCHANGE_RATES = "payment_exchange_enable_exchange_rates";
    
    // ==================== NOTIFICATIONS & ALERTS ====================
    /** Feature flag for enabling payment confirmation notifications */
    public static final String PAYMENT_ENABLE_NOTIFICATIONS = "payment_notifications_enable_notifications";
    
    /** Feature flag for enabling SMS payment receipts */
    public static final String PAYMENT_ENABLE_SMS_NOTIFICATIONS = "payment_notifications_enable_sms_notifications";
    
    /** Feature flag for enabling email payment receipts */
    public static final String PAYMENT_ENABLE_EMAIL_NOTIFICATIONS = "payment_notifications_enable_email_notifications";
    
    /** Feature flag for enabling failed payment alerts */
    public static final String PAYMENT_ENABLE_FAILURE_ALERTS = "payment_notifications_enable_failure_alerts";
    
    // ==================== DISPUTE & CHARGEBACK MANAGEMENT ====================
    /** Feature flag for enabling dispute handling workflow */
    public static final String PAYMENT_ENABLE_DISPUTE_HANDLING = "payment_disputes_enable_dispute_handling";
    
    /** Feature flag for enabling evidence submission for chargebacks */
    public static final String PAYMENT_ENABLE_CHARGEBACK_DEFENSE = "payment_disputes_enable_chargeback_defense";
    
    /** Feature flag for enabling dispute status tracking */
    public static final String PAYMENT_ENABLE_DISPUTE_TRACKING = "payment_disputes_enable_dispute_tracking";
    
    // ==================== ADVANCED FEATURES ====================
    /** Feature flag for enabling intelligent payment routing */
    public static final String PAYMENT_ENABLE_INTELLIGENT_ROUTING = "payment_advanced_enable_intelligent_routing";
    
    /** Feature flag for enabling machine learning based fraud prediction */
    public static final String PAYMENT_ENABLE_ML_FRAUD_DETECTION = "payment_advanced_enable_ml_fraud_detection";
    
    /** Feature flag for enabling payment retry logic with backoff */
    public static final String PAYMENT_ENABLE_RETRY_LOGIC = "payment_advanced_enable_retry_logic";
    
    /** Feature flag for enabling A/B testing of payment flows */
    public static final String PAYMENT_ENABLE_AB_TESTING = "payment_advanced_enable_ab_testing";
}
