package com.telecom.shoppingcart.util;

/**
 * Shopping Cart Feature Flag Constants
 * All feature flags for shopping cart functionality organized by category
 * Naming convention: cart_<category>_<feature>
 */
public class ShoppingCartFeatureFlagConstants {

    // ==================== PRODUCT MANAGEMENT ====================
    public static final String CART_PRODUCT_MANAGEMENT_DEVICE_PRODUCTS = "cart_product_management_device_products";
    public static final String CART_PRODUCT_MANAGEMENT_PLAN_PRODUCTS = "cart_product_management_plan_products";
    public static final String CART_PRODUCT_MANAGEMENT_ADD_ON_PRODUCTS = "cart_product_management_add_on_products";
    public static final String CART_PRODUCT_MANAGEMENT_BUNDLE_PRODUCTS = "cart_product_management_bundle_products";
    public static final String CART_PRODUCT_MANAGEMENT_ACCESSORY_PRODUCTS = "cart_product_management_accessory_products";
    public static final String CART_PRODUCT_MANAGEMENT_PRODUCT_SEARCH = "cart_product_management_product_search";
    public static final String CART_PRODUCT_MANAGEMENT_PRODUCT_FILTERS = "cart_product_management_product_filters";

    // ==================== INVENTORY MANAGEMENT ====================
    public static final String CART_INVENTORY_STOCK_CHECKING = "cart_inventory_stock_checking";
    public static final String CART_INVENTORY_BACKORDER_SUPPORT = "cart_inventory_backorder_support";
    public static final String CART_INVENTORY_WAREHOUSE_SELECTION = "cart_inventory_warehouse_selection";
    public static final String CART_INVENTORY_STOCK_RESERVATION = "cart_inventory_stock_reservation";
    public static final String CART_INVENTORY_REAL_TIME_TRACKING = "cart_inventory_real_time_tracking";
    public static final String CART_INVENTORY_LOW_STOCK_ALERTS = "cart_inventory_low_stock_alerts";

    // ==================== PRICING & PROMOTIONS ====================
    public static final String CART_PRICING_BUNDLE_DISCOUNTS = "cart_pricing_bundle_discounts";
    public static final String CART_PRICING_PROMOTIONAL_CODES = "cart_pricing_promotional_codes";
    public static final String CART_PRICING_TIERED_PRICING = "cart_pricing_tiered_pricing";
    public static final String CART_PRICING_VOLUME_DISCOUNTS = "cart_pricing_volume_discounts";
    public static final String CART_PRICING_LOYALTY_REWARDS = "cart_pricing_loyalty_rewards";
    public static final String CART_PRICING_SEASONAL_PROMOTIONS = "cart_pricing_seasonal_promotions";
    public static final String CART_PRICING_PERCENTAGE_OFF = "cart_pricing_percentage_off";
    public static final String CART_PRICING_FIXED_PRICE_OFF = "cart_pricing_fixed_price_off";

    // ==================== CART OPERATIONS ====================
    public static final String CART_OPERATIONS_ADD_TO_CART = "cart_operations_add_to_cart";
    public static final String CART_OPERATIONS_REMOVE_FROM_CART = "cart_operations_remove_from_cart";
    public static final String CART_OPERATIONS_UPDATE_QUANTITY = "cart_operations_update_quantity";
    public static final String CART_OPERATIONS_CLEAR_CART = "cart_operations_clear_cart";
    public static final String CART_OPERATIONS_SAVE_FOR_LATER = "cart_operations_save_for_later";
    public static final String CART_OPERATIONS_ABANDONED_CART_RECOVERY = "cart_operations_abandoned_cart_recovery";
    public static final String CART_OPERATIONS_CART_PERSISTENCE = "cart_operations_cart_persistence";

    // ==================== BUNDLING & RECOMMENDATIONS ====================
    public static final String CART_BUNDLING_DEVICE_PLAN_BUNDLES = "cart_bundling_device_plan_bundles";
    public static final String CART_BUNDLING_CROSS_SELL = "cart_bundling_cross_sell";
    public static final String CART_BUNDLING_UPSELL = "cart_bundling_upsell";
    public static final String CART_BUNDLING_ACCESSORY_BUNDLING = "cart_bundling_accessory_bundling";
    public static final String CART_BUNDLING_FREQUENTLY_BOUGHT_TOGETHER = "cart_bundling_frequently_bought_together";

    // ==================== DEVICE FINANCING ====================
    public static final String CART_FINANCING_INSTALLMENT_PLANS = "cart_financing_installment_plans";
    public static final String CART_FINANCING_TRADE_IN = "cart_financing_trade_in";
    public static final String CART_FINANCING_DEVICE_PROTECTION = "cart_financing_device_protection";
    public static final String CART_FINANCING_CREDIT_CHECK = "cart_financing_credit_check";
    public static final String CART_FINANCING_PAYMENT_PLANS = "cart_financing_payment_plans";

    // ==================== COMPATIBILITY & VALIDATION ====================
    public static final String CART_COMPATIBILITY_DEVICE_PLAN_COMPATIBILITY = "cart_compatibility_device_plan_compatibility";
    public static final String CART_COMPATIBILITY_NETWORK_COMPATIBILITY = "cart_compatibility_network_compatibility";
    public static final String CART_COMPATIBILITY_VALIDATION = "cart_compatibility_validation";
    public static final String CART_COMPATIBILITY_WARNINGS = "cart_compatibility_warnings";

    // ==================== TAX & FEES ====================
    public static final String CART_TAX_SALES_TAX = "cart_tax_sales_tax";
    public static final String CART_TAX_ACTIVATION_FEES = "cart_tax_activation_fees";
    public static final String CART_TAX_SHIPPING_FEES = "cart_tax_shipping_fees";
    public static final String CART_TAX_INSURANCE_FEES = "cart_tax_insurance_fees";
    public static final String CART_TAX_FEE_CALCULATION = "cart_tax_fee_calculation";

    // ==================== CHECKOUT ====================
    public static final String CART_CHECKOUT_VALIDATION = "cart_checkout_validation";
    public static final String CART_CHECKOUT_PAYMENT_PROCESSING = "cart_checkout_payment_processing";
    public static final String CART_CHECKOUT_ORDER_CREATION = "cart_checkout_order_creation";
    public static final String CART_CHECKOUT_FRAUD_DETECTION = "cart_checkout_fraud_detection";

    // ==================== ANALYTICS & REPORTING ====================
    public static final String CART_ANALYTICS_CART_METRICS = "cart_analytics_cart_metrics";
    public static final String CART_ANALYTICS_CONVERSION_TRACKING = "cart_analytics_conversion_tracking";
    public static final String CART_ANALYTICS_PRODUCT_POPULARITY = "cart_analytics_product_popularity";
    public static final String CART_ANALYTICS_REVENUE_REPORTING = "cart_analytics_revenue_reporting";

    // ==================== NOTIFICATIONS ====================
    public static final String CART_NOTIFICATIONS_PRICE_CHANGE_ALERTS = "cart_notifications_price_change_alerts";
    public static final String CART_NOTIFICATIONS_STOCK_AVAILABILITY = "cart_notifications_stock_availability";
    public static final String CART_NOTIFICATIONS_PROMOTIONAL_ALERTS = "cart_notifications_promotional_alerts";

    // ==================== SPECIAL OFFERS ====================
    public static final String CART_SPECIAL_OFFERS_NEW_CUSTOMER_DEALS = "cart_special_offers_new_customer_deals";
    public static final String CART_SPECIAL_OFFERS_LOYALTY_TIERS = "cart_special_offers_loyalty_tiers";
    public static final String CART_SPECIAL_OFFERS_FLASH_SALES = "cart_special_offers_flash_sales";
    public static final String CART_SPECIAL_OFFERS_REFERRAL_REWARDS = "cart_special_offers_referral_rewards";
}
