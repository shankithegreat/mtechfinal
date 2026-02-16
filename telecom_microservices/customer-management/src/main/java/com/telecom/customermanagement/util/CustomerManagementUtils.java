package com.telecom.customermanagement.util;

import com.telecom.customermanagement.config.FeatureFlagConstants;
import com.telecom.common.FeatureFlagReader;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Utility functions for Customer Management with comprehensive telecom business logic.
 * Provides validation, calculation, formatting, and analysis utilities for customer operations.
 */
public class CustomerManagementUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@(.+)$"
    );
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^\\+?1?\\d{9,15}$"
    );
    private static final Pattern MSISDN_PATTERN = Pattern.compile(
        "^\\+?[1-9]\\d{1,14}$"
    );

    /**
     * Validate email format with complex rules
     */
    public static boolean validateEmail(String email) {
        if (email == null || email.isEmpty()) return false;
        return EMAIL_PATTERN.matcher(email).matches() && email.length() <= 254;
    }

    /**
     * Validate phone number for telecom
     */
    public static boolean validatePhoneNumber(String phone) {
        if (phone == null || phone.isEmpty()) return false;
        return PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Validate MSISDN (international mobile number)
     */
    public static boolean validateMSISDN(String msisdn) {
        if (msisdn == null || msisdn.isEmpty()) return false;
        return MSISDN_PATTERN.matcher(msisdn).matches();
    }

    /**
     * Validate and normalize phone number
     */
    public static String normalizePhoneNumber(String phone) {
        if (phone == null) return "";
        // Remove all non-digits except + prefix
        String normalized = phone.replaceAll("[^0-9+]", "");
        if (normalized.startsWith("+")) {
            normalized = "+" + normalized.substring(1).replaceAll("\\D", "");
        } else {
            normalized = normalized.replaceAll("\\D", "");
        }
        return normalized;
    }

    /**
     * Validate customer name format
     */
    public static boolean validateCustomerName(String name) {
        if (name == null || name.isEmpty() || name.length() > 100) return false;
        // Allow letters, spaces, hyphens, and apostrophes
        return name.matches("^[a-zA-Z\\s\\-']+$");
    }

    /**
     * Calculate credit score adjustment based on payment history
     */
    public static double calculateCreditScoreAdjustment(int paymentHistory, int daysOverdue) {
        double adjustment = 0;
        
        // Perfect payment history bonus
        if (paymentHistory >= 24) {
            adjustment += 50;
        } else if (paymentHistory >= 12) {
            adjustment += 30;
        } else if (paymentHistory >= 6) {
            adjustment += 15;
        }
        
        // Late payment penalty
        if (daysOverdue > 90) {
            adjustment -= 150;
        } else if (daysOverdue > 60) {
            adjustment -= 100;
        } else if (daysOverdue > 30) {
            adjustment -= 50;
        }
        
        return adjustment;
    }

    /**
     * Calculate customer lifetime value with complex formula
     */
    public static double calculateComplexLifetimeValue(double monthlyCharges, int customerAgeMonths, 
                                                       double retentionRate, double churnProbability) {
        // LTV = (Monthly Revenue * Margin) * (Customer Lifespan / (1 + Discount Rate - Retention))
        double margin = 0.35; // 35% margin assumption
        double discountRate = 0.1; // 10% discount rate
        double customerLifespan = monthlyCharges * margin * (customerAgeMonths / (1 + discountRate - retentionRate + churnProbability));
        return Math.max(0, customerLifespan);
    }

    /**
     * Calculate early termination fee based on contract
     */
    public static double calculateEarlyTerminationFee(double contractValue, int monthsElapsed, int contractDurationMonths) {
        if (monthsElapsed >= contractDurationMonths) {
            return 0; // No fee after contract expires
        }
        
        int monthsRemaining = contractDurationMonths - monthsElapsed;
        // Fee is proportional to remaining contract period
        double remainingPercentage = (double) monthsRemaining / contractDurationMonths;
        return contractValue * remainingPercentage * 0.15; // 15% of remaining value
    }

    /**
     * Calculate monthly bill with usage charges
     */
    public static double calculateMonthlyBill(double basePlan, long dataUsedMB, long voiceMinutesUsed, 
                                              long smsCount, double extraDataRate) {
        double bill = basePlan;
        
        // Data overage charges (e.g., $5 per GB)
        double dataGB = dataUsedMB / 1024.0;
        if (dataGB > 10) { // Assume 10GB base
            bill += (dataGB - 10) * extraDataRate;
        }
        
        // International call charges (assume $0.25 per minute)
        if (voiceMinutesUsed > 500) { // Assume 500 minutes base
            bill += (voiceMinutesUsed - 500) * 0.25;
        }
        
        // SMS overage (assume $0.05 per SMS)
        if (smsCount > 200) { // Assume 200 SMS base
            bill += (smsCount - 200) * 0.05;
        }
        
        return Math.round(bill * 100.0) / 100.0;
    }

    /**
     * Determine customer segment based on LTV and usage
     */
    public static String determineCustomerSegment(double lifetimeValue, long monthlyDataUsage) {
        if (lifetimeValue > 10000 || monthlyDataUsage > 50000) {
            return "PLATINUM";
        } else if (lifetimeValue > 5000 || monthlyDataUsage > 20000) {
            return "GOLD";
        } else if (lifetimeValue > 2000 || monthlyDataUsage > 10000) {
            return "SILVER";
        } else {
            return "BRONZE";
        }
    }

    /**
     * Calculate churn probability based on multiple factors
     */
    public static double calculateChurnProbability(long daysSinceLastActivity, int invoicesDueCount, 
                                                   boolean hasComplaints, int contractMonthsRemaining) {
        double probability = 0.0;
        
        // Inactivity factor
        if (daysSinceLastActivity > 90) {
            probability += 0.4;
        } else if (daysSinceLastActivity > 60) {
            probability += 0.25;
        } else if (daysSinceLastActivity > 30) {
            probability += 0.1;
        }
        
        // Outstanding invoices factor
        if (invoicesDueCount > 0) {
            probability += invoicesDueCount * 0.15;
        }
        
        // Complaint factor
        if (hasComplaints) {
            probability += 0.2;
        }
        
        // Contract factor (less likely to churn with long contract)
        if (contractMonthsRemaining < 3) {
            probability += 0.2;
        }
        
        return Math.min(1.0, probability);
    }

    /**
     * Format phone number for display
     */
    public static String formatPhoneForDisplay(String phone) {
        if (phone == null || phone.isEmpty()) return "";
        
        String digits = phone.replaceAll("\\D", "");
        
        if (digits.length() == 10) {
            return String.format("(%s) %s-%s", digits.substring(0, 3), digits.substring(3, 6), digits.substring(6));
        } else if (digits.length() == 11) {
            return String.format("+%s %s-%s-%s", digits.charAt(0), digits.substring(1, 4), digits.substring(4, 7), digits.substring(7));
        } else if (digits.length() > 11) {
            return String.format("+%s", digits);
        }
        
        return phone;
    }

    /**
     * Mask sensitive customer data for logging
     */
    public static String maskSensitiveData(String data, String type) {
        if (data == null || data.isEmpty()) return "";
        
        switch (type.toUpperCase()) {
            case "EMAIL":
                int atIndex = data.indexOf('@');
                if (atIndex > 0) {
                    return data.substring(0, 2) + "****" + data.substring(atIndex);
                }
                break;
            case "PHONE":
                if (data.length() >= 4) {
                    return "****" + data.substring(data.length() - 4);
                }
                break;
            case "MSISDN":
                if (data.length() >= 4) {
                    return data.substring(0, Math.max(0, data.length() - 8)) + "****" + data.substring(Math.max(0, data.length() - 4));
                }
                break;
            case "ACCOUNT":
                if (data.length() >= 4) {
                    return "****" + data.substring(data.length() - 4);
                }
                break;
        }
        
        return "****";
    }

    /**
     * Validate billing address completeness
     */
    public static boolean validateBillingAddress(String street, String city, String state, 
                                                  String postalCode, String country) {
        return (street != null && !street.isEmpty() && street.length() <= 200)
            && (city != null && !city.isEmpty() && city.length() <= 100)
            && (state != null && !state.isEmpty() && state.length() <= 50)
            && (postalCode != null && !postalCode.isEmpty() && postalCode.length() <= 20)
            && (country != null && !country.isEmpty() && country.length() <= 100);
    }

    /**
     * Check if customer is eligible for loyalty benefits
     */
    public static boolean isEligibleForLoyalty(double lifetimeValue, int accountAgeMonths, 
                                               int outstandingInvoices) {
        return lifetimeValue >= 1000 
            && accountAgeMonths >= 6 
            && outstandingInvoices == 0;
    }

    /**
     * Calculate loyalty points earned
     */
    public static int calculateLoyaltyPoints(double monthlyCharges, String customerSegment) {
        double basePoints = monthlyCharges * 10; // 10 points per dollar
        
        // Bonus multiplier based on segment
        double multiplier = switch (customerSegment) {
            case "PLATINUM" -> 5.0;
            case "GOLD" -> 3.0;
            case "SILVER" -> 2.0;
            default -> 1.0;
        };
        
        return (int) (basePoints * multiplier);
    }

    /**
     * Determine if customer is at risk of churn
     */
    public static boolean isAtChurnRisk(int churnRiskScore) {
        return churnRiskScore > 70;
    }

    /**
     * Determine if customer is high value
     */
    public static boolean isHighValueCustomer(double lifetimeValue, String segment) {
        return lifetimeValue > 5000 || "PLATINUM".equals(segment) || "GOLD".equals(segment);
    }

    /**
     * Validate contract terms
     */
    public static boolean validateContractTerms(int durationMonths, double contractValue, 
                                                 double monthlyCharges) {
        return durationMonths > 0 
            && durationMonths <= 60 
            && contractValue > 0 
            && contractValue >= monthlyCharges * durationMonths * 0.8
            && contractValue <= monthlyCharges * durationMonths * 1.5;
    }

    /**
     * Calculate discount eligibility
     */
    public static double calculateDiscountPercentage(int consecutiveMonthsPaid, boolean isBulkSubscription, 
                                                     String customerSegment) {
        double discount = 0;
        
        // Loyalty discount
        if (consecutiveMonthsPaid > 24) {
            discount += 0.15; // 15% discount
        } else if (consecutiveMonthsPaid > 12) {
            discount += 0.10; // 10% discount
        } else if (consecutiveMonthsPaid > 6) {
            discount += 0.05; // 5% discount
        }
        
        // Bulk subscription discount
        if (isBulkSubscription) {
            discount += 0.10;
        }
        
        // Segment-based bonus
        if ("PLATINUM".equals(customerSegment)) {
            discount += 0.05;
        } else if ("GOLD".equals(customerSegment)) {
            discount += 0.03;
        }
        
        return Math.min(0.25, discount); // Max 25% discount
    }

    /**
     * Generate customer account number
     */
    public static String generateAccountNumber(String customerId, long timestamp) {
        String hash = String.valueOf(Math.abs(customerId.hashCode()));
        String timestampPart = String.valueOf(timestamp).substring(String.valueOf(timestamp).length() - 6);
        return "ACC-" + hash.substring(0, Math.min(4, hash.length())) + "-" + timestampPart;
    }

    /**
     * Validate IMEI for device registration
     */
    public static boolean validateIMEI(String imei) {
        if (imei == null || imei.length() != 15) return false;
        return imei.matches("\\d{15}");
    }

    /**
     * Calculate international roaming charges
     */
    public static double calculateInternationalRoamingCharges(String destination, long minutesUsed, 
                                                              long dataUsedMB, long smsCount) {
        // Different rates based on destination (simplified)
        double minuteRate = destination.equals("US") ? 0.50 : 1.50;
        double dataRate = destination.equals("US") ? 0.10 : 0.50; // per MB
        double smsRate = destination.equals("US") ? 0.25 : 0.75;
        
        double charges = (minutesUsed * minuteRate) + (dataUsedMB * dataRate) + (smsCount * smsRate);
        return Math.round(charges * 100.0) / 100.0;
    }
}

