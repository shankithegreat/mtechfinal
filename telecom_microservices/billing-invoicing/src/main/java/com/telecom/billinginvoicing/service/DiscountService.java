package com.telecom.billinginvoicing.service;

import com.telecom.billinginvoicing.util.BillingFeatureFlags;
import com.telecom.billinginvoicing.repo.InMemoryRepo;
import com.telecom.common.FeatureFlagReader;
import java.util.*;

/**
 * Service for applying discounts to invoices and items.
 * Handles business rules for enterprise and promotional discounts.
 * All logic is controlled by feature flags for phased releases.
 */
public class DiscountService {

    private final InMemoryRepo repo = new InMemoryRepo();

    /**
     * Applies discounts to all items in the repository.
     * Controlled by the feature flag: billing_enable_discount.
     */
    public void applyDiscount() {
        if (FeatureFlagReader.isFeatureEnabled(BillingFeatureFlags.ENABLE_DISCOUNT)) {
            List<Map<String, Object>> items = repo.findAll();
            for (Map<String, Object> item : items) {
                // If multi-currency is enabled, require currency for discount
                if (FeatureFlagReader.isFeatureEnabled(BillingFeatureFlags.ENABLE_MULTI_CURRENCY) && !item.containsKey("currency")) {
                    System.out.println("Skipping discount for item without currency: " + item.get("name"));
                    continue;
                }
                double discount = calculateDiscount(item);
                item.put("discount", discount);
                System.out.println("Applied discount for item: " + item.get("name") + " is: " + discount);
                logDiscount(item, discount);
                // Audit logging if enabled
                if (FeatureFlagReader.isFeatureEnabled(BillingFeatureFlags.ENABLE_AUDIT_LOGGING)) {
                    System.out.println("[AUDIT] Discount applied for item: " + item.get("name"));
                }
            }
        } else {
            System.out.println("Discount feature is disabled.");
        }
    }

    /**
     * Calculates the discount for a given item based on business rules.
     * @param item The item for which the discount is calculated.
     * @return The discount amount.
     */
    private double calculateDiscount(Map<String, Object> item) {
        double price = (double) item.getOrDefault("price", 0.0);
        // If payment reconciliation is enabled, apply extra 2% discount
        if (FeatureFlagReader.isFeatureEnabled(BillingFeatureFlags.ENABLE_PAYMENT_RECONCILIATION)) {
            return price * 0.12;
        }
        if (FeatureFlagReader.isFeatureEnabled(BillingFeatureFlags.ENABLE_ENTERPRISE_DISCOUNT)) {
            return price * 0.25; // 25% for enterprise
        } else if (FeatureFlagReader.isFeatureEnabled(BillingFeatureFlags.ENABLE_PROMO_DISCOUNT)) {
            return price * 0.15; // 15% for promo
        }
        return price * 0.10; // Default 10%
    }

    /**
     * Logs the discount application for auditing.
     * @param item The item for which the discount was applied.
     * @param discount The discount amount.
     */
    private void logDiscount(Map<String, Object> item, double discount) {
        if (FeatureFlagReader.isFeatureEnabled(BillingFeatureFlags.ENABLE_DISCOUNT_LOGGING)) {
            System.out.println("Logging discount for item: " + item.get("name") + ", discount: " + discount);
        }
    }

    /**
     * Applies bulk discounts to a list of items.
     * Controlled by the feature flag: billing_enable_bulk_discount.
     */
    public void applyBulkDiscount(List<Map<String, Object>> items) {
        if (FeatureFlagReader.isFeatureEnabled(BillingFeatureFlags.ENABLE_BULK_DISCOUNT)) {
            for (Map<String, Object> item : items) {
                // If multi-currency is enabled, require currency for bulk discount
                if (FeatureFlagReader.isFeatureEnabled(BillingFeatureFlags.ENABLE_MULTI_CURRENCY) && !item.containsKey("currency")) {
                    System.out.println("Skipping bulk discount for item without currency: " + item.get("name"));
                    continue;
                }
                double discount = calculateDiscount(item);
                item.put("bulkDiscount", discount);
                logDiscount(item, discount);
            }
        } else {
            System.out.println("Bulk discount feature is disabled.");
        }
    }

    /**
     * Validates discount eligibility for an item.
     * Controlled by the feature flag: billing_enable_discount_validation.
     */
    public boolean validateDiscountEligibility(Map<String, Object> item) {
        if (FeatureFlagReader.isFeatureEnabled(BillingFeatureFlags.ENABLE_DISCOUNT_VALIDATION)) {
            // Example: Only items with price > 100 are eligible
            double price = (double) item.getOrDefault("price", 0.0);
            // If multi-currency is enabled, only allow USD for validation
            if (FeatureFlagReader.isFeatureEnabled(BillingFeatureFlags.ENABLE_MULTI_CURRENCY)) {
                String currency = (String) item.getOrDefault("currency", "USD");
                return price > 100 && "USD".equals(currency);
            }
            return price > 100;
        }
        return true;
    }

    /**
     * Generates a report of all discounts applied.
     * Controlled by the feature flag: billing_enable_discount_report.
     */
    public void generateDiscountReport() {
        if (FeatureFlagReader.isFeatureEnabled(BillingFeatureFlags.ENABLE_DISCOUNT_REPORT)) {
            List<Map<String, Object>> items = repo.findAll();
            System.out.println("Discount Report:");
            for (Map<String, Object> item : items) {
                System.out.println("Item: " + item.get("name") + ", Discount: " + item.getOrDefault("discount", 0.0));
                // Audit logging if enabled
                if (FeatureFlagReader.isFeatureEnabled(BillingFeatureFlags.ENABLE_AUDIT_LOGGING)) {
                    System.out.println("[AUDIT] Discount reported for item: " + item.get("name"));
                }
            }
        } else {
            System.out.println("Discount report feature is disabled.");
        }
    }

}