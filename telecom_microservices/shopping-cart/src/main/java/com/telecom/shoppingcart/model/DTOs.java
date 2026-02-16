package com.telecom.shoppingcart.model;

import java.util.*;

/**
 * Comprehensive domain models for telecom shopping cart
 * Represents complete shopping experience from browsing to checkout
 */

// ==================== BASE/WRAPPER TYPES ====================

public class CreateRequest {
    private Map<String,Object> payload = new HashMap<>();
    public CreateRequest() {}
    public Map<String,Object> getPayload() { return payload; }
    public void setPayload(Map<String,Object> payload) { this.payload = payload; }
}

public class UpdateRequest {
    private Map<String,Object> patch = new HashMap<>();
    public UpdateRequest() {}
    public Map<String,Object> getPatch() { return patch; }
    public void setPatch(Map<String,Object> patch) { this.patch = patch; }
}

public class BulkResponse {
    private List<Map<String,Object>> items = new ArrayList<>();
    public BulkResponse() {}
    public List<Map<String,Object>> getItems() { return items; }
    public void setItems(List<Map<String,Object>> items) { this.items = items; }
}

// ==================== ENUMERATIONS ====================

enum ProductType {
    DEVICE, PLAN, ADD_ON, BUNDLE, ACCESSORY
}

enum CartStatus {
    ACTIVE, SAVED, ABANDONED, CONVERTED
}

enum PromoType {
    PERCENTAGE_OFF, FIXED_OFF, BUNDLE_DISCOUNT, LOYALTY_REWARD, FLASH_SALE, NEW_CUSTOMER
}

enum LoyaltyTier {
    BRONZE, SILVER, GOLD, PLATINUM
}

enum FinancingTerms {
    MONTHS_12, MONTHS_18, MONTHS_24, MONTHS_36
}

enum WarehouseLocation {
    WAREHOUSE_EAST, WAREHOUSE_CENTRAL, WAREHOUSE_WEST
}

// ==================== SHOPPING CART CORE ====================

class ShoppingCart {
    private String cartId;
    private String customerId;
    private String sessionId;
    private CartStatus status;
    private List<CartItem> items;
    private CartPricing pricing;
    private AppliedPromotion appliedPromotion;
    private LoyaltyAccount loyaltyAccount;
    private List<CompatibilityWarning> warnings;
    private long createdAt;
    private long lastModifiedAt;
    private long expiryTime;

    public ShoppingCart() {}

    public String getCartId() { return cartId; }
    public void setCartId(String cartId) { this.cartId = cartId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public CartStatus getStatus() { return status; }
    public void setStatus(CartStatus status) { this.status = status; }
    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }
    public CartPricing getPricing() { return pricing; }
    public void setPricing(CartPricing pricing) { this.pricing = pricing; }
    public AppliedPromotion getAppliedPromotion() { return appliedPromotion; }
    public void setAppliedPromotion(AppliedPromotion appliedPromotion) { this.appliedPromotion = appliedPromotion; }
    public LoyaltyAccount getLoyaltyAccount() { return loyaltyAccount; }
    public void setLoyaltyAccount(LoyaltyAccount loyaltyAccount) { this.loyaltyAccount = loyaltyAccount; }
    public List<CompatibilityWarning> getWarnings() { return warnings; }
    public void setWarnings(List<CompatibilityWarning> warnings) { this.warnings = warnings; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getLastModifiedAt() { return lastModifiedAt; }
    public void setLastModifiedAt(long lastModifiedAt) { this.lastModifiedAt = lastModifiedAt; }
    public long getExpiryTime() { return expiryTime; }
    public void setExpiryTime(long expiryTime) { this.expiryTime = expiryTime; }
}

// ==================== CART ITEM ====================

class CartItem {
    private String itemId;
    private String productId;
    private ProductType productType;
    private String productName;
    private double unitPrice;
    private int quantity;
    private double lineTotal;
    private InventoryInfo inventoryInfo;
    private DeviceFinancingOption financingOption;
    private List<String> bundledWith;
    private long addedAt;

    public CartItem() {}

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public ProductType getProductType() { return productType; }
    public void setProductType(ProductType productType) { this.productType = productType; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getLineTotal() { return lineTotal; }
    public void setLineTotal(double lineTotal) { this.lineTotal = lineTotal; }
    public InventoryInfo getInventoryInfo() { return inventoryInfo; }
    public void setInventoryInfo(InventoryInfo inventoryInfo) { this.inventoryInfo = inventoryInfo; }
    public DeviceFinancingOption getFinancingOption() { return financingOption; }
    public void setFinancingOption(DeviceFinancingOption financingOption) { this.financingOption = financingOption; }
    public List<String> getBundledWith() { return bundledWith; }
    public void setBundledWith(List<String> bundledWith) { this.bundledWith = bundledWith; }
    public long getAddedAt() { return addedAt; }
    public void setAddedAt(long addedAt) { this.addedAt = addedAt; }
}

// ==================== INVENTORY ====================

class InventoryInfo {
    private String warehouseLocation;
    private int stockQuantity;
    private boolean inStock;
    private int daysToRestockIfBackorder;
    private boolean backorderAllowed;

    public InventoryInfo() {}

    public String getWarehouseLocation() { return warehouseLocation; }
    public void setWarehouseLocation(String warehouseLocation) { this.warehouseLocation = warehouseLocation; }
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    public boolean isInStock() { return inStock; }
    public void setInStock(boolean inStock) { this.inStock = inStock; }
    public int getDaysToRestockIfBackorder() { return daysToRestockIfBackorder; }
    public void setDaysToRestockIfBackorder(int daysToRestockIfBackorder) { this.daysToRestockIfBackorder = daysToRestockIfBackorder; }
    public boolean isBackorderAllowed() { return backorderAllowed; }
    public void setBackorderAllowed(boolean backorderAllowed) { this.backorderAllowed = backorderAllowed; }
}

// ==================== PRICING ====================

class CartPricing {
    private double subtotal;
    private double discountAmount;
    private double discountPercentage;
    private double bundleDiscountAmount;
    private double loyaltyRewardAmount;
    private double subtotalAfterDiscount;
    private double saleTaxAmount;
    private double activationFeeAmount;
    private double shippingFeeAmount;
    private double insuranceFeeAmount;
    private double total;
    private String currency;
    private long calculatedAt;

    public CartPricing() {}

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }
    public double getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(double discountPercentage) { this.discountPercentage = discountPercentage; }
    public double getBundleDiscountAmount() { return bundleDiscountAmount; }
    public void setBundleDiscountAmount(double bundleDiscountAmount) { this.bundleDiscountAmount = bundleDiscountAmount; }
    public double getLoyaltyRewardAmount() { return loyaltyRewardAmount; }
    public void setLoyaltyRewardAmount(double loyaltyRewardAmount) { this.loyaltyRewardAmount = loyaltyRewardAmount; }
    public double getSubtotalAfterDiscount() { return subtotalAfterDiscount; }
    public void setSubtotalAfterDiscount(double subtotalAfterDiscount) { this.subtotalAfterDiscount = subtotalAfterDiscount; }
    public double getSaleTaxAmount() { return saleTaxAmount; }
    public void setSaleTaxAmount(double saleTaxAmount) { this.saleTaxAmount = saleTaxAmount; }
    public double getActivationFeeAmount() { return activationFeeAmount; }
    public void setActivationFeeAmount(double activationFeeAmount) { this.activationFeeAmount = activationFeeAmount; }
    public double getShippingFeeAmount() { return shippingFeeAmount; }
    public void setShippingFeeAmount(double shippingFeeAmount) { this.shippingFeeAmount = shippingFeeAmount; }
    public double getInsuranceFeeAmount() { return insuranceFeeAmount; }
    public void setInsuranceFeeAmount(double insuranceFeeAmount) { this.insuranceFeeAmount = insuranceFeeAmount; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public long getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(long calculatedAt) { this.calculatedAt = calculatedAt; }
}

// ==================== PROMOTIONS ====================

class AppliedPromotion {
    private String promotionCode;
    private PromoType promoType;
    private double discountValue;
    private boolean isPercentage;
    private double maxDiscountAmount;
    private String minimumPurchaseRequirement;
    private long expiryDate;
    private int usageCount;
    private int maxUsageCount;

    public AppliedPromotion() {}

    public String getPromotionCode() { return promotionCode; }
    public void setPromotionCode(String promotionCode) { this.promotionCode = promotionCode; }
    public PromoType getPromoType() { return promoType; }
    public void setPromoType(PromoType promoType) { this.promoType = promoType; }
    public double getDiscountValue() { return discountValue; }
    public void setDiscountValue(double discountValue) { this.discountValue = discountValue; }
    public boolean isPercentage() { return isPercentage; }
    public void setPercentage(boolean percentage) { isPercentage = percentage; }
    public double getMaxDiscountAmount() { return maxDiscountAmount; }
    public void setMaxDiscountAmount(double maxDiscountAmount) { this.maxDiscountAmount = maxDiscountAmount; }
    public String getMinimumPurchaseRequirement() { return minimumPurchaseRequirement; }
    public void setMinimumPurchaseRequirement(String minimumPurchaseRequirement) { this.minimumPurchaseRequirement = minimumPurchaseRequirement; }
    public long getExpiryDate() { return expiryDate; }
    public void setExpiryDate(long expiryDate) { this.expiryDate = expiryDate; }
    public int getUsageCount() { return usageCount; }
    public void setUsageCount(int usageCount) { this.usageCount = usageCount; }
    public int getMaxUsageCount() { return maxUsageCount; }
    public void setMaxUsageCount(int maxUsageCount) { this.maxUsageCount = maxUsageCount; }
}

// ==================== LOYALTY ====================

class LoyaltyAccount {
    private String loyaltyId;
    private LoyaltyTier currentTier;
    private int totalPoints;
    private int pointsEarned;
    private int pointsRedeemed;
    private double rewardValue;
    private long memberSince;
    private long lastTierUpgradeDate;

    public LoyaltyAccount() {}

    public String getLoyaltyId() { return loyaltyId; }
    public void setLoyaltyId(String loyaltyId) { this.loyaltyId = loyaltyId; }
    public LoyaltyTier getCurrentTier() { return currentTier; }
    public void setCurrentTier(LoyaltyTier currentTier) { this.currentTier = currentTier; }
    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }
    public int getPointsEarned() { return pointsEarned; }
    public void setPointsEarned(int pointsEarned) { this.pointsEarned = pointsEarned; }
    public int getPointsRedeemed() { return pointsRedeemed; }
    public void setPointsRedeemed(int pointsRedeemed) { this.pointsRedeemed = pointsRedeemed; }
    public double getRewardValue() { return rewardValue; }
    public void setRewardValue(double rewardValue) { this.rewardValue = rewardValue; }
    public long getMemberSince() { return memberSince; }
    public void setMemberSince(long memberSince) { this.memberSince = memberSince; }
    public long getLastTierUpgradeDate() { return lastTierUpgradeDate; }
    public void setLastTierUpgradeDate(long lastTierUpgradeDate) { this.lastTierUpgradeDate = lastTierUpgradeDate; }
}

// ==================== DEVICE FINANCING ====================

class DeviceFinancingOption {
    private String financingId;
    private boolean financingAvailable;
    private FinancingTerms selectedTerms;
    private double devicePrice;
    private double downPayment;
    private int monthlyPayment;
    private double interestRate;
    private int totalPayments;
    private boolean tradeInEligible;
    private TradeInInfo tradeInInfo;
    private boolean deviceProtectionAvailable;
    private double deviceProtectionCost;

    public DeviceFinancingOption() {}

    public String getFinancingId() { return financingId; }
    public void setFinancingId(String financingId) { this.financingId = financingId; }
    public boolean isFinancingAvailable() { return financingAvailable; }
    public void setFinancingAvailable(boolean financingAvailable) { this.financingAvailable = financingAvailable; }
    public FinancingTerms getSelectedTerms() { return selectedTerms; }
    public void setSelectedTerms(FinancingTerms selectedTerms) { this.selectedTerms = selectedTerms; }
    public double getDevicePrice() { return devicePrice; }
    public void setDevicePrice(double devicePrice) { this.devicePrice = devicePrice; }
    public double getDownPayment() { return downPayment; }
    public void setDownPayment(double downPayment) { this.downPayment = downPayment; }
    public int getMonthlyPayment() { return monthlyPayment; }
    public void setMonthlyPayment(int monthlyPayment) { this.monthlyPayment = monthlyPayment; }
    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }
    public int getTotalPayments() { return totalPayments; }
    public void setTotalPayments(int totalPayments) { this.totalPayments = totalPayments; }
    public boolean isTradeInEligible() { return tradeInEligible; }
    public void setTradeInEligible(boolean tradeInEligible) { this.tradeInEligible = tradeInEligible; }
    public TradeInInfo getTradeInInfo() { return tradeInInfo; }
    public void setTradeInInfo(TradeInInfo tradeInInfo) { this.tradeInInfo = tradeInInfo; }
    public boolean isDeviceProtectionAvailable() { return deviceProtectionAvailable; }
    public void setDeviceProtectionAvailable(boolean deviceProtectionAvailable) { this.deviceProtectionAvailable = deviceProtectionAvailable; }
    public double getDeviceProtectionCost() { return deviceProtectionCost; }
    public void setDeviceProtectionCost(double deviceProtectionCost) { this.deviceProtectionCost = deviceProtectionCost; }
}

// ==================== TRADE-IN ====================

class TradeInInfo {
    private String tradeInId;
    private String oldDeviceModel;
    private String condition;
    private double estimatedValue;
    private double appliedCredit;
    private String tradeInStatus;

    public TradeInInfo() {}

    public String getTradeInId() { return tradeInId; }
    public void setTradeInId(String tradeInId) { this.tradeInId = tradeInId; }
    public String getOldDeviceModel() { return oldDeviceModel; }
    public void setOldDeviceModel(String oldDeviceModel) { this.oldDeviceModel = oldDeviceModel; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public double getEstimatedValue() { return estimatedValue; }
    public void setEstimatedValue(double estimatedValue) { this.estimatedValue = estimatedValue; }
    public double getAppliedCredit() { return appliedCredit; }
    public void setAppliedCredit(double appliedCredit) { this.appliedCredit = appliedCredit; }
    public String getTradeInStatus() { return tradeInStatus; }
    public void setTradeInStatus(String tradeInStatus) { this.tradeInStatus = tradeInStatus; }
}

// ==================== BUNDLING ====================

class ProductBundle {
    private String bundleId;
    private String bundleName;
    private String bundleDescription;
    private List<String> deviceProductIds;
    private List<String> planProductIds;
    private List<String> addonProductIds;
    private double regularBundlePrice;
    private double bundleDiscountPercentage;
    private double bundlePrice;
    private String bundleType;

    public ProductBundle() {}

    public String getBundleId() { return bundleId; }
    public void setBundleId(String bundleId) { this.bundleId = bundleId; }
    public String getBundleName() { return bundleName; }
    public void setBundleName(String bundleName) { this.bundleName = bundleName; }
    public String getBundleDescription() { return bundleDescription; }
    public void setBundleDescription(String bundleDescription) { this.bundleDescription = bundleDescription; }
    public List<String> getDeviceProductIds() { return deviceProductIds; }
    public void setDeviceProductIds(List<String> deviceProductIds) { this.deviceProductIds = deviceProductIds; }
    public List<String> getPlanProductIds() { return planProductIds; }
    public void setPlanProductIds(List<String> planProductIds) { this.planProductIds = planProductIds; }
    public List<String> getAddonProductIds() { return addonProductIds; }
    public void setAddonProductIds(List<String> addonProductIds) { this.addonProductIds = addonProductIds; }
    public double getRegularBundlePrice() { return regularBundlePrice; }
    public void setRegularBundlePrice(double regularBundlePrice) { this.regularBundlePrice = regularBundlePrice; }
    public double getBundleDiscountPercentage() { return bundleDiscountPercentage; }
    public void setBundleDiscountPercentage(double bundleDiscountPercentage) { this.bundleDiscountPercentage = bundleDiscountPercentage; }
    public double getBundlePrice() { return bundlePrice; }
    public void setBundlePrice(double bundlePrice) { this.bundlePrice = bundlePrice; }
    public String getBundleType() { return bundleType; }
    public void setBundleType(String bundleType) { this.bundleType = bundleType; }
}

// ==================== COMPATIBILITY ====================

class CompatibilityWarning {
    private String warningId;
    private String deviceProductId;
    private String planProductId;
    private String warningType;
    private String warningMessage;
    private boolean resolvable;
    private String resolution;

    public CompatibilityWarning() {}

    public String getWarningId() { return warningId; }
    public void setWarningId(String warningId) { this.warningId = warningId; }
    public String getDeviceProductId() { return deviceProductId; }
    public void setDeviceProductId(String deviceProductId) { this.deviceProductId = deviceProductId; }
    public String getPlanProductId() { return planProductId; }
    public void setPlanProductId(String planProductId) { this.planProductId = planProductId; }
    public String getWarningType() { return warningType; }
    public void setWarningType(String warningType) { this.warningType = warningType; }
    public String getWarningMessage() { return warningMessage; }
    public void setWarningMessage(String warningMessage) { this.warningMessage = warningMessage; }
    public boolean isResolvable() { return resolvable; }
    public void setResolvable(boolean resolvable) { this.resolvable = resolvable; }
    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }
}

// ==================== CHECKOUT ====================

class CheckoutRequest {
    private String cartId;
    private String customerId;
    private ShippingAddress shippingAddress;
    private String paymentMethodId;
    private boolean acceptTerms;
    private String deviceActivationPreference;
    private String planEffectiveDate;
    private List<String> selectedAddOns;

    public CheckoutRequest() {}

    public String getCartId() { return cartId; }
    public void setCartId(String cartId) { this.cartId = cartId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public ShippingAddress getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(ShippingAddress shippingAddress) { this.shippingAddress = shippingAddress; }
    public String getPaymentMethodId() { return paymentMethodId; }
    public void setPaymentMethodId(String paymentMethodId) { this.paymentMethodId = paymentMethodId; }
    public boolean isAcceptTerms() { return acceptTerms; }
    public void setAcceptTerms(boolean acceptTerms) { this.acceptTerms = acceptTerms; }
    public String getDeviceActivationPreference() { return deviceActivationPreference; }
    public void setDeviceActivationPreference(String deviceActivationPreference) { this.deviceActivationPreference = deviceActivationPreference; }
    public String getPlanEffectiveDate() { return planEffectiveDate; }
    public void setPlanEffectiveDate(String planEffectiveDate) { this.planEffectiveDate = planEffectiveDate; }
    public List<String> getSelectedAddOns() { return selectedAddOns; }
    public void setSelectedAddOns(List<String> selectedAddOns) { this.selectedAddOns = selectedAddOns; }
}

class ShippingAddress {
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;

    public ShippingAddress() {}

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}

// ==================== ORDER ====================

class Order {
    private String orderId;
    private String cartId;
    private String customerId;
    private List<CartItem> items;
    private CartPricing orderPricing;
    private String orderStatus;
    private long createdAt;
    private ShippingAddress shippingAddress;
    private String trackingNumber;
    private long estimatedDeliveryDate;

    public Order() {}

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getCartId() { return cartId; }
    public void setCartId(String cartId) { this.cartId = cartId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }
    public CartPricing getOrderPricing() { return orderPricing; }
    public void setOrderPricing(CartPricing orderPricing) { this.orderPricing = orderPricing; }
    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public ShippingAddress getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(ShippingAddress shippingAddress) { this.shippingAddress = shippingAddress; }
    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    public long getEstimatedDeliveryDate() { return estimatedDeliveryDate; }
    public void setEstimatedDeliveryDate(long estimatedDeliveryDate) { this.estimatedDeliveryDate = estimatedDeliveryDate; }
}

// ==================== CART ANALYTICS ====================

class CartAnalytics {
    private int totalCartsCreated;
    private int activeCartsCount;
    private int abandonedCartsCount;
    private int convertedCartsCount;
    private double averageCartValue;
    private double conversionRate;
    private double averageItemsPerCart;
    private double totalRevenue;
    private Map<String, Integer> popularProducts;
    private long reportGeneratedAt;

    public CartAnalytics() {}

    public int getTotalCartsCreated() { return totalCartsCreated; }
    public void setTotalCartsCreated(int totalCartsCreated) { this.totalCartsCreated = totalCartsCreated; }
    public int getActiveCartsCount() { return activeCartsCount; }
    public void setActiveCartsCount(int activeCartsCount) { this.activeCartsCount = activeCartsCount; }
    public int getAbandonedCartsCount() { return abandonedCartsCount; }
    public void setAbandonedCartsCount(int abandonedCartsCount) { this.abandonedCartsCount = abandonedCartsCount; }
    public int getConvertedCartsCount() { return convertedCartsCount; }
    public void setConvertedCartsCount(int convertedCartsCount) { this.convertedCartsCount = convertedCartsCount; }
    public double getAverageCartValue() { return averageCartValue; }
    public void setAverageCartValue(double averageCartValue) { this.averageCartValue = averageCartValue; }
    public double getConversionRate() { return conversionRate; }
    public void setConversionRate(double conversionRate) { this.conversionRate = conversionRate; }
    public double getAverageItemsPerCart() { return averageItemsPerCart; }
    public void setAverageItemsPerCart(double averageItemsPerCart) { this.averageItemsPerCart = averageItemsPerCart; }
    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
    public Map<String, Integer> getPopularProducts() { return popularProducts; }
    public void setPopularProducts(Map<String, Integer> popularProducts) { this.popularProducts = popularProducts; }
    public long getReportGeneratedAt() { return reportGeneratedAt; }
    public void setReportGeneratedAt(long reportGeneratedAt) { this.reportGeneratedAt = reportGeneratedAt; }
}
