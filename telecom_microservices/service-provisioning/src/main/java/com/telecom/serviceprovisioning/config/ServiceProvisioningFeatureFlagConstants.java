package com.telecom.serviceprovisioning.config;

/**
 * Centralized feature flag constants for Service Provisioning service.
 * All flags declared here prevent magic strings throughout the codebase.
 * Flags are read from feature-flags.json via FeatureFlagReader.
 *
 * Naming convention: provisioning_<category>_<feature>
 */
public class ServiceProvisioningFeatureFlagConstants {

    // ==================== SERVICE ACTIVATION (8 flags) ====================
    public static final String PROVISIONING_ENABLE_SIM_ACTIVATION = "provisioning_enable_sim_activation";
    public static final String PROVISIONING_ENABLE_DEVICE_ACTIVATION = "provisioning_enable_device_activation";
    public static final String PROVISIONING_ENABLE_NETWORK_CONFIG = "provisioning_enable_network_config";
    public static final String PROVISIONING_ENABLE_MSISDN_ALLOCATION = "provisioning_enable_msisdn_allocation";
    public static final String PROVISIONING_ENABLE_IMEI_ASSIGNMENT = "provisioning_enable_imei_assignment";
    public static final String PROVISIONING_ENABLE_APN_CONFIG = "provisioning_enable_apn_config";
    public static final String PROVISIONING_ENABLE_5G_NETWORK = "provisioning_enable_5g_network";
    public static final String PROVISIONING_ENABLE_VPN_CONFIG = "provisioning_enable_vpn_config";

    // ==================== SERVICE CONFIGURATION (7 flags) ====================
    public static final String PROVISIONING_ENABLE_VOICE_CONFIG = "provisioning_enable_voice_config";
    public static final String PROVISIONING_ENABLE_DATA_CONFIG = "provisioning_enable_data_config";
    public static final String PROVISIONING_ENABLE_SMS_CONFIG = "provisioning_enable_sms_config";
    public static final String PROVISIONING_ENABLE_ROAMING_CONFIG = "provisioning_enable_roaming_config";
    public static final String PROVISIONING_ENABLE_CALL_FORWARDING = "provisioning_enable_call_forwarding";
    public static final String PROVISIONING_ENABLE_VOICEMAIL = "provisioning_enable_voicemail";
    public static final String PROVISIONING_ENABLE_SUPPLEMENTARY_SERVICES = "provisioning_enable_supplementary_services";

    // ==================== BILLING INTEGRATION (6 flags) ====================
    public static final String PROVISIONING_ENABLE_BILLING_SYNC = "provisioning_enable_billing_sync";
    public static final String PROVISIONING_ENABLE_RECURRING_CHARGES = "provisioning_enable_recurring_charges";
    public static final String PROVISIONING_ENABLE_USAGE_TRACKING = "provisioning_enable_usage_tracking";
    public static final String PROVISIONING_ENABLE_QUOTA_MANAGEMENT = "provisioning_enable_quota_management";
    public static final String PROVISIONING_ENABLE_THROTTLING = "provisioning_enable_throttling";
    public static final String PROVISIONING_ENABLE_OVERAGE_CHARGES = "provisioning_enable_overage_charges";

    // ==================== SERVICE MANAGEMENT (6 flags) ====================
    public static final String PROVISIONING_ENABLE_SERVICE_UPGRADE = "provisioning_enable_service_upgrade";
    public static final String PROVISIONING_ENABLE_SERVICE_DOWNGRADE = "provisioning_enable_service_downgrade";
    public static final String PROVISIONING_ENABLE_PLAN_CHANGE = "provisioning_enable_plan_change";
    public static final String PROVISIONING_ENABLE_ADD_ON_MANAGEMENT = "provisioning_enable_add_on_management";
    public static final String PROVISIONING_ENABLE_SERVICE_SUSPENSION = "provisioning_enable_service_suspension";
    public static final String PROVISIONING_ENABLE_SERVICE_TERMINATION = "provisioning_enable_service_termination";

    // ==================== QUALITY OF SERVICE (5 flags) ====================
    public static final String PROVISIONING_ENABLE_QOS_MANAGEMENT = "provisioning_enable_qos_management";
    public static final String PROVISIONING_ENABLE_BANDWIDTH_ALLOCATION = "provisioning_enable_bandwidth_allocation";
    public static final String PROVISIONING_ENABLE_PRIORITY_LEVELS = "provisioning_enable_priority_levels";
    public static final String PROVISIONING_ENABLE_LATENCY_CONTROL = "provisioning_enable_latency_control";
    public static final String PROVISIONING_ENABLE_TRAFFIC_SHAPING = "provisioning_enable_traffic_shaping";

    // ==================== AUTHENTICATION & SECURITY (5 flags) ====================
    public static final String PROVISIONING_ENABLE_AUTHENTICATION = "provisioning_enable_authentication";
    public static final String PROVISIONING_ENABLE_ENCRYPTION = "provisioning_enable_encryption";
    public static final String PROVISIONING_ENABLE_SECURITY_GROUPS = "provisioning_enable_security_groups";
    public static final String PROVISIONING_ENABLE_FIREWALL_CONFIG = "provisioning_enable_firewall_config";
    public static final String PROVISIONING_ENABLE_DLP = "provisioning_enable_dlp";

    // ==================== MULTI-CARRIER SUPPORT (4 flags) ====================
    public static final String PROVISIONING_ENABLE_MULTI_CARRIER = "provisioning_enable_multi_carrier";
    public static final String PROVISIONING_ENABLE_CARRIER_ROUTING = "provisioning_enable_carrier_routing";
    public static final String PROVISIONING_ENABLE_INTER_CARRIER_COMMUNICATION = "provisioning_enable_inter_carrier_communication";
    public static final String PROVISIONING_ENABLE_CARRIER_SPECIFIC_CONFIG = "provisioning_enable_carrier_specific_config";

    // ==================== AUTOMATION & ORCHESTRATION (4 flags) ====================
    public static final String PROVISIONING_ENABLE_WORKFLOW_AUTOMATION = "provisioning_enable_workflow_automation";
    public static final String PROVISIONING_ENABLE_ORCHESTRATION = "provisioning_enable_orchestration";
    public static final String PROVISIONING_ENABLE_AUTO_SCALING = "provisioning_enable_auto_scaling";
    public static final String PROVISIONING_ENABLE_SMART_PROVISIONING = "provisioning_enable_smart_provisioning";

    // ==================== REPORTING & ANALYTICS (4 flags) ====================
    public static final String PROVISIONING_ENABLE_USAGE_ANALYTICS = "provisioning_enable_usage_analytics";
    public static final String PROVISIONING_ENABLE_PERFORMANCE_METRICS = "provisioning_enable_performance_metrics";
    public static final String PROVISIONING_ENABLE_AUDIT_LOGS = "provisioning_enable_audit_logs";
    public static final String PROVISIONING_ENABLE_PROVISIONING_REPORTS = "provisioning_enable_provisioning_reports";

    // ==================== NOTIFICATIONS & ALERTS (3 flags) ====================
    public static final String PROVISIONING_ENABLE_NOTIFICATIONS = "provisioning_enable_notifications";
    public static final String PROVISIONING_ENABLE_ALERTS = "provisioning_enable_alerts";
    public static final String PROVISIONING_ENABLE_SERVICE_STATUS_UPDATES = "provisioning_enable_service_status_updates";

    // ==================== NETWORK SLICING (3 flags) ====================
    public static final String PROVISIONING_ENABLE_NETWORK_SLICING = "provisioning_enable_network_slicing";
    public static final String PROVISIONING_ENABLE_SLICE_MANAGEMENT = "provisioning_enable_slice_management";
    public static final String PROVISIONING_ENABLE_SLICE_ISOLATION = "provisioning_enable_slice_isolation";

    // Total: 56 feature flags across 13 categories
}
