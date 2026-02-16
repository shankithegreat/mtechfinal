package com.telecom.productcatalog.model;

import java.util.*;

/**
 * Comprehensive domain models for telecom product catalog
 * Represents complete product lifecycle including devices, plans, bundles, pricing, inventory
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
    DEVICE, SERVICE_PLAN, ADD_ON, BUNDLE
}

enum DeviceType {
    SMARTPHONE, TABLET, SMARTWATCH, MODEM, ROUTER, HOTSPOT, FEATURE_PHONE, OTHER
}

enum DeviceBrand {
    APPLE, SAMSUNG, GOOGLE, NOKIA, MOTOROLA, HUAWEI, XIAOMI, OPPO, VIVO, REALME, OTHER
}

enum NetworkType {
    NETWORK_5G, NETWORK_4G, NETWORK_LTE, NETWORK_3G, NETWORK_2G
}

enum PlanType {
    VOICE_PLAN, DATA_PLAN, HYBRID_PLAN, ENTERPRISE_PLAN
}

enum BillingFrequency {
    DAILY, WEEKLY, MONTHLY, QUARTERLY, SEMI_ANNUAL, ANNUAL
}

enum DataUnit {
    MB, GB, UNLIMITED
}

enum AvailabilityStatus {
    IN_STOCK, LOW_STOCK, OUT_OF_STOCK, DISCONTINUED, COMING_SOON
}

enum RiskLevel {
    LOW, MEDIUM, HIGH
}

// ==================== CORE PRODUCT MODEL ====================

class TelecomProduct {
    private String productId;
    private String productName;
    private ProductType productType;
    private String description;
    private String sku;
    private double basePrice;
    private String currency;
    private String category;
    private String subCategory;
    private AvailabilityStatus availabilityStatus;
    private int rating;
    private int reviewCount;
    private ProductDetails details;
    private InventoryInfo inventoryInfo;
    private PricingInfo pricingInfo;
    private List<Promotion> applicablePromotions;
    private RegionalAvailability regionalAvailability;
    private List<ProductReview> reviews;
    private long createdAt;
    private long updatedAt;

    public TelecomProduct() {}

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public ProductType getProductType() { return productType; }
    public void setProductType(ProductType productType) { this.productType = productType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getSubCategory() { return subCategory; }
    public void setSubCategory(String subCategory) { this.subCategory = subCategory; }
    public AvailabilityStatus getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(AvailabilityStatus availabilityStatus) { this.availabilityStatus = availabilityStatus; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }
    public ProductDetails getDetails() { return details; }
    public void setDetails(ProductDetails details) { this.details = details; }
    public InventoryInfo getInventoryInfo() { return inventoryInfo; }
    public void setInventoryInfo(InventoryInfo inventoryInfo) { this.inventoryInfo = inventoryInfo; }
    public PricingInfo getPricingInfo() { return pricingInfo; }
    public void setPricingInfo(PricingInfo pricingInfo) { this.pricingInfo = pricingInfo; }
    public List<Promotion> getApplicablePromotions() { return applicablePromotions; }
    public void setApplicablePromotions(List<Promotion> applicablePromotions) { this.applicablePromotions = applicablePromotions; }
    public RegionalAvailability getRegionalAvailability() { return regionalAvailability; }
    public void setRegionalAvailability(RegionalAvailability regionalAvailability) { this.regionalAvailability = regionalAvailability; }
    public List<ProductReview> getReviews() { return reviews; }
    public void setReviews(List<ProductReview> reviews) { this.reviews = reviews; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}

// ==================== DEVICE-SPECIFIC MODEL ====================

class DeviceProduct extends TelecomProduct {
    private DeviceType deviceType;
    private DeviceBrand brand;
    private String model;
    private DeviceSpecifications specifications;
    private List<NetworkType> supportedNetworks;
    private DeviceCompatibility compatibility;
    private WarrantyInfo warranty;
    private FinancingOption financingOption;
    private TradeInInfo tradeInInfo;

    public DeviceProduct() {}

    public DeviceType getDeviceType() { return deviceType; }
    public void setDeviceType(DeviceType deviceType) { this.deviceType = deviceType; }
    public DeviceBrand getBrand() { return brand; }
    public void setBrand(DeviceBrand brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public DeviceSpecifications getSpecifications() { return specifications; }
    public void setSpecifications(DeviceSpecifications specifications) { this.specifications = specifications; }
    public List<NetworkType> getSupportedNetworks() { return supportedNetworks; }
    public void setSupportedNetworks(List<NetworkType> supportedNetworks) { this.supportedNetworks = supportedNetworks; }
    public DeviceCompatibility getCompatibility() { return compatibility; }
    public void setCompatibility(DeviceCompatibility compatibility) { this.compatibility = compatibility; }
    public WarrantyInfo getWarranty() { return warranty; }
    public void setWarranty(WarrantyInfo warranty) { this.warranty = warranty; }
    public FinancingOption getFinancingOption() { return financingOption; }
    public void setFinancingOption(FinancingOption financingOption) { this.financingOption = financingOption; }
    public TradeInInfo getTradeInInfo() { return tradeInInfo; }
    public void setTradeInInfo(TradeInInfo tradeInInfo) { this.tradeInInfo = tradeInInfo; }
}

// ==================== SERVICE PLAN MODEL ====================

class ServicePlanProduct extends TelecomProduct {
    private PlanType planType;
    private BillingFrequency billingFrequency;
    private int durationMonths;
    private VoiceAllowance voiceAllowance;
    private DataAllowance dataAllowance;
    private List<String> includedFeatures;
    private List<AddOnProduct> bundledAddOns;
    private RoamingDetails roamingDetails;
    private InternationalDetails internationalDetails;
    private List<String> compatibleDevices;

    public ServicePlanProduct() {}

    public PlanType getPlanType() { return planType; }
    public void setPlanType(PlanType planType) { this.planType = planType; }
    public BillingFrequency getBillingFrequency() { return billingFrequency; }
    public void setBillingFrequency(BillingFrequency billingFrequency) { this.billingFrequency = billingFrequency; }
    public int getDurationMonths() { return durationMonths; }
    public void setDurationMonths(int durationMonths) { this.durationMonths = durationMonths; }
    public VoiceAllowance getVoiceAllowance() { return voiceAllowance; }
    public void setVoiceAllowance(VoiceAllowance voiceAllowance) { this.voiceAllowance = voiceAllowance; }
    public DataAllowance getDataAllowance() { return dataAllowance; }
    public void setDataAllowance(DataAllowance dataAllowance) { this.dataAllowance = dataAllowance; }
    public List<String> getIncludedFeatures() { return includedFeatures; }
    public void setIncludedFeatures(List<String> includedFeatures) { this.includedFeatures = includedFeatures; }
    public List<AddOnProduct> getBundledAddOns() { return bundledAddOns; }
    public void setBundledAddOns(List<AddOnProduct> bundledAddOns) { this.bundledAddOns = bundledAddOns; }
    public RoamingDetails getRoamingDetails() { return roamingDetails; }
    public void setRoamingDetails(RoamingDetails roamingDetails) { this.roamingDetails = roamingDetails; }
    public InternationalDetails getInternationalDetails() { return internationalDetails; }
    public void setInternationalDetails(InternationalDetails internationalDetails) { this.internationalDetails = internationalDetails; }
    public List<String> getCompatibleDevices() { return compatibleDevices; }
    public void setCompatibleDevices(List<String> compatibleDevices) { this.compatibleDevices = compatibleDevices; }
}

// ==================== ADD-ON PRODUCT MODEL ====================

class AddOnProduct extends TelecomProduct {
    private String addOnType;
    private double monthlyFee;
    private List<String> compatiblePlans;
    private String description;

    public AddOnProduct() {}

    public String getAddOnType() { return addOnType; }
    public void setAddOnType(String addOnType) { this.addOnType = addOnType; }
    public double getMonthlyFee() { return monthlyFee; }
    public void setMonthlyFee(double monthlyFee) { this.monthlyFee = monthlyFee; }
    public List<String> getCompatiblePlans() { return compatiblePlans; }
    public void setCompatiblePlans(List<String> compatiblePlans) { this.compatiblePlans = compatiblePlans; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

// ==================== BUNDLE MODEL ====================

class ProductBundle extends TelecomProduct {
    private List<String> includedProductIds;
    private double bundleDiscount;
    private double bundlePrice;
    private int savingsPercentage;
    private String bundleDescription;

    public ProductBundle() {}

    public List<String> getIncludedProductIds() { return includedProductIds; }
    public void setIncludedProductIds(List<String> includedProductIds) { this.includedProductIds = includedProductIds; }
    public double getBundleDiscount() { return bundleDiscount; }
    public void setBundleDiscount(double bundleDiscount) { this.bundleDiscount = bundleDiscount; }
    public double getBundlePrice() { return bundlePrice; }
    public void setBundlePrice(double bundlePrice) { this.bundlePrice = bundlePrice; }
    public int getSavingsPercentage() { return savingsPercentage; }
    public void setSavingsPercentage(int savingsPercentage) { this.savingsPercentage = savingsPercentage; }
    public String getBundleDescription() { return bundleDescription; }
    public void setBundleDescription(String bundleDescription) { this.bundleDescription = bundleDescription; }
}

// ==================== DEVICE SPECIFICATIONS ====================

class DeviceSpecifications {
    private String processorName;
    private String processorSpeed;
    private int ramGB;
    private int storageGB;
    private String displaySize;
    private String displayResolution;
    private String displayType;
    private double weight;
    private List<String> cameras;
    private String battery;
    private List<String> connectivityOptions;
    private List<String> sensors;
    private String os;
    private String osVersion;

    public DeviceSpecifications() {}

    public String getProcessorName() { return processorName; }
    public void setProcessorName(String processorName) { this.processorName = processorName; }
    public String getProcessorSpeed() { return processorSpeed; }
    public void setProcessorSpeed(String processorSpeed) { this.processorSpeed = processorSpeed; }
    public int getRamGB() { return ramGB; }
    public void setRamGB(int ramGB) { this.ramGB = ramGB; }
    public int getStorageGB() { return storageGB; }
    public void setStorageGB(int storageGB) { this.storageGB = storageGB; }
    public String getDisplaySize() { return displaySize; }
    public void setDisplaySize(String displaySize) { this.displaySize = displaySize; }
    public String getDisplayResolution() { return displayResolution; }
    public void setDisplayResolution(String displayResolution) { this.displayResolution = displayResolution; }
    public String getDisplayType() { return displayType; }
    public void setDisplayType(String displayType) { this.displayType = displayType; }
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
    public List<String> getCameras() { return cameras; }
    public void setCameras(List<String> cameras) { this.cameras = cameras; }
    public String getBattery() { return battery; }
    public void setBattery(String battery) { this.battery = battery; }
    public List<String> getConnectivityOptions() { return connectivityOptions; }
    public void setConnectivityOptions(List<String> connectivityOptions) { this.connectivityOptions = connectivityOptions; }
    public List<String> getSensors() { return sensors; }
    public void setSensors(List<String> sensors) { this.sensors = sensors; }
    public String getOs() { return os; }
    public void setOs(String os) { this.os = os; }
    public String getOsVersion() { return osVersion; }
    public void setOsVersion(String osVersion) { this.osVersion = osVersion; }
}

// ==================== DEVICE COMPATIBILITY ====================

class DeviceCompatibility {
    private List<String> compatibleNetworks;
    private List<String> compatiblePlans;
    private List<String> incompatibilities;
    private List<String> requiredApps;

    public DeviceCompatibility() {}

    public List<String> getCompatibleNetworks() { return compatibleNetworks; }
    public void setCompatibleNetworks(List<String> compatibleNetworks) { this.compatibleNetworks = compatibleNetworks; }
    public List<String> getCompatiblePlans() { return compatiblePlans; }
    public void setCompatiblePlans(List<String> compatiblePlans) { this.compatiblePlans = compatiblePlans; }
    public List<String> getIncompatibilities() { return incompatibilities; }
    public void setIncompatibilities(List<String> incompatibilities) { this.incompatibilities = incompatibilities; }
    public List<String> getRequiredApps() { return requiredApps; }
    public void setRequiredApps(List<String> requiredApps) { this.requiredApps = requiredApps; }
}

// ==================== WARRANTY INFO ====================

class WarrantyInfo {
    private int warrantyMonths;
    private String warrantyType;
    private List<String> coveredIssues;
    private List<String> excludedIssues;
    private double extendedWarrantyCost;

    public WarrantyInfo() {}

    public int getWarrantyMonths() { return warrantyMonths; }
    public void setWarrantyMonths(int warrantyMonths) { this.warrantyMonths = warrantyMonths; }
    public String getWarrantyType() { return warrantyType; }
    public void setWarrantyType(String warrantyType) { this.warrantyType = warrantyType; }
    public List<String> getCoveredIssues() { return coveredIssues; }
    public void setCoveredIssues(List<String> coveredIssues) { this.coveredIssues = coveredIssues; }
    public List<String> getExcludedIssues() { return excludedIssues; }
    public void setExcludedIssues(List<String> excludedIssues) { this.excludedIssues = excludedIssues; }
    public double getExtendedWarrantyCost() { return extendedWarrantyCost; }
    public void setExtendedWarrantyCost(double extendedWarrantyCost) { this.extendedWarrantyCost = extendedWarrantyCost; }
}

// ==================== FINANCING OPTION ====================

class FinancingOption {
    private boolean availableForFinancing;
    private int installmentMonths;
    private double monthlyPayment;
    private double interestRate;
    private double totalWithInterest;

    public FinancingOption() {}

    public boolean isAvailableForFinancing() { return availableForFinancing; }
    public void setAvailableForFinancing(boolean availableForFinancing) { this.availableForFinancing = availableForFinancing; }
    public int getInstallmentMonths() { return installmentMonths; }
    public void setInstallmentMonths(int installmentMonths) { this.installmentMonths = installmentMonths; }
    public double getMonthlyPayment() { return monthlyPayment; }
    public void setMonthlyPayment(double monthlyPayment) { this.monthlyPayment = monthlyPayment; }
    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }
    public double getTotalWithInterest() { return totalWithInterest; }
    public void setTotalWithInterest(double totalWithInterest) { this.totalWithInterest = totalWithInterest; }
}

// ==================== TRADE-IN INFO ====================

class TradeInInfo {
    private boolean tradeInEligible;
    private List<String> acceptedDeviceModels;
    private double baseTradeInValue;
    private String conditionGrading;

    public TradeInInfo() {}

    public boolean isTradeInEligible() { return tradeInEligible; }
    public void setTradeInEligible(boolean tradeInEligible) { this.tradeInEligible = tradeInEligible; }
    public List<String> getAcceptedDeviceModels() { return acceptedDeviceModels; }
    public void setAcceptedDeviceModels(List<String> acceptedDeviceModels) { this.acceptedDeviceModels = acceptedDeviceModels; }
    public double getBaseTradeInValue() { return baseTradeInValue; }
    public void setBaseTradeInValue(double baseTradeInValue) { this.baseTradeInValue = baseTradeInValue; }
    public String getConditionGrading() { return conditionGrading; }
    public void setConditionGrading(String conditionGrading) { this.conditionGrading = conditionGrading; }
}

// ==================== VOICE ALLOWANCE ====================

class VoiceAllowance {
    private int minutesPerMonth;
    private int localMinutes;
    private int longDistanceMinutes;
    private List<String> includedCountries;

    public VoiceAllowance() {}

    public int getMinutesPerMonth() { return minutesPerMonth; }
    public void setMinutesPerMonth(int minutesPerMonth) { this.minutesPerMonth = minutesPerMonth; }
    public int getLocalMinutes() { return localMinutes; }
    public void setLocalMinutes(int localMinutes) { this.localMinutes = localMinutes; }
    public int getLongDistanceMinutes() { return longDistanceMinutes; }
    public void setLongDistanceMinutes(int longDistanceMinutes) { this.longDistanceMinutes = longDistanceMinutes; }
    public List<String> getIncludedCountries() { return includedCountries; }
    public void setIncludedCountries(List<String> includedCountries) { this.includedCountries = includedCountries; }
}

// ==================== DATA ALLOWANCE ====================

class DataAllowance {
    private DataUnit dataUnit;
    private double dataAmount;
    private double speedMbps;
    private boolean unlimitedData;
    private List<String> includedServices;

    public DataAllowance() {}

    public DataUnit getDataUnit() { return dataUnit; }
    public void setDataUnit(DataUnit dataUnit) { this.dataUnit = dataUnit; }
    public double getDataAmount() { return dataAmount; }
    public void setDataAmount(double dataAmount) { this.dataAmount = dataAmount; }
    public double getSpeedMbps() { return speedMbps; }
    public void setSpeedMbps(double speedMbps) { this.speedMbps = speedMbps; }
    public boolean isUnlimitedData() { return unlimitedData; }
    public void setUnlimitedData(boolean unlimitedData) { this.unlimitedData = unlimitedData; }
    public List<String> getIncludedServices() { return includedServices; }
    public void setIncludedServices(List<String> includedServices) { this.includedServices = includedServices; }
}

// ==================== ROAMING DETAILS ====================

class RoamingDetails {
    private boolean roamingIncluded;
    private List<String> supportedCountries;
    private double roamingRate;
    private String roamingType;

    public RoamingDetails() {}

    public boolean isRoamingIncluded() { return roamingIncluded; }
    public void setRoamingIncluded(boolean roamingIncluded) { this.roamingIncluded = roamingIncluded; }
    public List<String> getSupportedCountries() { return supportedCountries; }
    public void setSupportedCountries(List<String> supportedCountries) { this.supportedCountries = supportedCountries; }
    public double getRoamingRate() { return roamingRate; }
    public void setRoamingRate(double roamingRate) { this.roamingRate = roamingRate; }
    public String getRoamingType() { return roamingType; }
    public void setRoamingType(String roamingType) { this.roamingType = roamingType; }
}

// ==================== INTERNATIONAL DETAILS ====================

class InternationalDetails {
    private boolean internationalCallsIncluded;
    private List<String> includedCountries;
    private double internationalRate;
    private int internationalMinutes;

    public InternationalDetails() {}

    public boolean isInternationalCallsIncluded() { return internationalCallsIncluded; }
    public void setInternationalCallsIncluded(boolean internationalCallsIncluded) { this.internationalCallsIncluded = internationalCallsIncluded; }
    public List<String> getIncludedCountries() { return includedCountries; }
    public void setIncludedCountries(List<String> includedCountries) { this.includedCountries = includedCountries; }
    public double getInternationalRate() { return internationalRate; }
    public void setInternationalRate(double internationalRate) { this.internationalRate = internationalRate; }
    public int getInternationalMinutes() { return internationalMinutes; }
    public void setInternationalMinutes(int internationalMinutes) { this.internationalMinutes = internationalMinutes; }
}

// ==================== PRODUCT DETAILS ====================

class ProductDetails {
    private Map<String, String> specifications;
    private List<String> features;
    private List<String> tags;
    private String manufacturer;

    public ProductDetails() {}

    public Map<String, String> getSpecifications() { return specifications; }
    public void setSpecifications(Map<String, String> specifications) { this.specifications = specifications; }
    public List<String> getFeatures() { return features; }
    public void setFeatures(List<String> features) { this.features = features; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
}

// ==================== INVENTORY INFO ====================

class InventoryInfo {
    private int totalQuantity;
    private int availableQuantity;
    private int reservedQuantity;
    private int damageQuantity;
    private List<WarehouseInventory> warehouseStock;
    private long lastStockUpdateTime;

    public InventoryInfo() {}

    public int getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }
    public int getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; }
    public int getReservedQuantity() { return reservedQuantity; }
    public void setReservedQuantity(int reservedQuantity) { this.reservedQuantity = reservedQuantity; }
    public int getDamageQuantity() { return damageQuantity; }
    public void setDamageQuantity(int damageQuantity) { this.damageQuantity = damageQuantity; }
    public List<WarehouseInventory> getWarehouseStock() { return warehouseStock; }
    public void setWarehouseStock(List<WarehouseInventory> warehouseStock) { this.warehouseStock = warehouseStock; }
    public long getLastStockUpdateTime() { return lastStockUpdateTime; }
    public void setLastStockUpdateTime(long lastStockUpdateTime) { this.lastStockUpdateTime = lastStockUpdateTime; }
}

// ==================== WAREHOUSE INVENTORY ====================

class WarehouseInventory {
    private String warehouseId;
    private String warehouseName;
    private String location;
    private int quantityOnHand;
    private int reservedQuantity;
    private int damageQuantity;

    public WarehouseInventory() {}

    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }
    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public int getQuantityOnHand() { return quantityOnHand; }
    public void setQuantityOnHand(int quantityOnHand) { this.quantityOnHand = quantityOnHand; }
    public int getReservedQuantity() { return reservedQuantity; }
    public void setReservedQuantity(int reservedQuantity) { this.reservedQuantity = reservedQuantity; }
    public int getDamageQuantity() { return damageQuantity; }
    public void setDamageQuantity(int damageQuantity) { this.damageQuantity = damageQuantity; }
}

// ==================== PRICING INFO ====================

class PricingInfo {
    private double basePrice;
    private double discountedPrice;
    private double discountPercentage;
    private double taxAmount;
    private double finalPrice;
    private String pricingTier;
    private List<TierPrice> tierPrices;
    private List<String> applicableCoupons;

    public PricingInfo() {}

    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }
    public double getDiscountedPrice() { return discountedPrice; }
    public void setDiscountedPrice(double discountedPrice) { this.discountedPrice = discountedPrice; }
    public double getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(double discountPercentage) { this.discountPercentage = discountPercentage; }
    public double getTaxAmount() { return taxAmount; }
    public void setTaxAmount(double taxAmount) { this.taxAmount = taxAmount; }
    public double getFinalPrice() { return finalPrice; }
    public void setFinalPrice(double finalPrice) { this.finalPrice = finalPrice; }
    public String getPricingTier() { return pricingTier; }
    public void setPricingTier(String pricingTier) { this.pricingTier = pricingTier; }
    public List<TierPrice> getTierPrices() { return tierPrices; }
    public void setTierPrices(List<TierPrice> tierPrices) { this.tierPrices = tierPrices; }
    public List<String> getApplicableCoupons() { return applicableCoupons; }
    public void setApplicableCoupons(List<String> applicableCoupons) { this.applicableCoupons = applicableCoupons; }
}

// ==================== TIER PRICE ====================

class TierPrice {
    private int minimumQuantity;
    private int maximumQuantity;
    private double pricePerUnit;
    private double savingsPercentage;

    public TierPrice() {}

    public int getMinimumQuantity() { return minimumQuantity; }
    public void setMinimumQuantity(int minimumQuantity) { this.minimumQuantity = minimumQuantity; }
    public int getMaximumQuantity() { return maximumQuantity; }
    public void setMaximumQuantity(int maximumQuantity) { this.maximumQuantity = maximumQuantity; }
    public double getPricePerUnit() { return pricePerUnit; }
    public void setPricePerUnit(double pricePerUnit) { this.pricePerUnit = pricePerUnit; }
    public double getSavingsPercentage() { return savingsPercentage; }
    public void setSavingsPercentage(double savingsPercentage) { this.savingsPercentage = savingsPercentage; }
}

// ==================== PROMOTION ====================

class Promotion {
    private String promotionId;
    private String promotionName;
    private String promotionType;
    private double discountPercentage;
    private double discountAmount;
    private long startDate;
    private long endDate;
    private boolean active;

    public Promotion() {}

    public String getPromotionId() { return promotionId; }
    public void setPromotionId(String promotionId) { this.promotionId = promotionId; }
    public String getPromotionName() { return promotionName; }
    public void setPromotionName(String promotionName) { this.promotionName = promotionName; }
    public String getPromotionType() { return promotionType; }
    public void setPromotionType(String promotionType) { this.promotionType = promotionType; }
    public double getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(double discountPercentage) { this.discountPercentage = discountPercentage; }
    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }
    public long getStartDate() { return startDate; }
    public void setStartDate(long startDate) { this.startDate = startDate; }
    public long getEndDate() { return endDate; }
    public void setEndDate(long endDate) { this.endDate = endDate; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}

// ==================== REGIONAL AVAILABILITY ====================

class RegionalAvailability {
    private List<String> availableRegions;
    private List<String> availableNetworkProviders;
    private Map<String, Double> regionalPricing;
    private String blackoutRegions;

    public RegionalAvailability() {}

    public List<String> getAvailableRegions() { return availableRegions; }
    public void setAvailableRegions(List<String> availableRegions) { this.availableRegions = availableRegions; }
    public List<String> getAvailableNetworkProviders() { return availableNetworkProviders; }
    public void setAvailableNetworkProviders(List<String> availableNetworkProviders) { this.availableNetworkProviders = availableNetworkProviders; }
    public Map<String, Double> getRegionalPricing() { return regionalPricing; }
    public void setRegionalPricing(Map<String, Double> regionalPricing) { this.regionalPricing = regionalPricing; }
    public String getBlackoutRegions() { return blackoutRegions; }
    public void setBlackoutRegions(String blackoutRegions) { this.blackoutRegions = blackoutRegions; }
}

// ==================== PRODUCT REVIEW ====================

class ProductReview {
    private String reviewId;
    private String customerId;
    private int ratingScore;
    private String reviewTitle;
    private String reviewBody;
    private int helpfulCount;
    private boolean verified;
    private long reviewDate;

    public ProductReview() {}

    public String getReviewId() { return reviewId; }
    public void setReviewId(String reviewId) { this.reviewId = reviewId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public int getRatingScore() { return ratingScore; }
    public void setRatingScore(int ratingScore) { this.ratingScore = ratingScore; }
    public String getReviewTitle() { return reviewTitle; }
    public void setReviewTitle(String reviewTitle) { this.reviewTitle = reviewTitle; }
    public String getReviewBody() { return reviewBody; }
    public void setReviewBody(String reviewBody) { this.reviewBody = reviewBody; }
    public int getHelpfulCount() { return helpfulCount; }
    public void setHelpfulCount(int helpfulCount) { this.helpfulCount = helpfulCount; }
    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
    public long getReviewDate() { return reviewDate; }
    public void setReviewDate(long reviewDate) { this.reviewDate = reviewDate; }
}
