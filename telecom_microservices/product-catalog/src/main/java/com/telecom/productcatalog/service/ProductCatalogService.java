                        // Cross-service: If cart conversion tracking is enabled in shopping-cart, log conversion
                        if (FeatureFlagReader.isFeatureEnabled("cart_analytics_conversion_tracking")) {
                            System.out.println("[CART] Conversion tracked: " + cartId + " (cart_analytics_conversion_tracking)");
                        }
                // Cross-service: If predictive inventory is enabled in order-management, run prediction
                if (FeatureFlagReader.isFeatureEnabled("order_advanced_enable_predictive_inventory")) {
                    System.out.println("[PREDICTIVE] Inventory prediction run (order_advanced_enable_predictive_inventory)");
                }
        // Cross-service: If customer account hierarchy is enabled, allow assignment
        if (!FeatureFlagReader.isFeatureEnabled("customer_enable_account_hierarchy")) {
            throw new IllegalStateException("Account hierarchy assignment is disabled (customer_enable_account_hierarchy)");
        }
package com.telecom.productcatalog.service;

import org.springframework.stereotype.Service;
import com.telecom.common.FeatureFlagReader;
import com.telecom.productcatalog.model.*;
import com.telecom.productcatalog.config.ProductCatalogFeatureFlagConstants;
import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service layer for ProductCatalog with comprehensive telecom product management.
 * Implements complex business logic for devices, plans, bundles, pricing, inventory, and recommendations.
 * All operations are feature flag protected for runtime control without redeployment.
 */
@Service
public class ProductCatalogService {

    // Dedicated stores for different product types
    private final Map<String, TelecomProduct> productStore = new ConcurrentHashMap<>();
    private final Map<String, DeviceProduct> deviceStore = new ConcurrentHashMap<>();
    private final Map<String, ServicePlanProduct> planStore = new ConcurrentHashMap<>();
    private final Map<String, AddOnProduct> addOnStore = new ConcurrentHashMap<>();
    private final Map<String, ProductBundle> bundleStore = new ConcurrentHashMap<>();
    private final Map<String, ProductReview> reviewStore = new ConcurrentHashMap<>();
    private final Map<String, Object> genericStore = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        // Initialize with sample telecom products
        initializeSampleProducts();
    }

    private void initializeSampleProducts() {
        // Create sample devices
        for (int i = 1; i <= 3; i++) {
            DeviceProduct device = new DeviceProduct();
            String id = "DEVICE-" + i;
            device.setProductId(id);
            device.setProductName("Smartphone Model " + i);
            device.setDeviceType(i == 1 ? DeviceType.SMARTPHONE : (i == 2 ? DeviceType.TABLET : DeviceType.SMARTWATCH));
            device.setBrand(DeviceBrand.SAMSUNG);
            device.setModel("Galaxy S" + (21 + i));
            device.setBasePrice(800.0 + (i * 200));
            device.setCurrency("USD");
            device.setAvailabilityStatus(AvailabilityStatus.IN_STOCK);
            device.setCreatedAt(System.currentTimeMillis());
            deviceStore.put(id, device);
            productStore.put(id, device);
        }

        // Create sample plans
        for (int i = 1; i <= 2; i++) {
            ServicePlanProduct plan = new ServicePlanProduct();
            String id = "PLAN-" + i;
            plan.setProductId(id);
            plan.setProductName("Monthly Plan " + i);
            plan.setPlanType(PlanType.HYBRID_PLAN);
            plan.setBillingFrequency(BillingFrequency.MONTHLY);
            plan.setBasePrice(50.0 + (i * 30));
            plan.setCurrency("USD");
            plan.setAvailabilityStatus(AvailabilityStatus.IN_STOCK);
            plan.setCreatedAt(System.currentTimeMillis());
            planStore.put(id, plan);
            productStore.put(id, plan);
        }
    }

    // ==================== PRODUCT CREATION & MANAGEMENT ====================

    public TelecomProduct createProduct(TelecomProduct product) {
        if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_PRODUCT_CREATION)) {
            throw new RuntimeException("Product creation is disabled");
        }

        String productId = "PROD-" + UUID.randomUUID().toString();
        product.setProductId(productId);
        product.setCreatedAt(System.currentTimeMillis());
        
        // Initialize pricing info with tax calculation
        if (product.getPricingInfo() == null) {
            product.setPricingInfo(calculatePricing(product.getBasePrice()));
        }

        // Initialize inventory
        if (product.getInventoryInfo() == null) {
            InventoryInfo inventory = new InventoryInfo();
            inventory.setTotalQuantity(100);
            inventory.setAvailableQuantity(100);
            product.setInventoryInfo(inventory);
        }

        productStore.put(productId, product);
        return product;
    }

    public TelecomProduct updateProduct(String productId, TelecomProduct updates) {
        if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_PRODUCT_UPDATE)) {
            throw new RuntimeException("Product update is disabled");
        }

        TelecomProduct existing = productStore.get(productId);
        if (existing == null) return null;

        existing.setProductName(updates.getProductName() != null ? updates.getProductName() : existing.getProductName());
        existing.setDescription(updates.getDescription() != null ? updates.getDescription() : existing.getDescription());
        existing.setBasePrice(updates.getBasePrice() > 0 ? updates.getBasePrice() : existing.getBasePrice());
        existing.setAvailabilityStatus(updates.getAvailabilityStatus() != null ? updates.getAvailabilityStatus() : existing.getAvailabilityStatus());
        existing.setUpdatedAt(System.currentTimeMillis());

        // Recalculate pricing if base price changed
        if (updates.getBasePrice() > 0) {
            existing.setPricingInfo(calculatePricing(updates.getBasePrice()));
        }

        productStore.put(productId, existing);
        return existing;
    }

    public boolean deleteProduct(String productId) {
        if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_PRODUCT_DELETION)) {
            throw new RuntimeException("Product deletion is disabled");
        }

        boolean removed = productStore.remove(productId) != null;
        deviceStore.remove(productId);
        planStore.remove(productId);
        addOnStore.remove(productId);
        bundleStore.remove(productId);
        return removed;
    }

    // ==================== PRICING CALCULATIONS ====================

    private PricingInfo calculatePricing(double basePrice) {
        PricingInfo pricing = new PricingInfo();
        pricing.setBasePrice(basePrice);
        
        // Apply 10% discount for demonstration
        double discountPercentage = 10.0;
        double discountedPrice = basePrice * (1 - discountPercentage / 100.0);
        pricing.setDiscountPercentage(discountPercentage);
        pricing.setDiscountedPrice(discountedPrice);

        // Apply 8% tax
        double taxAmount = discountedPrice * 0.08;
        pricing.setTaxAmount(taxAmount);

        // Final price
        double finalPrice = discountedPrice + taxAmount;
        pricing.setFinalPrice(finalPrice);

        return pricing;
    }

    public double calculateBundlePrice(List<String> productIds) {
        if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_BUNDLE_PRICING)) {
            throw new RuntimeException("Bundle pricing is disabled");
        }

        double totalPrice = 0.0;
        for (String productId : productIds) {
            TelecomProduct product = productStore.get(productId);
            if (product != null) {
                totalPrice += product.getBasePrice();
            }
        }

        // Apply bundle discount: 15% for 2+ items
        double bundleDiscount = productIds.size() >= 2 ? 0.15 : 0.0;
        double discountedPrice = totalPrice * (1 - bundleDiscount);
        
        // Apply tax
        double taxAmount = discountedPrice * 0.08;
        return discountedPrice + taxAmount;
    }

    public double applyTieredPricing(double basePrice, int quantity) {
        if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_TIERED_PRICING)) {
            return basePrice;
        }

        // Tiered pricing: quantity discounts
        double discountPercentage = 0.0;
        if (quantity >= 100) {
            discountPercentage = 20.0; // 20% for 100+
        } else if (quantity >= 50) {
            discountPercentage = 15.0; // 15% for 50-99
        } else if (quantity >= 10) {
            discountPercentage = 10.0; // 10% for 10-49
        } else if (quantity >= 1) {
            discountPercentage = 0.0; // No discount for 1-9
        }

        return basePrice * (1 - discountPercentage / 100.0);
    }

    public double applyVolumeDiscount(double basePrice, int orderQuantity) {
        if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_VOLUME_DISCOUNTS)) {
            return basePrice;
        }

        double discountRate = 0.0;
        if (orderQuantity >= 500) {
            discountRate = 0.25; // 25% discount for 500+ units
        } else if (orderQuantity >= 200) {
            discountRate = 0.20; // 20% for 200-499
        } else if (orderQuantity >= 100) {
            discountRate = 0.15; // 15% for 100-199
        } else if (orderQuantity >= 50) {
            discountRate = 0.10; // 10% for 50-99
        }

        return basePrice * (1 - discountRate);
    }

    // ==================== DEVICE MANAGEMENT ====================

    public DeviceProduct createDevice(DeviceProduct device) {
        if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_DEVICES)) {
            throw new RuntimeException("Device creation is disabled");
        }

        String deviceId = "DEVICE-" + UUID.randomUUID().toString();
        device.setProductId(deviceId);
        device.setCreatedAt(System.currentTimeMillis());
        device.setProductType(ProductType.DEVICE);

        if (device.getPricingInfo() == null) {
            device.setPricingInfo(calculatePricing(device.getBasePrice()));
        }

        if (device.getInventoryInfo() == null) {
            InventoryInfo inventory = new InventoryInfo();
            inventory.setTotalQuantity(50);
            inventory.setAvailableQuantity(50);
            device.setInventoryInfo(inventory);
        }

        deviceStore.put(deviceId, device);
        productStore.put(deviceId, device);
        return device;
    }

    public boolean checkDeviceCompatibility(String deviceId, String planId) {
        if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_DEVICE_COMPATIBILITY)) {
            return true; // Assume compatible if feature disabled
        }

        DeviceProduct device = deviceStore.get(deviceId);
        ServicePlanProduct plan = planStore.get(planId);

        if (device == null || plan == null) return false;

        if (device.getCompatibility() != null && device.getCompatibility().getCompatiblePlans() != null) {
            return device.getCompatibility().getCompatiblePlans().contains(planId);
        }

        return true;
    }

    public double calculateDeviceFinancing(String deviceId, int installmentMonths) {
        if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_DEVICE_FINANCING)) {
            throw new RuntimeException("Device financing is disabled");
        }

        DeviceProduct device = deviceStore.get(deviceId);
        if (device == null) return 0.0;

        double devicePrice = device.getBasePrice();
        double interestRate = 0.05; // 5% interest rate
        
        // Simple interest calculation: (Principal * Rate * Time) / 100
        double interest = (devicePrice * interestRate * installmentMonths) / 100.0;
        double totalWithInterest = devicePrice + interest;
        
        return totalWithInterest / installmentMonths; // Monthly payment
    }

    public double calculateTradeInValue(String deviceId, String conditionGrade) {
        if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_DEVICE_TRADE_IN)) {
            throw new RuntimeException("Device trade-in is disabled");
        }

        DeviceProduct device = deviceStore.get(deviceId);
        if (device == null || device.getTradeInInfo() == null) return 0.0;

        double baseTradeInValue = device.getTradeInInfo().getBaseTradeInValue();

        // Apply condition-based discount
        double conditionMultiplier = 1.0;
        switch (conditionGrade != null ? conditionGrade : "FAIR") {
            case "EXCELLENT": conditionMultiplier = 0.85; break;
            case "VERY_GOOD": conditionMultiplier = 0.75; break;
            case "GOOD": conditionMultiplier = 0.60; break;
            case "FAIR": conditionMultiplier = 0.45; break;
            case "POOR": conditionMultiplier = 0.25; break;
            default: conditionMultiplier = 0.45;
        }

        return baseTradeInValue * conditionMultiplier;
    }

    // ==================== SERVICE PLAN MANAGEMENT ====================

    public ServicePlanProduct createServicePlan(ServicePlanProduct plan) {
        if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_SERVICE_PLANS)) {
            throw new RuntimeException("Service plan creation is disabled");
        }

        String planId = "PLAN-" + UUID.randomUUID().toString();
        plan.setProductId(planId);
        plan.setCreatedAt(System.currentTimeMillis());
        plan.setProductType(ProductType.SERVICE_PLAN);

        if (plan.getPricingInfo() == null) {
            plan.setPricingInfo(calculatePricing(plan.getBasePrice()));
        }

        planStore.put(planId, plan);
        productStore.put(planId, plan);
        return plan;
    }

    public Map<String, Object> comparePlans(List<String> planIds) {
        if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_PLAN_COMPARISON)) {
            throw new RuntimeException("Plan comparison is disabled");
        }

        Map<String, Object> comparison = new HashMap<>();
        List<Map<String, Object>> planDetails = new ArrayList<>();

        for (String planId : planIds) {
            ServicePlanProduct plan = planStore.get(planId);
            if (plan != null) {
                Map<String, Object> details = new HashMap<>();
                details.put("planId", planId);
                details.put("name", plan.getProductName());
                details.put("price", plan.getBasePrice());
                details.put("type", plan.getPlanType());
                if (plan.getDataAllowance() != null) {
                    details.put("dataAmount", plan.getDataAllowance().getDataAmount());
                    details.put("dataUnit", plan.getDataAllowance().getDataUnit());
                }
                if (plan.getVoiceAllowance() != null) {
                    details.put("minutes", plan.getVoiceAllowance().getMinutesPerMonth());
                }
                planDetails.add(details);
            }
        }

        comparison.put("totalPlans", planIds.size());
        comparison.put("plans", planDetails);
        return comparison;
    }

    // ==================== BUNDLING & PROMOTIONS ====================

    public ProductBundle createBundle(List<String> productIds, String bundleName) {
        if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_BUNDLES)) {
            throw new RuntimeException("Bundle creation is disabled");
        }

        ProductBundle bundle = new ProductBundle();
        String bundleId = "BUNDLE-" + UUID.randomUUID().toString();
        bundle.setProductId(bundleId);
        bundle.setProductName(bundleName);
        bundle.setIncludedProductIds(productIds);
        bundle.setProductType(ProductType.BUNDLE);
        bundle.setCreatedAt(System.currentTimeMillis());

        double bundlePrice = calculateBundlePrice(productIds);
        bundle.setBundlePrice(bundlePrice);

        // Calculate savings
        double regularPrice = productIds.stream()
                .mapToDouble(id -> {
                    TelecomProduct p = productStore.get(id);
                    return p != null ? p.getBasePrice() : 0.0;
                })
                .sum();
        
        double savings = regularPrice - bundlePrice;
        int savingsPercentage = (int) ((savings / regularPrice) * 100);
        bundle.setSavingsPercentage(savingsPercentage);
        bundle.setBundleDiscount(savings);

        bundle.setBasePrice(bundlePrice);
        bundleStore.put(bundleId, bundle);
        productStore.put(bundleId, bundle);
        return bundle;
    }

    public List<TelecomProduct> getPromotionalItems() {
        if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_PROMOTIONS)) {
            return new ArrayList<>();
        }

        return productStore.values().stream()
                .filter(p -> p.getApplicablePromotions() != null && !p.getApplicablePromotions().isEmpty())
                .collect(Collectors.toList());
    }

    // ==================== INVENTORY MANAGEMENT ====================

    public boolean reserveInventory(String productId, int quantity) {
        if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_INVENTORY_TRACKING)) {
            return true;
        }

        TelecomProduct product = productStore.get(productId);
        if (product == null || product.getInventoryInfo() == null) return false;

        InventoryInfo inventory = product.getInventoryInfo();
        if (inventory.getAvailableQuantity() >= quantity) {
            inventory.setAvailableQuantity(inventory.getAvailableQuantity() - quantity);
            inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);
            inventory.setLastStockUpdateTime(System.currentTimeMillis());
            return true;
        }

        // Check backorder capability
        if (FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_BACKORDER)) {
            product.setAvailabilityStatus(AvailabilityStatus.OUT_OF_STOCK);
            return true; // Allow backorder
        }

        return false;
    }

    public List<String> checkLowStockProducts(int threshold) {
        if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_STOCK_ALERTS)) {
            return new ArrayList<>();
        }

        return productStore.values().stream()
                .filter(p -> p.getInventoryInfo() != null && p.getInventoryInfo().getAvailableQuantity() <= threshold)
                .map(TelecomProduct::getProductId)
                .collect(Collectors.toList());
    }

    // ==================== RATINGS & REVIEWS ====================

    public ProductReview addReview(String productId, ProductReview review) {
        if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_REVIEWS)) {
            throw new RuntimeException("Reviews are disabled");
        }

        String reviewId = "REVIEW-" + UUID.randomUUID().toString();
        review.setReviewId(reviewId);
        review.setReviewDate(System.currentTimeMillis());
        
        reviewStore.put(reviewId, review);

        // Update product rating
        TelecomProduct product = productStore.get(productId);
        if (product != null) {
            List<ProductReview> productReviews = product.getReviews() != null ? product.getReviews() : new ArrayList<>();
            productReviews.add(review);
            product.setReviews(productReviews);

            // Calculate average rating
            double avgRating = productReviews.stream()
                    .mapToInt(ProductReview::getRatingScore)
                    .average()
                    .orElse(0.0);
            product.setRating((int) avgRating);
            product.setReviewCount(productReviews.size());
        }

        return review;
    }

    // ==================== SEARCH & FILTERING ====================

    public List<TelecomProduct> searchProducts(String keyword) {
        if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_PRODUCT_SEARCH)) {
            return new ArrayList<>();
        }

        String lowerKeyword = keyword.toLowerCase();
        return productStore.values().stream()
                .filter(p -> p.getProductName().toLowerCase().contains(lowerKeyword) ||
                            (p.getDescription() != null && p.getDescription().toLowerCase().contains(lowerKeyword)))
                .collect(Collectors.toList());
    }

    public List<TelecomProduct> filterByCategory(String category) {
        if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_CATEGORY_FILTERING)) {
            return new ArrayList<>();
        }

        return productStore.values().stream()
                .filter(p -> category.equals(p.getCategory()))
                .collect(Collectors.toList());
    }

    public List<TelecomProduct> filterByPriceRange(double minPrice, double maxPrice) {
        if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_ADVANCED_FILTERS)) {
            return new ArrayList<>();
        }

        return productStore.values().stream()
                .filter(p -> p.getBasePrice() >= minPrice && p.getBasePrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    // ==================== RECOMMENDATIONS ====================

    public List<TelecomProduct> getBestSellers(int limit) {
        if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_BEST_SELLERS)) {
            return new ArrayList<>();
        }

        return productStore.values().stream()
                .sorted((a, b) -> Integer.compare(b.getReviewCount(), a.getReviewCount()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<String> getCompatibleAddOns(String planId) {
        if (!FeatureFlagReader.isFeatureEnabled(ProductCatalogFeatureFlagConstants.CATALOG_ENABLE_CROSS_SELL)) {
            return new ArrayList<>();
        }

        return addOnStore.values().stream()
                .filter(addOn -> addOn.getCompatiblePlans() != null && addOn.getCompatiblePlans().contains(planId))
                .map(AddOnProduct::getProductId)
                .collect(Collectors.toList());
    }

    // ==================== LEGACY GENERIC METHODS ====================

    public List<Object> listAll() {
        return new ArrayList<>(productStore.values());
    }

    public Object getById(String id) {
        return productStore.get(id);
    }

    public Object create(Map<String,Object> payload) {
        String id = UUID.randomUUID().toString();
        payload.put("id", id);
        payload.put("createdAt", System.currentTimeMillis());
        genericStore.put(id, payload);
        return payload;
    }

    public Object update(String id, Map<String,Object> payload) {
        Object existing = genericStore.get(id);
        if (existing instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String,Object> map = (Map<String,Object>) existing;
            map.putAll(payload);
            map.put("updatedAt", System.currentTimeMillis());
            genericStore.put(id, map);
            return map;
        }
        return null;
    }

    public boolean delete(String id) {
        boolean removed = productStore.remove(id) != null || genericStore.remove(id) != null;
        deviceStore.remove(id);
        planStore.remove(id);
        addOnStore.remove(id);
        bundleStore.remove(id);
        return removed;
    }

    public List<Object> search(Map<String,String> params) {
        return productStore.values().stream()
                .filter(p -> params.entrySet().stream().allMatch(entry -> {
                    String field = entry.getKey();
                    String value = entry.getValue().toLowerCase();
                    if ("name".equalsIgnoreCase(field)) {
                        return p.getProductName() != null && p.getProductName().toLowerCase().contains(value);
                    }
                    if ("category".equalsIgnoreCase(field)) {
                        return p.getCategory() != null && p.getCategory().toLowerCase().contains(value);
                    }
                    if ("type".equalsIgnoreCase(field)) {
                        return p.getProductType() != null && p.getProductType().toString().toLowerCase().contains(value);
                    }
                    return true;
                }))
                .map(p -> (Object) p)
                .collect(Collectors.toList());
    }

    public List<Object> bulkCreate(List<Map<String,Object>> payloads) {
        List<Object> created = new ArrayList<>();
        for (Map<String,Object> payload : payloads) {
            created.add(create(payload));
        }
        return created;
    }

    public int count() {
        return productStore.size();
    }
}
