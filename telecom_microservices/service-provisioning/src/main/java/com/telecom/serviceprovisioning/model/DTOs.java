package com.telecom.serviceprovisioning.model;

import java.util.*;

/**
 * Comprehensive domain models for telecom service provisioning
 * Represents complete service lifecycle from activation to termination
 */

// ==================== BASE/WRAPPER TYPES ====================

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

// ==================== ENUMERATIONS ====================

enum ProvisioningStatus {
    PENDING, PROVISIONING, ACTIVE, SUSPENDED, TERMINATED, FAILED
}

enum ServiceType {
    VOICE, DATA, SMS, ROAMING, ADD_ON, SUPPLEMENTARY_SERVICE
}

enum NetworkType {
    NETWORK_5G, NETWORK_4G, NETWORK_LTE, NETWORK_3G
}

enum BillingFrequency {
    DAILY, WEEKLY, MONTHLY, QUARTERLY, SEMI_ANNUAL, ANNUAL
}

enum PriorityLevel {
    BRONZE, SILVER, GOLD, PLATINUM
}

enum AlertSeverity {
    INFO, WARNING, CRITICAL
}

// ==================== CORE PROVISIONING MODEL ====================

class ServiceProvisioningRequest {
    private String requestId;
    private String customerId;
    private String billingAccountId;
    private String phoneNumber;
    private String imei;
    private String iccid;
    private ServiceType serviceType;
    private String planId;
    private ProvisioningStatus status;
    private ServiceActivationDetails activationDetails;
    private NetworkConfigurationDetails networkConfig;
    private BillingConfigurationDetails billingConfig;
    private SecurityConfiguration securityConfig;
    private QualityOfServiceConfig qosConfig;
    private List<ProvisioningEvent> events;
    private long createdAt;
    private long updatedAt;
    private long completedAt;

    public ServiceProvisioningRequest() {}

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getBillingAccountId() { return billingAccountId; }
    public void setBillingAccountId(String billingAccountId) { this.billingAccountId = billingAccountId; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getImei() { return imei; }
    public void setImei(String imei) { this.imei = imei; }
    public String getIccid() { return iccid; }
    public void setIccid(String iccid) { this.iccid = iccid; }
    public ServiceType getServiceType() { return serviceType; }
    public void setServiceType(ServiceType serviceType) { this.serviceType = serviceType; }
    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }
    public ProvisioningStatus getStatus() { return status; }
    public void setStatus(ProvisioningStatus status) { this.status = status; }
    public ServiceActivationDetails getActivationDetails() { return activationDetails; }
    public void setActivationDetails(ServiceActivationDetails activationDetails) { this.activationDetails = activationDetails; }
    public NetworkConfigurationDetails getNetworkConfig() { return networkConfig; }
    public void setNetworkConfig(NetworkConfigurationDetails networkConfig) { this.networkConfig = networkConfig; }
    public BillingConfigurationDetails getBillingConfig() { return billingConfig; }
    public void setBillingConfig(BillingConfigurationDetails billingConfig) { this.billingConfig = billingConfig; }
    public SecurityConfiguration getSecurityConfig() { return securityConfig; }
    public void setSecurityConfig(SecurityConfiguration securityConfig) { this.securityConfig = securityConfig; }
    public QualityOfServiceConfig getQosConfig() { return qosConfig; }
    public void setQosConfig(QualityOfServiceConfig qosConfig) { this.qosConfig = qosConfig; }
    public List<ProvisioningEvent> getEvents() { return events; }
    public void setEvents(List<ProvisioningEvent> events) { this.events = events; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
    public long getCompletedAt() { return completedAt; }
    public void setCompletedAt(long completedAt) { this.completedAt = completedAt; }
}

// ==================== SERVICE ACTIVATION ====================

class ServiceActivationDetails {
    private String simCardId;
    private String simStatus;
    private String msisdn;
    private String imsi;
    private String activationCode;
    private boolean voiceEnabled;
    private boolean dataEnabled;
    private boolean smsEnabled;
    private long activationTime;

    public ServiceActivationDetails() {}

    public String getSimCardId() { return simCardId; }
    public void setSimCardId(String simCardId) { this.simCardId = simCardId; }
    public String getSimStatus() { return simStatus; }
    public void setSimStatus(String simStatus) { this.simStatus = simStatus; }
    public String getMsisdn() { return msisdn; }
    public void setMsisdn(String msisdn) { this.msisdn = msisdn; }
    public String getImsi() { return imsi; }
    public void setImsi(String imsi) { this.imsi = imsi; }
    public String getActivationCode() { return activationCode; }
    public void setActivationCode(String activationCode) { this.activationCode = activationCode; }
    public boolean isVoiceEnabled() { return voiceEnabled; }
    public void setVoiceEnabled(boolean voiceEnabled) { this.voiceEnabled = voiceEnabled; }
    public boolean isDataEnabled() { return dataEnabled; }
    public void setDataEnabled(boolean dataEnabled) { this.dataEnabled = dataEnabled; }
    public boolean isSmsEnabled() { return smsEnabled; }
    public void setSmsEnabled(boolean smsEnabled) { this.smsEnabled = smsEnabled; }
    public long getActivationTime() { return activationTime; }
    public void setActivationTime(long activationTime) { this.activationTime = activationTime; }
}

// ==================== NETWORK CONFIGURATION ====================

class NetworkConfigurationDetails {
    private NetworkType primaryNetwork;
    private List<NetworkType> supportedNetworks;
    private String primaryApn;
    private String primaryDns;
    private String secondaryDns;
    private String mccMnc;
    private String lac;
    private String cellId;
    private boolean roamingEnabled;
    private List<String> roamingCountries;
    private VpnConfiguration vpnConfig;

    public NetworkConfigurationDetails() {}

    public NetworkType getPrimaryNetwork() { return primaryNetwork; }
    public void setPrimaryNetwork(NetworkType primaryNetwork) { this.primaryNetwork = primaryNetwork; }
    public List<NetworkType> getSupportedNetworks() { return supportedNetworks; }
    public void setSupportedNetworks(List<NetworkType> supportedNetworks) { this.supportedNetworks = supportedNetworks; }
    public String getPrimaryApn() { return primaryApn; }
    public void setPrimaryApn(String primaryApn) { this.primaryApn = primaryApn; }
    public String getPrimaryDns() { return primaryDns; }
    public void setPrimaryDns(String primaryDns) { this.primaryDns = primaryDns; }
    public String getSecondaryDns() { return secondaryDns; }
    public void setSecondaryDns(String secondaryDns) { this.secondaryDns = secondaryDns; }
    public String getMccMnc() { return mccMnc; }
    public void setMccMnc(String mccMnc) { this.mccMnc = mccMnc; }
    public String getLac() { return lac; }
    public void setLac(String lac) { this.lac = lac; }
    public String getCellId() { return cellId; }
    public void setCellId(String cellId) { this.cellId = cellId; }
    public boolean isRoamingEnabled() { return roamingEnabled; }
    public void setRoamingEnabled(boolean roamingEnabled) { this.roamingEnabled = roamingEnabled; }
    public List<String> getRoamingCountries() { return roamingCountries; }
    public void setRoamingCountries(List<String> roamingCountries) { this.roamingCountries = roamingCountries; }
    public VpnConfiguration getVpnConfig() { return vpnConfig; }
    public void setVpnConfig(VpnConfiguration vpnConfig) { this.vpnConfig = vpnConfig; }
}

// ==================== VPN CONFIGURATION ====================

class VpnConfiguration {
    private boolean vpnEnabled;
    private String vpnType;
    private String vpnServerAddress;
    private int vpnPort;
    private String encryptionMethod;
    private String authenticationMethod;

    public VpnConfiguration() {}

    public boolean isVpnEnabled() { return vpnEnabled; }
    public void setVpnEnabled(boolean vpnEnabled) { this.vpnEnabled = vpnEnabled; }
    public String getVpnType() { return vpnType; }
    public void setVpnType(String vpnType) { this.vpnType = vpnType; }
    public String getVpnServerAddress() { return vpnServerAddress; }
    public void setVpnServerAddress(String vpnServerAddress) { this.vpnServerAddress = vpnServerAddress; }
    public int getVpnPort() { return vpnPort; }
    public void setVpnPort(int vpnPort) { this.vpnPort = vpnPort; }
    public String getEncryptionMethod() { return encryptionMethod; }
    public void setEncryptionMethod(String encryptionMethod) { this.encryptionMethod = encryptionMethod; }
    public String getAuthenticationMethod() { return authenticationMethod; }
    public void setAuthenticationMethod(String authenticationMethod) { this.authenticationMethod = authenticationMethod; }
}

// ==================== BILLING CONFIGURATION ====================

class BillingConfigurationDetails {
    private String billingAccountId;
    private double monthlyRecurringCharge;
    private String currency;
    private BillingFrequency billingCycle;
    private int gracePeriodDays;
    private double overageCostPerMb;
    private double overageCostPerMinute;
    private boolean throttlingEnabled;
    private double throttleThresholdMb;
    private long nextBillingDate;

    public BillingConfigurationDetails() {}

    public String getBillingAccountId() { return billingAccountId; }
    public void setBillingAccountId(String billingAccountId) { this.billingAccountId = billingAccountId; }
    public double getMonthlyRecurringCharge() { return monthlyRecurringCharge; }
    public void setMonthlyRecurringCharge(double monthlyRecurringCharge) { this.monthlyRecurringCharge = monthlyRecurringCharge; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public BillingFrequency getBillingCycle() { return billingCycle; }
    public void setBillingCycle(BillingFrequency billingCycle) { this.billingCycle = billingCycle; }
    public int getGracePeriodDays() { return gracePeriodDays; }
    public void setGracePeriodDays(int gracePeriodDays) { this.gracePeriodDays = gracePeriodDays; }
    public double getOverageCostPerMb() { return overageCostPerMb; }
    public void setOverageCostPerMb(double overageCostPerMb) { this.overageCostPerMb = overageCostPerMb; }
    public double getOverageCostPerMinute() { return overageCostPerMinute; }
    public void setOverageCostPerMinute(double overageCostPerMinute) { this.overageCostPerMinute = overageCostPerMinute; }
    public boolean isThrottlingEnabled() { return throttlingEnabled; }
    public void setThrottlingEnabled(boolean throttlingEnabled) { this.throttlingEnabled = throttlingEnabled; }
    public double getThrottleThresholdMb() { return throttleThresholdMb; }
    public void setThrottleThresholdMb(double throttleThresholdMb) { this.throttleThresholdMb = throttleThresholdMb; }
    public long getNextBillingDate() { return nextBillingDate; }
    public void setNextBillingDate(long nextBillingDate) { this.nextBillingDate = nextBillingDate; }
}

// ==================== SECURITY CONFIGURATION ====================

class SecurityConfiguration {
    private boolean authenticationEnabled;
    private String authenticationMethod;
    private boolean encryptionEnabled;
    private String encryptionAlgorithm;
    private List<String> securityGroups;
    private boolean firewallEnabled;
    private List<FirewallRule> firewallRules;
    private boolean dlpEnabled;
    private List<String> dlpPolicies;

    public SecurityConfiguration() {}

    public boolean isAuthenticationEnabled() { return authenticationEnabled; }
    public void setAuthenticationEnabled(boolean authenticationEnabled) { this.authenticationEnabled = authenticationEnabled; }
    public String getAuthenticationMethod() { return authenticationMethod; }
    public void setAuthenticationMethod(String authenticationMethod) { this.authenticationMethod = authenticationMethod; }
    public boolean isEncryptionEnabled() { return encryptionEnabled; }
    public void setEncryptionEnabled(boolean encryptionEnabled) { this.encryptionEnabled = encryptionEnabled; }
    public String getEncryptionAlgorithm() { return encryptionAlgorithm; }
    public void setEncryptionAlgorithm(String encryptionAlgorithm) { this.encryptionAlgorithm = encryptionAlgorithm; }
    public List<String> getSecurityGroups() { return securityGroups; }
    public void setSecurityGroups(List<String> securityGroups) { this.securityGroups = securityGroups; }
    public boolean isFirewallEnabled() { return firewallEnabled; }
    public void setFirewallEnabled(boolean firewallEnabled) { this.firewallEnabled = firewallEnabled; }
    public List<FirewallRule> getFirewallRules() { return firewallRules; }
    public void setFirewallRules(List<FirewallRule> firewallRules) { this.firewallRules = firewallRules; }
    public boolean isDlpEnabled() { return dlpEnabled; }
    public void setDlpEnabled(boolean dlpEnabled) { this.dlpEnabled = dlpEnabled; }
    public List<String> getDlpPolicies() { return dlpPolicies; }
    public void setDlpPolicies(List<String> dlpPolicies) { this.dlpPolicies = dlpPolicies; }
}

// ==================== FIREWALL RULE ====================

class FirewallRule {
    private String ruleId;
    private String direction;
    private String protocol;
    private String sourceIp;
    private String destinationIp;
    private int sourcePort;
    private int destinationPort;
    private String action;

    public FirewallRule() {}

    public String getRuleId() { return ruleId; }
    public void setRuleId(String ruleId) { this.ruleId = ruleId; }
    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }
    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }
    public String getSourceIp() { return sourceIp; }
    public void setSourceIp(String sourceIp) { this.sourceIp = sourceIp; }
    public String getDestinationIp() { return destinationIp; }
    public void setDestinationIp(String destinationIp) { this.destinationIp = destinationIp; }
    public int getSourcePort() { return sourcePort; }
    public void setSourcePort(int sourcePort) { this.sourcePort = sourcePort; }
    public int getDestinationPort() { return destinationPort; }
    public void setDestinationPort(int destinationPort) { this.destinationPort = destinationPort; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
}

// ==================== QUALITY OF SERVICE ====================

class QualityOfServiceConfig {
    private PriorityLevel priorityLevel;
    private int bandwidthMbps;
    private int maximumLatencyMs;
    private int packetLossPercentage;
    private boolean trafficShapingEnabled;
    private NetworkSliceConfig sliceConfig;

    public QualityOfServiceConfig() {}

    public PriorityLevel getPriorityLevel() { return priorityLevel; }
    public void setPriorityLevel(PriorityLevel priorityLevel) { this.priorityLevel = priorityLevel; }
    public int getBandwidthMbps() { return bandwidthMbps; }
    public void setBandwidthMbps(int bandwidthMbps) { this.bandwidthMbps = bandwidthMbps; }
    public int getMaximumLatencyMs() { return maximumLatencyMs; }
    public void setMaximumLatencyMs(int maximumLatencyMs) { this.maximumLatencyMs = maximumLatencyMs; }
    public int getPacketLossPercentage() { return packetLossPercentage; }
    public void setPacketLossPercentage(int packetLossPercentage) { this.packetLossPercentage = packetLossPercentage; }
    public boolean isTrafficShapingEnabled() { return trafficShapingEnabled; }
    public void setTrafficShapingEnabled(boolean trafficShapingEnabled) { this.trafficShapingEnabled = trafficShapingEnabled; }
    public NetworkSliceConfig getSliceConfig() { return sliceConfig; }
    public void setSliceConfig(NetworkSliceConfig sliceConfig) { this.sliceConfig = sliceConfig; }
}

// ==================== NETWORK SLICE CONFIG ====================

class NetworkSliceConfig {
    private String sliceId;
    private String sliceType;
    private int isolationLevel;
    private List<String> allowedServices;
    private int bandwidthAllocationPercent;
    private int latencyTarget;

    public NetworkSliceConfig() {}

    public String getSliceId() { return sliceId; }
    public void setSliceId(String sliceId) { this.sliceId = sliceId; }
    public String getSliceType() { return sliceType; }
    public void setSliceType(String sliceType) { this.sliceType = sliceType; }
    public int getIsolationLevel() { return isolationLevel; }
    public void setIsolationLevel(int isolationLevel) { this.isolationLevel = isolationLevel; }
    public List<String> getAllowedServices() { return allowedServices; }
    public void setAllowedServices(List<String> allowedServices) { this.allowedServices = allowedServices; }
    public int getBandwidthAllocationPercent() { return bandwidthAllocationPercent; }
    public void setBandwidthAllocationPercent(int bandwidthAllocationPercent) { this.bandwidthAllocationPercent = bandwidthAllocationPercent; }
    public int getLatencyTarget() { return latencyTarget; }
    public void setLatencyTarget(int latencyTarget) { this.latencyTarget = latencyTarget; }
}

// ==================== USAGE TRACKING ====================

class UsageTracking {
    private String trackingId;
    private String msisdn;
    private double dataUsedMb;
    private double dataAllowedMb;
    private int voiceUsedMinutes;
    private int voiceAllowedMinutes;
    private int smsUsed;
    private int smsAllowed;
    private double roamingDataUsedMb;
    private long trackingPeriodStart;
    private long trackingPeriodEnd;

    public UsageTracking() {}

    public String getTrackingId() { return trackingId; }
    public void setTrackingId(String trackingId) { this.trackingId = trackingId; }
    public String getMsisdn() { return msisdn; }
    public void setMsisdn(String msisdn) { this.msisdn = msisdn; }
    public double getDataUsedMb() { return dataUsedMb; }
    public void setDataUsedMb(double dataUsedMb) { this.dataUsedMb = dataUsedMb; }
    public double getDataAllowedMb() { return dataAllowedMb; }
    public void setDataAllowedMb(double dataAllowedMb) { this.dataAllowedMb = dataAllowedMb; }
    public int getVoiceUsedMinutes() { return voiceUsedMinutes; }
    public void setVoiceUsedMinutes(int voiceUsedMinutes) { this.voiceUsedMinutes = voiceUsedMinutes; }
    public int getVoiceAllowedMinutes() { return voiceAllowedMinutes; }
    public void setVoiceAllowedMinutes(int voiceAllowedMinutes) { this.voiceAllowedMinutes = voiceAllowedMinutes; }
    public int getSmsUsed() { return smsUsed; }
    public void setSmsUsed(int smsUsed) { this.smsUsed = smsUsed; }
    public int getSmsAllowed() { return smsAllowed; }
    public void setSmsAllowed(int smsAllowed) { this.smsAllowed = smsAllowed; }
    public double getRoamingDataUsedMb() { return roamingDataUsedMb; }
    public void setRoamingDataUsedMb(double roamingDataUsedMb) { this.roamingDataUsedMb = roamingDataUsedMb; }
    public long getTrackingPeriodStart() { return trackingPeriodStart; }
    public void setTrackingPeriodStart(long trackingPeriodStart) { this.trackingPeriodStart = trackingPeriodStart; }
    public long getTrackingPeriodEnd() { return trackingPeriodEnd; }
    public void setTrackingPeriodEnd(long trackingPeriodEnd) { this.trackingPeriodEnd = trackingPeriodEnd; }
}

// ==================== SERVICE MODIFICATION ====================

class ServiceModificationRequest {
    private String modificationId;
    private String msisdn;
    private String modificationType;
    private String currentPlanId;
    private String newPlanId;
    private List<String> addOnsToAdd;
    private List<String> addOnsToRemove;
    private ProvisioningStatus status;
    private long requestedDate;
    private long effectiveDate;

    public ServiceModificationRequest() {}

    public String getModificationId() { return modificationId; }
    public void setModificationId(String modificationId) { this.modificationId = modificationId; }
    public String getMsisdn() { return msisdn; }
    public void setMsisdn(String msisdn) { this.msisdn = msisdn; }
    public String getModificationType() { return modificationType; }
    public void setModificationType(String modificationType) { this.modificationType = modificationType; }
    public String getCurrentPlanId() { return currentPlanId; }
    public void setCurrentPlanId(String currentPlanId) { this.currentPlanId = currentPlanId; }
    public String getNewPlanId() { return newPlanId; }
    public void setNewPlanId(String newPlanId) { this.newPlanId = newPlanId; }
    public List<String> getAddOnsToAdd() { return addOnsToAdd; }
    public void setAddOnsToAdd(List<String> addOnsToAdd) { this.addOnsToAdd = addOnsToAdd; }
    public List<String> getAddOnsToRemove() { return addOnsToRemove; }
    public void setAddOnsToRemove(List<String> addOnsToRemove) { this.addOnsToRemove = addOnsToRemove; }
    public ProvisioningStatus getStatus() { return status; }
    public void setStatus(ProvisioningStatus status) { this.status = status; }
    public long getRequestedDate() { return requestedDate; }
    public void setRequestedDate(long requestedDate) { this.requestedDate = requestedDate; }
    public long getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(long effectiveDate) { this.effectiveDate = effectiveDate; }
}

// ==================== PROVISIONING EVENT ====================

class ProvisioningEvent {
    private String eventId;
    private String eventType;
    private long eventTime;
    private String status;
    private String description;
    private Map<String, String> details;
    private String actor;

    public ProvisioningEvent() {}

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public long getEventTime() { return eventTime; }
    public void setEventTime(long eventTime) { this.eventTime = eventTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Map<String, String> getDetails() { return details; }
    public void setDetails(Map<String, String> details) { this.details = details; }
    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }
}

// ==================== PROVISIONING ALERT ====================

class ProvisioningAlert {
    private String alertId;
    private String msisdn;
    private AlertSeverity severity;
    private String alertType;
    private String message;
    private long alertTime;
    private boolean acknowledged;
    private long acknowledgedTime;

    public ProvisioningAlert() {}

    public String getAlertId() { return alertId; }
    public void setAlertId(String alertId) { this.alertId = alertId; }
    public String getMsisdn() { return msisdn; }
    public void setMsisdn(String msisdn) { this.msisdn = msisdn; }
    public AlertSeverity getSeverity() { return severity; }
    public void setSeverity(AlertSeverity severity) { this.severity = severity; }
    public String getAlertType() { return alertType; }
    public void setAlertType(String alertType) { this.alertType = alertType; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public long getAlertTime() { return alertTime; }
    public void setAlertTime(long alertTime) { this.alertTime = alertTime; }
    public boolean isAcknowledged() { return acknowledged; }
    public void setAcknowledged(boolean acknowledged) { this.acknowledged = acknowledged; }
    public long getAcknowledgedTime() { return acknowledgedTime; }
    public void setAcknowledgedTime(long acknowledgedTime) { this.acknowledgedTime = acknowledgedTime; }
}
