package com.telecom.shoppingcart.controller;

import com.telecom.shoppingcart.model.*;
import com.telecom.shoppingcart.service.ShoppingCartService;
import com.telecom.shoppingcart.util.ShoppingCartFeatureFlagConstants;
import com.telecom.common.FeatureFlagReader;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;

/**
 * REST Controller for Shopping Cart endpoints
 * All endpoints are protected with feature flags for granular control
 */
@RestController
@RequestMapping("/api/shopping-cart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService service;
    
    @Autowired
    private FeatureFlagReader featureFlagReader;

    // ==================== CART MANAGEMENT ENDPOINTS ====================

    @PostMapping
    public ResponseEntity<?> createCart(@RequestParam String customerId,
                                        @RequestParam String sessionId) {
        if (!featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_OPERATIONS_ADD_TO_CART)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: Cart creation");
        }
        ShoppingCart cart = service.createCart(customerId, sessionId);
        return ResponseEntity.status(HttpStatus.CREATED).body(cart);
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<?> getCart(@PathVariable String cartId) {
        ShoppingCart cart = service.getCart(cartId);
        return cart != null ? ResponseEntity.ok(cart) : ResponseEntity.notFound().build();
    }

    @PostMapping("/{cartId}/clear")
    public ResponseEntity<?> clearCart(@PathVariable String cartId) {
        if (!featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_OPERATIONS_CLEAR_CART)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: Clear cart");
        }
        ShoppingCart cart = service.clearCart(cartId);
        return cart != null ? ResponseEntity.ok(cart) : ResponseEntity.notFound().build();
    }

    @PostMapping("/{cartId}/abandon")
    public ResponseEntity<?> abandonCart(@PathVariable String cartId) {
        boolean success = service.abandonCart(cartId);
        return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // ==================== CART ITEMS ENDPOINTS ====================

    @PostMapping("/{cartId}/items")
    public ResponseEntity<?> addToCart(@PathVariable String cartId,
                                       @RequestParam String productId,
                                       @RequestParam ProductType productType,
                                       @RequestParam String productName,
                                       @RequestParam double price,
                                       @RequestParam(defaultValue = "1") int quantity) {
        if (!featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_OPERATIONS_ADD_TO_CART)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: Add to cart");
        }
        CartItem item = service.addToCart(cartId, productId, productType, productName, price, quantity);
        return item != null ? ResponseEntity.status(HttpStatus.CREATED).body(item) : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<?> removeFromCart(@PathVariable String cartId,
                                            @PathVariable String productId) {
        if (!featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_OPERATIONS_REMOVE_FROM_CART)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: Remove from cart");
        }
        CartItem removed = service.removeFromCart(cartId, productId);
        return removed != null ? ResponseEntity.ok(removed) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{cartId}/items/{productId}/quantity")
    public ResponseEntity<?> updateQuantity(@PathVariable String cartId,
                                            @PathVariable String productId,
                                            @RequestParam int quantity) {
        if (!featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_OPERATIONS_UPDATE_QUANTITY)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: Update quantity");
        }
        CartItem updated = service.updateQuantity(cartId, productId, quantity);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    // ==================== INVENTORY ENDPOINTS ====================

    @GetMapping("/{cartId}/inventory-check")
    public ResponseEntity<?> checkInventory(@PathVariable String cartId,
                                            @RequestParam String productId,
                                            @RequestParam int requiredQuantity) {
        if (!featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_INVENTORY_STOCK_CHECKING)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: Inventory check");
        }
        boolean inStock = service.checkInventory(productId, requiredQuantity);
        return ResponseEntity.ok(Collections.singletonMap("inStock", inStock));
    }

    @GetMapping("/{cartId}/inventory-info")
    public ResponseEntity<?> getInventoryInfo(@PathVariable String cartId,
                                              @RequestParam String productId,
                                              @RequestParam(required = false) String warehouse) {
        if (!featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_INVENTORY_WAREHOUSE_SELECTION)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: Warehouse selection");
        }
        InventoryInfo info = service.getInventoryInfo(productId, warehouse);
        return ResponseEntity.ok(info);
    }

    // ==================== PRICING & PROMOTIONS ENDPOINTS ====================

    @PostMapping("/{cartId}/promo-code")
    public ResponseEntity<?> applyPromoCode(@PathVariable String cartId,
                                            @RequestParam String promoCode) {
        if (!featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_PRICING_PROMOTIONAL_CODES)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: Promotional codes");
        }
        boolean applied = service.applyPromotionalCode(cartId, promoCode);
        return applied ? ResponseEntity.ok().body("Promo code applied") : ResponseEntity.badRequest().build();
    }

    // ==================== BUNDLING ENDPOINTS ====================

    @GetMapping("/{cartId}/frequently-bought")
    public ResponseEntity<?> getFrequentlyBoughtTogether(@PathVariable String cartId,
                                                         @RequestParam String productId) {
        if (!featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_BUNDLING_FREQUENTLY_BOUGHT_TOGETHER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: Frequently bought together");
        }
        List<CartItem> recommendations = service.getFrequentlyBoughtTogether(productId);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/{cartId}/bundle-price")
    public ResponseEntity<?> getBundlePrice(@PathVariable String cartId,
                                            @RequestParam List<String> productIds) {
        if (!featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_BUNDLING_DEVICE_PLAN_BUNDLES)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: Device-plan bundles");
        }
        double bundlePrice = service.getBundlePrice(productIds);
        return ResponseEntity.ok(Collections.singletonMap("bundlePrice", bundlePrice));
    }

    // ==================== DEVICE FINANCING ENDPOINTS ====================

    @PostMapping("/{cartId}/financing-option")
    public ResponseEntity<?> calculateFinancing(@PathVariable String cartId,
                                                @RequestParam double devicePrice,
                                                @RequestParam FinancingTerms terms) {
        if (!featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_FINANCING_INSTALLMENT_PLANS)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: Installment plans");
        }
        DeviceFinancingOption option = service.calculateFinancing(devicePrice, terms);
        return ResponseEntity.ok(option);
    }

    // ==================== COMPATIBILITY ENDPOINTS ====================

    @GetMapping("/{cartId}/compatibility-check")
    public ResponseEntity<?> validateCompatibility(@PathVariable String cartId) {
        if (!featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_COMPATIBILITY_DEVICE_PLAN_COMPATIBILITY)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: Compatibility check");
        }
        List<CompatibilityWarning> warnings = service.validateCompatibility(cartId);
        return ResponseEntity.ok(Collections.singletonMap("warnings", warnings));
    }

    // ==================== CHECKOUT ENDPOINTS ====================

    @PostMapping("/{cartId}/checkout")
    public ResponseEntity<?> checkout(@PathVariable String cartId,
                                      @RequestBody CheckoutRequest checkoutRequest) {
        if (!featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_CHECKOUT_ORDER_CREATION)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: Checkout");
        }
        Order order = service.checkout(cartId, checkoutRequest);
        return order != null ? ResponseEntity.status(HttpStatus.CREATED).body(order) : ResponseEntity.badRequest().build();
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable String orderId) {
        Order order = service.getOrder(orderId);
        return order != null ? ResponseEntity.ok(order) : ResponseEntity.notFound().build();
    }

    // ==================== ANALYTICS ENDPOINTS ====================

    @GetMapping("/analytics/metrics")
    public ResponseEntity<?> getCartAnalytics() {
        if (!featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_ANALYTICS_CART_METRICS)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Feature not enabled: Cart analytics");
        }
        CartAnalytics analytics = service.getCartAnalytics();
        return ResponseEntity.ok(analytics);
    }

    // ==================== SYSTEM ENDPOINTS ====================

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("service", "shopping-cart");
        health.put("status", "UP");
        health.put("totalCarts", service.getAllCarts().size());
        return ResponseEntity.ok(health);
    }

    @GetMapping("/features")
    public ResponseEntity<Map<String, Object>> getEnabledFeatures() {
        Map<String, Object> features = new HashMap<>();
        features.put("addToCart", featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_OPERATIONS_ADD_TO_CART));
        features.put("removeFromCart", featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_OPERATIONS_REMOVE_FROM_CART));
        features.put("updateQuantity", featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_OPERATIONS_UPDATE_QUANTITY));
        features.put("clearCart", featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_OPERATIONS_CLEAR_CART));
        features.put("bundleDiscounts", featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_PRICING_BUNDLE_DISCOUNTS));
        features.put("promotionalCodes", featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_PRICING_PROMOTIONAL_CODES));
        features.put("tieredPricing", featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_PRICING_TIERED_PRICING));
        features.put("volumeDiscounts", featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_PRICING_VOLUME_DISCOUNTS));
        features.put("loyaltyRewards", featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_PRICING_LOYALTY_REWARDS));
        features.put("inventoryCheck", featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_INVENTORY_STOCK_CHECKING));
        features.put("warehouseSelection", featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_INVENTORY_WAREHOUSE_SELECTION));
        features.put("deviceFinancing", featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_FINANCING_INSTALLMENT_PLANS));
        features.put("compatibilityCheck", featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_COMPATIBILITY_DEVICE_PLAN_COMPATIBILITY));
        features.put("checkout", featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_CHECKOUT_ORDER_CREATION));
        features.put("cartAnalytics", featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_ANALYTICS_CART_METRICS));
        features.put("salesTax", featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_TAX_SALES_TAX));
        features.put("activationFees", featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_TAX_ACTIVATION_FEES));
        features.put("shippingFees", featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_TAX_SHIPPING_FEES));
        return ResponseEntity.ok(features);
    }
}
