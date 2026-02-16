
package com.telecom.customermanagement.service;

import org.springframework.stereotype.Service;
import com.telecom.customermanagement.model.*;
import com.telecom.customermanagement.config.FeatureFlagConstants;
import com.telecom.common.FeatureFlagReader;
import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service layer for Customer Management with complex telecom business logic.
 * Implements customer lifecycle management, billing, subscriptions, contracts,
 * and advanced features with feature flag support across all business operations.
 */
@Service
public class CustomerManagementService {

    // In-memory store for customers and related entities
    private final Map<String, TelecomCustomer> customerStore = new ConcurrentHashMap<>();
    private final Map<String, List<ServiceSubscription>> subscriptionsByCustomer = new ConcurrentHashMap<>();
    private final Map<String, List<Contract>> contractsByCustomer = new ConcurrentHashMap<>();
    private final Map<String, BillingProfile> billingProfiles = new ConcurrentHashMap<>();
    private final Map<String, Map<String,Object>> genericStore = new ConcurrentHashMap<>();

    // Loyalty program state
    private final Map<String, Integer> loyaltyPoints = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        // Initialize with sample customers
        initializeSampleCustomers();
    }

    /**
     * Initialize sample customers with complete telecom profiles
     */
    private void initializeSampleCustomers() {
        for (int i = 1; i <= 5; i++) {
            TelecomCustomer customer = createSampleCustomer(i);
            customerStore.put(customer.getCustomerId(), customer);
            
            // Create sample subscriptions if feature is enabled
            if (FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_SUBSCRIPTION_MANAGEMENT)) {
                List<ServiceSubscription> subs = createSampleSubscriptions(customer.getCustomerId(), i);
                subscriptionsByCustomer.put(customer.getCustomerId(), subs);
            }
        }
    }

    /**
     * Create a sample customer with complete profile
     */
    private TelecomCustomer createSampleCustomer(int index) {
        TelecomCustomer customer = new TelecomCustomer();
        customer.setCustomerId(UUID.randomUUID().toString());
        customer.setFirstName("John");
        customer.setLastName("Customer" + index);
        customer.setEmail("customer" + index + "@telecom.com");
        customer.setPhoneNumber("555-000" + String.format("%04d", index));
        customer.setMsisdn("+1234567890" + String.format("%02d", index));
        customer.setStatus(CustomerStatus.ACTIVE);
        customer.setCustomerType(index % 2 == 0 ? "BUSINESS" : "INDIVIDUAL");
        customer.setSegment(getSegmentByIndex(index));
        customer.setAccountCreatedAt(System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L * index));
        customer.setLastActivityAt(System.currentTimeMillis() - (24 * 60 * 60 * 1000L * (6 - index)));
        customer.setChurnRiskScore(calculateChurnRiskScore(customer.getLastActivityAt()));
        customer.setLifetimeValue(1000.0 * index);
        customer.setFraudRiskDetected(false);
        
        // Add billing profile
        BillingProfile billing = new BillingProfile();
        billing.setAccountNumber("ACC-" + String.format("%08d", index));
        billing.setBillingCycle("MONTHLY");
        billing.setCreditLimit(5000.0 * index);
        billing.setCurrentBalance(500.0 * index);
        billing.setPaymentMethod("CREDIT_CARD");
        billing.setAutoPaymentEnabled(true);
        customer.setBillingProfile(billing);
        billingProfiles.put(customer.getCustomerId(), billing);
        
        // Add preferences
        CustomerPreferences prefs = new CustomerPreferences();
        prefs.setPreferredLanguage("EN");
        prefs.setCommunicationChannel("EMAIL");
        prefs.setMarketingOptIn(true);
        prefs.setPaperlessMode(true);
        customer.setPreferences(prefs);
        
        // Add address
        LocalAddress address = new LocalAddress();
        address.setStreet(index + " Main Street");
        address.setCity("Tech City");
        address.setState("TC");
        address.setCountry("USA");
        address.setAddressType("RESIDENTIAL");
        customer.setBillingAddress(address);
        customer.setShippingAddress(address);
        
        return customer;
    }

    /**
     * Create sample subscriptions for a customer
     */
    private List<ServiceSubscription> createSampleSubscriptions(String customerId, int index) {
        List<ServiceSubscription> subs = new ArrayList<>();
        
        ServiceSubscription voiceSub = new ServiceSubscription();
        voiceSub.setSubscriptionId(UUID.randomUUID().toString());
        voiceSub.setServiceType("VOICE");
        voiceSub.setPlanName("Unlimited Voice Plan");
        voiceSub.setMonthlyCharges(49.99);
        voiceSub.setRenewalStatus("AUTO");
        voiceSub.setActive(true);
        voiceSub.getUsageMetrics().setVoiceMinutesUsed(500 + (index * 100));
        subs.add(voiceSub);
        
        ServiceSubscription dataSub = new ServiceSubscription();
        dataSub.setSubscriptionId(UUID.randomUUID().toString());
        dataSub.setServiceType("DATA");
        dataSub.setPlanName("10GB Monthly Data");
        dataSub.setMonthlyCharges(29.99);
        dataSub.setRenewalStatus("AUTO");
        dataSub.setActive(true);
        dataSub.getUsageMetrics().setDataUsedMB(2000 + (index * 500));
        subs.add(dataSub);
        
        return subs;
    }

    /**
     * Determine customer segment based on lifetime value
     */
    private String getSegmentByIndex(int index) {
        switch (index) {
            case 1: return "PLATINUM";
            case 2: return "GOLD";
            case 3: return "SILVER";
            default: return "BRONZE";
        }
    }

    /**
     * Calculate churn risk score based on inactivity
     */
    private int calculateChurnRiskScore(long lastActivityAt) {
        long daysSinceActivity = (System.currentTimeMillis() - lastActivityAt) / (24 * 60 * 60 * 1000L);
        if (daysSinceActivity > 90) return 85;
        if (daysSinceActivity > 60) return 65;
        if (daysSinceActivity > 30) return 45;
        return 25;
    }

    /**
     * Register a new customer with complete KYC and verification
     */
    public TelecomCustomer registerCustomer(Map<String, Object> customerData) {
        if (!FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_CUSTOMER_REGISTRATION)) {
            throw new RuntimeException("Customer registration feature is disabled");
        }
        // Cross-service: If 2FA is enabled in auth, require 2faSetup field
        if (FeatureFlagReader.isFeatureEnabled("auth_enable_2fa")) {
            if (!Boolean.TRUE.equals(customerData.get("2faSetup"))) {
                throw new RuntimeException("2FA setup required for registration (auth_enable_2fa)");
            }
        }

        TelecomCustomer customer = new TelecomCustomer();
        customer.setCustomerId(UUID.randomUUID().toString());
        customer.setFirstName((String) customerData.get("firstName"));
        customer.setLastName((String) customerData.get("lastName"));
        customer.setEmail((String) customerData.get("email"));
        customer.setPhoneNumber((String) customerData.get("phoneNumber"));
        customer.setMsisdn((String) customerData.get("msisdn"));
        customer.setCustomerType((String) customerData.getOrDefault("customerType", "INDIVIDUAL"));
        customer.setStatus(CustomerStatus.REGISTERED);
        customer.setAccountCreatedAt(System.currentTimeMillis());
        customer.setLastActivityAt(System.currentTimeMillis());
        customer.setSegment("BRONZE");
        customer.setChurnRiskScore(0);
        customer.setLifetimeValue(0.0);

        // Verify customer if feature enabled
        if (FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_CUSTOMER_VERIFICATION)) {
            verifyCustomer(customer);
        }

        // Perform KYC validation if enabled
        if (FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_KYC_VALIDATION)) {
            performKYCValidation(customer);
            customer.setKycVerifiedAt(System.currentTimeMillis());
        }

        // Perform credit check if enabled
        if (FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_CREDIT_CHECK)) {
            performCreditCheck(customer);
        }

        // Perform fraud detection if enabled
        if (FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_FRAUD_DETECTION)) {
            performFraudDetection(customer);
        }

        // Check for duplicates if enabled
        if (FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_DUPLICATE_DETECTION)) {
            detectDuplicateCustomer(customer);
        }

        // Initialize billing profile
        BillingProfile billing = new BillingProfile();
        billing.setAccountNumber("ACC-" + customer.getCustomerId().substring(0, 8).toUpperCase());
        billing.setBillingCycle("MONTHLY");
        billing.setCreditLimit(1000.0); // Default credit limit
        billing.setAutoPaymentEnabled(false);
        customer.setBillingProfile(billing);
        billingProfiles.put(customer.getCustomerId(), billing);

        customerStore.put(customer.getCustomerId(), customer);
        return customer;
    }

    /**
     * Activate a customer account
     */
    public TelecomCustomer activateCustomer(String customerId) {
        if (!FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_CUSTOMER_ACTIVATION)) {
            throw new RuntimeException("Customer activation feature is disabled");
        }

        TelecomCustomer customer = customerStore.get(customerId);
        if (customer == null) return null;

        if (customer.getStatus() != CustomerStatus.VERIFIED) {
            throw new RuntimeException("Customer must be verified before activation");
        }

        customer.setStatus(CustomerStatus.ACTIVE);
        customer.setLastActivityAt(System.currentTimeMillis());
        customerStore.put(customerId, customer);
        return customer;
    }

    /**
     * Suspend a customer account
     */
    public TelecomCustomer suspendCustomer(String customerId, String reason) {
        TelecomCustomer customer = customerStore.get(customerId);
        if (customer == null) return null;

        customer.setStatus(CustomerStatus.SUSPENDED);
        customer.setLastActivityAt(System.currentTimeMillis());

        // Suspend all active subscriptions
        List<ServiceSubscription> subs = subscriptionsByCustomer.get(customerId);
        if (subs != null) {
            subs.forEach(sub -> sub.setActive(false));
        }

        customerStore.put(customerId, customer);
        return customer;
    }
    /**
     * Terminate a customer account
     */
    public boolean terminateCustomer(String customerId, String reason) {
        if (!FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_CUSTOMER_TERMINATION)) {
            throw new RuntimeException("Customer termination feature is disabled");
        }

        TelecomCustomer customer = customerStore.get(customerId);
        if (customer == null) return false;

        customer.setStatus(CustomerStatus.TERMINATED);
        customer.setLastActivityAt(System.currentTimeMillis());
        customerStore.put(customerId, customer);
        return true;
    }

    /**
     * Add a service subscription for a customer
     */
    public ServiceSubscription addServiceSubscription(String customerId, Map<String, Object> subscriptionData) {
        if (!FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_SUBSCRIPTION_MANAGEMENT)) {
            throw new RuntimeException("Subscription management feature is disabled");
        }

        TelecomCustomer customer = customerStore.get(customerId);
        if (customer == null) throw new RuntimeException("Customer not found");

        ServiceSubscription subscription = new ServiceSubscription();
        subscription.setSubscriptionId(UUID.randomUUID().toString());
        subscription.setServiceType((String) subscriptionData.get("serviceType"));
        subscription.setPlanName((String) subscriptionData.get("planName"));
        subscription.setMonthlyCharges((Double) subscriptionData.getOrDefault("monthlyCharges", 29.99));
        subscription.setRenewalStatus((String) subscriptionData.getOrDefault("renewalStatus", "AUTO"));
        subscription.setActive(true);

        List<ServiceSubscription> subs = subscriptionsByCustomer.computeIfAbsent(customerId, k -> new ArrayList<>());
        subs.add(subscription);
        customer.setActiveSubscriptions(subs);

        // Apply auto-renewal if enabled
        if (FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_AUTO_RENEWAL) 
            && "AUTO".equals(subscription.getRenewalStatus())) {
            // Auto-renewal logic would go here
        }

        customerStore.put(customerId, customer);
        return subscription;
    }

    /**
     * Update billing profile with complex billing logic
     */
    public BillingProfile updateBillingProfile(String customerId, Map<String, Object> billingData) {
        if (!FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_BILLING_ACCOUNT)) {
            throw new RuntimeException("Billing account feature is disabled");
        }

        BillingProfile billing = billingProfiles.get(customerId);
        if (billing == null) throw new RuntimeException("Billing profile not found");

        billing.setPaymentMethod((String) billingData.getOrDefault("paymentMethod", billing.getPaymentMethod()));
        billing.setAutoPaymentEnabled((Boolean) billingData.getOrDefault("autoPaymentEnabled", billing.isAutoPaymentEnabled()));
        billing.setBillingCycle((String) billingData.getOrDefault("billingCycle", billing.getBillingCycle()));

        // Sync with external billing system if enabled
        if (FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_BILLING_SYNC)) {
            syncWithBillingSystem(customerId, billing);
        }

        billingProfiles.put(customerId, billing);
        return billing;
    }

    /**
     * Create a contract for a customer
     */
    public Contract createContract(String customerId, Map<String, Object> contractData) {
        if (!FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_CONTRACT_MANAGEMENT)) {
            throw new RuntimeException("Contract management feature is disabled");
        }

        TelecomCustomer customer = customerStore.get(customerId);
        if (customer == null) throw new RuntimeException("Customer not found");

        Contract contract = new Contract();
        contract.setContractId(UUID.randomUUID().toString());
        contract.setContractType((String) contractData.get("contractType"));
        contract.setDurationMonths((Integer) contractData.getOrDefault("durationMonths", 12));
        contract.setContractValue((Double) contractData.get("contractValue"));
        contract.setContractStatus("ACTIVE");
        contract.setRenewalOption((String) contractData.getOrDefault("renewalOption", "AUTO_RENEW"));

        // Calculate early termination fee
        double contractValue = contract.getContractValue();
        contract.setEarlyTerminationFee(contractValue * 0.10); // 10% of contract value

        List<Contract> contracts = contractsByCustomer.computeIfAbsent(customerId, k -> new ArrayList<>());
        contracts.add(contract);
        customer.setContracts(contracts);

        customerStore.put(customerId, customer);
        return contract;
    }

    /**
     * Renew a customer contract
     */
    public Contract renewContract(String customerId, String contractId) {
        if (!FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_CONTRACT_RENEWAL)) {
            throw new RuntimeException("Contract renewal feature is disabled");
        }

        List<Contract> contracts = contractsByCustomer.get(customerId);
        if (contracts == null) throw new RuntimeException("No contracts found");

        Contract contract = contracts.stream()
            .filter(c -> c.getContractId().equals(contractId))
            .findFirst()
            .orElse(null);

        if (contract == null) throw new RuntimeException("Contract not found");

        contract.setContractStatus("RENEWED");
        return contract;
    }

    /**
     * Perform early termination with fee calculation
     */
    public Map<String, Object> earlyTerminateContract(String customerId, String contractId) {
        if (!FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_EARLY_TERMINATION)) {
            throw new RuntimeException("Early termination feature is disabled");
        }

        List<Contract> contracts = contractsByCustomer.get(customerId);
        if (contracts == null) throw new RuntimeException("No contracts found");

        Contract contract = contracts.stream()
            .filter(c -> c.getContractId().equals(contractId))
            .findFirst()
            .orElse(null);

        if (contract == null) throw new RuntimeException("Contract not found");

        Map<String, Object> result = new HashMap<>();
        result.put("contractId", contractId);
        result.put("earlyTerminationFee", contract.getEarlyTerminationFee());
        result.put("status", "TERMINATED_EARLY");
        contract.setContractStatus("TERMINATED_EARLY");

        return result;
    }

    /**
     * Manage customer preferences
     */
    public CustomerPreferences updatePreferences(String customerId, Map<String, Object> preferencesData) {
        if (!FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_PREFERENCE_MANAGEMENT)) {
            throw new RuntimeException("Preference management feature is disabled");
        }

        TelecomCustomer customer = customerStore.get(customerId);
        if (customer == null) throw new RuntimeException("Customer not found");

        CustomerPreferences prefs = customer.getPreferences();
        if (prefs == null) {
            prefs = new CustomerPreferences();
        }

        prefs.setPreferredLanguage((String) preferencesData.getOrDefault("preferredLanguage", prefs.getPreferredLanguage()));
        prefs.setCommunicationChannel((String) preferencesData.getOrDefault("communicationChannel", prefs.getCommunicationChannel()));
        prefs.setMarketingOptIn((Boolean) preferencesData.getOrDefault("marketingOptIn", prefs.isMarketingOptIn()));
        prefs.setNewsLetterSubscribed((Boolean) preferencesData.getOrDefault("newsLetterSubscribed", prefs.isNewsLetterSubscribed()));
        prefs.setPaperlessMode((Boolean) preferencesData.getOrDefault("paperlessMode", prefs.isPaperlessMode()));

        customer.setPreferences(prefs);
        customerStore.put(customerId, customer);

        // Send notification preferences if enabled
        if (FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_NOTIFICATION_PREFERENCES)) {
            updateNotificationPreferences(customerId, prefs);
        }

        return prefs;
    }

    /**
     * Enroll customer in loyalty program
     */
    public Map<String, Object> enrollInLoyaltyProgram(String customerId) {
        if (!FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_LOYALTY_PROGRAM)) {
            throw new RuntimeException("Loyalty program feature is disabled");
        }

        TelecomCustomer customer = customerStore.get(customerId);
        if (customer == null) throw new RuntimeException("Customer not found");

        loyaltyPoints.putIfAbsent(customerId, 0);
        
        Map<String, Object> result = new HashMap<>();
        result.put("customerId", customerId);
        result.put("enrollmentStatus", "ACTIVE");
        result.put("currentPoints", loyaltyPoints.get(customerId));
        result.put("loyaltyTier", customer.getSegment());

        return result;
    }

    /**
     * Segment customers based on various criteria
     */
    public List<TelecomCustomer> segmentCustomers(String segmentCriteria) {
        if (!FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_CUSTOMER_SEGMENTATION)) {
            throw new RuntimeException("Customer segmentation feature is disabled");
        }

        return customerStore.values().stream()
            .filter(c -> matchesSegmentCriteria(c, segmentCriteria))
            .collect(Collectors.toList());
    }

    /**
     * Calculate customer lifetime value with complex formula
     */
    public Map<String, Object> calculateLifetimeValue(String customerId) {
        if (!FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_LIFETIME_VALUE_CALCULATION)) {
            throw new RuntimeException("Lifetime value calculation feature is disabled");
        }

        TelecomCustomer customer = customerStore.get(customerId);
        if (customer == null) throw new RuntimeException("Customer not found");

        double monthlyRevenue = 0;
        List<ServiceSubscription> subs = subscriptionsByCustomer.get(customerId);
        if (subs != null) {
            monthlyRevenue = subs.stream().mapToDouble(ServiceSubscription::getMonthlyCharges).sum();
        }

        int customerMonths = (int) ((System.currentTimeMillis() - customer.getAccountCreatedAt()) / (30L * 24 * 60 * 60 * 1000));
        double ltv = monthlyRevenue * customerMonths * 0.85; // 85% retention assumption

        customer.setLifetimeValue(ltv);
        customerStore.put(customerId, customer);

        Map<String, Object> result = new HashMap<>();
        result.put("customerId", customerId);
        result.put("lifetime_value", ltv);
        result.put("monthly_revenue", monthlyRevenue);
        result.put("customer_months", customerMonths);
        result.put("segment", customer.getSegment());

        return result;
    }

    /**
     * Predict customer churn
     */
    public Map<String, Object> predictChurn(String customerId) {
        if (!FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_CHURN_PREDICTION)) {
            throw new RuntimeException("Churn prediction feature is disabled");
        }

        TelecomCustomer customer = customerStore.get(customerId);
        if (customer == null) throw new RuntimeException("Customer not found");

        int riskScore = calculateChurnRiskScore(customer.getLastActivityAt());
        customer.setChurnRiskScore(riskScore);
        customerStore.put(customerId, customer);

        Map<String, Object> result = new HashMap<>();
        result.put("customerId", customerId);
        result.put("churn_risk_score", riskScore);
        result.put("risk_level", riskScore > 70 ? "HIGH" : riskScore > 50 ? "MEDIUM" : "LOW");
        result.put("days_since_activity", (System.currentTimeMillis() - customer.getLastActivityAt()) / (24 * 60 * 60 * 1000L));

        return result;
    }

    /**
     * Export customer data for compliance
     */
    public Map<String, Object> exportCustomerData(String customerId) {
        if (!FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_DATA_EXPORT)) {
            throw new RuntimeException("Data export feature is disabled");
        }

        TelecomCustomer customer = customerStore.get(customerId);
        if (customer == null) throw new RuntimeException("Customer not found");

        Map<String, Object> exportData = new HashMap<>();
        exportData.put("customer_id", customer.getCustomerId());
        exportData.put("name", customer.getFirstName() + " " + customer.getLastName());
        exportData.put("email", customer.getEmail());
        exportData.put("phone", customer.getPhoneNumber());
        exportData.put("msisdn", customer.getMsisdn());
        exportData.put("status", customer.getStatus().toString());
        exportData.put("customer_type", customer.getCustomerType());
        exportData.put("segment", customer.getSegment());
        exportData.put("account_created", customer.getAccountCreatedAt());
        exportData.put("kyc_verified", customer.getKycVerifiedAt());
        exportData.put("subscriptions", subscriptionsByCustomer.getOrDefault(customerId, new ArrayList<>()));
        exportData.put("contracts", contractsByCustomer.getOrDefault(customerId, new ArrayList<>()));

        return exportData;
    }

    /**
     * Handle GDPR compliance requests
     */
    public boolean handleGDPRComplianceRequest(String customerId, String requestType) {
        if (!FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_GDPR_COMPLIANCE)) {
            throw new RuntimeException("GDPR compliance feature is disabled");
        }

        if ("RIGHT_TO_BE_FORGOTTEN".equals(requestType)) {
            customerStore.remove(customerId);
            subscriptionsByCustomer.remove(customerId);
            contractsByCustomer.remove(customerId);
            billingProfiles.remove(customerId);
            loyaltyPoints.remove(customerId);
            return true;
        }

        return false;
    }

    /**
     * Generic CRUD operations with legacy support
     */
    public List<Object> listAll() {
        return new ArrayList<>(customerStore.values());
    }

    public Object getById(String id) {
        return customerStore.getOrDefault(id, genericStore.get(id));
    }

    public Object create(Map<String, Object> payload) {
        String id = UUID.randomUUID().toString();
        payload.put("id", id);
        payload.put("createdAt", System.currentTimeMillis());
        genericStore.put(id, payload);
        return payload;
    }

    public Object update(String id, Map<String, Object> payload) {
        Map<String, Object> existing = genericStore.get(id);
        if (existing == null) return null;
        existing.putAll(payload);
        existing.put("updatedAt", System.currentTimeMillis());
        genericStore.put(id, existing);
        return existing;
    }

    public boolean delete(String id) {
        return genericStore.remove(id) != null;
    }

    public List<Object> search(Map<String, String> params) {
        List<Object> out = new ArrayList<>();
        for (Map<String, Object> v : genericStore.values()) {
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

    public List<Object> bulkCreate(List<Map<String, Object>> payloads) {
        List<Object> created = new ArrayList<>();
        for (Map<String, Object> p : payloads) {
            created.add(create(p));
        }
        return created;
    }

    public int count() {
        return customerStore.size() + genericStore.size();
    }

    public Object updateProfile(String id, Map<String, Object> payload) {
        if (!FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_PROFILE_EDIT)) {
            throw new RuntimeException("Profile edit feature is disabled");
        }
        // Cross-service: If account lock is enabled in auth, block update if locked
        if (FeatureFlagReader.isFeatureEnabled("auth_enable_account_lock")) {
            TelecomCustomer customer = customerStore.get(id);
            if (customer != null && Boolean.TRUE.equals(customer.getMeta() != null ? customer.getMeta().get("locked") : null)) {
                throw new RuntimeException("Account is locked (auth_enable_account_lock)");
            }
        }

        TelecomCustomer customer = customerStore.get(id);
        if (customer == null) return null;

        customer.setFirstName((String) payload.getOrDefault("firstName", customer.getFirstName()));
        customer.setLastName((String) payload.getOrDefault("lastName", customer.getLastName()));
        customer.setEmail((String) payload.getOrDefault("email", customer.getEmail()));
        customer.setPhoneNumber((String) payload.getOrDefault("phoneNumber", customer.getPhoneNumber()));

        if (FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_PROFILE_VALIDATION)) {
            validateProfile(customer);
        }

        if (FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_ADVANCED_PROFILE_SYNC)) {
            syncProfileToExternalSystems(customer);
        }

        customerStore.put(id, customer);
        return customer;
    }

    // ============ Private Helper Methods ============

    private void verifyCustomer(TelecomCustomer customer) {
        customer.setStatus(CustomerStatus.VERIFIED);
    }

    private void performKYCValidation(TelecomCustomer customer) {
        // Complex KYC validation logic
        double randomScore = Math.random();
        if (randomScore > 0.1) {
            customer.setStatus(CustomerStatus.VERIFIED);
        }
    }

    private void performCreditCheck(TelecomCustomer customer) {
        CreditHistory history = new CreditHistory();
        history.setRecordId(UUID.randomUUID().toString());
        history.setCreditScore(600 + Math.random() * 400); // 600-1000
        history.setStatus("APPROVED");

        if (history.getCreditScore() < 700) {
            customer.setBillingProfile(customer.getBillingProfile());
            customer.getBillingProfile().setCreditLimit(500.0);
        }

        customer.getCreditHistory().add(history);
    }

    private void performFraudDetection(TelecomCustomer customer) {
        // Fraud detection logic
        if (Math.random() > 0.95) {
            customer.setFraudRiskDetected(true);
        }
    }

    private void detectDuplicateCustomer(TelecomCustomer customer) {
        // Duplicate detection logic
        customerStore.values().stream()
            .filter(c -> c.getEmail().equals(customer.getEmail()))
            .findFirst()
            .ifPresent(dup -> {
                throw new RuntimeException("Duplicate customer detected with email: " + customer.getEmail());
            });
    }

    private void syncWithBillingSystem(String customerId, BillingProfile billing) {
        // Sync with external billing system (Kafka/API call)
    }

    private void updateNotificationPreferences(String customerId, CustomerPreferences prefs) {
        // Send to notification service
    }

    private boolean matchesSegmentCriteria(TelecomCustomer customer, String criteria) {
        if ("HIGH_VALUE".equals(criteria)) {
            return customer.getLifetimeValue() > 5000;
        }
        if ("AT_RISK".equals(criteria)) {
            return customer.getChurnRiskScore() > 70;
        }
        if ("ENTERPRISE".equals(criteria)) {
            return "ENTERPRISE".equals(customer.getCustomerType());
        }
        return customer.getSegment().equals(criteria);
    }

    private void validateProfile(TelecomCustomer customer) {
        if (customer.getEmail() == null || !customer.getEmail().contains("@")) {
            throw new RuntimeException("Invalid email format");
        }
        if (customer.getPhoneNumber() == null || customer.getPhoneNumber().length() < 10) {
            throw new RuntimeException("Invalid phone number");
        }
    }

    private void syncProfileToExternalSystems(TelecomCustomer customer) {
        // Sync to CRM, Billing, Notification systems
    }
}
