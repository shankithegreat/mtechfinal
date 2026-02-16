package com.telecom.productcatalog.controller;

import com.telecom.common.FeatureFlagReader;
import com.telecom.productcatalog.model.*;
import com.telecom.productcatalog.service.*;
import com.telecom.productcatalog.config.ProductCatalogFeatureFlagConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * REST controllers for ProductCatalog endpoints with comprehensive feature flag protection.
 * Supports device management, service plans, bundling, pricing, inventory, and recommendations.
 */
@RestController
@RequestMapping("/api/product-catalog")
public class ProductCatalogController {

    @Autowired
    private ProductCatalogService service;

    // ==================== PRODUCT MANAGEMENT ENDPOINTS ====================

    /**
     * Create new product
     */
    @PostMapping("/products")
    public ResponseEntity<?> createProduct(@RequestBody TelecomProduct product) {
        try {
            TelecomProduct created = service.createProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Update existing product
     */
    @PutMapping("/products/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable String productId, @RequestBody TelecomProduct updates) {
        try {
            TelecomProduct updated = service.updateProduct(productId, updates);
            if (updated == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Delete product
     */
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable String productId) {
        try {
            boolean deleted = service.deleteProduct(productId);
            if (!deleted) return ResponseEntity.notFound().build();
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== DEVICE ENDPOINTS ====================

    /**
     * Create device product
     */
    @PostMapping("/devices")
    public ResponseEntity<?> createDevice(@RequestBody DeviceProduct device) {
        try {
            if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_DEVICES)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Device management is disabled"));
            }
            DeviceProduct created = service.createDevice(device);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Check device-plan compatibility
     */
    @GetMapping("/devices/{deviceId}/compatibility/{planId}")
    public ResponseEntity<?> checkCompatibility(@PathVariable String deviceId, @PathVariable String planId) {
        try {
            if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_DEVICE_COMPATIBILITY)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Device compatibility checking is disabled"));
            }
            boolean compatible = service.checkDeviceCompatibility(deviceId, planId);
            return ResponseEntity.ok(Map.of("compatible", compatible));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Calculate device financing
     */
    @PostMapping("/devices/{deviceId}/financing")
    public ResponseEntity<?> calculateFinancing(@PathVariable String deviceId, @RequestParam int months) {
        try {
            if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_DEVICE_FINANCING)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Device financing is disabled"));
            }
            double monthlyPayment = service.calculateDeviceFinancing(deviceId, months);
            return ResponseEntity.ok(Map.of("monthlyPayment", monthlyPayment, "months", months));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Calculate trade-in value
     */
    @PostMapping("/devices/{deviceId}/trade-in")
    public ResponseEntity<?> calculateTradeIn(@PathVariable String deviceId, @RequestParam String condition) {
        try {
            if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_DEVICE_TRADE_IN)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Device trade-in is disabled"));
            }
            double tradeInValue = service.calculateTradeInValue(deviceId, condition);
            return ResponseEntity.ok(Map.of("tradeInValue", tradeInValue, "condition", condition));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== SERVICE PLAN ENDPOINTS ====================

    /**
     * Create service plan
     */
    @PostMapping("/plans")
    public ResponseEntity<?> createPlan(@RequestBody ServicePlanProduct plan) {
        try {
            if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_SERVICE_PLANS)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Service plan creation is disabled"));
            }
            ServicePlanProduct created = service.createServicePlan(plan);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Compare plans
     */
    @PostMapping("/plans/compare")
    public ResponseEntity<?> comparePlans(@RequestBody List<String> planIds) {
        try {
            if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_PLAN_COMPARISON)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Plan comparison is disabled"));
            }
            Map<String, Object> comparison = service.comparePlans(planIds);
            return ResponseEntity.ok(comparison);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== PRICING ENDPOINTS ====================

    /**
     * Calculate bundle pricing
     */
    @PostMapping("/pricing/bundle")
    public ResponseEntity<?> calculateBundlePrice(@RequestBody List<String> productIds) {
        try {
            double bundlePrice = service.calculateBundlePrice(productIds);
            return ResponseEntity.ok(Map.of("bundlePrice", bundlePrice, "itemCount", productIds.size()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Apply tiered pricing
     */
    @PostMapping("/pricing/tiered")
    public ResponseEntity<?> applyTieredPricing(@RequestParam double basePrice, @RequestParam int quantity) {
        try {
            if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_TIERED_PRICING)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Tiered pricing is disabled"));
            }
            double tieredPrice = service.applyTieredPricing(basePrice, quantity);
            return ResponseEntity.ok(Map.of("basePrice", basePrice, "tieredPrice", tieredPrice, "quantity", quantity));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Apply volume discount
     */
    @PostMapping("/pricing/volume-discount")
    public ResponseEntity<?> applyVolumeDiscount(@RequestParam double basePrice, @RequestParam int quantity) {
        try {
            if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_VOLUME_DISCOUNTS)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Volume discounts are disabled"));
            }
            double discountedPrice = service.applyVolumeDiscount(basePrice, quantity);
            return ResponseEntity.ok(Map.of("basePrice", basePrice, "discountedPrice", discountedPrice, "quantity", quantity));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== BUNDLING ENDPOINTS ====================

    /**
     * Create product bundle
     */
    @PostMapping("/bundles")
    public ResponseEntity<?> createBundle(@RequestParam List<String> productIds, @RequestParam String bundleName) {
        try {
            if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_BUNDLES)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Bundle creation is disabled"));
            }
            ProductBundle bundle = service.createBundle(productIds, bundleName);
            return ResponseEntity.status(HttpStatus.CREATED).body(bundle);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get promotional items
     */
    @GetMapping("/promotions")
    public ResponseEntity<?> getPromotions() {
        try {
            List<TelecomProduct> promotions = service.getPromotionalItems();
            return ResponseEntity.ok(promotions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== INVENTORY ENDPOINTS ====================

    /**
     * Reserve inventory
     */
    @PostMapping("/inventory/reserve/{productId}")
    public ResponseEntity<?> reserveInventory(@PathVariable String productId, @RequestParam int quantity) {
        try {
            if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_INVENTORY_TRACKING)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Inventory tracking is disabled"));
            }
            boolean reserved = service.reserveInventory(productId, quantity);
            return ResponseEntity.ok(Map.of("reserved", reserved, "productId", productId, "quantity", quantity));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Check low stock products
     */
    @GetMapping("/inventory/low-stock/{threshold}")
    public ResponseEntity<?> checkLowStock(@PathVariable int threshold) {
        try {
            if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_STOCK_ALERTS)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Stock alerts are disabled"));
            }
            List<String> lowStockProducts = service.checkLowStockProducts(threshold);
            return ResponseEntity.ok(Map.of("lowStockProducts", lowStockProducts, "threshold", threshold));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== REVIEWS & RATINGS ENDPOINTS ====================

    /**
     * Add product review
     */
    @PostMapping("/products/{productId}/reviews")
    public ResponseEntity<?> addReview(@PathVariable String productId, @RequestBody ProductReview review) {
        try {
            if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_REVIEWS)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Reviews are disabled"));
            }
            ProductReview added = service.addReview(productId, review);
            return ResponseEntity.status(HttpStatus.CREATED).body(added);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== SEARCH & FILTERING ENDPOINTS ====================

    /**
     * Search products
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(@RequestParam String keyword) {
        try {
            if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_PRODUCT_SEARCH)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Product search is disabled"));
            }
            List<TelecomProduct> results = service.searchProducts(keyword);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Filter by category
     */
    @GetMapping("/products/category/{category}")
    public ResponseEntity<?> filterByCategory(@PathVariable String category) {
        try {
            if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_CATEGORY_FILTERING)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Category filtering is disabled"));
            }
            List<TelecomProduct> results = service.filterByCategory(category);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Filter by price range
     */
    @GetMapping("/products/price-range")
    public ResponseEntity<?> filterByPrice(@RequestParam double minPrice, @RequestParam double maxPrice) {
        try {
            if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_ADVANCED_FILTERS)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Price filtering is disabled"));
            }
            List<TelecomProduct> results = service.filterByPriceRange(minPrice, maxPrice);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== RECOMMENDATIONS ENDPOINTS ====================

    /**
     * Get best sellers
     */
    @GetMapping("/recommendations/best-sellers/{limit}")
    public ResponseEntity<?> getBestSellers(@PathVariable int limit) {
        try {
            if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_BEST_SELLERS)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Best sellers feature is disabled"));
            }
            List<TelecomProduct> bestSellers = service.getBestSellers(limit);
            return ResponseEntity.ok(bestSellers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get compatible add-ons
     */
    @GetMapping("/plans/{planId}/add-ons")
    public ResponseEntity<?> getAddOns(@PathVariable String planId) {
        try {
            if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_CROSS_SELL)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Cross-sell recommendations are disabled"));
            }
            List<String> addOns = service.getCompatibleAddOns(planId);
            return ResponseEntity.ok(Map.of("planId", planId, "compatibleAddOns", addOns));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== LEGACY GENERIC ENDPOINTS ====================

    /**
     * List all products
     */
    @GetMapping
    public ResponseEntity<List<Object>> listAll() {
        return ResponseEntity.ok(service.listAll());
    }

    /**
     * Get product by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable String id) {
        Object obj = service.getById(id);
        if (obj == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(obj);
    }

    /**
     * Generic create endpoint
     */
    @PostMapping
    public ResponseEntity<Object> create(@RequestBody Map<String, Object> payload) {
        try {
            Object created = service.create(payload);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Generic update endpoint
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        try {
            Object updated = service.update(id, payload);
            if (updated == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Generic delete endpoint
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        boolean ok = service.delete(id);
        if (!ok) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }

    /**
     * Search endpoint
     */
    @GetMapping("/search-advanced")
    public ResponseEntity<List<Object>> search(@RequestParam Map<String,String> params) {
        return ResponseEntity.ok(service.search(params));
    }

    /**
     * Bulk create endpoint
     */
    @PostMapping("/bulk")
    public ResponseEntity<List<Object>> bulkCreate(@RequestBody List<Map<String,Object>> payloads) {
        return ResponseEntity.ok(service.bulkCreate(payloads));
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String,Object>> health() {
        Map<String,Object> health = new HashMap<>();
        health.put("service", "product-catalog");
        health.put("status", "UP");
        health.put("totalProducts", service.count());
        health.put("featuresEnabled", getEnabledFeatures());
        return ResponseEntity.ok(health);
    }

    /**
     * Get feature flag status
     */
    @GetMapping("/features")
    public ResponseEntity<Map<String, Object>> getFeaturesStatus() {
        Map<String, Object> features = new HashMap<>();

        features.put("productManagement", Map.of(
            "creationEnabled", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_PRODUCT_CREATION),
            "updateEnabled", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_PRODUCT_UPDATE),
            "deletionEnabled", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_PRODUCT_DELETION),
            "bulkImportEnabled", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_BULK_IMPORT),
            "searchEnabled", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_PRODUCT_SEARCH)
        ));

        features.put("devices", Map.of(
            "devicesEnabled", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_DEVICES),
            "specsEnabled", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_DEVICE_SPECS),
            "compatibilityEnabled", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_DEVICE_COMPATIBILITY),
            "tradeInEnabled", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_DEVICE_TRADE_IN),
            "financingEnabled", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_DEVICE_FINANCING)
        ));

        features.put("plans", Map.of(
            "plansEnabled", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_SERVICE_PLANS),
            "comparisonEnabled", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_PLAN_COMPARISON),
            "customizationEnabled", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_PLAN_CUSTOMIZATION),
            "roamingEnabled", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_ROAMING_PLANS),
            "dataPlansEnabled", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_DATA_PLANS)
        ));

        features.put("pricing", Map.of(
            "dynamicPricingEnabled", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_DYNAMIC_PRICING),
            "tieredPricingEnabled", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_TIERED_PRICING),
            "discountsEnabled", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_DISCOUNTS),
            "volumeDiscountsEnabled", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_VOLUME_DISCOUNTS)
        ));

        features.put("bundling", Map.of(
            "bundlesEnabled", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_BUNDLES),
            "bundlePricingEnabled", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_BUNDLE_PRICING),
            "promotionsEnabled", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_PROMOTIONS)
        ));

        features.put("inventory", Map.of(
            "trackingEnabled", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_INVENTORY_TRACKING),
            "alertsEnabled", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_STOCK_ALERTS),
            "multiWarehouseEnabled", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_MULTI_WAREHOUSE),
            "backorderEnabled", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_BACKORDER)
        ));

        features.put("reviews", Map.of(
            "reviewsEnabled", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_REVIEWS),
            "ratingsEnabled", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_RATINGS)
        ));

        features.put("recommendations", Map.of(
            "bestSellersEnabled", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_BEST_SELLERS),
            "crossSellEnabled", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_CROSS_SELL),
            "mlRecommendationsEnabled", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_ML_RECOMMENDATIONS)
        ));

        return ResponseEntity.ok(features);
    }

    private Map<String, Boolean> getEnabledFeatures() {
        Map<String, Boolean> flags = new HashMap<>();
        flags.put("productCreation", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_PRODUCT_CREATION));
        flags.put("devices", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_DEVICES));
        flags.put("servicePlans", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_SERVICE_PLANS));
        flags.put("bundles", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_BUNDLES));
        flags.put("inventory", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_INVENTORY_TRACKING));
        flags.put("reviews", FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_REVIEWS));
        return flags;
    }
}
    @GetMapping("/search")
    public ResponseEntity<List<Object>> search(@RequestParam Map<String,String> params) {
        return ResponseEntity.ok(service.search(params));
    }

    // 7. Bulk create
    @PostMapping("/bulk")
    public ResponseEntity<List<Object>> bulkCreate(@RequestBody List<Map<String,Object>> payloads) {
        return ResponseEntity.ok(service.bulkCreate(payloads));
    }

    // 8. Health / diagnostics for this service
    @GetMapping("/health")
    public ResponseEntity<Map<String,Object>> health() {
        Map<String,Object> m = new HashMap<>();
        m.put("service", "product-catalog");
        m.put("status", "UP");
        m.put("items", service.count());
        return ResponseEntity.ok(m);
    }

    // Feature flag example for enabling discounts
    public void exampleFeatureFlagUsage() {
        if (FeatureFlagReader.isFeatureEnabled("product_enable_discount")) {
            // Logic for discounts
        }
    }
}
