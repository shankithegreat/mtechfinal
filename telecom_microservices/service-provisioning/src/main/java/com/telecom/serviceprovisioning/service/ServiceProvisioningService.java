                // Cross-service: If payment optimization is enabled in payment-processing, optimize payment
                if (FeatureFlagReader.isFeatureEnabled("payment_advanced_enable_payment_optimization")) {
                    System.out.println("[PAYMENT] Optimization run (payment_advanced_enable_payment_optimization)");
                }
        // Cross-service: If notification failover is enabled, handle failover
        if (!FeatureFlagReader.isFeatureEnabled("notification_failover_enable")) {
            throw new IllegalStateException("Notification failover is disabled (notification_failover_enable)");
        }
package com.telecom.serviceprovisioning.service;

import com.telecom.serviceprovisioning.model.*;
import com.telecom.serviceprovisioning.util.ServiceProvisioningFeatureFlagConstants;
import com.telecom.serviceprovisioning.util.FeatureFlagReader;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ServiceProvisioningService {

    private final FeatureFlagReader featureFlagReader;
    
    // In-memory stores for thread-safe operation
    private final Map<String, ServiceProvisioningRequest> provisioningStore = new ConcurrentHashMap<>();
    private final Map<String, UsageTracking> usageStore = new ConcurrentHashMap<>();
    private final Map<String, ServiceModificationRequest> modificationStore = new ConcurrentHashMap<>();
    private final Map<String, ProvisioningAlert> alertStore = new ConcurrentHashMap<>();
    
    // Counters for ID generation
    private final AtomicInteger provisioningIdCounter = new AtomicInteger(1000);
    private final AtomicInteger msisdnCounter = new AtomicInteger(1000000000);
    private final AtomicInteger simCardCounter = new AtomicInteger(89886);
    
    public ServiceProvisioningService(FeatureFlagReader featureFlagReader) {
        this.featureFlagReader = featureFlagReader;
    }

    // ==================== SERVICE ACTIVATION ====================

    public ServiceProvisioningRequest initiateSimActivation(ServiceProvisioningRequest request) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SERVICE_ACTIVATION_SIM_ACTIVATION)) {
            return null;
        }
        
        request.setRequestId("REQ-" + provisioningIdCounter.incrementAndGet());
        request.setStatus(ProvisioningStatus.PROVISIONING);
        request.setCreatedAt(System.currentTimeMillis());
        request.setEvents(new ArrayList<>());
        
        // Generate unique SIM card ID with validation
        String simCardId = "89886" + String.format("%010d", simCardCounter.incrementAndGet());
        String imsi = generateImsi(request.getCustomerId());
        String iccid = generateIccid();
        
        ServiceActivationDetails activation = new ServiceActivationDetails();
        activation.setSimCardId(simCardId);
        activation.setImsi(imsi);
        activation.setSimStatus("ACTIVATED");
        activation.setActivationCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        activation.setActivationTime(System.currentTimeMillis());
        activation.setVoiceEnabled(true);
        activation.setDataEnabled(true);
        activation.setSmsEnabled(true);
        
        request.setActivationDetails(activation);
        request.setIccid(iccid);
        request.setStatus(ProvisioningStatus.ACTIVE);
        
        addEvent(request, "SIM_ACTIVATED", "SIM card successfully activated", "SYSTEM");
        provisioningStore.put(request.getRequestId(), request);
        
        return request;
    }

    public ServiceProvisioningRequest initiateDeviceActivation(ServiceProvisioningRequest request) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SERVICE_ACTIVATION_DEVICE_ACTIVATION)) {
            return null;
        }
        
        if (request.getImei() == null || request.getImei().isEmpty()) {
            return null;
        }
        
        request.setRequestId("DEV-" + provisioningIdCounter.incrementAndGet());
        request.setStatus(ProvisioningStatus.PROVISIONING);
        request.setCreatedAt(System.currentTimeMillis());
        request.setEvents(new ArrayList<>());
        
        // Validate IMEI format (15 digits)
        if (validateImei(request.getImei())) {
            addEvent(request, "DEVICE_IMEI_VALIDATED", "IMEI " + request.getImei() + " validated successfully", "SYSTEM");
        } else {
            request.setStatus(ProvisioningStatus.FAILED);
            addEvent(request, "DEVICE_ACTIVATION_FAILED", "Invalid IMEI format", "SYSTEM");
            return request;
        }
        
        request.setStatus(ProvisioningStatus.ACTIVE);
        provisioningStore.put(request.getRequestId(), request);
        addEvent(request, "DEVICE_ACTIVATED", "Device with IMEI " + request.getImei() + " activated", "SYSTEM");
        
        return request;
    }

    public ServiceProvisioningRequest allocateMsisdn(String customerId, String preferredMsisdn) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SERVICE_ACTIVATION_MSISDN_ALLOCATION)) {
            return null;
        }
        
        ServiceProvisioningRequest request = new ServiceProvisioningRequest();
        request.setRequestId("MSISDN-" + provisioningIdCounter.incrementAndGet());
        request.setCustomerId(customerId);
        request.setCreatedAt(System.currentTimeMillis());
        request.setEvents(new ArrayList<>());
        
        String allocatedMsisdn;
        if (preferredMsisdn != null && preferredMsisdn.length() == 10) {
            allocatedMsisdn = "+1" + preferredMsisdn;
            addEvent(request, "MSISDN_PREFERRED_ALLOCATED", "Preferred MSISDN allocated: " + allocatedMsisdn, "SYSTEM");
        } else {
            // Auto-generate MSISDN: +1 + 10 digit number
            allocatedMsisdn = "+1" + String.format("%010d", msisdnCounter.incrementAndGet());
            addEvent(request, "MSISDN_AUTO_ALLOCATED", "Auto-generated MSISDN allocated: " + allocatedMsisdn, "SYSTEM");
        }
        
        request.setPhoneNumber(allocatedMsisdn);
        request.setStatus(ProvisioningStatus.ACTIVE);
        provisioningStore.put(request.getRequestId(), request);
        
        return request;
    }

    // ==================== NETWORK CONFIGURATION ====================

    public ServiceProvisioningRequest configureNetworkSettings(String msisdn, NetworkType primaryNetwork, String apn) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SERVICE_ACTIVATION_NETWORK_CONFIG)) {
            return null;
        }
        
        ServiceProvisioningRequest request = findByMsisdn(msisdn);
        if (request == null) {
            request = new ServiceProvisioningRequest();
            request.setRequestId("NET-" + provisioningIdCounter.incrementAndGet());
            request.setPhoneNumber(msisdn);
            request.setCreatedAt(System.currentTimeMillis());
            request.setEvents(new ArrayList<>());
        }
        
        NetworkConfigurationDetails netConfig = new NetworkConfigurationDetails();
        netConfig.setPrimaryNetwork(primaryNetwork);
        netConfig.setSupportedNetworks(Arrays.asList(primaryNetwork));
        netConfig.setPrimaryApn(apn != null ? apn : "internet.carrier.com");
        netConfig.setPrimaryDns("8.8.8.8");
        netConfig.setSecondaryDns("8.8.4.4");
        netConfig.setMccMnc("310410"); // US MCC-MNC
        netConfig.setRoamingEnabled(false);
        
        request.setNetworkConfig(netConfig);
        request.setStatus(ProvisioningStatus.ACTIVE);
        addEvent(request, "NETWORK_CONFIGURED", primaryNetwork + " configured with APN: " + netConfig.getPrimaryApn(), "SYSTEM");
        
        provisioningStore.put(request.getRequestId(), request);
        return request;
    }

    public ServiceProvisioningRequest configureVpn(String msisdn, String vpnType, String serverAddress) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SERVICE_ACTIVATION_VPN_CONFIG)) {
            return null;
        }
        
        ServiceProvisioningRequest request = findByMsisdn(msisdn);
        if (request == null || request.getNetworkConfig() == null) {
            return null;
        }
        
        VpnConfiguration vpnConfig = new VpnConfiguration();
        vpnConfig.setVpnEnabled(true);
        vpnConfig.setVpnType(vpnType);
        vpnConfig.setVpnServerAddress(serverAddress);
        vpnConfig.setVpnPort(1194);
        vpnConfig.setEncryptionMethod("AES-256-GCM");
        vpnConfig.setAuthenticationMethod("CERTIFICATE");
        
        request.getNetworkConfig().setVpnConfig(vpnConfig);
        addEvent(request, "VPN_CONFIGURED", vpnType + " VPN configured to " + serverAddress, "SYSTEM");
        
        return request;
    }

    // ==================== SERVICE CONFIGURATION ====================

    public ServiceProvisioningRequest configureVoiceService(String msisdn, boolean enabled, String voiceType) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SERVICE_CONFIG_VOICE_CONFIG)) {
            return null;
        }
        
        ServiceProvisioningRequest request = findByMsisdn(msisdn);
        if (request == null || request.getActivationDetails() == null) {
            return null;
        }
        
        request.getActivationDetails().setVoiceEnabled(enabled);
        addEvent(request, "VOICE_SERVICE_" + (enabled ? "ENABLED" : "DISABLED"), 
                 "Voice service " + (enabled ? "enabled" : "disabled") + " with type: " + voiceType, "SYSTEM");
        
        return request;
    }

    public ServiceProvisioningRequest configureDataService(String msisdn, boolean enabled, int dataAllowanceMb) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SERVICE_CONFIG_DATA_CONFIG)) {
            return null;
        }
        
        ServiceProvisioningRequest request = findByMsisdn(msisdn);
        if (request == null) {
            return null;
        }
        
        if (request.getActivationDetails() == null) {
            ServiceActivationDetails activation = new ServiceActivationDetails();
            activation.setDataEnabled(enabled);
            request.setActivationDetails(activation);
        } else {
            request.getActivationDetails().setDataEnabled(enabled);
        }
        
        // Initialize usage tracking if not exists
        UsageTracking usage = usageStore.getOrDefault(msisdn, new UsageTracking());
        usage.setMsisdn(msisdn);
        usage.setDataAllowedMb(dataAllowanceMb);
        usage.setTrackingPeriodStart(System.currentTimeMillis());
        usageStore.put(msisdn, usage);
        
        addEvent(request, "DATA_SERVICE_" + (enabled ? "ENABLED" : "DISABLED"), 
                 "Data service " + (enabled ? "enabled" : "disabled") + " with allowance: " + dataAllowanceMb + "MB", "SYSTEM");
        
        return request;
    }

    public ServiceProvisioningRequest enableRoaming(String msisdn, List<String> countries) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SERVICE_CONFIG_ROAMING_CONFIG)) {
            return null;
        }
        
        ServiceProvisioningRequest request = findByMsisdn(msisdn);
        if (request == null || request.getNetworkConfig() == null) {
            return null;
        }
        
        request.getNetworkConfig().setRoamingEnabled(true);
        request.getNetworkConfig().setRoamingCountries(countries != null ? countries : new ArrayList<>());
        addEvent(request, "ROAMING_ENABLED", "Roaming enabled for countries: " + countries, "SYSTEM");
        
        return request;
    }

    // ==================== BILLING INTEGRATION ====================

    public ServiceProvisioningRequest setupBillingConfiguration(String msisdn, String billingAccountId, 
                                                                double monthlyCharge, BillingFrequency frequency) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_BILLING_INTEGRATION_BILLING_SYNC)) {
            return null;
        }
        
        ServiceProvisioningRequest request = findByMsisdn(msisdn);
        if (request == null) {
            return null;
        }
        
        BillingConfigurationDetails billingConfig = new BillingConfigurationDetails();
        billingConfig.setBillingAccountId(billingAccountId);
        billingConfig.setMonthlyRecurringCharge(monthlyCharge);
        billingConfig.setCurrency("USD");
        billingConfig.setBillingCycle(frequency);
        billingConfig.setGracePeriodDays(7);
        billingConfig.setOverageCostPerMb(0.10);
        billingConfig.setOverageCostPerMinute(0.15);
        billingConfig.setThrottlingEnabled(false);
        billingConfig.setNextBillingDate(calculateNextBillingDate(frequency));
        
        request.setBillingConfig(billingConfig);
        addEvent(request, "BILLING_CONFIGURED", "Billing account " + billingAccountId + " configured with charge: $" + monthlyCharge, "SYSTEM");
        
        return request;
    }

    public boolean enableUsageTracking(String msisdn, double dataAllowanceMb, int voiceAllowanceMinutes, int smsAllowance) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_BILLING_INTEGRATION_USAGE_TRACKING)) {
            return false;
        }
        
        UsageTracking usage = new UsageTracking();
        usage.setTrackingId("TRACK-" + UUID.randomUUID().toString());
        usage.setMsisdn(msisdn);
        usage.setDataAllowedMb(dataAllowanceMb);
        usage.setVoiceAllowedMinutes(voiceAllowanceMinutes);
        usage.setSmsAllowed(smsAllowance);
        usage.setTrackingPeriodStart(System.currentTimeMillis());
        
        usageStore.put(msisdn, usage);
        return true;
    }

    public boolean enableThrottling(String msisdn, double thresholdMb) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_BILLING_INTEGRATION_THROTTLING)) {
            return false;
        }
        
        ServiceProvisioningRequest request = findByMsisdn(msisdn);
        if (request == null || request.getBillingConfig() == null) {
            return false;
        }
        
        request.getBillingConfig().setThrottlingEnabled(true);
        request.getBillingConfig().setThrottleThresholdMb(thresholdMb);
        addEvent(request, "THROTTLING_ENABLED", "Data throttling enabled at " + thresholdMb + "MB", "SYSTEM");
        
        return true;
    }

    // ==================== SERVICE MANAGEMENT ====================

    public ServiceModificationRequest requestServiceUpgrade(String msisdn, String newPlanId) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SERVICE_MANAGEMENT_SERVICE_UPGRADE)) {
            return null;
        }
        
        ServiceProvisioningRequest request = findByMsisdn(msisdn);
        if (request == null) {
            return null;
        }
        
        ServiceModificationRequest modification = new ServiceModificationRequest();
        modification.setModificationId("MOD-" + provisioningIdCounter.incrementAndGet());
        modification.setMsisdn(msisdn);
        modification.setModificationType("UPGRADE");
        modification.setCurrentPlanId(request.getPlanId());
        modification.setNewPlanId(newPlanId);
        modification.setStatus(ProvisioningStatus.ACTIVE);
        modification.setRequestedDate(System.currentTimeMillis());
        modification.setEffectiveDate(System.currentTimeMillis());
        
        request.setPlanId(newPlanId);
        addEvent(request, "SERVICE_UPGRADED", "Service upgraded from " + modification.getCurrentPlanId() + " to " + newPlanId, "SYSTEM");
        
        modificationStore.put(modification.getModificationId(), modification);
        return modification;
    }

    public ServiceModificationRequest requestServiceDowngrade(String msisdn, String newPlanId) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SERVICE_MANAGEMENT_SERVICE_DOWNGRADE)) {
            return null;
        }
        
        ServiceProvisioningRequest request = findByMsisdn(msisdn);
        if (request == null) {
            return null;
        }
        
        ServiceModificationRequest modification = new ServiceModificationRequest();
        modification.setModificationId("MOD-" + provisioningIdCounter.incrementAndGet());
        modification.setMsisdn(msisdn);
        modification.setModificationType("DOWNGRADE");
        modification.setCurrentPlanId(request.getPlanId());
        modification.setNewPlanId(newPlanId);
        modification.setStatus(ProvisioningStatus.ACTIVE);
        modification.setRequestedDate(System.currentTimeMillis());
        // Downgrade effective after 30 days
        modification.setEffectiveDate(System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000));
        
        addEvent(request, "SERVICE_DOWNGRADE_REQUESTED", "Service downgrade scheduled to " + newPlanId, "SYSTEM");
        modificationStore.put(modification.getModificationId(), modification);
        
        return modification;
    }

    public ServiceModificationRequest addServiceAddon(String msisdn, String addonId) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SERVICE_MANAGEMENT_ADD_ON_MANAGEMENT)) {
            return null;
        }
        
        ServiceProvisioningRequest request = findByMsisdn(msisdn);
        if (request == null) {
            return null;
        }
        
        ServiceModificationRequest modification = new ServiceModificationRequest();
        modification.setModificationId("MOD-" + provisioningIdCounter.incrementAndGet());
        modification.setMsisdn(msisdn);
        modification.setModificationType("ADD_ADDON");
        modification.setAddOnsToAdd(Arrays.asList(addonId));
        modification.setStatus(ProvisioningStatus.ACTIVE);
        modification.setRequestedDate(System.currentTimeMillis());
        modification.setEffectiveDate(System.currentTimeMillis());
        
        addEvent(request, "ADDON_ADDED", "Add-on " + addonId + " added to service", "SYSTEM");
        modificationStore.put(modification.getModificationId(), modification);
        
        return modification;
    }

    public ServiceProvisioningRequest suspendService(String msisdn, String reason) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SERVICE_MANAGEMENT_SERVICE_SUSPENSION)) {
            return null;
        }
        
        ServiceProvisioningRequest request = findByMsisdn(msisdn);
        if (request == null) {
            return null;
        }
        
        request.setStatus(ProvisioningStatus.SUSPENDED);
        addEvent(request, "SERVICE_SUSPENDED", "Service suspended. Reason: " + reason, "SYSTEM");
        
        return request;
    }

    public ServiceProvisioningRequest terminateService(String msisdn, String reason) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SERVICE_MANAGEMENT_SERVICE_TERMINATION)) {
            return null;
        }
        
        ServiceProvisioningRequest request = findByMsisdn(msisdn);
        if (request == null) {
            return null;
        }
        
        request.setStatus(ProvisioningStatus.TERMINATED);
        request.setCompletedAt(System.currentTimeMillis());
        addEvent(request, "SERVICE_TERMINATED", "Service terminated. Reason: " + reason, "SYSTEM");
        
        return request;
    }

    // ==================== QOS MANAGEMENT ====================

    public ServiceProvisioningRequest configureQos(String msisdn, PriorityLevel priorityLevel, int bandwidthMbps, int maxLatencyMs) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_QOS_QOS_MANAGEMENT)) {
            return null;
        }
        
        ServiceProvisioningRequest request = findByMsisdn(msisdn);
        if (request == null) {
            return null;
        }
        
        QualityOfServiceConfig qos = new QualityOfServiceConfig();
        qos.setPriorityLevel(priorityLevel);
        qos.setBandwidthMbps(bandwidthMbps);
        qos.setMaximumLatencyMs(maxLatencyMs);
        qos.setPacketLossPercentage(0);
        qos.setTrafficShapingEnabled(false);
        
        request.setQosConfig(qos);
        addEvent(request, "QOS_CONFIGURED", "QoS configured: " + priorityLevel + ", " + bandwidthMbps + "Mbps, latency: " + maxLatencyMs + "ms", "SYSTEM");
        
        return request;
    }

    public ServiceProvisioningRequest allocateBandwidth(String msisdn, int allocationMbps) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_QOS_BANDWIDTH_ALLOCATION)) {
            return null;
        }
        
        ServiceProvisioningRequest request = findByMsisdn(msisdn);
        if (request == null || request.getQosConfig() == null) {
            return null;
        }
        
        request.getQosConfig().setBandwidthMbps(allocationMbps);
        addEvent(request, "BANDWIDTH_ALLOCATED", "Bandwidth allocated: " + allocationMbps + "Mbps", "SYSTEM");
        
        return request;
    }

    public ServiceProvisioningRequest enableTrafficShaping(String msisdn, double shapeRateMbps) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_QOS_TRAFFIC_SHAPING)) {
            return null;
        }
        
        ServiceProvisioningRequest request = findByMsisdn(msisdn);
        if (request == null || request.getQosConfig() == null) {
            return null;
        }
        
        request.getQosConfig().setTrafficShapingEnabled(true);
        addEvent(request, "TRAFFIC_SHAPING_ENABLED", "Traffic shaping enabled at " + shapeRateMbps + "Mbps", "SYSTEM");
        
        return request;
    }

    // ==================== SECURITY MANAGEMENT ====================

    public ServiceProvisioningRequest configureSecurityPolicy(String msisdn, boolean authEnabled, boolean encryptionEnabled) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SECURITY_AUTHENTICATION)) {
            return null;
        }
        
        ServiceProvisioningRequest request = findByMsisdn(msisdn);
        if (request == null) {
            return null;
        }
        
        SecurityConfiguration secConfig = new SecurityConfiguration();
        secConfig.setAuthenticationEnabled(authEnabled);
        secConfig.setAuthenticationMethod(authEnabled ? "CERTIFICATE" : "NONE");
        secConfig.setEncryptionEnabled(encryptionEnabled);
        secConfig.setEncryptionAlgorithm(encryptionEnabled ? "AES-256-GCM" : "NONE");
        secConfig.setSecurityGroups(Arrays.asList("default"));
        secConfig.setFirewallEnabled(false);
        secConfig.setDlpEnabled(false);
        
        request.setSecurityConfig(secConfig);
        addEvent(request, "SECURITY_CONFIGURED", 
                 "Security: auth=" + authEnabled + ", encryption=" + encryptionEnabled, "SYSTEM");
        
        return request;
    }

    public ServiceProvisioningRequest addFirewallRule(String msisdn, FirewallRule rule) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SECURITY_FIREWALL_CONFIG)) {
            return null;
        }
        
        ServiceProvisioningRequest request = findByMsisdn(msisdn);
        if (request == null || request.getSecurityConfig() == null) {
            return null;
        }
        
        request.getSecurityConfig().setFirewallEnabled(true);
        if (request.getSecurityConfig().getFirewallRules() == null) {
            request.getSecurityConfig().setFirewallRules(new ArrayList<>());
        }
        request.getSecurityConfig().getFirewallRules().add(rule);
        
        addEvent(request, "FIREWALL_RULE_ADDED", "Firewall rule added: " + rule.getAction() + " " + rule.getProtocol(), "SYSTEM");
        return request;
    }

    // ==================== NETWORK SLICING ====================

    public ServiceProvisioningRequest configureNetworkSlice(String msisdn, String sliceId, String sliceType, int isolation) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_NETWORK_SLICING_NETWORK_SLICING)) {
            return null;
        }
        
        ServiceProvisioningRequest request = findByMsisdn(msisdn);
        if (request == null || request.getQosConfig() == null) {
            return null;
        }
        
        NetworkSliceConfig sliceConfig = new NetworkSliceConfig();
        sliceConfig.setSliceId(sliceId);
        sliceConfig.setSliceType(sliceType);
        sliceConfig.setIsolationLevel(isolation);
        sliceConfig.setAllowedServices(Arrays.asList("VOICE", "DATA", "SMS"));
        sliceConfig.setBandwidthAllocationPercent(100);
        sliceConfig.setLatencyTarget(20);
        
        request.getQosConfig().setSliceConfig(sliceConfig);
        addEvent(request, "NETWORK_SLICE_CONFIGURED", "Network slice " + sliceId + " configured with isolation level " + isolation, "SYSTEM");
        
        return request;
    }

    // ==================== ANALYTICS AND REPORTING ====================

    public Map<String, Object> getProvisioningMetrics() {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_REPORTING_PROVISIONING_REPORTS)) {
            return new HashMap<>();
        }
        
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalProvisioning", provisioningStore.size());
        metrics.put("activeServices", provisioningStore.values().stream()
                .filter(p -> p.getStatus() == ProvisioningStatus.ACTIVE).count());
        metrics.put("suspendedServices", provisioningStore.values().stream()
                .filter(p -> p.getStatus() == ProvisioningStatus.SUSPENDED).count());
        metrics.put("failedProvisioning", provisioningStore.values().stream()
                .filter(p -> p.getStatus() == ProvisioningStatus.FAILED).count());
        
        return metrics;
    }

    public UsageTracking getUsageAnalytics(String msisdn) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_REPORTING_USAGE_ANALYTICS)) {
            return null;
        }
        
        return usageStore.get(msisdn);
    }

    // ==================== HELPER METHODS ====================

    private ServiceProvisioningRequest findByMsisdn(String msisdn) {
        return provisioningStore.values().stream()
                .filter(p -> msisdn.equals(p.getPhoneNumber()))
                .findFirst()
                .orElse(null);
    }

    private void addEvent(ServiceProvisioningRequest request, String eventType, String description, String actor) {
        if (request.getEvents() == null) {
            request.setEvents(new ArrayList<>());
        }
        
        ProvisioningEvent event = new ProvisioningEvent();
        event.setEventId("EVT-" + UUID.randomUUID().toString());
        event.setEventType(eventType);
        event.setEventTime(System.currentTimeMillis());
        event.setStatus(request.getStatus().toString());
        event.setDescription(description);
        event.setActor(actor);
        
        request.getEvents().add(event);
    }

    private String generateImsi(String customerId) {
        // IMSI format: MCC (3) + MNC (2) + MSIN (9)
        return "310" + "41" + String.format("%09d", customerId.hashCode() % 1000000000);
    }

    private String generateIccid() {
        // ICCID format: 19-20 digits (IIN + ICC)
        return "89886" + String.format("%014d", System.nanoTime() % 100000000000000L);
    }

    private boolean validateImei(String imei) {
        return imei != null && imei.matches("^\\d{15}$");
    }

    private long calculateNextBillingDate(BillingFrequency frequency) {
        long now = System.currentTimeMillis();
        switch (frequency) {
            case DAILY: return now + (24 * 60 * 60 * 1000L);
            case WEEKLY: return now + (7L * 24 * 60 * 60 * 1000);
            case MONTHLY: return now + (30L * 24 * 60 * 60 * 1000);
            case QUARTERLY: return now + (90L * 24 * 60 * 60 * 1000);
            case SEMI_ANNUAL: return now + (180L * 24 * 60 * 60 * 1000);
            case ANNUAL: return now + (365L * 24 * 60 * 60 * 1000);
            default: return now + (30L * 24 * 60 * 60 * 1000);
        }
    }

    // Query methods
    public ServiceProvisioningRequest getProvisioningRequest(String requestId) {
        return provisioningStore.get(requestId);
    }

    public List<ServiceProvisioningRequest> getAllProvisioning() {
        return new ArrayList<>(provisioningStore.values());
    }

    public List<ServiceProvisioningRequest> searchProvisioning(String customerId) {
        return new ArrayList<>(provisioningStore.values().stream()
                .filter(p -> customerId.equals(p.getCustomerId()))
                .toList());
    }

    public void deleteProvisioning(String requestId) {
        provisioningStore.remove(requestId);
    }
}
