package com.telecom.notificationservice.model;

import java.util.Map;
import java.util.List;

public enum Channel { SMS, EMAIL, PUSH, IVR }
public enum Severity { LOW, MEDIUM, HIGH, CRITICAL }
public enum NotificationStatus { PENDING, SENT, PARTIAL, FAILED, BLOCKED }

public class NotificationRequest {
    private String idempotencyKey;      // optional, for dedupe
    private String msisdn;              // E.164 format for telecom (e.g., +9199xxxxxxx)
    private String email;
    private Channel primaryChannel;
    private List<Channel> failoverChannels;
    private Severity severity;
    private String templateId;
    private Map<String, Object> templateParams;
    private String rawMessage;          // used when templates disabled
    private Map<String, Object> meta;   // campaignId, tenantId, traceId, etc.

    // getters/setters omitted for brevity
}

public class NotificationRecord {
    private String id;
    private NotificationRequest request;
    private NotificationStatus status;
    private List<String> deliveryIds;   // provider transaction IDs
    private String failureReason;
    private long createdAt;
    private Long sentAt;

    // getters/setters omitted
}
