package com.telecom.notificationservice.constants;

public final class NotificationFeatureFlagConstants {

    // Orchestration / routing
    public static final String ENABLE_ROUTING = "notification_routing_enable";
    public static final String ENABLE_FAILOVER = "notification_failover_enable";
    public static final String ENABLE_MULTI_CHANNEL = "notification_multichannel_enable";

    // Channels
    public static final String ENABLE_SMS = "notification_channel_sms_enable";
    public static final String ENABLE_EMAIL = "notification_channel_email_enable";
    public static final String ENABLE_PUSH = "notification_channel_push_enable";
    public static final String ENABLE_IVR = "notification_channel_ivr_enable";

    // Compliance
    public static final String ENABLE_DND_CHECKS = "notification_dnd_checks_enable";
    public static final String ENABLE_CONSENT_CHECKS = "notification_consent_checks_enable";
    public static final String ENABLE_WHITELIST_SENDER_IDS = "notification_senderid_whitelist_enable";
    public static final String ENABLE_TEMPLATES = "notification_templates_enable";

    // Throttling / rate limits
    public static final String ENABLE_GLOBAL_RATE_LIMIT = "notification_rate_global_enable";
    public static final String ENABLE_PER_MSISDN_RATE_LIMIT = "notification_rate_msisdn_enable";
    public static final String ENABLE_CAMPAIGN_WINDOW = "notification_campaign_window_enable";

    // Reliability
    public static final String ENABLE_RETRY_POLICY = "notification_retry_enable";
    public static final String ENABLE_IDEMPOTENCY = "notification_idempotency_enable";
    public static final String ENABLE_AUDIT_LOGS = "notification_audit_enable";

    // Content
    public static final String ENABLE_UNICODE_SMS = "notification_sms_unicode_enable";
    public static final String ENABLE_SMS_FRAGMENTATION = "notification_sms_fragmentation_enable";

    // Observability
    public static final String ENABLE_METRICS = "notification_metrics_enable";
    public static final String ENABLE_DIAGNOSTIC_HEADERS = "notification_diag_headers_enable";

    private NotificationFeatureFlagConstants() {}
}
