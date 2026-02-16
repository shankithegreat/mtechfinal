package com.telecom.paymentprocessing.controller;

import com.telecom.common.FeatureFlagReader;
import com.telecom.paymentprocessing.model.*;
import com.telecom.paymentprocessing.service.*;
import com.telecom.paymentprocessing.config.PaymentProcessingFeatureFlagConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * REST controllers for PaymentProcessing endpoints.
 * Comprehensive payment processing with feature flags integrated across all operations.
 * Supports payment transactions, invoicing, refunds, subscriptions, disputes, and compliance.
 */
@RestController
@RequestMapping("/api/payment-processing")
public class PaymentProcessingController {

    @Autowired
    private PaymentProcessingService service;

    // ==================== PAYMENT TRANSACTION ENDPOINTS ====================

    /**
     * Process a payment transaction
     */
    @PostMapping("/transactions")
    public ResponseEntity<?> processPayment(@RequestBody PaymentTransaction paymentRequest) {
        try {
            if (!FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_TRANSACTION_PROCESSING)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Payment transaction processing is disabled"));
            }

            PaymentTransaction transaction = service.processPayment(paymentRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get transaction details
     */
    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<?> getTransaction(@PathVariable String transactionId) {
        try {
            Object transaction = service.getById(transactionId);
            if (transaction == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Settle a transaction
     */
    @PostMapping("/transactions/{transactionId}/settle")
    public ResponseEntity<?> settleTransaction(@PathVariable String transactionId) {
        try {
            if (!FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_SETTLEMENT)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Payment settlement is disabled"));
            }

            PaymentTransaction settled = service.settleTransaction(transactionId);
            return ResponseEntity.ok(settled);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== INVOICE ENDPOINTS ====================

    /**
     * Generate invoice
     */
    @PostMapping("/invoices")
    public ResponseEntity<?> generateInvoice(
            @RequestParam String customerId,
            @RequestParam String billingAccountId,
            @RequestBody List<InvoiceLineItem> lineItems) {
        try {
            if (!FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_INVOICE_GENERATION)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Invoice generation is disabled"));
            }

            Invoice invoice = service.generateInvoice(customerId, billingAccountId, lineItems);
            return ResponseEntity.status(HttpStatus.CREATED).body(invoice);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Track overdue bills
     */
    @GetMapping("/invoices/overdue/{customerId}")
    public ResponseEntity<?> trackOverdueBills(@PathVariable String customerId) {
        try {
            if (!FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_OVERDUE_TRACKING)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Overdue tracking is disabled"));
            }

            Map<String, Object> overdueReport = service.trackOverdueBills(customerId);
            return ResponseEntity.ok(overdueReport);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Reconcile payments with invoice
     */
    @PostMapping("/reconciliation/{invoiceId}")
    public ResponseEntity<?> reconcilePayments(@PathVariable String invoiceId) {
        try {
            if (!FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_RECONCILIATION)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Payment reconciliation is disabled"));
            }

            Map<String, Object> reconciliation = service.reconcilePayments(invoiceId);
            return ResponseEntity.ok(reconciliation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== REFUND ENDPOINTS ====================

    /**
     * Process refund
     */
    @PostMapping("/refunds")
    public ResponseEntity<?> processRefund(
            @RequestParam String transactionId,
            @RequestParam RefundReason reason,
            @RequestParam double refundAmount) {
        try {
            if (!FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_FULL_REFUND) &&
                !FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_PARTIAL_REFUND)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Refund processing is disabled"));
            }

            Refund refund = service.processRefund(transactionId, reason, refundAmount);
            return ResponseEntity.status(HttpStatus.CREATED).body(refund);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== RECURRING PAYMENT ENDPOINTS ====================

    /**
     * Setup recurring payment
     */
    @PostMapping("/recurring-payments")
    public ResponseEntity<?> setupRecurringPayment(@RequestBody RecurringPayment paymentRequest) {
        try {
            if (!FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_RECURRING_PAYMENT)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Recurring payment setup is disabled"));
            }

            RecurringPayment recurring = service.setupRecurringPayment(paymentRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(recurring);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== DISPUTE ENDPOINTS ====================

    /**
     * Handle payment dispute
     */
    @PostMapping("/disputes")
    public ResponseEntity<?> handleDispute(
            @RequestParam String transactionId,
            @RequestParam DisputeType disputeType,
            @RequestParam String reason) {
        try {
            if (!FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_DISPUTE_HANDLING)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Dispute handling is disabled"));
            }

            PaymentDispute dispute = service.handleDispute(transactionId, disputeType, reason);
            return ResponseEntity.status(HttpStatus.CREATED).body(dispute);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Submit dispute evidence
     */
    @PostMapping("/disputes/{disputeId}/evidence")
    public ResponseEntity<?> submitDisputeEvidence(
            @PathVariable String disputeId,
            @RequestBody DisputeEvidence evidence) {
        try {
            if (!FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_CHARGEBACK_DEFENSE)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Chargeback defense is disabled"));
            }

            PaymentDispute dispute = service.submitDisputeEvidence(disputeId, evidence);
            return ResponseEntity.ok(dispute);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== LEGACY GENERIC ENDPOINTS ====================

    /**
     * List all payment records
     */
    @GetMapping
    public ResponseEntity<List<Object>> listAll() {
        return ResponseEntity.ok(service.listAll());
    }

    /**
     * Get payment record by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable String id) {
        Object obj = service.getById(id);
        if (obj == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(obj);
    }

    /**
     * Create generic payment record (legacy endpoint)
     */
    @PostMapping
    public ResponseEntity<Object> create(@RequestBody Map<String, Object> payload) {
        try {
            Object created = service.create(payload);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Update payment record by ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        try {
            Object updated = service.update(id, payload);
            if (updated == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Delete payment record by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        boolean ok = service.delete(id);
        if (!ok) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }

    /**
     * Search payment records
     */
    @GetMapping("/search")
    public ResponseEntity<List<Object>> search(@RequestParam Map<String,String> params) {
        return ResponseEntity.ok(service.search(params));
    }

    /**
     * Bulk create payment records
     */
    @PostMapping("/bulk")
    public ResponseEntity<List<Object>> bulkCreate(@RequestBody List<Map<String,Object>> payloads) {
        return ResponseEntity.ok(service.bulkCreate(payloads));
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String,Object>> health() {
        Map<String,Object> health = new HashMap<>();
        health.put("service", "payment-processing");
        health.put("status", "UP");
        health.put("transactions", service.count());
        health.put("featureFlagsEnabled", getEnabledFeatureFlags());
        return ResponseEntity.ok(health);
    }

    /**
     * Get information about enabled feature flags
     */
    @GetMapping("/features")
    public ResponseEntity<Map<String, Object>> getFeaturesStatus() {
        Map<String, Object> features = new HashMap<>();

        features.put("paymentMethods", Map.of(
            "creditCardEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_CREDIT_CARD),
            "debitCardEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_DEBIT_CARD),
            "bankTransferEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_BANK_TRANSFER),
            "digitalWalletEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_DIGITAL_WALLET),
            "ussdEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_USSD_PAYMENT),
            "cashEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_CASH_PAYMENT)
        ));

        features.put("transactions", Map.of(
            "processingEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_TRANSACTION_PROCESSING),
            "threeDSecureEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_3D_SECURE),
            "validationEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_VALIDATION),
            "fraudDetectionEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_FRAUD_DETECTION),
            "settlementEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_SETTLEMENT)
        ));

        features.put("refunds", Map.of(
            "partialRefundEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_PARTIAL_REFUND),
            "fullRefundEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_FULL_REFUND),
            "autoRefundEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_AUTO_REFUND),
            "refundTrackingEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_REFUND_TRACKING)
        ));

        features.put("recurring", Map.of(
            "recurringPaymentEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_RECURRING_PAYMENT),
            "autoBillingEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_AUTO_BILLING),
            "paymentPlansEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_PAYMENT_PLANS),
            "dunningEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_DUNNING)
        ));

        features.put("billing", Map.of(
            "invoiceGenerationEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_INVOICE_GENERATION),
            "billingCyclesEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_BILLING_CYCLES),
            "overdueTrackingEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_OVERDUE_TRACKING),
            "lateFeeEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_LATE_FEE),
            "creditMemoEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_CREDIT_MEMO)
        ));

        features.put("reconciliation", Map.of(
            "reconciliationEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_RECONCILIATION),
            "discrepancyDetectionEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_DISCREPANCY_DETECTION),
            "paymentMatchingEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_PAYMENT_MATCHING)
        ));

        features.put("compliance", Map.of(
            "pciComplianceEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_PCI_COMPLIANCE),
            "amlChecksEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_AML_CHECKS),
            "kycValidationEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_KYC_VALIDATION),
            "tokenizationEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_TOKENIZATION)
        ));

        features.put("disputes", Map.of(
            "disputeHandlingEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_DISPUTE_HANDLING),
            "chargebackDefenseEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_CHARGEBACK_DEFENSE),
            "disputeTrackingEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_DISPUTE_TRACKING)
        ));

        features.put("advanced", Map.of(
            "intelligentRoutingEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_INTELLIGENT_ROUTING),
            "mlFraudDetectionEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_ML_FRAUD_DETECTION),
            "retryLogicEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_RETRY_LOGIC),
            "abTestingEnabled", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_AB_TESTING)
        ));

        return ResponseEntity.ok(features);
    }

    private Map<String, Boolean> getEnabledFeatureFlags() {
        Map<String, Boolean> flags = new HashMap<>();
        flags.put("transactionProcessing", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_TRANSACTION_PROCESSING));
        flags.put("fraudDetection", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_FRAUD_DETECTION));
        flags.put("refunds", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_FULL_REFUND));
        flags.put("recurringPayments", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_RECURRING_PAYMENT));
        flags.put("invoicing", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_INVOICE_GENERATION));
        flags.put("disputes", FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_DISPUTE_HANDLING));
        return flags;
    }
}
