package com.telecom.inventorymanagement.config;

/**
 * Centralized feature flag constants for Inventory Management service.
 * All feature flags used in this microservice are declared here to provide
 * a single source of truth and improve maintainability.
 */
public class FeatureFlagConstants {

    // Equipment Management Flags
    public static final String ENABLE_EQUIPMENT_REGISTRATION = "inventory_enable_equipment_registration";
    public static final String ENABLE_EQUIPMENT_TRACKING = "inventory_enable_equipment_tracking";
    public static final String ENABLE_EQUIPMENT_MAINTENANCE = "inventory_enable_equipment_maintenance";
    public static final String ENABLE_DEVICE_PROVISIONING = "inventory_enable_device_provisioning";
    
    // Stock Management Flags
    public static final String ENABLE_STOCK_MANAGEMENT = "inventory_enable_stock_management";
    public static final String ENABLE_STOCK_ALERTS = "inventory_enable_stock_alerts";
    public static final String ENABLE_LOW_STOCK_WARNING = "inventory_enable_low_stock_warning";
    public static final String ENABLE_STOCK_FORECASTING = "inventory_enable_stock_forecasting";
    public static final String ENABLE_STOCK_REORDER = "inventory_enable_stock_reorder";
    
    // Warehouse Management Flags
    public static final String ENABLE_WAREHOUSE_MANAGEMENT = "inventory_enable_warehouse_management";
    public static final String ENABLE_MULTI_WAREHOUSE = "inventory_enable_multi_warehouse";
    public static final String ENABLE_LOCATION_TRACKING = "inventory_enable_location_tracking";
    public static final String ENABLE_WAREHOUSE_OPTIMIZATION = "inventory_enable_warehouse_optimization";
    
    // Order & Fulfillment Flags
    public static final String ENABLE_ORDER_MANAGEMENT = "inventory_enable_order_management";
    public static final String ENABLE_BACKORDER = "inventory_enable_backorder";
    public static final String ENABLE_BATCH_ALLOCATION = "inventory_enable_batch_allocation";
    public static final String ENABLE_PICKING_PACKING = "inventory_enable_picking_packing";
    
    // Supplier Management Flags
    public static final String ENABLE_SUPPLIER_MANAGEMENT = "inventory_enable_supplier_management";
    public static final String ENABLE_PURCHASE_ORDER = "inventory_enable_purchase_order";
    public static final String ENABLE_SUPPLIER_QUALITY = "inventory_enable_supplier_quality";
    public static final String ENABLE_SUPPLIER_SCORING = "inventory_enable_supplier_scoring";
    
    // Lifecycle & Obsolescence Flags
    public static final String ENABLE_DEVICE_LIFECYCLE = "inventory_enable_device_lifecycle";
    public static final String ENABLE_OBSOLESCENCE_MANAGEMENT = "inventory_enable_obsolescence_management";
    public static final String ENABLE_DEPRECIATION_CALCULATION = "inventory_enable_depreciation_calculation";
    public static final String ENABLE_END_OF_LIFE = "inventory_enable_end_of_life";
    
    // Analytics & Reporting Flags
    public static final String ENABLE_INVENTORY_ANALYTICS = "inventory_enable_inventory_analytics";
    public static final String ENABLE_USAGE_ANALYTICS = "inventory_enable_usage_analytics";
    public static final String ENABLE_TURNOVER_ANALYSIS = "inventory_enable_turnover_analysis";
    public static final String ENABLE_ROI_CALCULATION = "inventory_enable_roi_calculation";
    
    // Quality & Compliance Flags
    public static final String ENABLE_QUALITY_CONTROL = "inventory_enable_quality_control";
    public static final String ENABLE_DAMAGE_TRACKING = "inventory_enable_damage_tracking";
    public static final String ENABLE_RECALL_MANAGEMENT = "inventory_enable_recall_management";
    public static final String ENABLE_COMPLIANCE_REPORTING = "inventory_enable_compliance_reporting";
    
    // Cost & Pricing Flags
    public static final String ENABLE_COST_TRACKING = "inventory_enable_cost_tracking";
    public static final String ENABLE_ABC_ANALYSIS = "inventory_enable_abc_analysis";
    public static final String ENABLE_VALUATION_METHODS = "inventory_enable_valuation_methods";
    public static final String ENABLE_PRICE_OPTIMIZATION = "inventory_enable_price_optimization";
}
