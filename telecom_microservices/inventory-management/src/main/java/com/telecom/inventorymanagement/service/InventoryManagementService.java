                        // Cross-service: If order recommendations are enabled in order-management, recommend items
                        if (FeatureFlagReader.isFeatureEnabled("order_advanced_enable_order_recommendations")) {
                            System.out.println("[ORDER] Recommending items (order_advanced_enable_order_recommendations)");
                        }
                // Cross-service: If audit trail is enabled in billing, log audit info
                if (FeatureFlagReader.isFeatureEnabled("billing_enable_audit_trail")) {
                    System.out.println("[AUDIT] Inventory action: " + action + " (billing_enable_audit_trail)");
                }
        // Cross-service: If customer account linking is enabled, allow linking
        if (!FeatureFlagReader.isFeatureEnabled("customer_enable_account_linking")) {
            throw new IllegalStateException("Account linking is disabled (customer_enable_account_linking)");
        }
package com.telecom.inventorymanagement.service;

import org.springframework.stereotype.Service;
import com.telecom.inventorymanagement.model.*;
import com.telecom.inventorymanagement.config.FeatureFlagConstants;
import com.telecom.common.FeatureFlagReader;
import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service layer for Inventory Management with complex telecom equipment management logic.
 * Implements equipment tracking, stock management, warehouse operations, supplier management,
 * and advanced analytics with comprehensive feature flag support.
 */
@Service
public class InventoryManagementService {

    // In-memory stores for different entities
    private final Map<String, TelecomEquipment> equipmentStore = new ConcurrentHashMap<>();
    private final Map<String, InventoryStock> stockStore = new ConcurrentHashMap<>();
    private final Map<String, Warehouse> warehouseStore = new ConcurrentHashMap<>();
    private final Map<String, Supplier> supplierStore = new ConcurrentHashMap<>();
    private final Map<String, PurchaseOrder> purchaseOrderStore = new ConcurrentHashMap<>();
    private final Map<String, List<MaintenanceRecord>> maintenanceRecordsStore = new ConcurrentHashMap<>();
    private final Map<String, Map<String,Object>> genericStore = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        initializeSampleData();
    }

    /**
     * Initialize sample data for demonstration
     */
    private void initializeSampleData() {
        // Create sample warehouses
        for (int i = 1; i <= 3; i++) {
            Warehouse warehouse = new Warehouse();
            warehouse.setWarehouseId("WH-" + String.format("%03d", i));
            warehouse.setName("Distribution Center " + i);
            warehouse.setCity("City-" + i);
            warehouse.setCapacity(100000 + (i * 10000));
            warehouse.setCurrentUtilization(45.0 + (i * 10));
            warehouse.setStatus("ACTIVE");
            warehouseStore.put(warehouse.getWarehouseId(), warehouse);
        }

        // Create sample equipment
        String[] equipmentTypes = {"ROUTER", "SWITCH", "MODEM", "OPTICAL_NODE"};
        for (int i = 1; i <= 5; i++) {
            TelecomEquipment equipment = createSampleEquipment(i, equipmentTypes[(i - 1) % equipmentTypes.length]);
            equipmentStore.put(equipment.getEquipmentId(), equipment);
        }

        // Create sample stock levels
        for (String equipmentType : equipmentTypes) {
            InventoryStock stock = new InventoryStock();
            stock.setStockId("STOCK-" + equipmentType);
            stock.setEquipmentType(equipmentType);
            stock.setQuantityOnHand(100 + (int)(Math.random() * 200));
            stock.setQuantityAllocated(20 + (int)(Math.random() * 50));
            stock.setReorderPoint(50);
            stock.setReorderQuantity(100);
            stock.setWarehouseId("WH-001");
            stock.setStockStatus("IN_STOCK");
            stockStore.put(stock.getStockId(), stock);
        }
    }

    /**
     * Create sample equipment
     */
    private TelecomEquipment createSampleEquipment(int index, String equipmentType) {
        TelecomEquipment equipment = new TelecomEquipment();
        equipment.setEquipmentId(UUID.randomUUID().toString());
        equipment.setEquipmentType(equipmentType);
        equipment.setManufacturer("TechManufacturer-" + (index % 3 + 1));
        equipment.setModel("Model-" + equipmentType + "-2024");
        equipment.setSerialNumber("SN-" + String.format("%08d", System.currentTimeMillis() % 100000000 + index));
        equipment.setStatus("IN_STOCK");
        equipment.setWarehouseId("WH-00" + (index % 3 + 1));
        equipment.setPurchaseCost(5000 + (index * 1000));
        equipment.setCurrentValue(equipment.getPurchaseCost());
        equipment.setRegisteredAt(System.currentTimeMillis());
        equipment.setLastUpdatedAt(System.currentTimeMillis());
        equipment.setIsTracked(true);
        
        // Set lifecycle info
        LifecycleInfo lifecycle = new LifecycleInfo();
        lifecycle.setLifecycleStage("DEPLOYMENT");
        lifecycle.setDeploymentDate(System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L));
        lifecycle.setDepreciationRate(0.5); // 0.5% per month
        equipment.setLifecycleInfo(lifecycle);

        // Set specifications
        EquipmentSpecifications specs = new EquipmentSpecifications();
        specs.setFrequency("5G");
        specs.setBandwidth("100");
        specs.setWarranty("24");
        specs.setCompatibleServices(Arrays.asList("VOICE", "DATA"));
        equipment.setSpecifications(specs);

        // Set quality status
        QualityStatus quality = new QualityStatus();
        quality.setQualityRating("EXCELLENT");
        quality.setDefectCount(0);
        quality.setQcStatus("PASSED");
        equipment.setQualityStatus(quality);

        return equipment;
    }

    /**
     * Register new equipment with validation
     */
    public TelecomEquipment registerEquipment(Map<String, Object> equipmentData) {
        if (!FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_EQUIPMENT_REGISTRATION)) {
            throw new RuntimeException("Equipment registration feature is disabled");
        }

        TelecomEquipment equipment = new TelecomEquipment();
        equipment.setEquipmentId(UUID.randomUUID().toString());
        equipment.setEquipmentType((String) equipmentData.get("equipmentType"));
        equipment.setManufacturer((String) equipmentData.get("manufacturer"));
        equipment.setModel((String) equipmentData.get("model"));
        equipment.setSerialNumber((String) equipmentData.get("serialNumber"));
        equipment.setStatus("NEW");
        equipment.setWarehouseId((String) equipmentData.getOrDefault("warehouseId", "WH-001"));
        equipment.setPurchaseCost((Double) equipmentData.get("purchaseCost"));
        equipment.setCurrentValue(equipment.getPurchaseCost());
        equipment.setRegisteredAt(System.currentTimeMillis());
        equipment.setLastUpdatedAt(System.currentTimeMillis());
        equipment.setIsTracked(true);

        equipmentStore.put(equipment.getEquipmentId(), equipment);
        return equipment;
    }

    /**
     * Deploy equipment to customer or location
     */
    public TelecomEquipment deployEquipment(String equipmentId, String customerId, String location) {
        if (!FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_DEVICE_PROVISIONING)) {
            throw new RuntimeException("Device provisioning feature is disabled");
        }

        TelecomEquipment equipment = equipmentStore.get(equipmentId);
        if (equipment == null) throw new RuntimeException("Equipment not found");

        equipment.setStatus("DEPLOYED");
        equipment.setCurrentLocation(location);
        equipment.getAssignedToCustomers().add(customerId);
        equipment.setTotalDeployments(equipment.getTotalDeployments() + 1);
        equipment.setLastUpdatedAt(System.currentTimeMillis());

        equipmentStore.put(equipmentId, equipment);
        return equipment;
    }

    /**
     * Track equipment location and status
     */
    public Map<String, Object> trackEquipment(String equipmentId) {
        if (!FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_EQUIPMENT_TRACKING)) {
            throw new RuntimeException("Equipment tracking feature is disabled");
        }

        TelecomEquipment equipment = equipmentStore.get(equipmentId);
        if (equipment == null) throw new RuntimeException("Equipment not found");

        Map<String, Object> tracking = new HashMap<>();
        tracking.put("equipmentId", equipmentId);
        tracking.put("status", equipment.getStatus());
        tracking.put("location", equipment.getCurrentLocation());
        tracking.put("warehouseId", equipment.getWarehouseId());
        tracking.put("lastUpdated", equipment.getLastUpdatedAt());
        tracking.put("deployments", equipment.getTotalDeployments());
        tracking.put("assignedCustomers", equipment.getAssignedToCustomers());

        return tracking;
    }

    /**
     * Manage equipment maintenance
     */
    public MaintenanceRecord scheduleMaintenanceRecord(String equipmentId, Map<String, Object> maintenanceData) {
        if (!FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_EQUIPMENT_MAINTENANCE)) {
            throw new RuntimeException("Equipment maintenance feature is disabled");
        }

        TelecomEquipment equipment = equipmentStore.get(equipmentId);
        if (equipment == null) throw new RuntimeException("Equipment not found");

        MaintenanceRecord record = new MaintenanceRecord();
        record.setMaintenanceId(UUID.randomUUID().toString());
        record.setPerformedDate(System.currentTimeMillis());
        record.setMaintenanceType((String) maintenanceData.getOrDefault("type", "PREVENTIVE"));
        record.setDescription((String) maintenanceData.getOrDefault("description", ""));
        record.setTechnician((String) maintenanceData.getOrDefault("technician", ""));
        record.setCost((Double) maintenanceData.getOrDefault("cost", 0.0));

        equipment.getMaintenanceHistory().getRecords().add(record);
        equipment.getMaintenanceHistory().setLastMaintenanceDate(System.currentTimeMillis());
        equipment.setLastUpdatedAt(System.currentTimeMillis());

        equipmentStore.put(equipmentId, equipment);
        return record;
    }

    /**
     * Manage stock inventory
     */
    public InventoryStock updateStock(String stockId, Map<String, Object> stockData) {
        if (!FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_STOCK_MANAGEMENT)) {
            throw new RuntimeException("Stock management feature is disabled");
        }

        InventoryStock stock = stockStore.get(stockId);
        if (stock == null) throw new RuntimeException("Stock not found");

        stock.setQuantityOnHand((Integer) stockData.getOrDefault("quantityOnHand", stock.getQuantityOnHand()));
        stock.setQuantityAllocated((Integer) stockData.getOrDefault("quantityAllocated", stock.getQuantityAllocated()));
        stock.setLastUpdated(System.currentTimeMillis());

        // Recalculate available quantity
        stock.setQuantityAvailable(stock.getQuantityOnHand() - stock.getQuantityAllocated());

        // Check stock status
        updateStockStatus(stock);

        stockStore.put(stockId, stock);
        return stock;
    }

    /**
     * Update stock status based on thresholds
     */
    private void updateStockStatus(InventoryStock stock) {
        if (stock.getQuantityAvailable() <= 0) {
            stock.setStockStatus("OUT_OF_STOCK");
        } else if (stock.getQuantityAvailable() <= stock.getReorderPoint()) {
            stock.setStockStatus("LOW_STOCK");
            
            // Trigger reorder if feature enabled
            if (FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_STOCK_REORDER)) {
                triggerReorder(stock);
            }
        } else if (stock.getQuantityOnHand() > stock.getReorderPoint() * 3) {
            stock.setStockStatus("OVERSTOCKED");
        } else {
            stock.setStockStatus("IN_STOCK");
        }
    }

    /**
     * Generate low stock alerts
     */
    public List<Map<String, Object>> checkLowStockAlerts() {
        if (!FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_STOCK_ALERTS)) {
            throw new RuntimeException("Stock alerts feature is disabled");
        }

        List<Map<String, Object>> alerts = new ArrayList<>();

        for (InventoryStock stock : stockStore.values()) {
            if ("LOW_STOCK".equals(stock.getStockStatus())) {
                Map<String, Object> alert = new HashMap<>();
                alert.put("stockId", stock.getStockId());
                alert.put("equipmentType", stock.getEquipmentType());
                alert.put("quantityAvailable", stock.getQuantityAvailable());
                alert.put("reorderPoint", stock.getReorderPoint());
                alert.put("severity", calculateAlertSeverity(stock));
                alerts.add(alert);
            }
        }

        return alerts;
    }

    /**
     * Calculate alert severity
     */
    private String calculateAlertSeverity(InventoryStock stock) {
        if (stock.getQuantityAvailable() <= 0) return "CRITICAL";
        if (stock.getQuantityAvailable() <= stock.getReorderPoint() / 2) return "HIGH";
        return "MEDIUM";
    }

    /**
     * Forecast stock requirements
     */
    public Map<String, Object> forecastStockNeeds(String equipmentType, int forecastMonths) {
        if (!FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_STOCK_FORECASTING)) {
            throw new RuntimeException("Stock forecasting feature is disabled");
        }

        InventoryStock stock = stockStore.values().stream()
            .filter(s -> s.getEquipmentType().equals(equipmentType))
            .findFirst()
            .orElse(null);

        if (stock == null) throw new RuntimeException("Stock for equipment type not found");

        // Simple forecasting: assume 10% monthly consumption
        int currentAvailable = stock.getQuantityAvailable();
        int monthlyConsumption = (int) (stock.getQuantityOnHand() * 0.1);
        int forecastedNeed = monthlyConsumption * forecastMonths;
        int recommendedOrder = Math.max(forecastedNeed - currentAvailable, stock.getReorderQuantity());

        Map<String, Object> forecast = new HashMap<>();
        forecast.put("equipmentType", equipmentType);
        forecast.put("currentAvailable", currentAvailable);
        forecast.put("monthlyConsumption", monthlyConsumption);
        forecast.put("forecastMonths", forecastMonths);
        forecast.put("recommendedOrder", recommendedOrder);

        return forecast;
    }

    /**
     * Trigger reorder for low stock
     */
    private void triggerReorder(InventoryStock stock) {
        // Integration point for order management system
    }

    /**
     * Manage warehouse operations
     */
    public Warehouse updateWarehouse(String warehouseId, Map<String, Object> warehouseData) {
        if (!FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_WAREHOUSE_MANAGEMENT)) {
            throw new RuntimeException("Warehouse management feature is disabled");
        }

        Warehouse warehouse = warehouseStore.get(warehouseId);
        if (warehouse == null) throw new RuntimeException("Warehouse not found");

        warehouse.setName((String) warehouseData.getOrDefault("name", warehouse.getName()));
        warehouse.setStatus((String) warehouseData.getOrDefault("status", warehouse.getStatus()));
        warehouse.setCurrentUtilization((Double) warehouseData.getOrDefault("currentUtilization", warehouse.getCurrentUtilization()));

        warehouseStore.put(warehouseId, warehouse);
        return warehouse;
    }

    /**
     * Optimize warehouse distribution
     */
    public Map<String, Object> optimizeWarehouseDistribution() {
        if (!FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_WAREHOUSE_OPTIMIZATION)) {
            throw new RuntimeException("Warehouse optimization feature is disabled");
        }

        Map<String, Object> optimization = new HashMap<>();
        
        for (Warehouse warehouse : warehouseStore.values()) {
            double utilizationRate = warehouse.getCurrentUtilization() / warehouse.getCapacity() * 100;
            optimization.put(warehouse.getWarehouseId(), new HashMap<String, Object>() {{
                put("name", warehouse.getName());
                put("capacity", warehouse.getCapacity());
                put("utilization", warehouse.getCurrentUtilization());
                put("utilizationRate", String.format("%.2f%%", utilizationRate));
                put("recommendation", utilizationRate > 80 ? "REDISTRIBUTE" : "OPTIMAL");
            }});
        }

        return optimization;
    }

    /**
     * Register supplier
     */
    public Supplier registerSupplier(Map<String, Object> supplierData) {
        if (!FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_SUPPLIER_MANAGEMENT)) {
            throw new RuntimeException("Supplier management feature is disabled");
        }

        Supplier supplier = new Supplier();
        supplier.setSupplierId(UUID.randomUUID().toString());
        supplier.setName((String) supplierData.get("name"));
        supplier.setContactPerson((String) supplierData.getOrDefault("contactPerson", ""));
        supplier.setEmail((String) supplierData.getOrDefault("email", ""));
        supplier.setPhone((String) supplierData.getOrDefault("phone", ""));
        supplier.setCountry((String) supplierData.getOrDefault("country", ""));
        supplier.setLeadTimeDays((Integer) supplierData.getOrDefault("leadTimeDays", 30));
        supplier.setEquipmentTypes((List<String>) supplierData.getOrDefault("equipmentTypes", new ArrayList<>()));
        supplier.setMinimumOrderQuantity((Double) supplierData.getOrDefault("minimumOrderQuantity", 10.0));
        supplier.setIsActive(true);
        supplier.setSupplierRating(0.0);
        supplier.setQualityScore(0);

        supplierStore.put(supplier.getSupplierId(), supplier);
        return supplier;
    }

    /**
     * Create purchase order
     */
    public PurchaseOrder createPurchaseOrder(Map<String, Object> poData) {
        if (!FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_PURCHASE_ORDER)) {
            throw new RuntimeException("Purchase order feature is disabled");
        }

        PurchaseOrder po = new PurchaseOrder();
        po.setPoNumber("PO-" + System.currentTimeMillis());
        po.setSupplierId((String) poData.get("supplierId"));
        po.setCreatedDate(System.currentTimeMillis());
        po.setExpectedDeliveryDate(System.currentTimeMillis() + (30 * 24 * 60 * 60 * 1000L));
        po.setStatus("DRAFT");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> lineItemsData = (List<Map<String, Object>>) poData.get("lineItems");
        
        double totalAmount = 0;
        int totalQuantity = 0;
        
        if (lineItemsData != null) {
            for (Map<String, Object> itemData : lineItemsData) {
                POLineItem lineItem = new POLineItem();
                lineItem.setLineItemId(UUID.randomUUID().toString());
                lineItem.setEquipmentType((String) itemData.get("equipmentType"));
                lineItem.setQuantity((Integer) itemData.get("quantity"));
                lineItem.setUnitPrice((Double) itemData.get("unitPrice"));
                lineItem.setLineTotal(lineItem.getQuantity() * lineItem.getUnitPrice());
                lineItem.setDeliveryStatus("PENDING");
                
                po.getLineItems().add(lineItem);
                totalAmount += lineItem.getLineTotal();
                totalQuantity += lineItem.getQuantity();
            }
        }

        po.setTotalAmount(totalAmount);
        po.setQuantityOrdered(totalQuantity);

        purchaseOrderStore.put(po.getPoNumber(), po);
        return po;
    }

    /**
     * Receive purchase order
     */
    public PurchaseOrder receivePurchaseOrder(String poNumber, int quantityReceived) {
        if (!FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_PURCHASE_ORDER)) {
            throw new RuntimeException("Purchase order feature is disabled");
        }

        PurchaseOrder po = purchaseOrderStore.get(poNumber);
        if (po == null) throw new RuntimeException("Purchase order not found");

        po.setStatus("RECEIVED");
        po.setActualDeliveryDate(System.currentTimeMillis());
        po.setQuantityReceived(quantityReceived);

        purchaseOrderStore.put(poNumber, po);
        return po;
    }

    /**
     * Calculate equipment depreciation
     */
    public Map<String, Object> calculateDepreciation(String equipmentId) {
        if (!FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_DEPRECIATION_CALCULATION)) {
            throw new RuntimeException("Depreciation calculation feature is disabled");
        }

        TelecomEquipment equipment = equipmentStore.get(equipmentId);
        if (equipment == null) throw new RuntimeException("Equipment not found");

        long ageMonths = (System.currentTimeMillis() - equipment.getRegisteredAt()) / (30L * 24 * 60 * 60 * 1000);
        double depreciationRate = equipment.getLifecycleInfo().getDepreciationRate();
        double depreciatedValue = equipment.getPurchaseCost() * Math.pow(1 - depreciationRate / 100, ageMonths);
        double totalDepreciation = equipment.getPurchaseCost() - depreciatedValue;

        equipment.setCurrentValue(depreciatedValue);
        equipment.getLifecycleInfo().setTotalDepreciation(totalDepreciation);
        equipment.getLifecycleInfo().setUsageMonths((int) ageMonths);
        equipmentStore.put(equipmentId, equipment);

        Map<String, Object> result = new HashMap<>();
        result.put("equipmentId", equipmentId);
        result.put("originalCost", equipment.getPurchaseCost());
        result.put("ageMonths", ageMonths);
        result.put("depreciationRate", depreciationRate);
        result.put("currentValue", depreciatedValue);
        result.put("totalDepreciation", totalDepreciation);

        return result;
    }

    /**
     * Analyze equipment ROI
     */
    public Map<String, Object> calculateROI(String equipmentId) {
        if (!FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_ROI_CALCULATION)) {
            throw new RuntimeException("ROI calculation feature is disabled");
        }

        TelecomEquipment equipment = equipmentStore.get(equipmentId);
        if (equipment == null) throw new RuntimeException("Equipment not found");

        double totalMaintenanceCost = equipment.getMaintenanceHistory().getRecords().stream()
            .mapToDouble(MaintenanceRecord::getCost).sum();
        
        double assumedRevenue = equipment.getTotalDeployments() * 500.0; // Assume $500 per deployment
        double totalCost = equipment.getPurchaseCost() + totalMaintenanceCost;
        double roi = ((assumedRevenue - totalCost) / totalCost) * 100;

        Map<String, Object> result = new HashMap<>();
        result.put("equipmentId", equipmentId);
        result.put("purchaseCost", equipment.getPurchaseCost());
        result.put("maintenanceCost", totalMaintenanceCost);
        result.put("totalCost", totalCost);
        result.put("assumedRevenue", assumedRevenue);
        result.put("roi", String.format("%.2f%%", roi));

        return result;
    }

    /**
     * Track equipment end of life
     */
    public Map<String, Object> trackEndOfLife(String equipmentId) {
        if (!FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_END_OF_LIFE)) {
            throw new RuntimeException("End of life tracking feature is disabled");
        }

        TelecomEquipment equipment = equipmentStore.get(equipmentId);
        if (equipment == null) throw new RuntimeException("Equipment not found");

        long ageMonths = (System.currentTimeMillis() - equipment.getRegisteredAt()) / (30L * 24 * 60 * 60 * 1000);
        boolean isObsolete = ageMonths > 60; // Assume 5-year lifecycle

        equipment.getLifecycleInfo().setLifecycleStage(isObsolete ? "OBSOLETE" : "ACTIVE");
        equipmentStore.put(equipmentId, equipment);

        Map<String, Object> result = new HashMap<>();
        result.put("equipmentId", equipmentId);
        result.put("ageMonths", ageMonths);
        result.put("isObsolete", isObsolete);
        result.put("lifecycleStage", equipment.getLifecycleInfo().getLifecycleStage());

        return result;
    }

    // ============ Legacy CRUD operations ============

    public List<Object> listAll() {
        return new ArrayList<>(equipmentStore.values());
    }

    public Object getById(String id) {
        return equipmentStore.getOrDefault(id, genericStore.get(id));
    }

    public Object create(Map<String,Object> payload) {
        String id = UUID.randomUUID().toString();
        payload.put("id", id);
        payload.put("createdAt", System.currentTimeMillis());
        genericStore.put(id, payload);
        return payload;
    }

    public Object update(String id, Map<String,Object> payload) {
        Map<String,Object> existing = genericStore.get(id);
        if (existing == null) return null;
        existing.putAll(payload);
        existing.put("updatedAt", System.currentTimeMillis());
        genericStore.put(id, existing);
        return existing;
    }

    public boolean delete(String id) {
        return genericStore.remove(id) != null;
    }

    public List<Object> search(Map<String,String> params) {
        List<Object> out = new ArrayList<>();
        for (Map<String,Object> v : genericStore.values()) {
            boolean match = true;
            for (String k : params.keySet()) {
                String val = params.get(k).toLowerCase();
                Object field = v.get(k);
                if (field == null || !field.toString().toLowerCase().contains(val)) {
                    match = false;
                    break;
                }
            }
            if (match) out.add(v);
        }
        return out;
    }

    public List<Object> bulkCreate(List<Map<String,Object>> payloads) {
        List<Object> created = new ArrayList<>();
        for (Map<String,Object> p : payloads) {
            created.add(create(p));
        }
        return created;
    }

    public int count() {
        return equipmentStore.size() + genericStore.size();
    }
}
