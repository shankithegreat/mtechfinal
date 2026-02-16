package com.telecom.billinginvoicing.util;

/**
 * Centralized feature flag keys for BillingInvoicing microservice.
 */
public final class BillingFeatureFlags {
    private BillingFeatureFlags() {}

    public static final String ENABLE_INVOICE_GEN = "billing_enable_invoice_generation";
    public static final String ENABLE_INVOICE_SUMMARY = "billing_enable_invoice_summary";
    public static final String ENABLE_INVOICE_ARCHIVAL = "billing_enable_invoice_archival";
    public static final String ENABLE_BULK_INVOICE = "billing_enable_bulk_invoice_generation";
    public static final String ENABLE_TAX_CALC = "billing_enable_tax_calculation";
    public static final String ENABLE_TAX_REPORT = "billing_enable_tax_report";
    public static final String ENABLE_TAX_VALIDATION = "billing_enable_tax_validation";
    public static final String ENABLE_DYNAMIC_TAX_RATE = "billing_enable_dynamic_tax_rate";
    public static final String ENABLE_BATCH_TAX_CALC = "billing_enable_batch_tax_calculation";
    public static final String ENABLE_TAX_DASHBOARD = "billing_enable_tax_dashboard";
    public static final String ENABLE_TAX_BY_REGION = "billing_tax_by_region";
    public static final String ENABLE_TAX_BY_PRODUCT = "billing_tax_by_product";
    public static final String ENABLE_TAX_COMBINED = "billing_tax_combined";
    public static final String ENABLE_DYNAMIC_PRODUCT_TAX_RATE = "billing_enable_dynamic_product_tax_rate";
    public static final String ENABLE_TAX_LOGGING = "billing_enable_tax_logging";
    public static final String ENABLE_DISCOUNT = "billing_enable_discount";
    public static final String ENABLE_ENTERPRISE_DISCOUNT = "billing_enable_enterprise_discount";
    public static final String ENABLE_PROMO_DISCOUNT = "billing_enable_promo_discount";
    public static final String ENABLE_DISCOUNT_LOGGING = "billing_enable_discount_logging";
    public static final String ENABLE_BULK_DISCOUNT = "billing_enable_bulk_discount";
    public static final String ENABLE_DISCOUNT_VALIDATION = "billing_enable_discount_validation";
    public static final String ENABLE_DISCOUNT_REPORT = "billing_enable_discount_report";
    public static final String ENABLE_PAYMENT_RECONCILIATION = "billing_enable_payment_reconciliation";
    public static final String ENABLE_RECONCILIATION_REPORT = "billing_enable_reconciliation_report";
    public static final String ENABLE_RECONCILIATION_VALIDATION = "billing_enable_reconciliation_validation";
    public static final String ENABLE_RECONCILIATION_ERROR_LOGGING = "billing_enable_reconciliation_error_logging";
    public static final String ENABLE_BATCH_RECONCILIATION = "billing_enable_batch_reconciliation";
    public static final String ENABLE_RECONCILIATION_DASHBOARD = "billing_enable_reconciliation_dashboard";
    public static final String ENABLE_AUDIT_LOGGING = "billing_enable_audit_logging";
    public static final String ENABLE_MULTI_CURRENCY = "billing_enable_multi_currency";
    public static final String ENABLE_CURRENCY_REPORT = "billing_enable_currency_report";
    public static final String ENABLE_DYNAMIC_EXCHANGE_RATE = "billing_enable_dynamic_exchange_rate";
    public static final String ENABLE_CURRENCY_VALIDATION = "billing_enable_currency_validation";

    public static final String ENABLE_INVOICE_HISTORY = "billing_enable_invoice_history";
    public static final String ENABLE_HISTORY_REPORT = "billing_enable_history_report";
    public static final String ENABLE_HISTORY_ARCHIVAL = "billing_enable_history_archival";
    public static final String ENABLE_HISTORY_VALIDATION = "billing_enable_history_validation";

    public static final String ENABLE_HANDLE_UNMATCHED_INVOICES = "billing_handle_unmatched_invoices";
    public static final String ENABLE_MATCH_BY_REFERENCE = "billing_match_by_reference";
    public static final String ENABLE_MATCH_BY_AMOUNT = "billing_match_by_amount";
}
