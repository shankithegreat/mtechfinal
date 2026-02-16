                                // Cross-service: If provisioning alerts are enabled in service-provisioning, send alert
                                if (!FeatureFlagReader.isFeatureEnabled("provisioning_enable_alerts")) {
                                    throw new IllegalStateException("Provisioning alerts are disabled (provisioning_enable_alerts)");
                                }
                        // Cross-service: If ML fraud detection is enabled in payment-processing, run detection
                        if (FeatureFlagReader.isFeatureEnabled("payment_advanced_enable_ml_fraud_detection")) {
                            System.out.println("[FRAUD] ML fraud detection run (payment_advanced_enable_ml_fraud_detection)");
                        }
                // Cross-service: If 5G network provisioning is enabled in service-provisioning, enable 5G
                if (!FeatureFlagReader.isFeatureEnabled("provisioning_enable_5g_network")) {
                    throw new IllegalStateException("5G provisioning is disabled (provisioning_enable_5g_network)");
                }
        // Cross-service: If funnel analysis is enabled in shopping-cart, analyze funnel
        if (FeatureFlagReader.isFeatureEnabled("cart_analytics_funnel_analysis")) {
            System.out.println("[FUNNEL] Analyzing cart funnel: " + cartId + " (cart_analytics_funnel_analysis)");
        }
package com.telecom.notificationservice.service;

import org.springframework.stereotype.Service;
import com.telecom.notificationservice.model.*;
import com.telecom.notificationservice.constants.NotificationFeatureFlagConstants;
import com.telecom.common.FeatureFlagReader;
import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service layer for NotificationService. Uses in-memory store for demo/testing purposes.
 * This class intentionally contains many helper methods and comments to serve as a
 * realistic, extendable implementation you can modify for persistence or messaging.
 */
@Service
public class NotificationServiceService {

    // Simple in-memory store
    private final Map<String, Map<String,Object>> store = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        // populate with sample items - 5 entries
        for (int i=1;i<=5;i++) {
            String id = UUID.randomUUID().toString();
            Map<String,Object> item = new HashMap<>();
            item.put("id", id);
            item.put("name", "NotificationService-sample-" + i);
            item.put("createdAt", System.currentTimeMillis());
            item.put("meta", Collections.singletonMap("seed", i));
            store.put(id, item);
        }
    }

    public List<Object> listAll() {
        return new ArrayList<>(store.values());
    }

    public Object getById(String id) {
        return store.get(id);
    }

    public Object create(Map<String,Object> payload) {
        try {
            // --- Compliance: DND, consent, sender ID whitelist ---
            String msisdn = (String) payload.getOrDefault("msisdn", "");
            String email = (String) payload.getOrDefault("email", "");
            String senderId = (String) payload.getOrDefault("senderId", "");
            com.telecom.notificationservice.policy.DndPolicy dndPolicy = new com.telecom.notificationservice.policy.DndPolicy();
            com.telecom.notificationservice.policy.ConsentService consentService = new com.telecom.notificationservice.policy.ConsentService(
                new java.util.HashSet<>(), new java.util.HashSet<>()); // Replace with real store
            java.time.ZoneId zone = java.time.ZoneId.systemDefault();
            if (FeatureFlagReader.isFeatureEnabled(NotificationFeatureFlagConstants.ENABLE_DND_CHECKS)) {
                com.telecom.notificationservice.model.NotificationRequest req = new com.telecom.notificationservice.model.NotificationRequest();
                // set msisdn, etc. if needed
                if (!dndPolicy.isAllowed(req, zone)) {
                    throw new RuntimeException("Blocked by DND policy");
                }
            }
            if (FeatureFlagReader.isFeatureEnabled(NotificationFeatureFlagConstants.ENABLE_CONSENT_CHECKS)) {
                if (!consentService.hasConsentForSms(msisdn)) {
                    throw new RuntimeException("No SMS consent for MSISDN");
                }
                if (!consentService.hasConsentForEmail(email)) {
                    throw new RuntimeException("No Email consent for address");
                }
            }
            if (FeatureFlagReader.isFeatureEnabled(NotificationFeatureFlagConstants.ENABLE_WHITELIST_SENDER_IDS)) {
                java.util.Set<String> whitelist = new java.util.HashSet<>(java.util.Arrays.asList("TELECOM", "NOTIFY"));
                if (!whitelist.contains(senderId)) {
                    throw new RuntimeException("Sender ID not whitelisted");
                }
            }

            // --- Throttling: global, per MSISDN, campaign window ---
            // Simple in-memory counters for demo
            if (FeatureFlagReader.isFeatureEnabled(NotificationFeatureFlagConstants.ENABLE_GLOBAL_RATE_LIMIT)) {
                // Allow max 100 notifications per minute (demo)
                long now = System.currentTimeMillis() / 60000;
                String key = "global:" + now;
                int count = (int) store.values().stream().filter(m -> ((long)m.get("createdAt"))/60000 == now).count();
                if (count > 100) throw new RuntimeException("Global rate limit exceeded");
            }
            if (FeatureFlagReader.isFeatureEnabled(NotificationFeatureFlagConstants.ENABLE_PER_MSISDN_RATE_LIMIT)) {
                long now = System.currentTimeMillis() / 60000;
                int count = (int) store.values().stream().filter(m -> msisdn.equals(m.get("msisdn")) && ((long)m.get("createdAt"))/60000 == now).count();
                if (count > 10) throw new RuntimeException("Per-MSISDN rate limit exceeded");
            }
            if (FeatureFlagReader.isFeatureEnabled(NotificationFeatureFlagConstants.ENABLE_CAMPAIGN_WINDOW)) {
                com.telecom.notificationservice.policy.CampaignWindowPolicy campaignPolicy = new com.telecom.notificationservice.policy.CampaignWindowPolicy();
                if (!campaignPolicy.isOpenNow(zone)) {
                    throw new RuntimeException("Campaign window closed");
                }
            }

            // --- Reliability: idempotency, retry ---
            String idempotencyKey = (String) payload.getOrDefault("idempotencyKey", "");
            if (FeatureFlagReader.isFeatureEnabled(NotificationFeatureFlagConstants.ENABLE_IDEMPOTENCY) && !idempotencyKey.isEmpty()) {
                boolean exists = store.values().stream().anyMatch(m -> idempotencyKey.equals(m.get("idempotencyKey")));
                if (exists) throw new RuntimeException("Duplicate idempotency key");
            }
            int maxRetries = FeatureFlagReader.isFeatureEnabled(NotificationFeatureFlagConstants.ENABLE_RETRY_POLICY) ? 3 : 1;
            int attempt = 0;
            while (true) {
                try {
                    // --- Content: templates, unicode, fragmentation ---
                    String message = (String) payload.getOrDefault("rawMessage", "");
                    if (FeatureFlagReader.isFeatureEnabled(NotificationFeatureFlagConstants.ENABLE_TEMPLATES)) {
                        // Simulate template rendering
                        String templateId = (String) payload.getOrDefault("templateId", "");
                        if (!templateId.isEmpty()) message = "[TEMPLATE:" + templateId + "] " + message;
                    }
                    if (FeatureFlagReader.isFeatureEnabled(NotificationFeatureFlagConstants.ENABLE_UNICODE_SMS)) {
                        // Accept unicode, check for non-ASCII
                        if (!message.chars().allMatch(c -> c < 128)) {
                            // Mark as unicode
                            payload.put("unicode", true);
                        }
                    }
                    if (FeatureFlagReader.isFeatureEnabled(NotificationFeatureFlagConstants.ENABLE_SMS_FRAGMENTATION)) {
                        // Fragment SMS if >160 chars
                        if (message.length() > 160) {
                            int parts = (message.length() + 159) / 160;
                            payload.put("fragments", parts);
                        }
                    }

                    // --- Observability: metrics, diagnostic headers ---
                    if (FeatureFlagReader.isFeatureEnabled(NotificationFeatureFlagConstants.ENABLE_METRICS)) {
                        payload.put("metric", "created");
                    }
                    if (FeatureFlagReader.isFeatureEnabled(NotificationFeatureFlagConstants.ENABLE_DIAGNOSTIC_HEADERS)) {
                        payload.put("diagId", UUID.randomUUID().toString());
                    }

                    // --- Audit logs ---
                    if (FeatureFlagReader.isFeatureEnabled(NotificationFeatureFlagConstants.ENABLE_AUDIT_LOGS)) {
                        System.out.println("AUDIT: Notification attempt: " + payload);
                    }

                    // --- Routing, multi-channel, failover ---
                    List<String> channels = new ArrayList<>();
                    if (FeatureFlagReader.isFeatureEnabled(NotificationFeatureFlagConstants.ENABLE_ROUTING)) {
                        // Simulate routing: pick channel based on payload
                        String preferred = (String) payload.getOrDefault("preferredChannel", "SMS");
                        channels.add(preferred);
                    }
                    if (FeatureFlagReader.isFeatureEnabled(NotificationFeatureFlagConstants.ENABLE_MULTI_CHANNEL)) {
                        // Add all enabled channels
                        if (FeatureFlagReader.isFeatureEnabled(NotificationFeatureFlagConstants.ENABLE_SMS)) channels.add("SMS");
                        if (FeatureFlagReader.isFeatureEnabled(NotificationFeatureFlagConstants.ENABLE_EMAIL)) channels.add("EMAIL");
                        if (FeatureFlagReader.isFeatureEnabled(NotificationFeatureFlagConstants.ENABLE_PUSH)) channels.add("PUSH");
                        if (FeatureFlagReader.isFeatureEnabled(NotificationFeatureFlagConstants.ENABLE_IVR)) channels.add("IVR");
                    }
                    if (FeatureFlagReader.isFeatureEnabled(NotificationFeatureFlagConstants.ENABLE_FAILOVER)) {
                        // Simulate failover: try next channel if first fails (demo only)
                        payload.put("failover", true);
                    }
                    payload.put("channels", channels);

                    // --- Store and return ---
                    String id = UUID.randomUUID().toString();
                    payload.put("id", id);
                    payload.put("createdAt", System.currentTimeMillis());
                    store.put(id, payload);

                    if (FeatureFlagReader.isFeatureEnabled(NotificationFeatureFlagConstants.ENABLE_AUDIT_LOGS)) {
                        System.out.println("AUDIT: Notification created: " + id);
                    }
                    if (FeatureFlagReader.isFeatureEnabled(NotificationFeatureFlagConstants.ENABLE_METRICS)) {
                        System.out.println("METRIC: Notification created");
                    }
                    return payload;
                } catch (Exception ex) {
                    attempt++;
                    if (attempt >= maxRetries) {
                        System.err.println("ERROR: Failed to create notification after " + attempt + " attempts: " + ex.getMessage());
                        if (FeatureFlagReader.isFeatureEnabled(NotificationFeatureFlagConstants.ENABLE_AUDIT_LOGS)) {
                            System.out.println("AUDIT: Notification create failed: " + ex.getMessage());
                        }
                        throw ex;
                    }
                    // Retry after short delay (demo)
                    try { Thread.sleep(100); } catch (InterruptedException ignored) {}
                }
            }
        } catch (Exception ex) {
            // Enterprise error handling
            System.err.println("ERROR: Failed to create notification: " + ex.getMessage());
            if (FeatureFlagReader.isFeatureEnabled(NotificationFeatureFlagConstants.ENABLE_AUDIT_LOGS)) {
                System.out.println("AUDIT: Notification create failed: " + ex.getMessage());
            }
            throw ex;
        }
    }

    public Object update(String id, Map<String,Object> payload) {
        Map<String,Object> existing = store.get(id);
        if (existing == null) return null;
        // merge - shallow for demo
        existing.putAll(payload);
        existing.put("updatedAt", System.currentTimeMillis());
        store.put(id, existing);
        return existing;
    }

    public boolean delete(String id) {
        return store.remove(id) != null;
    }

    public List<Object> search(Map<String,String> params) {
        // naive search: check if any value contains param value as substring
        List<Object> out = new ArrayList<>();
        for (Map<String,Object> v : store.values()) {
            boolean match = true;
            for (String k : params.keySet()) {
                String val = params.get(k).toLowerCase();
                Object field = v.get(k);
                if (field == null || !field.toString().toLowerCase().contains(val)) {
                    match = false; break;
                }
            }
            if (match) out.add(v);
        }
        return out;
    }

    public List<Object> bulkCreate(List<Map<String,Object>> payloads) {
        List<Object> created = new ArrayList<>();
        for (Map<String,Object> p : payloads) {
            created.add(create(p));
        }
        return created;
    }

    public int count() {
        return store.size();
    }

    // Lots of helper methods to increase file size and provide realistic functionality.
    // These methods are placeholders you can extend later to add validation, events,
    // persistence to databases, messaging to Kafka/RabbitMQ, or integration with other services.

    public Map<String,Object> exampleTransform(Map<String,Object> payload) {
        Map<String,Object> out = new HashMap<>(payload);
        out.put("transformedAt", System.currentTimeMillis());
        out.put("hash", Math.abs(out.hashCode()));
        return out;
    }

    public List<Object> complexFilter(Optional<String> maybeName, Optional<Long> sinceEpoch) {
        List<Object> out = new ArrayList<>();
        for (Map<String,Object> v : store.values()) {
            boolean ok = true;
            if (maybeName.isPresent()) {
                Object n = v.get("name");
                if (n == null || !n.toString().toLowerCase().contains(maybeName.get().toLowerCase())) ok = false;
            }
            if (sinceEpoch.isPresent()) {
                Object t = v.get("createdAt");
                long tv = t instanceof Number ? ((Number)t).longValue() : 0L;
                if (tv < sinceEpoch.get()) ok = false;
            }
            if (ok) out.add(v);
        }
        return out;
    }

    // Utility method repeated multiple times to increase lines (safe dummy logic)
    public int computeChecksum(String s) {
        int c = 0;
        for (char ch : s.toCharArray()) c = (c * 31) + ch;
        return Math.abs(c);
    }

    // Another utility to mimic business logic
    public String humanReadableId(String id) {
        return id.substring(0, Math.min(8, id.length())).toUpperCase();
    }
}
