package com.telecom.inventorymanagement.model;

import java.io.Serializable;
import java.util.*;

/**
 * Extra DTOs and wrapper types for InventoryManagement
 */
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

/**
 * Telecom equipment/device inventory item representing physical or virtual equipment.
 */
class TelecomEquipment implements Serializable {
    private String equipmentId;
    private String equipmentType; // ROUTER, SWITCH, MODEM, PHONE, SIM_CARD, OPTICAL_NODE
    private String manufacturer;
    private String model;
    private String serialNumber;
    private String imei; // For mobile devices
    private String status; // NEW, IN_STOCK, DEPLOYED, IN_MAINTENANCE, DAMAGED, OBSOLETE, RETIRED
    private String currentLocation;
    private String warehouseId;
    private EquipmentSpecifications specifications;
    private LifecycleInfo lifecycleInfo;
    private MaintenanceHistory maintenanceHistory;
    private double purchaseCost;
    private double currentValue;
    private long registeredAt;
    private long lastUpdatedAt;
    private boolean isTracked;
    private int totalDeployments;
    private QualityStatus qualityStatus;
    private List<String> assignedToCustomers;

    // Constructors
    public TelecomEquipment() {
        this.assignedToCustomers = new ArrayList<>();
        this.maintenanceHistory = new MaintenanceHistory();
        this.lifecycleInfo = new LifecycleInfo();
        this.isTracked = false;
        this.totalDeployments = 0;
    }

    // Getters and Setters
    public String getEquipmentId() { return equipmentId; }
    public void setEquipmentId(String equipmentId) { this.equipmentId = equipmentId; }

    public String getEquipmentType() { return equipmentType; }
    public void setEquipmentType(String equipmentType) { this.equipmentType = equipmentType; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public String getImei() { return imei; }
    public void setImei(String imei) { this.imei = imei; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }

    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }

    public EquipmentSpecifications getSpecifications() { return specifications; }
    public void setSpecifications(EquipmentSpecifications specifications) { this.specifications = specifications; }

    public LifecycleInfo getLifecycleInfo() { return lifecycleInfo; }
    public void setLifecycleInfo(LifecycleInfo lifecycleInfo) { this.lifecycleInfo = lifecycleInfo; }

    public MaintenanceHistory getMaintenanceHistory() { return maintenanceHistory; }
    public void setMaintenanceHistory(MaintenanceHistory maintenanceHistory) { this.maintenanceHistory = maintenanceHistory; }

    public double getPurchaseCost() { return purchaseCost; }
    public void setPurchaseCost(double purchaseCost) { this.purchaseCost = purchaseCost; }

    public double getCurrentValue() { return currentValue; }
    public void setCurrentValue(double currentValue) { this.currentValue = currentValue; }

    public long getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(long registeredAt) { this.registeredAt = registeredAt; }

    public long getLastUpdatedAt() { return lastUpdatedAt; }
    public void setLastUpdatedAt(long lastUpdatedAt) { this.lastUpdatedAt = lastUpdatedAt; }

    public boolean isTracked() { return isTracked; }
    public void setTracked(boolean tracked) { isTracked = tracked; }

    public int getTotalDeployments() { return totalDeployments; }
    public void setTotalDeployments(int totalDeployments) { this.totalDeployments = totalDeployments; }

    public QualityStatus getQualityStatus() { return qualityStatus; }
    public void setQualityStatus(QualityStatus qualityStatus) { this.qualityStatus = qualityStatus; }

    public List<String> getAssignedToCustomers() { return assignedToCustomers; }
    public void setAssignedToCustomers(List<String> assignedToCustomers) { this.assignedToCustomers = assignedToCustomers; }
}

/**
 * Equipment specifications and technical details.
 */
class EquipmentSpecifications implements Serializable {
    private String frequency; // 2G, 3G, 4G, 5G, etc.
    private String bandwidth; // In MHz
    private double weight; // In kg
    private String powerConsumption; // In watts
    private String operatingTemperature;
    private String warranty; // In months
    private List<String> compatibleServices; // VOICE, DATA, IoT, etc.

    // Getters and Setters
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public String getBandwidth() { return bandwidth; }
    public void setBandwidth(String bandwidth) { this.bandwidth = bandwidth; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public String getPowerConsumption() { return powerConsumption; }
    public void setPowerConsumption(String powerConsumption) { this.powerConsumption = powerConsumption; }

    public String getOperatingTemperature() { return operatingTemperature; }
    public void setOperatingTemperature(String operatingTemperature) { this.operatingTemperature = operatingTemperature; }

    public String getWarranty() { return warranty; }
    public void setWarranty(String warranty) { this.warranty = warranty; }

    public List<String> getCompatibleServices() { return compatibleServices; }
    public void setCompatibleServices(List<String> compatibleServices) { this.compatibleServices = compatibleServices; }
}

/**
 * Equipment lifecycle tracking.
 */
class LifecycleInfo implements Serializable {
    private String lifecycleStage; // DESIGN, PROCUREMENT, DEPLOYMENT, ACTIVE, MAINTENANCE, DEPRECATION, RETIREMENT
    private long deploymentDate;
    private long expectedEndOfLifeDate;
    private long retirementDate;
    private int usageMonths;
    private double depreciationRate; // Percentage per month
    private double totalDepreciation;

    // Getters and Setters
    public String getLifecycleStage() { return lifecycleStage; }
    public void setLifecycleStage(String lifecycleStage) { this.lifecycleStage = lifecycleStage; }

    public long getDeploymentDate() { return deploymentDate; }
    public void setDeploymentDate(long deploymentDate) { this.deploymentDate = deploymentDate; }

    public long getExpectedEndOfLifeDate() { return expectedEndOfLifeDate; }
    public void setExpectedEndOfLifeDate(long expectedEndOfLifeDate) { this.expectedEndOfLifeDate = expectedEndOfLifeDate; }

    public long getRetirementDate() { return retirementDate; }
    public void setRetirementDate(long retirementDate) { this.retirementDate = retirementDate; }

    public int getUsageMonths() { return usageMonths; }
    public void setUsageMonths(int usageMonths) { this.usageMonths = usageMonths; }

    public double getDepreciationRate() { return depreciationRate; }
    public void setDepreciationRate(double depreciationRate) { this.depreciationRate = depreciationRate; }

    public double getTotalDepreciation() { return totalDepreciation; }
    public void setTotalDepreciation(double totalDepreciation) { this.totalDepreciation = totalDepreciation; }
}

/**
 * Maintenance history tracking for equipment.
 */
class MaintenanceHistory implements Serializable {
    private List<MaintenanceRecord> records;
    private long lastMaintenanceDate;
    private long nextScheduledDate;
    private String maintenanceStatus; // ACTIVE, PENDING, COMPLETED, OVERDUE

    public MaintenanceHistory() {
        this.records = new ArrayList<>();
    }

    // Getters and Setters
    public List<MaintenanceRecord> getRecords() { return records; }
    public void setRecords(List<MaintenanceRecord> records) { this.records = records; }

    public long getLastMaintenanceDate() { return lastMaintenanceDate; }
    public void setLastMaintenanceDate(long lastMaintenanceDate) { this.lastMaintenanceDate = lastMaintenanceDate; }

    public long getNextScheduledDate() { return nextScheduledDate; }
    public void setNextScheduledDate(long nextScheduledDate) { this.nextScheduledDate = nextScheduledDate; }

    public String getMaintenanceStatus() { return maintenanceStatus; }
    public void setMaintenanceStatus(String maintenanceStatus) { this.maintenanceStatus = maintenanceStatus; }
}

/**
 * Individual maintenance record.
 */
class MaintenanceRecord implements Serializable {
    private String maintenanceId;
    private long performedDate;
    private String maintenanceType; // PREVENTIVE, CORRECTIVE, INSPECTION, UPGRADE
    private String description;
    private String technician;
    private double cost;
    private int durationHours;

    // Getters and Setters
    public String getMaintenanceId() { return maintenanceId; }
    public void setMaintenanceId(String maintenanceId) { this.maintenanceId = maintenanceId; }

    public long getPerformedDate() { return performedDate; }
    public void setPerformedDate(long performedDate) { this.performedDate = performedDate; }

    public String getMaintenanceType() { return maintenanceType; }
    public void setMaintenanceType(String maintenanceType) { this.maintenanceType = maintenanceType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTechnician() { return technician; }
    public void setTechnician(String technician) { this.technician = technician; }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }

    public int getDurationHours() { return durationHours; }
    public void setDurationHours(int durationHours) { this.durationHours = durationHours; }
}

/**
 * Quality control status for equipment.
 */
class QualityStatus implements Serializable {
    private String qualityRating; // EXCELLENT, GOOD, ACCEPTABLE, POOR, DEFECTIVE
    private int defectCount;
    private List<String> defectDetails;
    private long lastQualityCheck;
    private String qcStatus; // PASSED, FAILED, PENDING_INSPECTION

    public QualityStatus() {
        this.defectDetails = new ArrayList<>();
    }

    // Getters and Setters
    public String getQualityRating() { return qualityRating; }
    public void setQualityRating(String qualityRating) { this.qualityRating = qualityRating; }

    public int getDefectCount() { return defectCount; }
    public void setDefectCount(int defectCount) { this.defectCount = defectCount; }

    public List<String> getDefectDetails() { return defectDetails; }
    public void setDefectDetails(List<String> defectDetails) { this.defectDetails = defectDetails; }

    public long getLastQualityCheck() { return lastQualityCheck; }
    public void setLastQualityCheck(long lastQualityCheck) { this.lastQualityCheck = lastQualityCheck; }

    public String getQcStatus() { return qcStatus; }
    public void setQcStatus(String qcStatus) { this.qcStatus = qcStatus; }
}

/**
 * Stock inventory for equipment.
 */
class InventoryStock implements Serializable {
    private String stockId;
    private String equipmentType;
    private int quantityOnHand;
    private int quantityAllocated;
    private int quantityAvailable;
    private int reorderPoint;
    private int reorderQuantity;
    private double unitCost;
    private String warehouseId;
    private long lastUpdated;
    private String stockStatus; // IN_STOCK, LOW_STOCK, OUT_OF_STOCK, OVERSTOCKED

    // Getters and Setters
    public String getStockId() { return stockId; }
    public void setStockId(String stockId) { this.stockId = stockId; }

    public String getEquipmentType() { return equipmentType; }
    public void setEquipmentType(String equipmentType) { this.equipmentType = equipmentType; }

    public int getQuantityOnHand() { return quantityOnHand; }
    public void setQuantityOnHand(int quantityOnHand) { this.quantityOnHand = quantityOnHand; }

    public int getQuantityAllocated() { return quantityAllocated; }
    public void setQuantityAllocated(int quantityAllocated) { this.quantityAllocated = quantityAllocated; }

    public int getQuantityAvailable() { return quantityAvailable; }
    public void setQuantityAvailable(int quantityAvailable) { this.quantityAvailable = quantityAvailable; }

    public int getReorderPoint() { return reorderPoint; }
    public void setReorderPoint(int reorderPoint) { this.reorderPoint = reorderPoint; }

    public int getReorderQuantity() { return reorderQuantity; }
    public void setReorderQuantity(int reorderQuantity) { this.reorderQuantity = reorderQuantity; }

    public double getUnitCost() { return unitCost; }
    public void setUnitCost(double unitCost) { this.unitCost = unitCost; }

    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }

    public long getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }

    public String getStockStatus() { return stockStatus; }
    public void setStockStatus(String stockStatus) { this.stockStatus = stockStatus; }
}

/**
 * Warehouse location information.
 */
class Warehouse implements Serializable {
    private String warehouseId;
    private String name;
    private String location;
    private String city;
    private String country;
    private double latitude;
    private double longitude;
    private double capacity; // In units
    private double currentUtilization;
    private String status; // ACTIVE, INACTIVE, UNDER_MAINTENANCE
    private List<String> operatingHours;
    private List<String> managedEquipmentTypes;

    // Getters and Setters
    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public double getCapacity() { return capacity; }
    public void setCapacity(double capacity) { this.capacity = capacity; }

    public double getCurrentUtilization() { return currentUtilization; }
    public void setCurrentUtilization(double currentUtilization) { this.currentUtilization = currentUtilization; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<String> getOperatingHours() { return operatingHours; }
    public void setOperatingHours(List<String> operatingHours) { this.operatingHours = operatingHours; }

    public List<String> getManagedEquipmentTypes() { return managedEquipmentTypes; }
    public void setManagedEquipmentTypes(List<String> managedEquipmentTypes) { this.managedEquipmentTypes = managedEquipmentTypes; }
}

/**
 * Supplier information for equipment procurement.
 */
class Supplier implements Serializable {
    private String supplierId;
    private String name;
    private String contactPerson;
    private String email;
    private String phone;
    private String country;
    private List<String> equipmentTypes;
    private int leadTimeDays;
    private double minimumOrderQuantity;
    private double supplierRating; // 0-5 stars
    private int onTimeDeliveryRate;
    private int qualityScore;
    private boolean isActive;
    private double totalPurchasedAmount;

    // Getters and Setters
    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String supplierId) { this.supplierId = supplierId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public List<String> getEquipmentTypes() { return equipmentTypes; }
    public void setEquipmentTypes(List<String> equipmentTypes) { this.equipmentTypes = equipmentTypes; }

    public int getLeadTimeDays() { return leadTimeDays; }
    public void setLeadTimeDays(int leadTimeDays) { this.leadTimeDays = leadTimeDays; }

    public double getMinimumOrderQuantity() { return minimumOrderQuantity; }
    public void setMinimumOrderQuantity(double minimumOrderQuantity) { this.minimumOrderQuantity = minimumOrderQuantity; }

    public double getSupplierRating() { return supplierRating; }
    public void setSupplierRating(double supplierRating) { this.supplierRating = supplierRating; }

    public int getOnTimeDeliveryRate() { return onTimeDeliveryRate; }
    public void setOnTimeDeliveryRate(int onTimeDeliveryRate) { this.onTimeDeliveryRate = onTimeDeliveryRate; }

    public int getQualityScore() { return qualityScore; }
    public void setQualityScore(int qualityScore) { this.qualityScore = qualityScore; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public double getTotalPurchasedAmount() { return totalPurchasedAmount; }
    public void setTotalPurchasedAmount(double totalPurchasedAmount) { this.totalPurchasedAmount = totalPurchasedAmount; }
}

/**
 * Purchase order for equipment procurement.
 */
class PurchaseOrder implements Serializable {
    private String poNumber;
    private String supplierId;
    private long createdDate;
    private long expectedDeliveryDate;
    private long actualDeliveryDate;
    private List<POLineItem> lineItems;
    private double totalAmount;
    private String status; // DRAFT, SUBMITTED, CONFIRMED, SHIPPED, RECEIVED, CANCELLED
    private int quantityOrdered;
    private int quantityReceived;

    public PurchaseOrder() {
        this.lineItems = new ArrayList<>();
    }

    // Getters and Setters
    public String getPoNumber() { return poNumber; }
    public void setPoNumber(String poNumber) { this.poNumber = poNumber; }

    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String supplierId) { this.supplierId = supplierId; }

    public long getCreatedDate() { return createdDate; }
    public void setCreatedDate(long createdDate) { this.createdDate = createdDate; }

    public long getExpectedDeliveryDate() { return expectedDeliveryDate; }
    public void setExpectedDeliveryDate(long expectedDeliveryDate) { this.expectedDeliveryDate = expectedDeliveryDate; }

    public long getActualDeliveryDate() { return actualDeliveryDate; }
    public void setActualDeliveryDate(long actualDeliveryDate) { this.actualDeliveryDate = actualDeliveryDate; }

    public List<POLineItem> getLineItems() { return lineItems; }
    public void setLineItems(List<POLineItem> lineItems) { this.lineItems = lineItems; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getQuantityOrdered() { return quantityOrdered; }
    public void setQuantityOrdered(int quantityOrdered) { this.quantityOrdered = quantityOrdered; }

    public int getQuantityReceived() { return quantityReceived; }
    public void setQuantityReceived(int quantityReceived) { this.quantityReceived = quantityReceived; }
}

/**
 * Line item in a purchase order.
 */
class POLineItem implements Serializable {
    private String lineItemId;
    private String equipmentType;
    private int quantity;
    private double unitPrice;
    private double lineTotal;
    private String deliveryStatus;

    // Getters and Setters
    public String getLineItemId() { return lineItemId; }
    public void setLineItemId(String lineItemId) { this.lineItemId = lineItemId; }

    public String getEquipmentType() { return equipmentType; }
    public void setEquipmentType(String equipmentType) { this.equipmentType = equipmentType; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public double getLineTotal() { return lineTotal; }
    public void setLineTotal(double lineTotal) { this.lineTotal = lineTotal; }

    public String getDeliveryStatus() { return deliveryStatus; }
    public void setDeliveryStatus(String deliveryStatus) { this.deliveryStatus = deliveryStatus; }
}
