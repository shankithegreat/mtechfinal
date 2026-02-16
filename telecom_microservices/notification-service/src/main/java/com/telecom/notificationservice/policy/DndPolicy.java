package com.telecom.notificationservice.policy;

import com.telecom.common.FeatureFlagReader;
import com.telecom.notificationservice.constants.NotificationFeatureFlagConstants;
import com.telecom.notificationservice.model.NotificationRequest;

import java.time.*;
import java.util.Set;

public class DndPolicy {

    // e.g., 9pm–9am quiet hours local time, configurable per tenant/campaign
    private final LocalTime quietStart = LocalTime.of(21, 0);
    private final LocalTime quietEnd   = LocalTime.of(9, 0);

    public boolean isAllowed(NotificationRequest req, ZoneId zone) {
        if (!FeatureFlagReader.isFeatureEnabled(NotificationFeatureFlagConstants.ENABLE_DND_CHECKS)) return true;
        LocalTime now = LocalDateTime.now(zone).toLocalTime();
        boolean inQuiet = now.isAfter(quietStart) || now.isBefore(quietEnd);
        return !inQuiet;
    }
}

public class ConsentService {

    // Example: in-memory consent lists; replace with store
    private final Set<String> smsOptOutMsisdns;
    private final Set<String> emailOptOuts;

    public ConsentService(Set<String> smsOptOutMsisdns, Set<String> emailOptOuts) {
        this.smsOptOutMsisdns = smsOptOutMsisdns;
        this.emailOptOuts = emailOptOuts;
    }

    public boolean hasConsentForSms(String msisdn) {
        if (!FeatureFlagReader.isFeatureEnabled(NotificationFeatureFlagConstants.ENABLE_CONSENT_CHECKS)) return true;
        return msisdn != null && !smsOptOutMsisdns.contains(msisdn);
    }

    public boolean hasConsentForEmail(String email) {
        if (!FeatureFlagReader.isFeatureEnabled(NotificationFeatureFlagConstants.ENABLE_CONSENT_CHECKS)) return true;
        return email != null && !emailOptOuts.contains(email);
    }
}

public class CampaignWindowPolicy {

    // Example: allow sends only in configured day/time windows (e.g., weekdays 9–20)
    public boolean isOpenNow(ZoneId zone) {
        if (!FeatureFlagReader.isFeatureEnabled(NotificationFeatureFlagConstants.ENABLE_CAMPAIGN_WINDOW)) return true;
        LocalDateTime dt = LocalDateTime.now(zone);
        DayOfWeek dow = dt.getDayOfWeek();
        LocalTime time = dt.toLocalTime();
        boolean weekday = dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY;
        boolean withinHours = !time.isBefore(LocalTime.of(9,0)) && !time.isAfter(LocalTime.of(20,0));
        return weekday && withinHours;
    }
}
