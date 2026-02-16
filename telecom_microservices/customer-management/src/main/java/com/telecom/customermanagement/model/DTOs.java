package com.telecom.customermanagement.model;

import java.io.Serializable;
import java.util.*;

/**
 * Extra DTOs and wrapper types for CustomerManagement
 */
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

/**
 * Core customer domain model for telecom service.
 * Represents a customer with complete lifecycle, billing, and service information.
 */
class TelecomCustomer implements Serializable {
    private String customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String msisdn; // Mobile Station Integrated Services Digital Network
    private CustomerStatus status;
    private String customerType; // INDIVIDUAL, BUSINESS, ENTERPRISE
    private String segment; // BRONZE, SILVER, GOLD, PLATINUM
    private LocalAddress billingAddress;
    private LocalAddress shippingAddress;
    private BillingProfile billingProfile;
    private List<ServiceSubscription> activeSubscriptions;
    private List<Contract> contracts;
    private List<CreditHistory> creditHistory;
    private CustomerPreferences preferences;
    private long kycVerifiedAt;
    private long accountCreatedAt;
    private long lastActivityAt;
    private double lifetimeValue;
    private boolean fraudRiskDetected;
    private int churnRiskScore;

    // Constructors
    public TelecomCustomer() {
        this.activeSubscriptions = new ArrayList<>();
        this.contracts = new ArrayList<>();
        this.creditHistory = new ArrayList<>();
        this.churnRiskScore = 0;
        this.fraudRiskDetected = false;
    }

    // Getters and Setters
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getMsisdn() { return msisdn; }
    public void setMsisdn(String msisdn) { this.msisdn = msisdn; }

    public CustomerStatus getStatus() { return status; }
    public void setStatus(CustomerStatus status) { this.status = status; }

    public String getCustomerType() { return customerType; }
    public void setCustomerType(String customerType) { this.customerType = customerType; }

    public String getSegment() { return segment; }
    public void setSegment(String segment) { this.segment = segment; }

    public LocalAddress getBillingAddress() { return billingAddress; }
    public void setBillingAddress(LocalAddress billingAddress) { this.billingAddress = billingAddress; }

    public LocalAddress getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(LocalAddress shippingAddress) { this.shippingAddress = shippingAddress; }

    public BillingProfile getBillingProfile() { return billingProfile; }
    public void setBillingProfile(BillingProfile billingProfile) { this.billingProfile = billingProfile; }

    public List<ServiceSubscription> getActiveSubscriptions() { return activeSubscriptions; }
    public void setActiveSubscriptions(List<ServiceSubscription> activeSubscriptions) { this.activeSubscriptions = activeSubscriptions; }

    public List<Contract> getContracts() { return contracts; }
    public void setContracts(List<Contract> contracts) { this.contracts = contracts; }

    public List<CreditHistory> getCreditHistory() { return creditHistory; }
    public void setCreditHistory(List<CreditHistory> creditHistory) { this.creditHistory = creditHistory; }

    public CustomerPreferences getPreferences() { return preferences; }
    public void setPreferences(CustomerPreferences preferences) { this.preferences = preferences; }

    public long getKycVerifiedAt() { return kycVerifiedAt; }
    public void setKycVerifiedAt(long kycVerifiedAt) { this.kycVerifiedAt = kycVerifiedAt; }

    public long getAccountCreatedAt() { return accountCreatedAt; }
    public void setAccountCreatedAt(long accountCreatedAt) { this.accountCreatedAt = accountCreatedAt; }

    public long getLastActivityAt() { return lastActivityAt; }
    public void setLastActivityAt(long lastActivityAt) { this.lastActivityAt = lastActivityAt; }

    public double getLifetimeValue() { return lifetimeValue; }
    public void setLifetimeValue(double lifetimeValue) { this.lifetimeValue = lifetimeValue; }

    public boolean isFraudRiskDetected() { return fraudRiskDetected; }
    public void setFraudRiskDetected(boolean fraudRiskDetected) { this.fraudRiskDetected = fraudRiskDetected; }

    public int getChurnRiskScore() { return churnRiskScore; }
    public void setChurnRiskScore(int churnRiskScore) { this.churnRiskScore = churnRiskScore; }
}

/**
 * Customer lifecycle status enumeration.
 */
enum CustomerStatus {
    PROSPECT,           // Potential customer not yet registered
    REGISTERED,         // Registered but not verified
    VERIFIED,           // KYC verified
    ACTIVE,             // Paying active customer
    SUSPENDED,          // Temporarily suspended
    DORMANT,            // Inactive for 90+ days
    TERMINATED,         // Account closed
    BLACKLISTED         // Fraud/non-payment
}

/**
 * Billing profile for a customer.
 */
class BillingProfile implements Serializable {
    private String accountNumber;
    private String billingCycle; // MONTHLY, QUARTERLY, ANNUALLY
    private LocalDateTime nextBillingDate;
    private double creditLimit;
    private double currentBalance;
    private double totalOutstanding;
    private String paymentMethod; // CREDIT_CARD, BANK_TRANSFER, CASH, DIGITAL_WALLET
    private LocalDateTime paymentDueDate;
    private int daysOverdue;
    private List<Invoice> invoices;
    private boolean autoPaymentEnabled;

    public BillingProfile() {
        this.invoices = new ArrayList<>();
    }

    // Getters and setters
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getBillingCycle() { return billingCycle; }
    public void setBillingCycle(String billingCycle) { this.billingCycle = billingCycle; }

    public LocalDateTime getNextBillingDate() { return nextBillingDate; }
    public void setNextBillingDate(LocalDateTime nextBillingDate) { this.nextBillingDate = nextBillingDate; }

    public double getCreditLimit() { return creditLimit; }
    public void setCreditLimit(double creditLimit) { this.creditLimit = creditLimit; }

    public double getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(double currentBalance) { this.currentBalance = currentBalance; }

    public double getTotalOutstanding() { return totalOutstanding; }
    public void setTotalOutstanding(double totalOutstanding) { this.totalOutstanding = totalOutstanding; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public LocalDateTime getPaymentDueDate() { return paymentDueDate; }
    public void setPaymentDueDate(LocalDateTime paymentDueDate) { this.paymentDueDate = paymentDueDate; }

    public int getDaysOverdue() { return daysOverdue; }
    public void setDaysOverdue(int daysOverdue) { this.daysOverdue = daysOverdue; }

    public List<Invoice> getInvoices() { return invoices; }
    public void setInvoices(List<Invoice> invoices) { this.invoices = invoices; }

    public boolean isAutoPaymentEnabled() { return autoPaymentEnabled; }
    public void setAutoPaymentEnabled(boolean autoPaymentEnabled) { this.autoPaymentEnabled = autoPaymentEnabled; }
}

/**
 * Service subscription for a customer (e.g., voice, data, SMS plans).
 */
class ServiceSubscription implements Serializable {
    private String subscriptionId;
    private String serviceType; // VOICE, DATA, SMS, VIDEO, ENTERPRISE
    private String planName;
    private double monthlyCharges;
    private LocalDateTime subscriptionStartDate;
    private LocalDateTime subscriptionEndDate;
    private LocalDateTime autoRenewalDate;
    private String renewalStatus; // AUTO, MANUAL, NONE
    private UsageMetrics usageMetrics;
    private double usageBasedCharges;
    private boolean isActive;

    public ServiceSubscription() {
        this.usageMetrics = new UsageMetrics();
    }

    // Getters and setters
    public String getSubscriptionId() { return subscriptionId; }
    public void setSubscriptionId(String subscriptionId) { this.subscriptionId = subscriptionId; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public double getMonthlyCharges() { return monthlyCharges; }
    public void setMonthlyCharges(double monthlyCharges) { this.monthlyCharges = monthlyCharges; }

    public LocalDateTime getSubscriptionStartDate() { return subscriptionStartDate; }
    public void setSubscriptionStartDate(LocalDateTime subscriptionStartDate) { this.subscriptionStartDate = subscriptionStartDate; }

    public LocalDateTime getSubscriptionEndDate() { return subscriptionEndDate; }
    public void setSubscriptionEndDate(LocalDateTime subscriptionEndDate) { this.subscriptionEndDate = subscriptionEndDate; }

    public LocalDateTime getAutoRenewalDate() { return autoRenewalDate; }
    public void setAutoRenewalDate(LocalDateTime autoRenewalDate) { this.autoRenewalDate = autoRenewalDate; }

    public String getRenewalStatus() { return renewalStatus; }
    public void setRenewalStatus(String renewalStatus) { this.renewalStatus = renewalStatus; }

    public UsageMetrics getUsageMetrics() { return usageMetrics; }
    public void setUsageMetrics(UsageMetrics usageMetrics) { this.usageMetrics = usageMetrics; }

    public double getUsageBasedCharges() { return usageBasedCharges; }
    public void setUsageBasedCharges(double usageBasedCharges) { this.usageBasedCharges = usageBasedCharges; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}

/**
 * Usage metrics for tracking customer consumption.
 */
class UsageMetrics implements Serializable {
    private long voiceMinutesUsed;
    private long smsCount;
    private long dataUsedMB;
    private long videoCallMinutes;
    private LocalDateTime lastResetDate;
    private double percentageOfQuotaUsed;

    // Getters and setters
    public long getVoiceMinutesUsed() { return voiceMinutesUsed; }
    public void setVoiceMinutesUsed(long voiceMinutesUsed) { this.voiceMinutesUsed = voiceMinutesUsed; }

    public long getSmsCount() { return smsCount; }
    public void setSmsCount(long smsCount) { this.smsCount = smsCount; }

    public long getDataUsedMB() { return dataUsedMB; }
    public void setDataUsedMB(long dataUsedMB) { this.dataUsedMB = dataUsedMB; }

    public long getVideoCallMinutes() { return videoCallMinutes; }
    public void setVideoCallMinutes(long videoCallMinutes) { this.videoCallMinutes = videoCallMinutes; }

    public LocalDateTime getLastResetDate() { return lastResetDate; }
    public void setLastResetDate(LocalDateTime lastResetDate) { this.lastResetDate = lastResetDate; }

    public double getPercentageOfQuotaUsed() { return percentageOfQuotaUsed; }
    public void setPercentageOfQuotaUsed(double percentageOfQuotaUsed) { this.percentageOfQuotaUsed = percentageOfQuotaUsed; }
}

/**
 * Contract information for a customer.
 */
class Contract implements Serializable {
    private String contractId;
    private String contractType; // SERVICE_AGREEMENT, PAYMENT_PLAN, BUNDLE
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int durationMonths;
    private double contractValue;
    private String contractStatus; // ACTIVE, EXPIRED, TERMINATED_EARLY
    private double earlyTerminationFee;
    private String renewalOption; // AUTO_RENEW, MANUAL, NON_RENEWABLE

    // Getters and setters
    public String getContractId() { return contractId; }
    public void setContractId(String contractId) { this.contractId = contractId; }

    public String getContractType() { return contractType; }
    public void setContractType(String contractType) { this.contractType = contractType; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public int getDurationMonths() { return durationMonths; }
    public void setDurationMonths(int durationMonths) { this.durationMonths = durationMonths; }

    public double getContractValue() { return contractValue; }
    public void setContractValue(double contractValue) { this.contractValue = contractValue; }

    public String getContractStatus() { return contractStatus; }
    public void setContractStatus(String contractStatus) { this.contractStatus = contractStatus; }

    public double getEarlyTerminationFee() { return earlyTerminationFee; }
    public void setEarlyTerminationFee(double earlyTerminationFee) { this.earlyTerminationFee = earlyTerminationFee; }

    public String getRenewalOption() { return renewalOption; }
    public void setRenewalOption(String renewalOption) { this.renewalOption = renewalOption; }
}

/**
 * Credit history for a customer.
 */
class CreditHistory implements Serializable {
    private String recordId;
    private double creditScore;
    private LocalDateTime checkDate;
    private String status; // APPROVED, DECLINED, REVIEW
    private String creditLimitAssigned;

    // Getters and setters
    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }

    public double getCreditScore() { return creditScore; }
    public void setCreditScore(double creditScore) { this.creditScore = creditScore; }

    public LocalDateTime getCheckDate() { return checkDate; }
    public void setCheckDate(LocalDateTime checkDate) { this.checkDate = checkDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreditLimitAssigned() { return creditLimitAssigned; }
    public void setCreditLimitAssigned(String creditLimitAssigned) { this.creditLimitAssigned = creditLimitAssigned; }
}

/**
 * Customer address information.
 */
class LocalAddress implements Serializable {
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String addressType; // RESIDENTIAL, COMMERCIAL

    // Getters and setters
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getAddressType() { return addressType; }
    public void setAddressType(String addressType) { this.addressType = addressType; }
}

/**
 * Customer communication and service preferences.
 */
class CustomerPreferences implements Serializable {
    private String preferredLanguage;
    private String communicationChannel; // EMAIL, SMS, PHONE, PUSH_NOTIFICATION
    private boolean marketingOptIn;
    private boolean newsLetterSubscribed;
    private String billNotificationPreference; // EMAIL, SMS, BOTH
    private boolean paperlessMode;
    private List<String> serviceNotificationChannels;

    public CustomerPreferences() {
        this.serviceNotificationChannels = new ArrayList<>();
    }

    // Getters and setters
    public String getPreferredLanguage() { return preferredLanguage; }
    public void setPreferredLanguage(String preferredLanguage) { this.preferredLanguage = preferredLanguage; }

    public String getCommunicationChannel() { return communicationChannel; }
    public void setCommunicationChannel(String communicationChannel) { this.communicationChannel = communicationChannel; }

    public boolean isMarketingOptIn() { return marketingOptIn; }
    public void setMarketingOptIn(boolean marketingOptIn) { this.marketingOptIn = marketingOptIn; }

    public boolean isNewsLetterSubscribed() { return newsLetterSubscribed; }
    public void setNewsLetterSubscribed(boolean newsLetterSubscribed) { this.newsLetterSubscribed = newsLetterSubscribed; }

    public String getBillNotificationPreference() { return billNotificationPreference; }
    public void setBillNotificationPreference(String billNotificationPreference) { this.billNotificationPreference = billNotificationPreference; }

    public boolean isPaperlessMode() { return paperlessMode; }
    public void setPaperlessMode(boolean paperlessMode) { this.paperlessMode = paperlessMode; }

    public List<String> getServiceNotificationChannels() { return serviceNotificationChannels; }
    public void setServiceNotificationChannels(List<String> serviceNotificationChannels) { this.serviceNotificationChannels = serviceNotificationChannels; }
}

/**
 * Invoice information for billing.
 */
class Invoice implements Serializable {
    private String invoiceNumber;
    private LocalDateTime invoiceDate;
    private LocalDateTime dueDate;
    private double amount;
    private double amountPaid;
    private String status; // DRAFT, SENT, OVERDUE, PAID, PARTIALLY_PAID, CANCELLED

    // Getters and setters
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public LocalDateTime getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDateTime invoiceDate) { this.invoiceDate = invoiceDate; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

/**
 * Local date-time wrapper for serialization compatibility.
 */
class LocalDateTime implements Serializable {
    private long timestamp;

    public LocalDateTime(long timestamp) {
        this.timestamp = timestamp;
    }

    public LocalDateTime() {
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}

