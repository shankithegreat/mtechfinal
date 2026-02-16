package com.telecom.billinginvoicing.service;

import com.telecom.billinginvoicing.repo.InMemoryRepo;
import com.telecom.common.FeatureFlagReader;
import com.telecom.billinginvoicing.util.BillingFeatureFlags;
import java.util.*;

/**
 * Service for reconciling payments with invoices.
 * Handles business rules for matching, validation, reporting, and error handling.
 * All logic is controlled by feature flags for phased releases.
 */
public class PaymentReconciliationService {

    private final InMemoryRepo repo = new InMemoryRepo();

    /**
     * Reconciles all payments with invoices in the repository.
     * Controlled by the feature flag: billing_enable_payment_reconciliation.
     */
    public void reconcilePayments() {
        if (FeatureFlagReader.isFeatureEnabled(BillingFeatureFlags.ENABLE_PAYMENT_RECONCILIATION)) {
            List<Map<String, Object>> invoices = repo.findAll();
            List<Map<String, Object>> payments = fetchPayments();
            for (Map<String, Object> invoice : invoices) {
                Optional<Map<String, Object>> match = findMatchingPayment(invoice, payments);
                if (match.isPresent()) {
                    markAsReconciled(invoice, match.get());
                } else {
                    handleUnmatchedInvoice(invoice);
                }
            }
            generateReconciliationReport(invoices, payments);
        } else {
            System.out.println("Payment reconciliation feature is disabled.");
        }
    }

    /**
     * Fetches all payments from the payment system or repository.
     * In a real system, this would call an external service or database.
     */
    private List<Map<String, Object>> fetchPayments() {
        // Simulate fetching payments
        List<Map<String, Object>> payments = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Map<String, Object> payment = new HashMap<>();
            payment.put("paymentId", UUID.randomUUID().toString());
            payment.put("amount", 100.0 + i * 10);
            payment.put("reference", "INV-" + i);
            payment.put("status", "RECEIVED");
            payments.add(payment);
        }
        return payments;
    }

    /**
     * Finds a matching payment for a given invoice.
     * Matching logic can be controlled by feature flags for different strategies.
     */
    private Optional<Map<String, Object>> findMatchingPayment(Map<String, Object> invoice, List<Map<String, Object>> payments) {
        String invoiceRef = (String) invoice.getOrDefault("reference", "");
        double invoiceAmount = (double) invoice.getOrDefault("amount", 0.0);
        for (Map<String, Object> payment : payments) {
            boolean match = false;
            if (FeatureFlagReader.isFeatureEnabled(BillingFeatureFlags.ENABLE_MATCH_BY_REFERENCE)) {
                match = invoiceRef.equals(payment.get("reference"));
            } else if (FeatureFlagReader.isFeatureEnabled(BillingFeatureFlags.ENABLE_MATCH_BY_AMOUNT)) {
                match = invoiceAmount == (double) payment.get("amount");
            } else {
                // Default: match by both
                match = invoiceRef.equals(payment.get("reference")) && invoiceAmount == (double) payment.get("amount");
            }
            if (match) return Optional.of(payment);
        }
        return Optional.empty();
    }

    /**
     * Marks an invoice as reconciled with a payment.
     */
    private void markAsReconciled(Map<String, Object> invoice, Map<String, Object> payment) {
        invoice.put("reconciled", true);
        invoice.put("reconciledWith", payment.get("paymentId"));
        payment.put("status", "RECONCILED");
        System.out.println("Invoice " + invoice.get("reference") + " reconciled with payment " + payment.get("paymentId"));
    }

    /**
     * Handles invoices that could not be matched to a payment.
     * Controlled by the feature flag: billing_handle_unmatched_invoices.
     */
    private void handleUnmatchedInvoice(Map<String, Object> invoice) {
        if (FeatureFlagReader.isFeatureEnabled(BillingFeatureFlags.ENABLE_HANDLE_UNMATCHED_INVOICES)) {
            invoice.put("reconciled", false);
            invoice.put("reconciliationError", "No matching payment found");
            System.out.println("Unmatched invoice: " + invoice.get("reference"));
        }
    }

    /**
     * Generates a reconciliation report for all invoices and payments.
     * Controlled by the feature flag: billing_enable_reconciliation_report.
     */
    public void generateReconciliationReport(List<Map<String, Object>> invoices, List<Map<String, Object>> payments) {
        if (FeatureFlagReader.isFeatureEnabled(BillingFeatureFlags.ENABLE_RECONCILIATION_REPORT)) {
            System.out.println("Reconciliation Report:");
            for (Map<String, Object> invoice : invoices) {
                System.out.println("Invoice: " + invoice.getOrDefault("reference", "N/A") + ", Reconciled: " + invoice.getOrDefault("reconciled", false));
            }
            for (Map<String, Object> payment : payments) {
                System.out.println("Payment: " + payment.getOrDefault("paymentId", "N/A") + ", Status: " + payment.getOrDefault("status", "N/A"));
            }
        }
    }

    /**
     * Validates reconciliation results for compliance.
     * Controlled by the feature flag: billing_enable_reconciliation_validation.
     */
    public boolean validateReconciliation(Map<String, Object> invoice) {
        if (FeatureFlagReader.isFeatureEnabled(BillingFeatureFlags.ENABLE_RECONCILIATION_VALIDATION)) {
            return invoice.containsKey("reconciled") && (boolean) invoice.get("reconciled");
        }
        return true;
    }

    /**
     * Handles reconciliation errors and logs them for auditing.
     * Controlled by the feature flag: billing_enable_reconciliation_error_logging.
     */
    public void logReconciliationError(Map<String, Object> invoice) {
        if (FeatureFlagReader.isFeatureEnabled(BillingFeatureFlags.ENABLE_RECONCILIATION_ERROR_LOGGING)) {
            if (invoice.containsKey("reconciliationError")) {
                System.out.println("Reconciliation error for invoice: " + invoice.get("reference") + ", Error: " + invoice.get("reconciliationError"));
            }
        }
    }

    /**
     * Simulates a batch reconciliation process for a list of invoices.
     * Controlled by the feature flag: billing_enable_batch_reconciliation.
     */
    public void batchReconcile(List<Map<String, Object>> invoices) {
        if (FeatureFlagReader.isFeatureEnabled(BillingFeatureFlags.ENABLE_BATCH_RECONCILIATION)) {
            List<Map<String, Object>> payments = fetchPayments();
            for (Map<String, Object> invoice : invoices) {
                Optional<Map<String, Object>> match = findMatchingPayment(invoice, payments);
                if (match.isPresent()) {
                    markAsReconciled(invoice, match.get());
                } else {
                    handleUnmatchedInvoice(invoice);
                }
            }
            generateReconciliationReport(invoices, payments);
        }
    }

    /**
     * Provides a summary of reconciliation status for reporting dashboards.
     * Controlled by the feature flag: billing_enable_reconciliation_dashboard.
     */
    public Map<String, Integer> reconciliationSummary(List<Map<String, Object>> invoices) {
        Map<String, Integer> summary = new HashMap<>();
        if (FeatureFlagReader.isFeatureEnabled(BillingFeatureFlags.ENABLE_RECONCILIATION_DASHBOARD)) {
            int reconciled = 0, unmatched = 0;
            for (Map<String, Object> invoice : invoices) {
                if (invoice.containsKey("reconciled") && (boolean) invoice.get("reconciled")) {
                    reconciled++;
                } else {
                    unmatched++;
                }
            }
            summary.put("reconciled", reconciled);
            summary.put("unmatched", unmatched);
        }
        return summary;
    }

    /**
     * This class is intentionally verbose to support future extensibility and
     * to meet enterprise codebase standards for large, complex domains.
     * Each method can be expanded with additional business logic as needed.
     */
}