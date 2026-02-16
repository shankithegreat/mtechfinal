                        // Cross-service: If add-on management is enabled in service-provisioning, manage add-ons
                        if (!FeatureFlagReader.isFeatureEnabled("provisioning_enable_add_on_management")) {
                            throw new IllegalStateException("Add-on management is disabled (provisioning_enable_add_on_management)");
                        }
                // Cross-service: If notification multichannel is enabled, send notification
                if (!FeatureFlagReader.isFeatureEnabled("notification_multichannel_enable")) {
                    throw new IllegalStateException("Multichannel notification is disabled (notification_multichannel_enable)");
                }
        // Cross-service: If advance payment tracking is enabled in billing, require advancePaymentId
        if (FeatureFlagReader.isFeatureEnabled("billing_enable_advance_payment_tracking")) {
            if (request.getAdvancePaymentId() == null) {
                throw new IllegalArgumentException("Advance payment tracking is enabled. advancePaymentId is required (billing_enable_advance_payment_tracking)");
            }
        }
package com.telecom.paymentprocessing.service;

import org.springframework.stereotype.Service;
import com.telecom.common.FeatureFlagReader;
import com.telecom.paymentprocessing.model.*;
import com.telecom.paymentprocessing.config.PaymentProcessingFeatureFlagConstants;
import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service layer for PaymentProcessing with comprehensive telecom payment business logic.
 * Implements complex payment processing including transactions, invoicing, refunds, subscriptions,
 * fraud detection, compliance, and reconciliation.
 */
@Service
public class PaymentProcessingService {

    // In-memory stores for different entities
    private final Map<String, PaymentTransaction> transactionStore = new ConcurrentHashMap<>();
    private final Map<String, Invoice> invoiceStore = new ConcurrentHashMap<>();
    private final Map<String, Refund> refundStore = new ConcurrentHashMap<>();
    private final Map<String, RecurringPayment> recurringPaymentStore = new ConcurrentHashMap<>();
    private final Map<String, PaymentDispute> disputeStore = new ConcurrentHashMap<>();
    private final Map<String, Map<String,Object>> genericStore = new ConcurrentHashMap<>();
    
    // Fraud scoring configuration
    private static final double FRAUD_THRESHOLD_HIGH = 0.75;
    private static final double FRAUD_THRESHOLD_MEDIUM = 0.50;
    
    // Payment gateway configuration
    private static final Map<String, String> GATEWAY_CONFIG = new HashMap<>();
    
    static {
        GATEWAY_CONFIG.put("STRIPE", "stripe_key_default");
        GATEWAY_CONFIG.put("PAYPAL", "paypal_key_default");
        GATEWAY_CONFIG.put("SQUARE", "square_key_default");
        GATEWAY_CONFIG.put("GATEWAY2U", "gateway2u_key_default");
    }

    @PostConstruct
    public void init() {
        // Populate with sample transactions
        for (int i = 1; i <= 5; i++) {
            createSampleTransaction(i);
            createSampleInvoice(i);
        }
    }

    private void createSampleTransaction(int index) {
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setReferenceNumber("TXN-" + String.format("%08d", index));
        transaction.setCustomerId("CUST-" + String.format("%05d", index));
        transaction.setAmount(100.0 * index);
        transaction.setCurrency("USD");
        transaction.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        transaction.setStatus(TransactionStatus.SETTLED);
        transaction.setCreatedAt(System.currentTimeMillis());
        
        transactionStore.put(transaction.getTransactionId(), transaction);
        genericStore.put(transaction.getTransactionId(), convertToMap(transaction));
    }

    private void createSampleInvoice(int index) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceId(UUID.randomUUID().toString());
        invoice.setInvoiceNumber("INV-2024-" + String.format("%05d", index));
        invoice.setCustomerId("CUST-" + String.format("%05d", index));
        invoice.setTotalAmount(100.0 * index);
        invoice.setCurrency("USD");
        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setCreatedAt(System.currentTimeMillis());
        
        invoiceStore.put(invoice.getInvoiceId(), invoice);
    }

    // ==================== PAYMENT TRANSACTION PROCESSING ====================

    /**
     * Process a payment transaction with comprehensive validation and fraud checking
     */
    public PaymentTransaction processPayment(PaymentTransaction paymentRequest) {
        if (!FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_TRANSACTION_PROCESSING)) {
            throw new RuntimeException("Payment processing is disabled");
        }

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setReferenceNumber(generateReferenceNumber());
        transaction.setCustomerId(paymentRequest.getCustomerId());
        transaction.setOrderId(paymentRequest.getOrderId());
        transaction.setAmount(paymentRequest.getAmount());
        transaction.setCurrency(paymentRequest.getCurrency() != null ? paymentRequest.getCurrency() : "USD");
        transaction.setPaymentMethod(paymentRequest.getPaymentMethod());
        transaction.setDescription(paymentRequest.getDescription());
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setCreatedAt(System.currentTimeMillis());

        // Validate transaction
        if (FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_VALIDATION)) {
            validateTransaction(transaction);
        }

        // Check fraud
        if (FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_FRAUD_DETECTION)) {
            performFraudDetection(transaction);
        }

        // Check compliance
        if (FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_PCI_COMPLIANCE)) {
            performPCICompliance(transaction);
        }

        // Perform authorization
        if (FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_3D_SECURE)) {
            authorize3DSecure(transaction);
        } else {
            authorizeTransaction(transaction);
        }

        // Route to payment gateway
        routeToPaymentGateway(transaction);

        // Initialize events
        List<TransactionEvent> events = new ArrayList<>();
        events.add(new TransactionEvent("CREATED", System.currentTimeMillis(), "PENDING", "Transaction created"));
        transaction.setEvents(events);

        transactionStore.put(transaction.getTransactionId(), transaction);
        genericStore.put(transaction.getTransactionId(), convertToMap(transaction));

        return transaction;
    }

    /**
     * Validate transaction data
     */
    private void validateTransaction(PaymentTransaction transaction) {
        if (transaction.getAmount() <= 0) {
            throw new RuntimeException("Transaction amount must be positive");
        }
        if (transaction.getPaymentMethod() == null) {
            throw new RuntimeException("Payment method is required");
        }
        if (transaction.getCustomerId() == null || transaction.getCustomerId().isEmpty()) {
            throw new RuntimeException("Customer ID is required");
        }
    }

    /**
     * Perform fraud detection with multiple scoring mechanisms
     */
    private void performFraudDetection(PaymentTransaction transaction) {
        ComplianceInfo compliance = new ComplianceInfo();
        Map<String, Object> fraudScores = new HashMap<>();

        // Amount-based fraud scoring
        double amountScore = calculateAmountScore(transaction.getAmount());
        fraudScores.put("amountScore", amountScore);

        // Payment method risk scoring
        double methodScore = calculateMethodScore(transaction.getPaymentMethod());
        fraudScores.put("methodScore", methodScore);

        // Geographic risk scoring
        double geoScore = calculateGeoScore();
        fraudScores.put("geoScore", geoScore);

        // Velocity risk scoring
        double velocityScore = calculateVelocityScore(transaction.getCustomerId());
        fraudScores.put("velocityScore", velocityScore);

        // Overall fraud score
        double overallFraudScore = (amountScore + methodScore + geoScore + velocityScore) / 4.0;
        fraudScores.put("overallScore", overallFraudScore);

        compliance.setFraudScores(fraudScores);
        compliance.setComplianceCheckTime(System.currentTimeMillis());

        if (overallFraudScore >= FRAUD_THRESHOLD_HIGH) {
            compliance.setFraudRiskLevel("HIGH");
            compliance.setFraudDetected(true);
            compliance.setFraudReason("High fraud score: " + overallFraudScore);
            transaction.setStatus(TransactionStatus.FAILED);
        } else if (overallFraudScore >= FRAUD_THRESHOLD_MEDIUM) {
            compliance.setFraudRiskLevel("MEDIUM");
            compliance.setFraudReason("Medium fraud risk detected");
        } else {
            compliance.setFraudRiskLevel("LOW");
        }

        transaction.setComplianceInfo(compliance);
    }

    private double calculateAmountScore(double amount) {
        // Score increases for unusual amounts
        if (amount > 10000) return 0.7;
        if (amount > 5000) return 0.5;
        if (amount > 1000) return 0.3;
        return 0.1;
    }

    private double calculateMethodScore(PaymentMethod method) {
        switch(method) {
            case CREDIT_CARD: return 0.3;
            case DEBIT_CARD: return 0.2;
            case DIGITAL_WALLET: return 0.15;
            case BANK_TRANSFER: return 0.25;
            case USSD: return 0.4;
            default: return 0.5;
        }
    }

    private double calculateGeoScore() {
        // Simulated geographic risk
        return 0.2;
    }

    private double calculateVelocityScore(String customerId) {
        // Simulated velocity check - how many transactions in short time
        return 0.15;
    }

    /**
     * Perform PCI-DSS compliance checks
     */
    private void performPCICompliance(PaymentTransaction transaction) {
        ComplianceInfo compliance = transaction.getComplianceInfo() != null ? 
                                    transaction.getComplianceInfo() : new ComplianceInfo();

        // Tokenize payment data if enabled
        if (FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_TOKENIZATION)) {
            tokenizePaymentData(transaction);
            compliance.setPciCompliant(true);
        } else {
            compliance.setPciCompliant(false);
        }

        // AML checks
        if (FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_AML_CHECKS)) {
            compliance.setAmlChecked(true);
        }

        // KYC validation
        if (FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_KYC_VALIDATION)) {
            compliance.setKycValidated(true);
        }

        transaction.setComplianceInfo(compliance);
    }

    /**
     * Tokenize payment data for PCI compliance
     */
    private void tokenizePaymentData(PaymentTransaction transaction) {
        PaymentDetails paymentDetails = transaction.getPaymentDetails() != null ? 
                                        transaction.getPaymentDetails() : new PaymentDetails();

        if (transaction.getPaymentMethod() == PaymentMethod.CREDIT_CARD && paymentDetails.getCardDetails() != null) {
            CardDetails cardDetails = paymentDetails.getCardDetails();
            String last4 = cardDetails.getCardNumber().substring(cardDetails.getCardNumber().length() - 4);
            paymentDetails.setLast4Digits(last4);
            paymentDetails.setToken("tok_" + UUID.randomUUID().toString());
            paymentDetails.setTokenized(true);
        }

        transaction.setPaymentDetails(paymentDetails);
    }

    /**
     * Authorize transaction with 3D Secure
     */
    private void authorize3DSecure(PaymentTransaction transaction) {
        AuthorizationInfo authInfo = new AuthorizationInfo();
        authInfo.setAuthorizationCode("AUTH-" + UUID.randomUUID().toString().substring(0, 12));
        authInfo.setAuthorizationTime(System.currentTimeMillis());
        authInfo.setThreeDSecureApplied(true);
        authInfo.setAcsTransactionId("ACS-" + UUID.randomUUID().toString());
        authInfo.setAuthorizedAmount(transaction.getAmount());
        authInfo.setAuthorizationStatus("AUTHORIZED");
        authInfo.setResponseCode("00");
        authInfo.setResponseMessage("Authorization successful");
        authInfo.setExpiryTime(System.currentTimeMillis() + (24 * 60 * 60 * 1000));

        transaction.setAuthorizationInfo(authInfo);
        transaction.setStatus(TransactionStatus.AUTHORIZED);
    }

    /**
     * Authorize transaction without 3D Secure
     */
    private void authorizeTransaction(PaymentTransaction transaction) {
        AuthorizationInfo authInfo = new AuthorizationInfo();
        authInfo.setAuthorizationCode("AUTH-" + UUID.randomUUID().toString().substring(0, 12));
        authInfo.setAuthorizationTime(System.currentTimeMillis());
        authInfo.setThreeDSecureApplied(false);
        authInfo.setAuthorizedAmount(transaction.getAmount());
        authInfo.setAuthorizationStatus("AUTHORIZED");
        authInfo.setResponseCode("00");
        authInfo.setResponseMessage("Authorization successful");
        authInfo.setExpiryTime(System.currentTimeMillis() + (24 * 60 * 60 * 1000));

        transaction.setAuthorizationInfo(authInfo);
        transaction.setStatus(TransactionStatus.AUTHORIZED);
    }

    /**
     * Route transaction to appropriate payment gateway
     */
    private void routeToPaymentGateway(PaymentTransaction transaction) {
        String selectedGateway = selectPaymentGateway(transaction);
        
        // Simulate gateway processing
        SettlementInfo settlementInfo = new SettlementInfo();
        settlementInfo.setSettlementId("SETTLE-" + UUID.randomUUID().toString());
        settlementInfo.setSettlementStatus(SettlementStatus.PROCESSING);
        settlementInfo.setSettlementTime(System.currentTimeMillis() + (1000 * 60 * 2));  // 2 minutes
        settlementInfo.setSettlementAmount(transaction.getAmount());
        settlementInfo.setSettlementCurrency(transaction.getCurrency());
        settlementInfo.setExchangeRate(1.0);
        settlementInfo.setRetryCount(0);

        transaction.setSettlementInfo(settlementInfo);
        transaction.setStatus(TransactionStatus.CAPTURED);
        
        // Add event
        if (transaction.getEvents() != null) {
            transaction.getEvents().add(new TransactionEvent("CAPTURED", System.currentTimeMillis(), 
                    "CAPTURED", "Payment captured via " + selectedGateway));
        }
    }

    private String selectPaymentGateway(PaymentTransaction transaction) {
        if (FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_INTELLIGENT_ROUTING)) {
            // Intelligent routing based on amount and method
            if (transaction.getAmount() > 5000) {
                return "STRIPE";
            } else if (transaction.getPaymentMethod() == PaymentMethod.DIGITAL_WALLET) {
                return "PAYPAL";
            }
        }
        return "GATEWAY2U";
    }

    // ==================== SETTLEMENT & RECONCILIATION ====================

    /**
     * Settle a transaction
     */
    public PaymentTransaction settleTransaction(String transactionId) {
        PaymentTransaction transaction = transactionStore.get(transactionId);
        if (transaction == null) {
            throw new RuntimeException("Transaction not found: " + transactionId);
        }

        if (!FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_SETTLEMENT)) {
            throw new RuntimeException("Payment settlement is disabled");
        }

        if (transaction.getSettlementInfo() != null) {
            transaction.getSettlementInfo().setSettlementStatus(SettlementStatus.SETTLED);
            transaction.getSettlementInfo().setSettlementTime(System.currentTimeMillis());
            transaction.setStatus(TransactionStatus.SETTLED);

            if (transaction.getEvents() != null) {
                transaction.getEvents().add(new TransactionEvent("SETTLED", System.currentTimeMillis(), 
                        "SETTLED", "Payment settled successfully"));
            }
        }

        transactionStore.put(transactionId, transaction);
        return transaction;
    }

    /**
     * Reconcile payments with invoices
     */
    public Map<String, Object> reconcilePayments(String invoiceId) {
        Invoice invoice = invoiceStore.get(invoiceId);
        if (invoice == null) {
            throw new RuntimeException("Invoice not found: " + invoiceId);
        }

        if (!FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_RECONCILIATION)) {
            throw new RuntimeException("Payment reconciliation is disabled");
        }

        Map<String, Object> reconciliation = new HashMap<>();
        double totalPayments = 0;
        
        if (invoice.getPayments() != null) {
            for (PaymentRecord payment : invoice.getPayments()) {
                totalPayments += payment.getAmountPaid();
            }
        }

        double discrepancy = Math.abs(invoice.getTotalAmount() - totalPayments);
        
        reconciliation.put("invoiceId", invoice.getInvoiceId());
        reconciliation.put("invoiceAmount", invoice.getTotalAmount());
        reconciliation.put("totalPayments", totalPayments);
        reconciliation.put("discrepancy", discrepancy);
        reconciliation.put("status", discrepancy == 0 ? "MATCHED" : "DISCREPANCY");
        reconciliation.put("reconciliationTime", System.currentTimeMillis());

        return reconciliation;
    }

    // ==================== REFUND PROCESSING ====================

    /**
     * Process refund with approval and tracking
     */
    public Refund processRefund(String transactionId, RefundReason reason, double refundAmount) {
        PaymentTransaction originalTransaction = transactionStore.get(transactionId);
        if (originalTransaction == null) {
            throw new RuntimeException("Original transaction not found: " + transactionId);
        }

        if (!FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_FULL_REFUND) &&
            !FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_PARTIAL_REFUND)) {
            throw new RuntimeException("Refund processing is disabled");
        }

        Refund refund = new Refund();
        refund.setRefundId(UUID.randomUUID().toString());
        refund.setOriginalTransactionId(transactionId);
        refund.setCustomerId(originalTransaction.getCustomerId());
        refund.setRefundAmount(refundAmount);
        refund.setCurrency(originalTransaction.getCurrency());
        refund.setReason(reason);
        refund.setStatus(RefundStatus.PENDING);
        refund.setInitiatedAt(System.currentTimeMillis());

        RefundDetails refundDetails = new RefundDetails();
        refundDetails.setRefundMethod(originalTransaction.getPaymentMethod().toString());
        refundDetails.setProcessingFee(calculateRefundFee(refundAmount));
        refundDetails.setRefundableAmount(refundAmount - refundDetails.getProcessingFee());
        
        refund.setRefundDetails(refundDetails);

        // Auto-approve if feature enabled
        if (FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_AUTO_REFUND)) {
            refund.setStatus(RefundStatus.PROCESSING);
            refund.setProcessedAt(System.currentTimeMillis());
            originalTransaction.setStatus(TransactionStatus.REFUNDED);
        }

        // Enable refund tracking
        if (FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_REFUND_TRACKING)) {
            refund.setRefundDescription("Refund: " + reason);
        }

        refundStore.put(refund.getRefundId(), refund);
        transactionStore.put(transactionId, originalTransaction);

        return refund;
    }

    private double calculateRefundFee(double refundAmount) {
        // 2% refund processing fee
        return refundAmount * 0.02;
    }

    // ==================== RECURRING PAYMENT & BILLING ====================

    /**
     * Set up recurring payment
     */
    public RecurringPayment setupRecurringPayment(RecurringPayment paymentRequest) {
        if (!FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_RECURRING_PAYMENT)) {
            throw new RuntimeException("Recurring payment setup is disabled");
        }

        RecurringPayment recurring = new RecurringPayment();
        recurring.setRecurringPaymentId(UUID.randomUUID().toString());
        recurring.setCustomerId(paymentRequest.getCustomerId());
        recurring.setBillingAccountId(paymentRequest.getBillingAccountId());
        recurring.setPaymentMethod(paymentRequest.getPaymentMethod());
        recurring.setRecurringAmount(paymentRequest.getRecurringAmount());
        recurring.setCurrency(paymentRequest.getCurrency());
        recurring.setBillingFrequency(paymentRequest.getBillingFrequency());
        recurring.setStartDate(paymentRequest.getStartDate());
        recurring.setEndDate(paymentRequest.getEndDate());
        recurring.setStatus(RecurringPaymentStatus.ACTIVE);
        recurring.setExecutionCount(0);
        
        // Calculate next billing date
        recurring.setNextBillingDate(calculateNextBillingDate(paymentRequest.getStartDate(), paymentRequest.getBillingFrequency()));
        
        recurringPaymentStore.put(recurring.getRecurringPaymentId(), recurring);
        return recurring;
    }

    /**
     * Generate invoice for billing account
     */
    public Invoice generateInvoice(String customerId, String billingAccountId, List<InvoiceLineItem> lineItems) {
        if (!FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_INVOICE_GENERATION)) {
            throw new RuntimeException("Invoice generation is disabled");
        }

        Invoice invoice = new Invoice();
        invoice.setInvoiceId(UUID.randomUUID().toString());
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setCustomerId(customerId);
        invoice.setBillingAccountId(billingAccountId);
        invoice.setStatus(InvoiceStatus.DRAFT);
        invoice.setLineItems(lineItems);
        invoice.setCreatedAt(System.currentTimeMillis());

        // Calculate amounts
        double subtotal = 0;
        for (InvoiceLineItem item : lineItems) {
            item.setTotalPrice(item.getQuantity() * item.getUnitPrice());
            subtotal += item.getTotalPrice();
        }

        invoice.setSubtotal(subtotal);
        
        // Calculate tax if enabled
        if (FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_BILLING_CYCLES)) {
            double taxAmount = subtotal * 0.08;  // 8% tax
            invoice.setTaxAmount(taxAmount);
            invoice.setTotalAmount(subtotal + taxAmount);
        } else {
            invoice.setTotalAmount(subtotal);
        }

        invoice.setCurrency("USD");
        invoice.setDueDate(System.currentTimeMillis() + (30 * 24 * 60 * 60 * 1000));  // 30 days
        invoice.setIssuedAt(System.currentTimeMillis());

        invoiceStore.put(invoice.getInvoiceId(), invoice);
        return invoice;
    }

    /**
     * Track overdue bills
     */
    public Map<String, Object> trackOverdueBills(String customerId) {
        if (!FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_OVERDUE_TRACKING)) {
            throw new RuntimeException("Overdue tracking is disabled");
        }

        Map<String, Object> overdueReport = new HashMap<>();
        List<Invoice> overdueInvoices = new ArrayList<>();
        double totalOverdue = 0;

        for (Invoice invoice : invoiceStore.values()) {
            if (invoice.getCustomerId().equals(customerId) && 
                (invoice.getStatus() == InvoiceStatus.OVERDUE || invoice.getStatus() == InvoiceStatus.ISSUED)) {
                if (invoice.getDueDate() < System.currentTimeMillis()) {
                    overdueInvoices.add(invoice);
                    totalOverdue += invoice.getTotalAmount();
                    
                    // Apply late fee if enabled
                    if (FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_LATE_FEE)) {
                        double lateFee = invoice.getTotalAmount() * 0.05;  // 5% late fee
                        invoice.setTotalAmount(invoice.getTotalAmount() + lateFee);
                        invoice.setStatus(InvoiceStatus.OVERDUE);
                    }
                }
            }
        }

        overdueReport.put("customerId", customerId);
        overdueReport.put("overdueInvoiceCount", overdueInvoices.size());
        overdueReport.put("totalOverdue", totalOverdue);
        overdueReport.put("invoices", overdueInvoices);

        return overdueReport;
    }

    // ==================== DISPUTE MANAGEMENT ====================

    /**
     * Handle payment dispute/chargeback
     */
    public PaymentDispute handleDispute(String transactionId, DisputeType disputeType, String reason) {
        PaymentTransaction transaction = transactionStore.get(transactionId);
        if (transaction == null) {
            throw new RuntimeException("Transaction not found: " + transactionId);
        }

        if (!FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_DISPUTE_HANDLING)) {
            throw new RuntimeException("Dispute handling is disabled");
        }

        PaymentDispute dispute = new PaymentDispute();
        dispute.setDisputeId(UUID.randomUUID().toString());
        dispute.setOriginalTransactionId(transactionId);
        dispute.setCustomerId(transaction.getCustomerId());
        dispute.setDisputeType(disputeType);
        dispute.setReason(reason);
        dispute.setDisputeAmount(transaction.getAmount());
        dispute.setCurrency(transaction.getCurrency());
        dispute.setStatus(DisputeStatus.OPENED);
        dispute.setInitiatedDate(System.currentTimeMillis());
        dispute.setDueDate(System.currentTimeMillis() + (45 * 24 * 60 * 60 * 1000));  // 45 days for response

        List<DisputeEvent> events = new ArrayList<>();
        events.add(new DisputeEvent("OPENED", System.currentTimeMillis(), "OPENED", "Dispute opened"));
        dispute.setEvents(events);

        disputeStore.put(dispute.getDisputeId(), dispute);
        transaction.setStatus(TransactionStatus.DISPUTED);
        transactionStore.put(transactionId, transaction);

        return dispute;
    }

    /**
     * Submit evidence for dispute
     */
    public PaymentDispute submitDisputeEvidence(String disputeId, DisputeEvidence evidence) {
        PaymentDispute dispute = disputeStore.get(disputeId);
        if (dispute == null) {
            throw new RuntimeException("Dispute not found: " + disputeId);
        }

        if (!FeatureFlagReader.isFeatureEnabled(PaymentProcessingFeatureFlagConstants.PAYMENT_ENABLE_CHARGEBACK_DEFENSE)) {
            throw new RuntimeException("Chargeback defense is disabled");
        }

        dispute.setEvidence(evidence);
        dispute.setStatus(DisputeStatus.EVIDENCE_SUBMITTED);

        if (dispute.getEvents() != null) {
            dispute.getEvents().add(new DisputeEvent("EVIDENCE_SUBMITTED", System.currentTimeMillis(), 
                    "EVIDENCE_SUBMITTED", "Evidence submitted for dispute"));
        }

        disputeStore.put(disputeId, dispute);
        return dispute;
    }

    // ==================== LEGACY GENERIC METHODS ====================

    public List<Object> listAll() {
        return new ArrayList<>(genericStore.values());
    }

    public Object getById(String id) {
        return genericStore.get(id);
    }

    public Object create(Map<String,Object> payload) {
        String id = UUID.randomUUID().toString();
        payload.put("id", id);
        payload.put("createdAt", System.currentTimeMillis());
        genericStore.put(id, payload);
        return payload;
    }

    public Object update(String id, Map<String,Object> payload) {
        Map<String,Object> existing = genericStore.get(id);
        if (existing == null) return null;
        existing.putAll(payload);
        existing.put("updatedAt", System.currentTimeMillis());
        genericStore.put(id, existing);
        return existing;
    }

    public boolean delete(String id) {
        return genericStore.remove(id) != null;
    }

    public List<Object> search(Map<String,String> params) {
        List<Object> out = new ArrayList<>();
        for (Map<String,Object> v : genericStore.values()) {
            boolean match = true;
            for (String k : params.keySet()) {
                String val = params.get(k).toLowerCase();
                Object field = v.get(k);
                if (field == null || !field.toString().toLowerCase().contains(val)) {
                    match = false;
                    break;
                }
            }
            if (match) out.add(v);
        }
        return out;
    }

    public List<Object> bulkCreate(List<Map<String,Object>> payloads) {
        List<Object> created = new ArrayList<>();
        for (Map<String,Object> p : payloads) {
            created.add(create(p));
        }
        return created;
    }

    public int count() {
        return transactionStore.size();
    }

    // ==================== UTILITY METHODS ====================

    private String generateReferenceNumber() {
        String timestamp = String.format("%010d", System.currentTimeMillis() % 10000000000L);
        String random = String.format("%05d", (int)(Math.random() * 100000));
        return "TXN-" + timestamp + "-" + random;
    }

    private String generateInvoiceNumber() {
        String timestamp = String.format("%08d", System.currentTimeMillis() % 100000000L);
        String random = String.format("%03d", (int)(Math.random() * 1000));
        return "INV-" + timestamp + "-" + random;
    }

    private long calculateNextBillingDate(long startDate, BillingFrequency frequency) {
        long interval;
        switch(frequency) {
            case DAILY: interval = 1 * 24 * 60 * 60 * 1000L; break;
            case WEEKLY: interval = 7 * 24 * 60 * 60 * 1000L; break;
            case MONTHLY: interval = 30 * 24 * 60 * 60 * 1000L; break;
            case QUARTERLY: interval = 90 * 24 * 60 * 60 * 1000L; break;
            case SEMI_ANNUAL: interval = 180 * 24 * 60 * 60 * 1000L; break;
            case ANNUAL: interval = 365 * 24 * 60 * 60 * 1000L; break;
            default: interval = 30 * 24 * 60 * 60 * 1000L;
        }
        return startDate + interval;
    }

    private Map<String, Object> convertToMap(PaymentTransaction transaction) {
        Map<String, Object> map = new HashMap<>();
        map.put("transactionId", transaction.getTransactionId());
        map.put("referenceNumber", transaction.getReferenceNumber());
        map.put("amount", transaction.getAmount());
        map.put("currency", transaction.getCurrency());
        map.put("status", transaction.getStatus());
        map.put("paymentMethod", transaction.getPaymentMethod());
        map.put("createdAt", transaction.getCreatedAt());
        return map;
    }

    public int computeChecksum(String s) {
        int c = 0;
        for (char ch : s.toCharArray()) c = (c * 31) + ch;
        return Math.abs(c);
    }

    public String humanReadableId(String id) {
        return id.substring(0, Math.min(8, id.length())).toUpperCase();
    }
}
