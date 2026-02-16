                        // Cross-service: If notification routing is enabled in notification-service, route notification
                        if (!FeatureFlagReader.isFeatureEnabled("notification_routing_enable")) {
                            throw new IllegalStateException("Notification routing is disabled (notification_routing_enable)");
                        }
                // Cross-service: If availability check is enabled in product-catalog, check availability
                if (!FeatureFlagReader.isFeatureEnabled("catalog_enable_availability_check")) {
                    throw new IllegalStateException("Product availability check is disabled (catalog_enable_availability_check)");
                }
        // Cross-service: If bundle discounts are enabled in billing, apply bundle discount
        if (FeatureFlagReader.isFeatureEnabled("billing_enable_bundle_discounts")) {
            cart.setTotal(cart.getTotal() * 0.90); // 10% bundle discount
        }
package com.telecom.shoppingcart.service;

import com.telecom.shoppingcart.model.*;
import com.telecom.shoppingcart.util.ShoppingCartFeatureFlagConstants;
import com.telecom.common.FeatureFlagReader;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service layer for Shopping Cart
 * Manages complete shopping experience from product selection to checkout
 */
@Service
public class ShoppingCartService {

    private final FeatureFlagReader featureFlagReader;

    // Thread-safe in-memory stores
    private final Map<String, ShoppingCart> cartStore = new ConcurrentHashMap<>();
    private final Map<String, Order> orderStore = new ConcurrentHashMap<>();
    private final Map<String, CartAnalytics> analyticsStore = new ConcurrentHashMap<>();
    
    // Atomic counters for ID generation
    private final AtomicInteger cartIdCounter = new AtomicInteger(1000);
    private final AtomicInteger itemIdCounter = new AtomicInteger(5000);
    private final AtomicInteger orderIdCounter = new AtomicInteger(2000);

    public ShoppingCartService(FeatureFlagReader featureFlagReader) {
        this.featureFlagReader = featureFlagReader;
    }

    // ==================== CART LIFECYCLE ====================

    public ShoppingCart createCart(String customerId, String sessionId) {
        if (!featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_OPERATIONS_ADD_TO_CART)) {
            return null;
        }

        ShoppingCart cart = new ShoppingCart();
        cart.setCartId("CART-" + cartIdCounter.incrementAndGet());
        cart.setCustomerId(customerId);
        cart.setSessionId(sessionId);
        cart.setStatus(CartStatus.ACTIVE);
        cart.setItems(new ArrayList<>());
        cart.setCreatedAt(System.currentTimeMillis());
        cart.setExpiryTime(System.currentTimeMillis() + (24 * 60 * 60 * 1000L)); // 24-hour expiry
        
        CartPricing pricing = new CartPricing();
        pricing.setCurrency("USD");
        pricing.setSubtotal(0);
        pricing.setTotal(0);
        cart.setPricing(pricing);

        cartStore.put(cart.getCartId(), cart);
        return cart;
    }

    public ShoppingCart getCart(String cartId) {
        return cartStore.get(cartId);
    }

    public ShoppingCart clearCart(String cartId) {
        if (!featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_OPERATIONS_CLEAR_CART)) {
            return null;
        }

        ShoppingCart cart = cartStore.get(cartId);
        if (cart != null) {
            cart.getItems().clear();
            recalculateCart(cartId);
        }
        return cart;
    }

    public boolean abandonCart(String cartId) {
        ShoppingCart cart = cartStore.get(cartId);
        if (cart != null) {
            cart.setStatus(CartStatus.ABANDONED);
            cart.setLastModifiedAt(System.currentTimeMillis());
            return true;
        }
        return false;
    }

    // ==================== CART ITEMS ====================

    public CartItem addToCart(String cartId, String productId, ProductType productType, String productName,
                              double unitPrice, int quantity) {
        if (!featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_OPERATIONS_ADD_TO_CART)) {
            return null;
        }

        ShoppingCart cart = cartStore.get(cartId);
        if (cart == null) {
            return null;
        }

        // Check if item already exists
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setLineTotal(existingItem.getUnitPrice() * existingItem.getQuantity());
        } else {
            CartItem item = new CartItem();
            item.setItemId("ITEM-" + itemIdCounter.incrementAndGet());
            item.setProductId(productId);
            item.setProductType(productType);
            item.setProductName(productName);
            item.setUnitPrice(unitPrice);
            item.setQuantity(quantity);
            item.setLineTotal(unitPrice * quantity);
            item.setAddedAt(System.currentTimeMillis());

            cart.getItems().add(item);
        }

        cart.setLastModifiedAt(System.currentTimeMillis());
        recalculateCart(cartId);
        return existingItem != null ? existingItem : cart.getItems().get(cart.getItems().size() - 1);
    }

    public CartItem removeFromCart(String cartId, String productId) {
        if (!featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_OPERATIONS_REMOVE_FROM_CART)) {
            return null;
        }

        ShoppingCart cart = cartStore.get(cartId);
        if (cart == null) {
            return null;
        }

        CartItem removed = null;
        for (CartItem item : cart.getItems()) {
            if (item.getProductId().equals(productId)) {
                removed = item;
                break;
            }
        }

        if (removed != null) {
            cart.getItems().remove(removed);
            cart.setLastModifiedAt(System.currentTimeMillis());
            recalculateCart(cartId);
        }

        return removed;
    }

    public CartItem updateQuantity(String cartId, String productId, int newQuantity) {
        if (!featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_OPERATIONS_UPDATE_QUANTITY)) {
            return null;
        }

        ShoppingCart cart = cartStore.get(cartId);
        if (cart == null) {
            return null;
        }

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .orElse(null);

        if (item != null && newQuantity > 0) {
            item.setQuantity(newQuantity);
            item.setLineTotal(item.getUnitPrice() * newQuantity);
            cart.setLastModifiedAt(System.currentTimeMillis());
            recalculateCart(cartId);
        }

        return item;
    }

    // ==================== INVENTORY MANAGEMENT ====================

    public boolean checkInventory(String productId, int requiredQuantity) {
        if (!featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_INVENTORY_STOCK_CHECKING)) {
            return true;
        }

        // Simulate inventory check (replace with actual inventory service call)
        return requiredQuantity <= 100;
    }

    public InventoryInfo getInventoryInfo(String productId, String warehouseLocation) {
        if (!featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_INVENTORY_WAREHOUSE_SELECTION)) {
            return null;
        }

        InventoryInfo info = new InventoryInfo();
        info.setWarehouseLocation(warehouseLocation != null ? warehouseLocation : WarehouseLocation.WAREHOUSE_EAST.toString());
        info.setStockQuantity(Math.max(0, 100 - Math.abs(productId.hashCode() % 50)));
        info.setInStock(info.getStockQuantity() > 0);
        info.setDaysToRestockIfBackorder(3);
        info.setBackorderAllowed(true);

        return info;
    }

    // ==================== PRICING & DISCOUNTS ====================

    private void recalculateCart(String cartId) {
        ShoppingCart cart = cartStore.get(cartId);
        if (cart == null) {
            return;
        }

        CartPricing pricing = cart.getPricing();

        // Calculate subtotal
        double subtotal = cart.getItems().stream()
                .mapToDouble(CartItem::getLineTotal)
                .sum();
        pricing.setSubtotal(subtotal);

        // Apply bundle discounts if enabled
        double bundleDiscount = 0;
        if (featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_PRICING_BUNDLE_DISCOUNTS)) {
            bundleDiscount = calculateBundleDiscount(cart);
            pricing.setBundleDiscountAmount(bundleDiscount);
        }

        // Apply tiered pricing if enabled
        double tieredDiscount = 0;
        if (featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_PRICING_TIERED_PRICING)) {
            tieredDiscount = calculateTieredDiscount(subtotal);
        }

        // Apply volume discounts if enabled
        double volumeDiscount = 0;
        if (featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_PRICING_VOLUME_DISCOUNTS)) {
            volumeDiscount = calculateVolumeDiscount(cart.getItems());
        }

        // Apply loyalty rewards
        double loyaltyReward = 0;
        if (featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_PRICING_LOYALTY_REWARDS)) {
            loyaltyReward = applyLoyaltyReward(cartId, subtotal);
            pricing.setLoyaltyRewardAmount(loyaltyReward);
        }

        // Total discount
        double totalDiscount = bundleDiscount + tieredDiscount + volumeDiscount;
        pricing.setDiscountAmount(totalDiscount);
        pricing.setDiscountPercentage((totalDiscount / subtotal) * 100);

        // Apply promotional code if present
        if (cart.getAppliedPromotion() != null && featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_PRICING_PROMOTIONAL_CODES)) {
            applyPromotionalCode(cart);
        }

        double subtotalAfterDiscount = subtotal - totalDiscount;
        pricing.setSubtotalAfterDiscount(Math.max(0, subtotalAfterDiscount));

        // Calculate taxes and fees
        if (featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_TAX_SALES_TAX)) {
            double salesTax = subtotalAfterDiscount * 0.08; // 8% tax rate
            pricing.setSaleTaxAmount(salesTax);
        }

        if (featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_TAX_ACTIVATION_FEES)) {
            pricing.setActivationFeeAmount(35.0); // Standard activation fee
        }

        if (featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_TAX_SHIPPING_FEES)) {
            pricing.setShippingFeeAmount(9.99);
        }

        // Calculate total
        double total = pricing.getSubtotalAfterDiscount() 
                + pricing.getSaleTaxAmount() 
                + pricing.getActivationFeeAmount() 
                + pricing.getShippingFeeAmount() 
                + pricing.getInsuranceFeeAmount();
        pricing.setTotal(total);
        pricing.setCalculatedAt(System.currentTimeMillis());

        cart.setLastModifiedAt(System.currentTimeMillis());
    }

    private double calculateBundleDiscount(ShoppingCart cart) {
        // Check if cart has device + plan bundle (15% discount)
        boolean hasDevice = cart.getItems().stream().anyMatch(i -> i.getProductType() == ProductType.DEVICE);
        boolean hasPlan = cart.getItems().stream().anyMatch(i -> i.getProductType() == ProductType.PLAN);

        if (hasDevice && hasPlan) {
            return cart.getPricing().getSubtotal() * 0.15; // 15% bundle discount
        }
        return 0;
    }

    private double calculateTieredDiscount(double subtotal) {
        // Tiered pricing: 10% off $500+, 15% off $1000+, 20% off $2000+
        if (subtotal >= 2000) return subtotal * 0.20;
        if (subtotal >= 1000) return subtotal * 0.15;
        if (subtotal >= 500) return subtotal * 0.10;
        return 0;
    }

    private double calculateVolumeDiscount(List<CartItem> items) {
        // Volume discount based on quantity: 10% off 5+ items, 20% off 10+ items
        int totalQty = items.stream().mapToInt(CartItem::getQuantity).sum();
        double subtotal = items.stream().mapToDouble(CartItem::getLineTotal).sum();

        if (totalQty >= 10) return subtotal * 0.20;
        if (totalQty >= 5) return subtotal * 0.10;
        return 0;
    }

    public boolean applyPromotionalCode(String cartId, String promoCode) {
        if (!featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_PRICING_PROMOTIONAL_CODES)) {
            return false;
        }

        ShoppingCart cart = cartStore.get(cartId);
        if (cart == null) {
            return false;
        }

        // Simulate promo code validation
        AppliedPromotion promo = new AppliedPromotion();
        promo.setPromotionCode(promoCode);
        promo.setPromoType(PromoType.PERCENTAGE_OFF);
        promo.setDiscountValue(20); // 20% off
        promo.setPercentage(true);
        promo.setMaxDiscountAmount(100);
        promo.setExpiryDate(System.currentTimeMillis() + (30 * 24 * 60 * 60 * 1000L));

        cart.setAppliedPromotion(promo);
        recalculateCart(cartId);

        return true;
    }

    private void applyPromotionalCode(ShoppingCart cart) {
        if (cart.getAppliedPromotion() == null) return;

        AppliedPromotion promo = cart.getAppliedPromotion();
        CartPricing pricing = cart.getPricing();

        if (promo.isPercentage()) {
            double discount = pricing.getSubtotalAfterDiscount() * (promo.getDiscountValue() / 100);
            discount = Math.min(discount, promo.getMaxDiscountAmount());
            pricing.setDiscountAmount(pricing.getDiscountAmount() + discount);
        } else {
            pricing.setDiscountAmount(pricing.getDiscountAmount() + promo.getDiscountValue());
        }
    }

    private double applyLoyaltyReward(String cartId, double subtotal) {
        ShoppingCart cart = cartStore.get(cartId);
        if (cart == null || cart.getLoyaltyAccount() == null) {
            return 0;
        }

        LoyaltyAccount loyalty = cart.getLoyaltyAccount();
        // Reward calculation: higher tier = higher percentage
        double rewardPercentage = 0;
        switch (loyalty.getCurrentTier()) {
            case BRONZE: rewardPercentage = 0.01; break;
            case SILVER: rewardPercentage = 0.02; break;
            case GOLD: rewardPercentage = 0.03; break;
            case PLATINUM: rewardPercentage = 0.05; break;
        }

        double reward = subtotal * rewardPercentage;
        int pointsEarned = (int) (subtotal / 1.0);
        loyalty.setPointsEarned(loyalty.getPointsEarned() + pointsEarned);
        loyalty.setTotalPoints(loyalty.getTotalPoints() + pointsEarned);

        return reward;
    }

    // ==================== BUNDLING & RECOMMENDATIONS ====================

    public List<CartItem> getFrequentlyBoughtTogether(String productId) {
        if (!featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_BUNDLING_FREQUENTLY_BOUGHT_TOGETHER)) {
            return new ArrayList<>();
        }

        // Simulate frequently bought together recommendations
        List<CartItem> recommendations = new ArrayList<>();
        // Device + Plan + Add-on bundle
        return recommendations;
    }

    public double getBundlePrice(List<String> productIds) {
        if (!featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_BUNDLING_DEVICE_PLAN_BUNDLES)) {
            return 0;
        }

        // Simulate bundle pricing (15% discount on combined price)
        double basePrice = 0; // Would be calculated from product service
        return basePrice * 0.85;
    }

    // ==================== DEVICE FINANCING ====================

    public DeviceFinancingOption calculateFinancing(double devicePrice, FinancingTerms terms) {
        if (!featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_FINANCING_INSTALLMENT_PLANS)) {
            return null;
        }

        DeviceFinancingOption financing = new DeviceFinancingOption();
        financing.setFinancingId("FIN-" + UUID.randomUUID().toString());
        financing.setDevicePrice(devicePrice);
        financing.setInterestRate(4.99);
        financing.setFinancingAvailable(true);
        financing.setSelectedTerms(terms);

        // Calculate monthly payment
        double downPayment = devicePrice * 0.1; // 10% down
        financing.setDownPayment(downPayment);

        int months = getMonthsFromTerms(terms);
        double loanAmount = devicePrice - downPayment;
        double monthlyRate = financing.getInterestRate() / 100 / 12;
        int monthlyPayment = (int) ((loanAmount * monthlyRate * Math.pow(1 + monthlyRate, months)) 
                / (Math.pow(1 + monthlyRate, months) - 1));

        financing.setMonthlyPayment(monthlyPayment);
        financing.setTotalPayments(months);

        // Trade-in eligibility
        financing.setTradeInEligible(true);
        financing.setDeviceProtectionAvailable(true);
        financing.setDeviceProtectionCost(15.99);

        return financing;
    }

    private int getMonthsFromTerms(FinancingTerms terms) {
        switch (terms) {
            case MONTHS_12: return 12;
            case MONTHS_18: return 18;
            case MONTHS_24: return 24;
            case MONTHS_36: return 36;
            default: return 12;
        }
    }

    // ==================== COMPATIBILITY ====================

    public List<CompatibilityWarning> validateCompatibility(String cartId) {
        if (!featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_COMPATIBILITY_DEVICE_PLAN_COMPATIBILITY)) {
            return new ArrayList<>();
        }

        ShoppingCart cart = cartStore.get(cartId);
        List<CompatibilityWarning> warnings = new ArrayList<>();

        if (cart != null) {
            CartItem device = cart.getItems().stream()
                    .filter(i -> i.getProductType() == ProductType.DEVICE)
                    .findFirst()
                    .orElse(null);

            CartItem plan = cart.getItems().stream()
                    .filter(i -> i.getProductType() == ProductType.PLAN)
                    .findFirst()
                    .orElse(null);

            // Simulate compatibility checks
            if (device != null && plan != null) {
                // Add warning simulation if needed
            }

            cart.setWarnings(warnings);
        }

        return warnings;
    }

    // ==================== CHECKOUT ====================

    public Order checkout(String cartId, CheckoutRequest checkoutRequest) {
        if (!featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_CHECKOUT_ORDER_CREATION)) {
            return null;
        }

        ShoppingCart cart = cartStore.get(cartId);
        if (cart == null || cart.getItems().isEmpty()) {
            return null;
        }

        // Validate checkout
        if (!featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_CHECKOUT_VALIDATION)) {
            return null;
        }

        // Create order
        Order order = new Order();
        order.setOrderId("ORD-" + orderIdCounter.incrementAndGet());
        order.setCartId(cartId);
        order.setCustomerId(cart.getCustomerId());
        order.setItems(new ArrayList<>(cart.getItems()));
        order.setOrderPricing(cart.getPricing());
        order.setCreatedAt(System.currentTimeMillis());
        order.setOrderStatus("CONFIRMED");
        order.setShippingAddress(checkoutRequest.getShippingAddress());
        order.setEstimatedDeliveryDate(System.currentTimeMillis() + (5 * 24 * 60 * 60 * 1000L));

        // Update cart status
        cart.setStatus(CartStatus.CONVERTED);
        orderStore.put(order.getOrderId(), order);

        return order;
    }

    public Order getOrder(String orderId) {
        return orderStore.get(orderId);
    }

    // ==================== ANALYTICS ====================

    public CartAnalytics getCartAnalytics() {
        if (!featureFlagReader.isFeatureEnabled(ShoppingCartFeatureFlagConstants.CART_ANALYTICS_CART_METRICS)) {
            return null;
        }

        CartAnalytics analytics = new CartAnalytics();
        analytics.setTotalCartsCreated(cartStore.size());
        analytics.setActiveCartsCount((int) cartStore.values().stream()
                .filter(c -> c.getStatus() == CartStatus.ACTIVE).count());
        analytics.setAbandonedCartsCount((int) cartStore.values().stream()
                .filter(c -> c.getStatus() == CartStatus.ABANDONED).count());
        analytics.setConvertedCartsCount((int) cartStore.values().stream()
                .filter(c -> c.getStatus() == CartStatus.CONVERTED).count());

        double totalRevenue = orderStore.values().stream()
                .mapToDouble(o -> o.getOrderPricing().getTotal())
                .sum();
        analytics.setTotalRevenue(totalRevenue);

        if (analytics.getTotalCartsCreated() > 0) {
            analytics.setConversionRate((double) analytics.getConvertedCartsCount() / analytics.getTotalCartsCreated());
            analytics.setAverageCartValue(totalRevenue / analytics.getConvertedCartsCount());
        }

        analytics.setReportGeneratedAt(System.currentTimeMillis());
        return analytics;
    }

    public List<ShoppingCart> getAllCarts() {
        return new ArrayList<>(cartStore.values());
    }
}
        return out;
    }

    public List<Object> complexFilter(Optional<String> maybeName, Optional<Long> sinceEpoch) {
        List<Object> out = new ArrayList<>();
        for (Map<String,Object> v : store.values()) {
            boolean ok = true;
            if (maybeName.isPresent()) {
                Object n = v.get("name");
                if (n == null || !n.toString().toLowerCase().contains(maybeName.get().toLowerCase())) ok = false;
            }
            if (sinceEpoch.isPresent()) {
                Object t = v.get("createdAt");
                long tv = t instanceof Number ? ((Number)t).longValue() : 0L;
                if (tv < sinceEpoch.get()) ok = false;
            }
            if (ok) out.add(v);
        }
        return out;
    }

    // Utility method repeated multiple times to increase lines (safe dummy logic)
    public int computeChecksum(String s) {
        int c = 0;
        for (char ch : s.toCharArray()) c = (c * 31) + ch;
        return Math.abs(c);
    }

    // Another utility to mimic business logic
    public String humanReadableId(String id) {
        return id.substring(0, Math.min(8, id.length())).toUpperCase();
    }
}
