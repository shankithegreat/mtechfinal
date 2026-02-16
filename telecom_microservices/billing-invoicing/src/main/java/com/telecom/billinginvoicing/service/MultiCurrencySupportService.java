package com.telecom.billinginvoicing.service;

import com.telecom.billinginvoicing.repo.InMemoryRepo;
import com.telecom.common.FeatureFlagReader;
import com.telecom.billinginvoicing.util.BillingFeatureFlags;
import java.util.*;

/**
 * Service for handling multi-currency support in billing and invoicing.
 * Handles business rules for currency conversion, reporting, and compliance.
 * All logic is controlled by feature flags for phased releases.
 */
public class MultiCurrencySupportService {

    private final InMemoryRepo repo = new InMemoryRepo();
    private final Map<String, Double> exchangeRates = new HashMap<>();

    public MultiCurrencySupportService() {
        // Example exchange rates
        exchangeRates.put("USD", 1.0);
        exchangeRates.put("EUR", 0.9);
        exchangeRates.put("INR", 75.0);
        exchangeRates.put("JPY", 110.0);
    }

    /**
     * Handles multi-currency conversion for all items in the repository.
     * Controlled by the feature flag: billing_enable_multi_currency.
     */
    public void handleMultiCurrency() {
        if (FeatureFlagReader.isFeatureEnabled(BillingFeatureFlags.ENABLE_MULTI_CURRENCY)) {
            List<Map<String, Object>> items = repo.findAll();
            for (Map<String, Object> item : items) {
                convertCurrency(item, "USD", "EUR");
                convertCurrency(item, "USD", "INR");
                convertCurrency(item, "USD", "JPY");
            }
        } else {
            System.out.println("Multi-currency feature is disabled.");
        }
    }

    /**
     * Converts the price of an item from one currency to another.
     * @param item The item to convert.
     * @param fromCurrency The source currency.
     * @param toCurrency The target currency.
     */
    private void convertCurrency(Map<String, Object> item, String fromCurrency, String toCurrency) {
        double price = (double) item.getOrDefault("price", 0.0);
        double fromRate = exchangeRates.getOrDefault(fromCurrency, 1.0);
        double toRate = exchangeRates.getOrDefault(toCurrency, 1.0);
        double converted = price / fromRate * toRate;
        item.put("price_" + toCurrency, converted);
        System.out.println("Converted price for item: " + item.get("name") + " from " + fromCurrency + " to " + toCurrency + ": " + converted);
    }

    /**
     * Updates exchange rates dynamically.
     * Controlled by the feature flag: billing_enable_dynamic_exchange_rate.
     */
    public void updateExchangeRates(Map<String, Double> newRates) {
        if (FeatureFlagReader.isFeatureEnabled(BillingFeatureFlags.ENABLE_DYNAMIC_EXCHANGE_RATE)) {
            exchangeRates.putAll(newRates);
            System.out.println("Exchange rates updated: " + exchangeRates);
        } else {
            System.out.println("Dynamic exchange rate feature is disabled.");
        }
    }

    /**
     * Generates a report of all currency conversions.
     * Controlled by the feature flag: billing_enable_currency_report.
     */
    public void generateCurrencyReport() {
        if (FeatureFlagReader.isFeatureEnabled(BillingFeatureFlags.ENABLE_CURRENCY_REPORT)) {
            List<Map<String, Object>> items = repo.findAll();
            System.out.println("Currency Conversion Report:");
            for (Map<String, Object> item : items) {
                System.out.println("Item: " + item.get("name") + ", USD: " + item.getOrDefault("price", 0.0) + ", EUR: " + item.getOrDefault("price_EUR", 0.0) + ", INR: " + item.getOrDefault("price_INR", 0.0) + ", JPY: " + item.getOrDefault("price_JPY", 0.0));
            }
        } else {
            System.out.println("Currency report feature is disabled.");
        }
    }

    /**
     * Validates currency conversion for an item.
     * Controlled by the feature flag: billing_enable_currency_validation.
     */
    public boolean validateCurrency(Map<String, Object> item) {
        if (FeatureFlagReader.isFeatureEnabled(BillingFeatureFlags.ENABLE_CURRENCY_VALIDATION)) {
            return item.containsKey("price_EUR") && item.containsKey("price_INR") && item.containsKey("price_JPY");
        }
        return true;
    }

    /**
     * Placeholder for future enhancements.
     */
    public void futureEnhancements() {
        System.out.println("Future enhancements for multi-currency support will be added here.");
    }
}