package com.telecom.serviceprovisioning.controller;

import com.telecom.serviceprovisioning.model.*;
import com.telecom.serviceprovisioning.service.ServiceProvisioningService;
import com.telecom.serviceprovisioning.util.ServiceProvisioningFeatureFlagConstants;
import com.telecom.serviceprovisioning.util.FeatureFlagReader;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;

/**
 * REST Controller for Service Provisioning endpoints
 * All endpoints are protected with feature flags for granular control
 */
@RestController
@RequestMapping("/api/service-provisioning")
public class ServiceProvisioningController {

    @Autowired
    private ServiceProvisioningService service;
    
    @Autowired
    private FeatureFlagReader featureFlagReader;

    // ==================== SERVICE ACTIVATION ENDPOINTS ====================

    @PostMapping("/activate/sim")
    public ResponseEntity<?> activateSim(@RequestBody ServiceProvisioningRequest request) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SERVICE_ACTIVATION_SIM_ACTIVATION)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: SIM Activation");
        }
        ServiceProvisioningRequest result = service.initiateSimActivation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/activate/device")
    public ResponseEntity<?> activateDevice(@RequestBody ServiceProvisioningRequest request) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SERVICE_ACTIVATION_DEVICE_ACTIVATION)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: Device Activation");
        }
        ServiceProvisioningRequest result = service.initiateDeviceActivation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/allocate-msisdn")
    public ResponseEntity<?> allocateMsisdn(@RequestParam String customerId, @RequestParam(required = false) String preferred) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SERVICE_ACTIVATION_MSISDN_ALLOCATION)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: MSISDN Allocation");
        }
        ServiceProvisioningRequest result = service.allocateMsisdn(customerId, preferred);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // ==================== NETWORK CONFIGURATION ENDPOINTS ====================

    @PostMapping("/{msisdn}/network/configure")
    public ResponseEntity<?> configureNetwork(@PathVariable String msisdn, 
                                              @RequestParam NetworkType network,
                                              @RequestParam(required = false) String apn) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SERVICE_ACTIVATION_NETWORK_CONFIG)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: Network Configuration");
        }
        ServiceProvisioningRequest result = service.configureNetworkSettings(msisdn, network, apn);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{msisdn}/network/vpn")
    public ResponseEntity<?> configureVpn(@PathVariable String msisdn,
                                          @RequestParam String vpnType,
                                          @RequestParam String serverAddress) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SERVICE_ACTIVATION_VPN_CONFIG)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: VPN Configuration");
        }
        ServiceProvisioningRequest result = service.configureVpn(msisdn, vpnType, serverAddress);
        return ResponseEntity.ok(result);
    }

    // ==================== SERVICE CONFIGURATION ENDPOINTS ====================

    @PostMapping("/{msisdn}/services/voice")
    public ResponseEntity<?> configureVoice(@PathVariable String msisdn,
                                            @RequestParam boolean enabled,
                                            @RequestParam(required = false) String type) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SERVICE_CONFIG_VOICE_CONFIG)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: Voice Configuration");
        }
        ServiceProvisioningRequest result = service.configureVoiceService(msisdn, enabled, type != null ? type : "4G_VOICE");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{msisdn}/services/data")
    public ResponseEntity<?> configureData(@PathVariable String msisdn,
                                           @RequestParam boolean enabled,
                                           @RequestParam(required = false, defaultValue = "10240") int allowanceMb) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SERVICE_CONFIG_DATA_CONFIG)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: Data Configuration");
        }
        ServiceProvisioningRequest result = service.configureDataService(msisdn, enabled, allowanceMb);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{msisdn}/services/roaming")
    public ResponseEntity<?> enableRoaming(@PathVariable String msisdn,
                                           @RequestBody List<String> countries) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SERVICE_CONFIG_ROAMING_CONFIG)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: Roaming Configuration");
        }
        ServiceProvisioningRequest result = service.enableRoaming(msisdn, countries);
        return ResponseEntity.ok(result);
    }

    // ==================== BILLING ENDPOINTS ====================

    @PostMapping("/{msisdn}/billing/setup")
    public ResponseEntity<?> setupBilling(@PathVariable String msisdn,
                                          @RequestParam String billingAccountId,
                                          @RequestParam double monthlyCharge,
                                          @RequestParam(required = false) BillingFrequency frequency) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_BILLING_INTEGRATION_BILLING_SYNC)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: Billing Setup");
        }
        BillingFrequency freq = frequency != null ? frequency : BillingFrequency.MONTHLY;
        ServiceProvisioningRequest result = service.setupBillingConfiguration(msisdn, billingAccountId, monthlyCharge, freq);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{msisdn}/billing/usage-tracking")
    public ResponseEntity<?> setupUsageTracking(@PathVariable String msisdn,
                                                @RequestParam double dataAllowanceMb,
                                                @RequestParam(defaultValue = "500") int voiceMinutes,
                                                @RequestParam(defaultValue = "200") int smsAllowance) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_BILLING_INTEGRATION_USAGE_TRACKING)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: Usage Tracking");
        }
        boolean result = service.enableUsageTracking(msisdn, dataAllowanceMb, voiceMinutes, smsAllowance);
        return result ? ResponseEntity.ok().body("Usage tracking enabled") : ResponseEntity.badRequest().build();
    }

    @PostMapping("/{msisdn}/billing/throttling")
    public ResponseEntity<?> enableThrottling(@PathVariable String msisdn,
                                              @RequestParam double thresholdMb) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_BILLING_INTEGRATION_THROTTLING)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: Throttling");
        }
        boolean result = service.enableThrottling(msisdn, thresholdMb);
        return result ? ResponseEntity.ok().body("Throttling enabled") : ResponseEntity.badRequest().build();
    }

    // ==================== SERVICE MANAGEMENT ENDPOINTS ====================

    @PostMapping("/{msisdn}/services/upgrade")
    public ResponseEntity<?> upgradeService(@PathVariable String msisdn,
                                            @RequestParam String newPlanId) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SERVICE_MANAGEMENT_SERVICE_UPGRADE)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: Service Upgrade");
        }
        ServiceModificationRequest result = service.requestServiceUpgrade(msisdn, newPlanId);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/{msisdn}/services/downgrade")
    public ResponseEntity<?> downgradeService(@PathVariable String msisdn,
                                              @RequestParam String newPlanId) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SERVICE_MANAGEMENT_SERVICE_DOWNGRADE)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: Service Downgrade");
        }
        ServiceModificationRequest result = service.requestServiceDowngrade(msisdn, newPlanId);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/{msisdn}/services/add-on")
    public ResponseEntity<?> addServiceAddon(@PathVariable String msisdn,
                                             @RequestParam String addonId) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SERVICE_MANAGEMENT_ADD_ON_MANAGEMENT)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: Add-on Management");
        }
        ServiceModificationRequest result = service.addServiceAddon(msisdn, addonId);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/{msisdn}/services/suspend")
    public ResponseEntity<?> suspendService(@PathVariable String msisdn,
                                            @RequestParam(required = false) String reason) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SERVICE_MANAGEMENT_SERVICE_SUSPENSION)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: Service Suspension");
        }
        ServiceProvisioningRequest result = service.suspendService(msisdn, reason != null ? reason : "Admin suspension");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{msisdn}/services/terminate")
    public ResponseEntity<?> terminateService(@PathVariable String msisdn,
                                              @RequestParam(required = false) String reason) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SERVICE_MANAGEMENT_SERVICE_TERMINATION)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: Service Termination");
        }
        ServiceProvisioningRequest result = service.terminateService(msisdn, reason != null ? reason : "Customer requested");
        return ResponseEntity.ok(result);
    }

    // ==================== QOS ENDPOINTS ====================

    @PostMapping("/{msisdn}/qos/configure")
    public ResponseEntity<?> configureQos(@PathVariable String msisdn,
                                          @RequestParam PriorityLevel priority,
                                          @RequestParam(defaultValue = "100") int bandwidthMbps,
                                          @RequestParam(defaultValue = "50") int maxLatencyMs) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_QOS_QOS_MANAGEMENT)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: QoS Management");
        }
        ServiceProvisioningRequest result = service.configureQos(msisdn, priority, bandwidthMbps, maxLatencyMs);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{msisdn}/qos/bandwidth")
    public ResponseEntity<?> allocateBandwidth(@PathVariable String msisdn,
                                               @RequestParam int mbps) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_QOS_BANDWIDTH_ALLOCATION)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: Bandwidth Allocation");
        }
        ServiceProvisioningRequest result = service.allocateBandwidth(msisdn, mbps);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{msisdn}/qos/traffic-shaping")
    public ResponseEntity<?> enableTrafficShaping(@PathVariable String msisdn,
                                                  @RequestParam double rateMbps) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_QOS_TRAFFIC_SHAPING)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: Traffic Shaping");
        }
        ServiceProvisioningRequest result = service.enableTrafficShaping(msisdn, rateMbps);
        return ResponseEntity.ok(result);
    }

    // ==================== SECURITY ENDPOINTS ====================

    @PostMapping("/{msisdn}/security/configure")
    public ResponseEntity<?> configureSecurity(@PathVariable String msisdn,
                                               @RequestParam(defaultValue = "true") boolean authEnabled,
                                               @RequestParam(defaultValue = "true") boolean encryptionEnabled) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SECURITY_AUTHENTICATION)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: Security Configuration");
        }
        ServiceProvisioningRequest result = service.configureSecurityPolicy(msisdn, authEnabled, encryptionEnabled);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{msisdn}/security/firewall-rule")
    public ResponseEntity<?> addFirewallRule(@PathVariable String msisdn,
                                             @RequestBody FirewallRule rule) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SECURITY_FIREWALL_CONFIG)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: Firewall Configuration");
        }
        ServiceProvisioningRequest result = service.addFirewallRule(msisdn, rule);
        return ResponseEntity.ok(result);
    }

    // ==================== NETWORK SLICING ENDPOINTS ====================

    @PostMapping("/{msisdn}/slices/configure")
    public ResponseEntity<?> configureNetworkSlice(@PathVariable String msisdn,
                                                   @RequestParam String sliceId,
                                                   @RequestParam String sliceType,
                                                   @RequestParam(defaultValue = "3") int isolation) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_NETWORK_SLICING_NETWORK_SLICING)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: Network Slicing");
        }
        ServiceProvisioningRequest result = service.configureNetworkSlice(msisdn, sliceId, sliceType, isolation);
        return ResponseEntity.ok(result);
    }

    // ==================== ANALYTICS ENDPOINTS ====================

    @GetMapping("/metrics")
    public ResponseEntity<?> getMetrics() {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_REPORTING_PROVISIONING_REPORTS)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: Provisioning Reports");
        }
        Map<String, Object> metrics = service.getProvisioningMetrics();
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/{msisdn}/usage")
    public ResponseEntity<?> getUsageAnalytics(@PathVariable String msisdn) {
        if (!featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_REPORTING_USAGE_ANALYTICS)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: Usage Analytics");
        }
        UsageTracking usage = service.getUsageAnalytics(msisdn);
        return usage != null ? ResponseEntity.ok(usage) : ResponseEntity.notFound().build();
    }

    // ==================== SYSTEM ENDPOINTS ====================

    @GetMapping("/provisioning/{requestId}")
    public ResponseEntity<?> getProvisioning(@PathVariable String requestId) {
        ServiceProvisioningRequest request = service.getProvisioningRequest(requestId);
        return request != null ? ResponseEntity.ok(request) : ResponseEntity.notFound().build();
    }

    @GetMapping("/provisioning/customer/{customerId}")
    public ResponseEntity<?> getCustomerProvisioning(@PathVariable String customerId) {
        List<ServiceProvisioningRequest> requests = service.searchProvisioning(customerId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("service", "service-provisioning");
        health.put("status", "UP");
        health.put("totalProvisioning", service.getAllProvisioning().size());
        return ResponseEntity.ok(health);
    }

    @GetMapping("/features")
    public ResponseEntity<Map<String, Object>> getEnabledFeatures() {
        Map<String, Object> features = new HashMap<>();
        features.put("simActivation", featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SERVICE_ACTIVATION_SIM_ACTIVATION));
        features.put("deviceActivation", featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SERVICE_ACTIVATION_DEVICE_ACTIVATION));
        features.put("msisdnAllocation", featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SERVICE_ACTIVATION_MSISDN_ALLOCATION));
        features.put("networkConfig", featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SERVICE_ACTIVATION_NETWORK_CONFIG));
        features.put("vpnConfig", featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SERVICE_ACTIVATION_VPN_CONFIG));
        features.put("voiceConfig", featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SERVICE_CONFIG_VOICE_CONFIG));
        features.put("dataConfig", featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SERVICE_CONFIG_DATA_CONFIG));
        features.put("roamingConfig", featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SERVICE_CONFIG_ROAMING_CONFIG));
        features.put("billingSync", featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_BILLING_INTEGRATION_BILLING_SYNC));
        features.put("usageTracking", featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_BILLING_INTEGRATION_USAGE_TRACKING));
        features.put("throttling", featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_BILLING_INTEGRATION_THROTTLING));
        features.put("qosManagement", featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_QOS_QOS_MANAGEMENT));
        features.put("bandwidthAllocation", featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_QOS_BANDWIDTH_ALLOCATION));
        features.put("trafficShaping", featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_QOS_TRAFFIC_SHAPING));
        features.put("security", featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SECURITY_AUTHENTICATION));
        features.put("firewallConfig", featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_SECURITY_FIREWALL_CONFIG));
        features.put("networkSlicing", featureFlagReader.isFeatureEnabled(ServiceProvisioningFeatureFlagConstants.PROVISIONING_NETWORK_SLICING_NETWORK_SLICING));
        return ResponseEntity.ok(features);
    }
}
