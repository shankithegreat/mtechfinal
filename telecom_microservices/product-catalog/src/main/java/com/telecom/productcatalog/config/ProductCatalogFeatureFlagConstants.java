package com.telecom.productcatalog.config;

/**
 * Centralized feature flag constants for Product Catalog service.
 * All flags declared here prevent magic strings throughout the codebase.
 * Flags are read from feature-flags.json via FeatureFlagReader.
 *
 * Naming convention: catalog_<category>_<feature>
 */
public class ProductCatalogFeatureFlagConstants {

    // ==================== PRODUCT MANAGEMENT (7 flags) ====================
    public static final String CATALOG_ENABLE_PRODUCT_CREATION = "catalog_enable_product_creation";
    public static final String CATALOG_ENABLE_PRODUCT_UPDATE = "catalog_enable_product_update";
    public static final String CATALOG_ENABLE_PRODUCT_DELETION = "catalog_enable_product_deletion";
    public static final String CATALOG_ENABLE_BULK_IMPORT = "catalog_enable_bulk_import";
    public static final String CATALOG_ENABLE_PRODUCT_SEARCH = "catalog_enable_product_search";
    public static final String CATALOG_ENABLE_ADVANCED_FILTERS = "catalog_enable_advanced_filters";
    public static final String CATALOG_ENABLE_INVENTORY_SYNC = "catalog_enable_inventory_sync";

    // ==================== DEVICE CATALOG (6 flags) ====================
    public static final String CATALOG_ENABLE_DEVICES = "catalog_enable_devices";
    public static final String CATALOG_ENABLE_DEVICE_SPECS = "catalog_enable_device_specs";
    public static final String CATALOG_ENABLE_DEVICE_COMPATIBILITY = "catalog_enable_device_compatibility";
    public static final String CATALOG_ENABLE_DEVICE_TRADE_IN = "catalog_enable_device_trade_in";
    public static final String CATALOG_ENABLE_DEVICE_WARRANTY = "catalog_enable_device_warranty";
    public static final String CATALOG_ENABLE_DEVICE_FINANCING = "catalog_enable_device_financing";

    // ==================== SERVICE PLANS (6 flags) ====================
    public static final String CATALOG_ENABLE_SERVICE_PLANS = "catalog_enable_service_plans";
    public static final String CATALOG_ENABLE_PLAN_COMPARISON = "catalog_enable_plan_comparison";
    public static final String CATALOG_ENABLE_PLAN_CUSTOMIZATION = "catalog_enable_plan_customization";
    public static final String CATALOG_ENABLE_ROAMING_PLANS = "catalog_enable_roaming_plans";
    public static final String CATALOG_ENABLE_DATA_PLANS = "catalog_enable_data_plans";
    public static final String CATALOG_ENABLE_INTERNATIONAL_PLANS = "catalog_enable_international_plans";

    // ==================== BUNDLING & PROMOTIONS (6 flags) ====================
    public static final String CATALOG_ENABLE_BUNDLES = "catalog_enable_bundles";
    public static final String CATALOG_ENABLE_BUNDLE_PRICING = "catalog_enable_bundle_pricing";
    public static final String CATALOG_ENABLE_PROMOTIONS = "catalog_enable_promotions";
    public static final String CATALOG_ENABLE_SEASONAL_DEALS = "catalog_enable_seasonal_deals";
    public static final String CATALOG_ENABLE_LOYALTY_PRICING = "catalog_enable_loyalty_pricing";
    public static final String CATALOG_ENABLE_CROSS_SELL = "catalog_enable_cross_sell";

    // ==================== PRICING & DISCOUNTS (6 flags) ====================
    public static final String CATALOG_ENABLE_DYNAMIC_PRICING = "catalog_enable_dynamic_pricing";
    public static final String CATALOG_ENABLE_TIERED_PRICING = "catalog_enable_tiered_pricing";
    public static final String CATALOG_ENABLE_CORPORATE_PRICING = "catalog_enable_corporate_pricing";
    public static final String CATALOG_ENABLE_DISCOUNTS = "catalog_enable_discounts";
    public static final String CATALOG_ENABLE_VOLUME_DISCOUNTS = "catalog_enable_volume_discounts";
    public static final String CATALOG_ENABLE_PRICE_MATCHING = "catalog_enable_price_matching";

    // ==================== INVENTORY MANAGEMENT (5 flags) ====================
    public static final String CATALOG_ENABLE_INVENTORY_TRACKING = "catalog_enable_inventory_tracking";
    public static final String CATALOG_ENABLE_STOCK_ALERTS = "catalog_enable_stock_alerts";
    public static final String CATALOG_ENABLE_MULTI_WAREHOUSE = "catalog_enable_multi_warehouse";
    public static final String CATALOG_ENABLE_BACKORDER = "catalog_enable_backorder";
    public static final String CATALOG_ENABLE_PREORDER = "catalog_enable_preorder";

    // ==================== RATINGS & REVIEWS (4 flags) ====================
    public static final String CATALOG_ENABLE_REVIEWS = "catalog_enable_reviews";
    public static final String CATALOG_ENABLE_RATINGS = "catalog_enable_ratings";
    public static final String CATALOG_ENABLE_USER_FEEDBACK = "catalog_enable_user_feedback";
    public static final String CATALOG_ENABLE_REVIEW_MODERATION = "catalog_enable_review_moderation";

    // ==================== CATEGORIES (4 flags) ====================
    public static final String CATALOG_ENABLE_CATEGORIES = "catalog_enable_categories";
    public static final String CATALOG_ENABLE_SUBCATEGORIES = "catalog_enable_subcategories";
    public static final String CATALOG_ENABLE_CATEGORY_FILTERING = "catalog_enable_category_filtering";
    public static final String CATALOG_ENABLE_CATEGORY_RECOMMENDATIONS = "catalog_enable_category_recommendations";

    // ==================== AVAILABILITY & REGIONAL (4 flags) ====================
    public static final String CATALOG_ENABLE_REGIONAL_AVAILABILITY = "catalog_enable_regional_availability";
    public static final String CATALOG_ENABLE_NETWORK_COMPATIBILITY = "catalog_enable_network_compatibility";
    public static final String CATALOG_ENABLE_CARRIER_SPECIFIC = "catalog_enable_carrier_specific";
    public static final String CATALOG_ENABLE_REGION_PRICING = "catalog_enable_region_pricing";

    // ==================== ANALYTICS & REPORTING (4 flags) ====================
    public static final String CATALOG_ENABLE_ANALYTICS = "catalog_enable_analytics";
    public static final String CATALOG_ENABLE_TRENDING = "catalog_enable_trending";
    public static final String CATALOG_ENABLE_BEST_SELLERS = "catalog_enable_best_sellers";
    public static final String CATALOG_ENABLE_RECOMMENDATIONS = "catalog_enable_recommendations";

    // ==================== ADVANCED FEATURES (3 flags) ====================
    public static final String CATALOG_ENABLE_FEATURE_COMPARISON = "catalog_enable_feature_comparison";
    public static final String CATALOG_ENABLE_ML_RECOMMENDATIONS = "catalog_enable_ml_recommendations";
    public static final String CATALOG_ENABLE_SMART_SEARCH = "catalog_enable_smart_search";

    // Total: 55 feature flags across 11 categories
}
