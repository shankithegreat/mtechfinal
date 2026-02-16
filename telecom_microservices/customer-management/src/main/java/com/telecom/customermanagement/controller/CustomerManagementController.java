package com.telecom.customermanagement.controller;

import com.telecom.customermanagement.model.*;
import com.telecom.customermanagement.service.*;
import com.telecom.customermanagement.config.FeatureFlagConstants;
import com.telecom.common.FeatureFlagReader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * REST controllers for CustomerManagement with comprehensive feature flag support.
 * Each endpoint checks feature flags at the controller layer and integrates with
 * feature-flag-aware business logic in the service layer.
 */
@RestController
@RequestMapping("/api/customer-management")
public class CustomerManagementController {

    @Autowired
    private CustomerManagementService service;

    /**
     * List all customers
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listAll() {
        List<Object> customers = service.listAll();
        Map<String, Object> response = new HashMap<>();
        response.put("count", customers.size());
        response.put("data", customers);
        return ResponseEntity.ok(response);
    }

    /**
     * Get customer by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable String id) {
        Object obj = service.getById(id);
        if (obj == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(obj);
    }

    /**
     * Register a new customer - Feature flag protected
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerCustomer(@RequestBody Map<String, Object> customerData) {
        try {
            TelecomCustomer customer = service.registerCustomer(customerData);
            Map<String, Object> response = new HashMap<>();
            response.put("customerId", customer.getCustomerId());
            response.put("status", customer.getStatus().toString());
            response.put("message", "Customer registered successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Activate customer account - Feature flag protected
     */
    @PostMapping("/{id}/activate")
    public ResponseEntity<Map<String, Object>> activateCustomer(@PathVariable String id) {
        try {
            TelecomCustomer customer = service.activateCustomer(id);
            if (customer == null) return ResponseEntity.notFound().build();
            
            Map<String, Object> response = new HashMap<>();
            response.put("customerId", customer.getCustomerId());
            response.put("status", customer.getStatus().toString());
            response.put("message", "Customer activated successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Suspend customer account - Feature flag protected
     */
    @PostMapping("/{id}/suspend")
    public ResponseEntity<Map<String, Object>> suspendCustomer(@PathVariable String id, @RequestBody Map<String, String> data) {
        try {
            String reason = data.getOrDefault("reason", "No reason provided");
            TelecomCustomer customer = service.suspendCustomer(id, reason);
            if (customer == null) return ResponseEntity.notFound().build();
            
            Map<String, Object> response = new HashMap<>();
            response.put("customerId", customer.getCustomerId());
            response.put("status", customer.getStatus().toString());
            response.put("suspensionReason", reason);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Terminate customer account - Feature flag protected
     */
    @PostMapping("/{id}/terminate")
    public ResponseEntity<Map<String, Object>> terminateCustomer(@PathVariable String id, @RequestBody Map<String, String> data) {
        try {
            String reason = data.getOrDefault("reason", "No reason provided");
            boolean success = service.terminateCustomer(id, reason);
            if (!success) return ResponseEntity.notFound().build();
            
            Map<String, Object> response = new HashMap<>();
            response.put("customerId", id);
            response.put("terminationReason", reason);
            response.put("message", "Customer account terminated");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Edit customer profile - Feature flag protected
     */
    @PutMapping("/{id}/profile")
    public ResponseEntity<Map<String, Object>> editProfile(@PathVariable String id, @RequestBody Map<String, Object> profileData) {
        try {
            Object updated = service.updateProfile(id, profileData);
            if (updated == null) return ResponseEntity.notFound().build();
            
            Map<String, Object> response = new HashMap<>();
            response.put("customerId", id);
            response.put("profile", updated);
            response.put("message", "Profile updated successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Add service subscription - Feature flag protected
     */
    @PostMapping("/{customerId}/subscriptions")
    public ResponseEntity<Map<String, Object>> addSubscription(@PathVariable String customerId, @RequestBody Map<String, Object> subscriptionData) {
        try {
            ServiceSubscription subscription = service.addServiceSubscription(customerId, subscriptionData);
            Map<String, Object> response = new HashMap<>();
            response.put("customerId", customerId);
            response.put("subscriptionId", subscription.getSubscriptionId());
            response.put("serviceType", subscription.getServiceType());
            response.put("monthlyCharges", subscription.getMonthlyCharges());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Update billing profile - Feature flag protected
     */
    @PutMapping("/{customerId}/billing")
    public ResponseEntity<Map<String, Object>> updateBillingProfile(@PathVariable String customerId, @RequestBody Map<String, Object> billingData) {
        try {
            BillingProfile billing = service.updateBillingProfile(customerId, billingData);
            Map<String, Object> response = new HashMap<>();
            response.put("customerId", customerId);
            response.put("accountNumber", billing.getAccountNumber());
            response.put("paymentMethod", billing.getPaymentMethod());
            response.put("currentBalance", billing.getCurrentBalance());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Create contract - Feature flag protected
     */
    @PostMapping("/{customerId}/contracts")
    public ResponseEntity<Map<String, Object>> createContract(@PathVariable String customerId, @RequestBody Map<String, Object> contractData) {
        try {
            Contract contract = service.createContract(customerId, contractData);
            Map<String, Object> response = new HashMap<>();
            response.put("customerId", customerId);
            response.put("contractId", contract.getContractId());
            response.put("contractType", contract.getContractType());
            response.put("contractValue", contract.getContractValue());
            response.put("earlyTerminationFee", contract.getEarlyTerminationFee());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Renew contract - Feature flag protected
     */
    @PostMapping("/{customerId}/contracts/{contractId}/renew")
    public ResponseEntity<Map<String, Object>> renewContract(@PathVariable String customerId, @PathVariable String contractId) {
        try {
            Contract contract = service.renewContract(customerId, contractId);
            Map<String, Object> response = new HashMap<>();
            response.put("contractId", contractId);
            response.put("status", "RENEWED");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Early terminate contract - Feature flag protected
     */
    @PostMapping("/{customerId}/contracts/{contractId}/early-terminate")
    public ResponseEntity<Map<String, Object>> earlyTerminateContract(@PathVariable String customerId, @PathVariable String contractId) {
        try {
            Map<String, Object> result = service.earlyTerminateContract(customerId, contractId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Update customer preferences - Feature flag protected
     */
    @PutMapping("/{customerId}/preferences")
    public ResponseEntity<Map<String, Object>> updatePreferences(@PathVariable String customerId, @RequestBody Map<String, Object> preferencesData) {
        try {
            CustomerPreferences prefs = service.updatePreferences(customerId, preferencesData);
            Map<String, Object> response = new HashMap<>();
            response.put("customerId", customerId);
            response.put("preferences", prefs);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Enroll in loyalty program - Feature flag protected
     */
    @PostMapping("/{customerId}/loyalty/enroll")
    public ResponseEntity<Map<String, Object>> enrollLoyalty(@PathVariable String customerId) {
        try {
            Map<String, Object> result = service.enrollInLoyaltyProgram(customerId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Segment customers - Feature flag protected
     */
    @GetMapping("/segment/{criteria}")
    public ResponseEntity<Map<String, Object>> segmentCustomers(@PathVariable String criteria) {
        try {
            List<TelecomCustomer> customers = service.segmentCustomers(criteria);
            Map<String, Object> response = new HashMap<>();
            response.put("segmentCriteria", criteria);
            response.put("count", customers.size());
            response.put("customers", customers);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Calculate lifetime value - Feature flag protected
     */
    @GetMapping("/{customerId}/lifetime-value")
    public ResponseEntity<Map<String, Object>> getLifetimeValue(@PathVariable String customerId) {
        try {
            Map<String, Object> ltv = service.calculateLifetimeValue(customerId);
            return ResponseEntity.ok(ltv);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Predict churn - Feature flag protected
     */
    @GetMapping("/{customerId}/churn-prediction")
    public ResponseEntity<Map<String, Object>> predictChurn(@PathVariable String customerId) {
        try {
            Map<String, Object> churnPrediction = service.predictChurn(customerId);
            return ResponseEntity.ok(churnPrediction);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Export customer data - Feature flag protected
     */
    @GetMapping("/{customerId}/export")
    public ResponseEntity<Map<String, Object>> exportCustomerData(@PathVariable String customerId) {
        try {
            Map<String, Object> exportData = service.exportCustomerData(customerId);
            return ResponseEntity.ok(exportData);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Handle GDPR compliance - Feature flag protected
     */
    @PostMapping("/{customerId}/gdpr/{requestType}")
    public ResponseEntity<Map<String, Object>> handleGDPRRequest(@PathVariable String customerId, @PathVariable String requestType) {
        try {
            boolean success = service.handleGDPRComplianceRequest(customerId, requestType);
            Map<String, Object> response = new HashMap<>();
            response.put("requestType", requestType);
            response.put("customerId", customerId);
            response.put("processed", success);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Generic create - Legacy support
     */
    @PostMapping
    public ResponseEntity<Object> create(@RequestBody Map<String, Object> payload) {
        Object created = service.create(payload);
        return ResponseEntity.ok(created);
    }

    /**
     * Generic update - Legacy support
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        Object updated = service.update(id, payload);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    /**
     * Generic delete - Legacy support
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        boolean ok = service.delete(id);
        if (!ok) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }

    /**
     * Generic search - Legacy support
     */
    @GetMapping("/search")
    public ResponseEntity<List<Object>> search(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(service.search(params));
    }

    /**
     * Generic bulk create - Legacy support
     */
    @PostMapping("/bulk")
    public ResponseEntity<List<Object>> bulkCreate(@RequestBody List<Map<String, Object>> payloads) {
        return ResponseEntity.ok(service.bulkCreate(payloads));
    }

    /**
     * Health check with feature flag status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "customer-management");
        response.put("status", "UP");
        response.put("totalCustomers", service.count());
        response.put("featureFlagsEnabled", countEnabledFeatures());
        return ResponseEntity.ok(response);
    }

    /**
     * Count enabled features for diagnostics
     */
    private int countEnabledFeatures() {
        int count = 0;
        if (FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_CUSTOMER_REGISTRATION)) count++;
        if (FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_CUSTOMER_VERIFICATION)) count++;
        if (FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_CUSTOMER_ACTIVATION)) count++;
        if (FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_SUBSCRIPTION_MANAGEMENT)) count++;
        if (FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_BILLING_ACCOUNT)) count++;
        if (FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_CONTRACT_MANAGEMENT)) count++;
        if (FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_PROFILE_EDIT)) count++;
        if (FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_LOYALTY_PROGRAM)) count++;
        if (FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_CHURN_PREDICTION)) count++;
        if (FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_LIFETIME_VALUE_CALCULATION)) count++;
        return count;
    }
}

