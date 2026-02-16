package com.telecom.paymentprocessing.model;

import java.util.*;

/**
 * Domain Transfer Objects (DTOs) for Payment Processing
 * Comprehensive telecom payment models including transactions, invoices, refunds, subscriptions, and compliance
 */

// ==================== REQUEST/RESPONSE WRAPPERS ====================
public class CreateRequest {
    private Map<String,Object> payload = new HashMap<>();
    public CreateRequest() {}
    public Map<String,Object> getPayload() { return payload; }
    public void setPayload(Map<String,Object> payload) { this.payload = payload; }
}

public class UpdateRequest {
    private Map<String,Object> patch = new HashMap<>();
    public UpdateRequest() {}
    public Map<String,Object> getPatch() { return patch; }
    public void setPatch(Map<String,Object> patch) { this.patch = patch; }
}

public class BulkResponse {
    private List<Map<String,Object>> items = new ArrayList<>();
    public BulkResponse() {}
    public List<Map<String,Object>> getItems() { return items; }
    public void setItems(List<Map<String,Object>> items) { this.items = items; }
}

// ==================== PAYMENT TRANSACTION MODELS ====================

/**
 * Core payment transaction entity
 */
public class PaymentTransaction {
    private String transactionId;
    private String referenceNumber;
    private String orderId;
    private String customerId;
    private String billingAccountId;
    private PaymentMethod paymentMethod;
    private double amount;
    private String currency;
    private TransactionStatus status;
    private String description;
    private PaymentDetails paymentDetails;
    private AuthorizationInfo authorizationInfo;
    private SettlementInfo settlementInfo;
    private ComplianceInfo complianceInfo;
    private List<TransactionEvent> events;
    private long createdAt;
    private long updatedAt;

    // Getters and Setters
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    
    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public String getBillingAccountId() { return billingAccountId; }
    public void setBillingAccountId(String billingAccountId) { this.billingAccountId = billingAccountId; }
    
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public PaymentDetails getPaymentDetails() { return paymentDetails; }
    public void setPaymentDetails(PaymentDetails paymentDetails) { this.paymentDetails = paymentDetails; }
    
    public AuthorizationInfo getAuthorizationInfo() { return authorizationInfo; }
    public void setAuthorizationInfo(AuthorizationInfo authorizationInfo) { this.authorizationInfo = authorizationInfo; }
    
    public SettlementInfo getSettlementInfo() { return settlementInfo; }
    public void setSettlementInfo(SettlementInfo settlementInfo) { this.settlementInfo = settlementInfo; }
    
    public ComplianceInfo getComplianceInfo() { return complianceInfo; }
    public void setComplianceInfo(ComplianceInfo complianceInfo) { this.complianceInfo = complianceInfo; }
    
    public List<TransactionEvent> getEvents() { return events; }
    public void setEvents(List<TransactionEvent> events) { this.events = events; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}

/**
 * Payment method enumeration
 */
public enum PaymentMethod {
    CREDIT_CARD, DEBIT_CARD, BANK_TRANSFER, DIGITAL_WALLET, USSD, MOBILE_MONEY, CASH, CHECK
}

/**
 * Transaction status enumeration
 */
public enum TransactionStatus {
    PENDING, PROCESSING, AUTHORIZED, CAPTURED, SETTLED, FAILED, CANCELLED, REFUNDED, DISPUTED, CHARGEBACK
}

/**
 * Payment details based on payment method
 */
public class PaymentDetails {
    private String paymentMethodType;
    private CardDetails cardDetails;
    private BankDetails bankDetails;
    private DigitalWalletDetails walletDetails;
    private String last4Digits;
    private String token;
    private boolean tokenized;

    // Getters and Setters
    public String getPaymentMethodType() { return paymentMethodType; }
    public void setPaymentMethodType(String paymentMethodType) { this.paymentMethodType = paymentMethodType; }
    
    public CardDetails getCardDetails() { return cardDetails; }
    public void setCardDetails(CardDetails cardDetails) { this.cardDetails = cardDetails; }
    
    public BankDetails getBankDetails() { return bankDetails; }
    public void setBankDetails(BankDetails bankDetails) { this.bankDetails = bankDetails; }
    
    public DigitalWalletDetails getWalletDetails() { return walletDetails; }
    public void setWalletDetails(DigitalWalletDetails walletDetails) { this.walletDetails = walletDetails; }
    
    public String getLast4Digits() { return last4Digits; }
    public void setLast4Digits(String last4Digits) { this.last4Digits = last4Digits; }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public boolean isTokenized() { return tokenized; }
    public void setTokenized(boolean tokenized) { this.tokenized = tokenized; }
}

/**
 * Credit/Debit card details
 */
public class CardDetails {
    private String cardNumber;
    private String cardHolderName;
    private String expiryMonth;
    private String expiryYear;
    private String cvv;
    private String cardBrand;  // VISA, MASTERCARD, AMEX
    private String issuerBank;

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    
    public String getCardHolderName() { return cardHolderName; }
    public void setCardHolderName(String cardHolderName) { this.cardHolderName = cardHolderName; }
    
    public String getExpiryMonth() { return expiryMonth; }
    public void setExpiryMonth(String expiryMonth) { this.expiryMonth = expiryMonth; }
    
    public String getExpiryYear() { return expiryYear; }
    public void setExpiryYear(String expiryYear) { this.expiryYear = expiryYear; }
    
    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }
    
    public String getCardBrand() { return cardBrand; }
    public void setCardBrand(String cardBrand) { this.cardBrand = cardBrand; }
    
    public String getIssuerBank() { return issuerBank; }
    public void setIssuerBank(String issuerBank) { this.issuerBank = issuerBank; }
}

/**
 * Bank transfer details
 */
public class BankDetails {
    private String bankName;
    private String bankCode;
    private String accountNumber;
    private String accountHolderName;
    private String routingNumber;
    private String swiftCode;
    private String iban;

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    
    public String getBankCode() { return bankCode; }
    public void setBankCode(String bankCode) { this.bankCode = bankCode; }
    
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    
    public String getAccountHolderName() { return accountHolderName; }
    public void setAccountHolderName(String accountHolderName) { this.accountHolderName = accountHolderName; }
    
    public String getRoutingNumber() { return routingNumber; }
    public void setRoutingNumber(String routingNumber) { this.routingNumber = routingNumber; }
    
    public String getSwiftCode() { return swiftCode; }
    public void setSwiftCode(String swiftCode) { this.swiftCode = swiftCode; }
    
    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }
}

/**
 * Digital wallet details (mobile money, e-wallet)
 */
public class DigitalWalletDetails {
    private String walletProvider;  // MPESA, VODAFONE_CASH, AIRTEL_MONEY
    private String phoneNumber;
    private String walletId;
    private String walletAlias;

    public String getWalletProvider() { return walletProvider; }
    public void setWalletProvider(String walletProvider) { this.walletProvider = walletProvider; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getWalletId() { return walletId; }
    public void setWalletId(String walletId) { this.walletId = walletId; }
    
    public String getWalletAlias() { return walletAlias; }
    public void setWalletAlias(String walletAlias) { this.walletAlias = walletAlias; }
}

/**
 * Authorization information for payment
 */
public class AuthorizationInfo {
    private String authorizationCode;
    private String authorizationStatus;
    private long authorizationTime;
    private long expiryTime;
    private String responseCode;
    private String responseMessage;
    private boolean threeDSecureApplied;
    private String acsTransactionId;
    private double authorizedAmount;

    // Getters and Setters
    public String getAuthorizationCode() { return authorizationCode; }
    public void setAuthorizationCode(String authorizationCode) { this.authorizationCode = authorizationCode; }
    
    public String getAuthorizationStatus() { return authorizationStatus; }
    public void setAuthorizationStatus(String authorizationStatus) { this.authorizationStatus = authorizationStatus; }
    
    public long getAuthorizationTime() { return authorizationTime; }
    public void setAuthorizationTime(long authorizationTime) { this.authorizationTime = authorizationTime; }
    
    public long getExpiryTime() { return expiryTime; }
    public void setExpiryTime(long expiryTime) { this.expiryTime = expiryTime; }
    
    public String getResponseCode() { return responseCode; }
    public void setResponseCode(String responseCode) { this.responseCode = responseCode; }
    
    public String getResponseMessage() { return responseMessage; }
    public void setResponseMessage(String responseMessage) { this.responseMessage = responseMessage; }
    
    public boolean isThreeDSecureApplied() { return threeDSecureApplied; }
    public void setThreeDSecureApplied(boolean threeDSecureApplied) { this.threeDSecureApplied = threeDSecureApplied; }
    
    public String getAcsTransactionId() { return acsTransactionId; }
    public void setAcsTransactionId(String acsTransactionId) { this.acsTransactionId = acsTransactionId; }
    
    public double getAuthorizedAmount() { return authorizedAmount; }
    public void setAuthorizedAmount(double authorizedAmount) { this.authorizedAmount = authorizedAmount; }
}

/**
 * Settlement information
 */
public class SettlementInfo {
    private String settlementId;
    private SettlementStatus settlementStatus;
    private long settlementTime;
    private double settlementAmount;
    private String settlementCurrency;
    private double exchangeRate;
    private String bankAccount;
    private String batchNumber;
    private int retryCount;

    public String getSettlementId() { return settlementId; }
    public void setSettlementId(String settlementId) { this.settlementId = settlementId; }
    
    public SettlementStatus getSettlementStatus() { return settlementStatus; }
    public void setSettlementStatus(SettlementStatus settlementStatus) { this.settlementStatus = settlementStatus; }
    
    public long getSettlementTime() { return settlementTime; }
    public void setSettlementTime(long settlementTime) { this.settlementTime = settlementTime; }
    
    public double getSettlementAmount() { return settlementAmount; }
    public void setSettlementAmount(double settlementAmount) { this.settlementAmount = settlementAmount; }
    
    public String getSettlementCurrency() { return settlementCurrency; }
    public void setSettlementCurrency(String settlementCurrency) { this.settlementCurrency = settlementCurrency; }
    
    public double getExchangeRate() { return exchangeRate; }
    public void setExchangeRate(double exchangeRate) { this.exchangeRate = exchangeRate; }
    
    public String getBankAccount() { return bankAccount; }
    public void setBankAccount(String bankAccount) { this.bankAccount = bankAccount; }
    
    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }
    
    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
}

/**
 * Settlement status enumeration
 */
public enum SettlementStatus {
    PENDING, PROCESSING, SETTLED, FAILED, REVERSED
}

/**
 * Compliance and fraud information
 */
public class ComplianceInfo {
    private String fraudRiskLevel;  // LOW, MEDIUM, HIGH
    private boolean fraudDetected;
    private String fraudReason;
    private boolean pciCompliant;
    private boolean amlChecked;
    private boolean kycValidated;
    private Map<String, Object> fraudScores;
    private long complianceCheckTime;
    private String riskAssessment;

    public String getFraudRiskLevel() { return fraudRiskLevel; }
    public void setFraudRiskLevel(String fraudRiskLevel) { this.fraudRiskLevel = fraudRiskLevel; }
    
    public boolean isFraudDetected() { return fraudDetected; }
    public void setFraudDetected(boolean fraudDetected) { this.fraudDetected = fraudDetected; }
    
    public String getFraudReason() { return fraudReason; }
    public void setFraudReason(String fraudReason) { this.fraudReason = fraudReason; }
    
    public boolean isPciCompliant() { return pciCompliant; }
    public void setPciCompliant(boolean pciCompliant) { this.pciCompliant = pciCompliant; }
    
    public boolean isAmlChecked() { return amlChecked; }
    public void setAmlChecked(boolean amlChecked) { this.amlChecked = amlChecked; }
    
    public boolean isKycValidated() { return kycValidated; }
    public void setKycValidated(boolean kycValidated) { this.kycValidated = kycValidated; }
    
    public Map<String, Object> getFraudScores() { return fraudScores; }
    public void setFraudScores(Map<String, Object> fraudScores) { this.fraudScores = fraudScores; }
    
    public long getComplianceCheckTime() { return complianceCheckTime; }
    public void setComplianceCheckTime(long complianceCheckTime) { this.complianceCheckTime = complianceCheckTime; }
    
    public String getRiskAssessment() { return riskAssessment; }
    public void setRiskAssessment(String riskAssessment) { this.riskAssessment = riskAssessment; }
}

/**
 * Transaction event tracking
 */
public class TransactionEvent {
    private String eventType;
    private long timestamp;
    private String status;
    private String description;
    private String actor;

    public TransactionEvent() {}
    public TransactionEvent(String eventType, long timestamp, String status, String description) {
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.status = status;
        this.description = description;
    }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }
}

// ==================== INVOICE MODELS ====================

/**
 * Invoice for billing
 */
public class Invoice {
    private String invoiceId;
    private String invoiceNumber;
    private String customerId;
    private String billingAccountId;
    private InvoiceStatus status;
    private long billingPeriodStart;
    private long billingPeriodEnd;
    private long dueDate;
    private double subtotal;
    private double taxAmount;
    private double totalAmount;
    private String currency;
    private List<InvoiceLineItem> lineItems;
    private List<PaymentRecord> payments;
    private long createdAt;
    private long issuedAt;

    // Getters and Setters
    public String getInvoiceId() { return invoiceId; }
    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }
    
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public String getBillingAccountId() { return billingAccountId; }
    public void setBillingAccountId(String billingAccountId) { this.billingAccountId = billingAccountId; }
    
    public InvoiceStatus getStatus() { return status; }
    public void setStatus(InvoiceStatus status) { this.status = status; }
    
    public long getBillingPeriodStart() { return billingPeriodStart; }
    public void setBillingPeriodStart(long billingPeriodStart) { this.billingPeriodStart = billingPeriodStart; }
    
    public long getBillingPeriodEnd() { return billingPeriodEnd; }
    public void setBillingPeriodEnd(long billingPeriodEnd) { this.billingPeriodEnd = billingPeriodEnd; }
    
    public long getDueDate() { return dueDate; }
    public void setDueDate(long dueDate) { this.dueDate = dueDate; }
    
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    
    public double getTaxAmount() { return taxAmount; }
    public void setTaxAmount(double taxAmount) { this.taxAmount = taxAmount; }
    
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public List<InvoiceLineItem> getLineItems() { return lineItems; }
    public void setLineItems(List<InvoiceLineItem> lineItems) { this.lineItems = lineItems; }
    
    public List<PaymentRecord> getPayments() { return payments; }
    public void setPayments(List<PaymentRecord> payments) { this.payments = payments; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    
    public long getIssuedAt() { return issuedAt; }
    public void setIssuedAt(long issuedAt) { this.issuedAt = issuedAt; }
}

/**
 * Invoice status enumeration
 */
public enum InvoiceStatus {
    DRAFT, ISSUED, SENT, PARTIALLY_PAID, PAID, OVERDUE, CANCELLED, CREDITED
}

/**
 * Invoice line item
 */
public class InvoiceLineItem {
    private String lineItemId;
    private String description;
    private double quantity;
    private double unitPrice;
    private double totalPrice;
    private String category;  // SERVICE, DEVICE, ADDON

    public String getLineItemId() { return lineItemId; }
    public void setLineItemId(String lineItemId) { this.lineItemId = lineItemId; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}

/**
 * Payment record against invoice
 */
public class PaymentRecord {
    private String paymentId;
    private double amountPaid;
    private long paymentDate;
    private String paymentMethod;
    private String referenceNumber;

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    
    public double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }
    
    public long getPaymentDate() { return paymentDate; }
    public void setPaymentDate(long paymentDate) { this.paymentDate = paymentDate; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
}

// ==================== REFUND MODELS ====================

/**
 * Refund transaction
 */
public class Refund {
    private String refundId;
    private String originalTransactionId;
    private String customerId;
    private double refundAmount;
    private String currency;
    private RefundStatus status;
    private RefundReason reason;
    private String refundDescription;
    private RefundDetails refundDetails;
    private long initiatedAt;
    private long processedAt;

    public String getRefundId() { return refundId; }
    public void setRefundId(String refundId) { this.refundId = refundId; }
    
    public String getOriginalTransactionId() { return originalTransactionId; }
    public void setOriginalTransactionId(String originalTransactionId) { this.originalTransactionId = originalTransactionId; }
    
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public double getRefundAmount() { return refundAmount; }
    public void setRefundAmount(double refundAmount) { this.refundAmount = refundAmount; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public RefundStatus getStatus() { return status; }
    public void setStatus(RefundStatus status) { this.status = status; }
    
    public RefundReason getReason() { return reason; }
    public void setReason(RefundReason reason) { this.reason = reason; }
    
    public String getRefundDescription() { return refundDescription; }
    public void setRefundDescription(String refundDescription) { this.refundDescription = refundDescription; }
    
    public RefundDetails getRefundDetails() { return refundDetails; }
    public void setRefundDetails(RefundDetails refundDetails) { this.refundDetails = refundDetails; }
    
    public long getInitiatedAt() { return initiatedAt; }
    public void setInitiatedAt(long initiatedAt) { this.initiatedAt = initiatedAt; }
    
    public long getProcessedAt() { return processedAt; }
    public void setProcessedAt(long processedAt) { this.processedAt = processedAt; }
}

/**
 * Refund status enumeration
 */
public enum RefundStatus {
    PENDING, APPROVED, PROCESSING, COMPLETED, FAILED, CANCELLED, REVERSED
}

/**
 * Refund reason enumeration
 */
public enum RefundReason {
    DUPLICATE_CHARGE, UNAUTHORIZED, SERVICE_NOT_PROVIDED, CUSTOMER_REQUEST, BILLING_ERROR, CHARGEBACK, OTHER
}

/**
 * Refund details
 */
public class RefundDetails {
    private String refundMethod;
    private String destinationAccount;
    private double refundableAmount;
    private double processingFee;
    private String approvalBy;
    private long approvalTime;

    public String getRefundMethod() { return refundMethod; }
    public void setRefundMethod(String refundMethod) { this.refundMethod = refundMethod; }
    
    public String getDestinationAccount() { return destinationAccount; }
    public void setDestinationAccount(String destinationAccount) { this.destinationAccount = destinationAccount; }
    
    public double getRefundableAmount() { return refundableAmount; }
    public void setRefundableAmount(double refundableAmount) { this.refundableAmount = refundableAmount; }
    
    public double getProcessingFee() { return processingFee; }
    public void setProcessingFee(double processingFee) { this.processingFee = processingFee; }
    
    public String getApprovalBy() { return approvalBy; }
    public void setApprovalBy(String approvalBy) { this.approvalBy = approvalBy; }
    
    public long getApprovalTime() { return approvalTime; }
    public void setApprovalTime(long approvalTime) { this.approvalTime = approvalTime; }
}

// ==================== RECURRING PAYMENT MODELS ====================

/**
 * Recurring payment subscription
 */
public class RecurringPayment {
    private String recurringPaymentId;
    private String customerId;
    private String billingAccountId;
    private RecurringPaymentStatus status;
    private PaymentMethod paymentMethod;
    private double recurringAmount;
    private String currency;
    private BillingFrequency billingFrequency;
    private long startDate;
    private long endDate;
    private int executionCount;
    private long nextBillingDate;
    private List<RecurringPaymentExecution> executions;

    public String getRecurringPaymentId() { return recurringPaymentId; }
    public void setRecurringPaymentId(String recurringPaymentId) { this.recurringPaymentId = recurringPaymentId; }
    
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public String getBillingAccountId() { return billingAccountId; }
    public void setBillingAccountId(String billingAccountId) { this.billingAccountId = billingAccountId; }
    
    public RecurringPaymentStatus getStatus() { return status; }
    public void setStatus(RecurringPaymentStatus status) { this.status = status; }
    
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public double getRecurringAmount() { return recurringAmount; }
    public void setRecurringAmount(double recurringAmount) { this.recurringAmount = recurringAmount; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public BillingFrequency getBillingFrequency() { return billingFrequency; }
    public void setBillingFrequency(BillingFrequency billingFrequency) { this.billingFrequency = billingFrequency; }
    
    public long getStartDate() { return startDate; }
    public void setStartDate(long startDate) { this.startDate = startDate; }
    
    public long getEndDate() { return endDate; }
    public void setEndDate(long endDate) { this.endDate = endDate; }
    
    public int getExecutionCount() { return executionCount; }
    public void setExecutionCount(int executionCount) { this.executionCount = executionCount; }
    
    public long getNextBillingDate() { return nextBillingDate; }
    public void setNextBillingDate(long nextBillingDate) { this.nextBillingDate = nextBillingDate; }
    
    public List<RecurringPaymentExecution> getExecutions() { return executions; }
    public void setExecutions(List<RecurringPaymentExecution> executions) { this.executions = executions; }
}

/**
 * Recurring payment status
 */
public enum RecurringPaymentStatus {
    ACTIVE, SUSPENDED, CANCELLED, EXPIRED, FAILED
}

/**
 * Billing frequency
 */
public enum BillingFrequency {
    DAILY, WEEKLY, MONTHLY, QUARTERLY, SEMI_ANNUAL, ANNUAL
}

/**
 * Individual execution of recurring payment
 */
public class RecurringPaymentExecution {
    private String executionId;
    private String transactionId;
    private long executionDate;
    private double amount;
    private String status;
    private String failureReason;

    public String getExecutionId() { return executionId; }
    public void setExecutionId(String executionId) { this.executionId = executionId; }
    
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    
    public long getExecutionDate() { return executionDate; }
    public void setExecutionDate(long executionDate) { this.executionDate = executionDate; }
    
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
}

// ==================== DISPUTE/CHARGEBACK MODELS ====================

/**
 * Payment dispute/chargeback
 */
public class PaymentDispute {
    private String disputeId;
    private String originalTransactionId;
    private String customerId;
    private DisputeStatus status;
    private DisputeType disputeType;
    private double disputeAmount;
    private String currency;
    private String reason;
    private String description;
    private long initiatedDate;
    private long dueDate;
    private DisputeEvidence evidence;
    private List<DisputeEvent> events;

    public String getDisputeId() { return disputeId; }
    public void setDisputeId(String disputeId) { this.disputeId = disputeId; }
    
    public String getOriginalTransactionId() { return originalTransactionId; }
    public void setOriginalTransactionId(String originalTransactionId) { this.originalTransactionId = originalTransactionId; }
    
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public DisputeStatus getStatus() { return status; }
    public void setStatus(DisputeStatus status) { this.status = status; }
    
    public DisputeType getDisputeType() { return disputeType; }
    public void setDisputeType(DisputeType disputeType) { this.disputeType = disputeType; }
    
    public double getDisputeAmount() { return disputeAmount; }
    public void setDisputeAmount(double disputeAmount) { this.disputeAmount = disputeAmount; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public long getInitiatedDate() { return initiatedDate; }
    public void setInitiatedDate(long initiatedDate) { this.initiatedDate = initiatedDate; }
    
    public long getDueDate() { return dueDate; }
    public void setDueDate(long dueDate) { this.dueDate = dueDate; }
    
    public DisputeEvidence getEvidence() { return evidence; }
    public void setEvidence(DisputeEvidence evidence) { this.evidence = evidence; }
    
    public List<DisputeEvent> getEvents() { return events; }
    public void setEvents(List<DisputeEvent> events) { this.events = events; }
}

/**
 * Dispute status
 */
public enum DisputeStatus {
    OPENED, UNDER_REVIEW, EVIDENCE_SUBMITTED, RESOLVED, WON, LOST, WITHDRAWN
}

/**
 * Dispute type
 */
public enum DisputeType {
    CHARGEBACK, REFUND_REQUEST, BILLING_DISPUTE, FRAUD_CLAIM, UNAUTHORIZED_TRANSACTION
}

/**
 * Dispute evidence
 */
public class DisputeEvidence {
    private String evidenceType;
    private String description;
    private List<String> attachments;
    private long submittedDate;

    public String getEvidenceType() { return evidenceType; }
    public void setEvidenceType(String evidenceType) { this.evidenceType = evidenceType; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public List<String> getAttachments() { return attachments; }
    public void setAttachments(List<String> attachments) { this.attachments = attachments; }
    
    public long getSubmittedDate() { return submittedDate; }
    public void setSubmittedDate(long submittedDate) { this.submittedDate = submittedDate; }
}

/**
 * Dispute event
 */
public class DisputeEvent {
    private String eventType;
    private long timestamp;
    private String status;
    private String notes;

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
