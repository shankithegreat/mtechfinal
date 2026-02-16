package com.telecom.inventorymanagement.controller;

import com.telecom.inventorymanagement.config.FeatureFlagConstants;
import com.telecom.common.FeatureFlagReader;
import com.telecom.inventorymanagement.model.*;
import com.telecom.inventorymanagement.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * REST controllers for Inventory Management with comprehensive feature flag support.
 * All endpoints check feature flags and implement telecom inventory operations.
 */
@RestController
@RequestMapping("/api/inventory-management")
public class InventoryManagementController {

    @Autowired
    private InventoryManagementService service;

    /**
     * List all equipment
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listAll() {
        List<Object> equipment = service.listAll();
        Map<String, Object> response = new HashMap<>();
        response.put("count", equipment.size());
        response.put("data", equipment);
        return ResponseEntity.ok(response);
    }

    /**
     * Get equipment by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable String id) {
        Object obj = service.getById(id);
        if (obj == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(obj);
    }

    /**
     * Register new equipment - Feature flag protected
     */
    @PostMapping("/equipment/register")
    public ResponseEntity<Map<String, Object>> registerEquipment(@RequestBody Map<String, Object> equipmentData) {
        try {
            TelecomEquipment equipment = service.registerEquipment(equipmentData);
            Map<String, Object> response = new HashMap<>();
            response.put("equipmentId", equipment.getEquipmentId());
            response.put("equipmentType", equipment.getEquipmentType());
            response.put("status", equipment.getStatus());
            response.put("message", "Equipment registered successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Deploy equipment - Feature flag protected
     */
    @PostMapping("/equipment/{equipmentId}/deploy")
    public ResponseEntity<Map<String, Object>> deployEquipment(@PathVariable String equipmentId,
                                                               @RequestBody Map<String, String> data) {
        try {
            String customerId = data.get("customerId");
            String location = data.get("location");
            TelecomEquipment equipment = service.deployEquipment(equipmentId, customerId, location);
            
            Map<String, Object> response = new HashMap<>();
            response.put("equipmentId", equipmentId);
            response.put("status", equipment.getStatus());
            response.put("location", location);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Track equipment - Feature flag protected
     */
    @GetMapping("/equipment/{equipmentId}/track")
    public ResponseEntity<Map<String, Object>> trackEquipment(@PathVariable String equipmentId) {
        try {
            return ResponseEntity.ok(service.trackEquipment(equipmentId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Schedule maintenance - Feature flag protected
     */
    @PostMapping("/equipment/{equipmentId}/maintenance")
    public ResponseEntity<Map<String, Object>> scheduleMaintenance(@PathVariable String equipmentId,
                                                                   @RequestBody Map<String, Object> maintenanceData) {
        try {
            MaintenanceRecord record = service.scheduleMaintenanceRecord(equipmentId, maintenanceData);
            Map<String, Object> response = new HashMap<>();
            response.put("equipmentId", equipmentId);
            response.put("maintenanceId", record.getMaintenanceId());
            response.put("type", record.getMaintenanceType());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Update stock - Feature flag protected
     */
    @PutMapping("/stock/{stockId}")
    public ResponseEntity<Map<String, Object>> updateStock(@PathVariable String stockId,
                                                           @RequestBody Map<String, Object> stockData) {
        try {
            InventoryStock stock = service.updateStock(stockId, stockData);
            Map<String, Object> response = new HashMap<>();
            response.put("stockId", stockId);
            response.put("quantityOnHand", stock.getQuantityOnHand());
            response.put("quantityAvailable", stock.getQuantityAvailable());
            response.put("status", stock.getStockStatus());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Check low stock alerts - Feature flag protected
     */
    @GetMapping("/stock/alerts")
    public ResponseEntity<Map<String, Object>> checkLowStockAlerts() {
        try {
            List<Map<String, Object>> alerts = service.checkLowStockAlerts();
            Map<String, Object> response = new HashMap<>();
            response.put("alertCount", alerts.size());
            response.put("alerts", alerts);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Forecast stock needs - Feature flag protected
     */
    @PostMapping("/stock/forecast")
    public ResponseEntity<Map<String, Object>> forecastStock(@RequestBody Map<String, Object> data) {
        try {
            String equipmentType = (String) data.get("equipmentType");
            int forecastMonths = (Integer) data.getOrDefault("forecastMonths", 3);
            return ResponseEntity.ok(service.forecastStockNeeds(equipmentType, forecastMonths));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Update warehouse - Feature flag protected
     */
    @PutMapping("/warehouse/{warehouseId}")
    public ResponseEntity<Map<String, Object>> updateWarehouse(@PathVariable String warehouseId,
                                                               @RequestBody Map<String, Object> warehouseData) {
        try {
            Warehouse warehouse = service.updateWarehouse(warehouseId, warehouseData);
            Map<String, Object> response = new HashMap<>();
            response.put("warehouseId", warehouseId);
            response.put("name", warehouse.getName());
            response.put("utilization", warehouse.getCurrentUtilization());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Optimize warehouse distribution - Feature flag protected
     */
    @GetMapping("/warehouse/optimize")
    public ResponseEntity<Map<String, Object>> optimizeWarehouse() {
        try {
            return ResponseEntity.ok(service.optimizeWarehouseDistribution());
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Register supplier - Feature flag protected
     */
    @PostMapping("/supplier/register")
    public ResponseEntity<Map<String, Object>> registerSupplier(@RequestBody Map<String, Object> supplierData) {
        try {
            Supplier supplier = service.registerSupplier(supplierData);
            Map<String, Object> response = new HashMap<>();
            response.put("supplierId", supplier.getSupplierId());
            response.put("name", supplier.getName());
            response.put("status", "ACTIVE");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Create purchase order - Feature flag protected
     */
    @PostMapping("/purchase-order")
    public ResponseEntity<Map<String, Object>> createPurchaseOrder(@RequestBody Map<String, Object> poData) {
        try {
            PurchaseOrder po = service.createPurchaseOrder(poData);
            Map<String, Object> response = new HashMap<>();
            response.put("poNumber", po.getPoNumber());
            response.put("status", po.getStatus());
            response.put("totalAmount", po.getTotalAmount());
            response.put("quantityOrdered", po.getQuantityOrdered());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Receive purchase order - Feature flag protected
     */
    @PostMapping("/purchase-order/{poNumber}/receive")
    public ResponseEntity<Map<String, Object>> receivePurchaseOrder(@PathVariable String poNumber,
                                                                    @RequestBody Map<String, Integer> data) {
        try {
            int quantityReceived = data.getOrDefault("quantityReceived", 0);
            PurchaseOrder po = service.receivePurchaseOrder(poNumber, quantityReceived);
            Map<String, Object> response = new HashMap<>();
            response.put("poNumber", poNumber);
            response.put("status", po.getStatus());
            response.put("quantityReceived", po.getQuantityReceived());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Calculate depreciation - Feature flag protected
     */
    @GetMapping("/equipment/{equipmentId}/depreciation")
    public ResponseEntity<Map<String, Object>> calculateDepreciation(@PathVariable String equipmentId) {
        try {
            return ResponseEntity.ok(service.calculateDepreciation(equipmentId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Calculate ROI - Feature flag protected
     */
    @GetMapping("/equipment/{equipmentId}/roi")
    public ResponseEntity<Map<String, Object>> calculateROI(@PathVariable String equipmentId) {
        try {
            return ResponseEntity.ok(service.calculateROI(equipmentId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Track end of life - Feature flag protected
     */
    @GetMapping("/equipment/{equipmentId}/end-of-life")
    public ResponseEntity<Map<String, Object>> trackEndOfLife(@PathVariable String equipmentId) {
        try {
            return ResponseEntity.ok(service.trackEndOfLife(equipmentId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Generic create - Legacy support
     */
    @PostMapping
    public ResponseEntity<Object> create(@RequestBody Map<String, Object> payload) {
        Object created = service.create(payload);
        return ResponseEntity.ok(created);
    }

    /**
     * Generic update - Legacy support
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        Object updated = service.update(id, payload);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    /**
     * Generic delete - Legacy support
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        boolean ok = service.delete(id);
        if (!ok) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }

    /**
     * Generic search - Legacy support
     */
    @GetMapping("/search")
    public ResponseEntity<List<Object>> search(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(service.search(params));
    }

    /**
     * Generic bulk create - Legacy support
     */
    @PostMapping("/bulk")
    public ResponseEntity<List<Object>> bulkCreate(@RequestBody List<Map<String, Object>> payloads) {
        return ResponseEntity.ok(service.bulkCreate(payloads));
    }

    /**
     * Health check with feature flag status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "inventory-management");
        response.put("status", "UP");
        response.put("totalItems", service.count());
        response.put("featureFlagsEnabled", countEnabledFeatures());
        return ResponseEntity.ok(response);
    }

    /**
     * Count enabled features for diagnostics
     */
    private int countEnabledFeatures() {
        int count = 0;
        if (FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_EQUIPMENT_REGISTRATION)) count++;
        if (FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_EQUIPMENT_TRACKING)) count++;
        if (FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_EQUIPMENT_MAINTENANCE)) count++;
        if (FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_STOCK_MANAGEMENT)) count++;
        if (FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_STOCK_ALERTS)) count++;
        if (FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_WAREHOUSE_MANAGEMENT)) count++;
        if (FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_SUPPLIER_MANAGEMENT)) count++;
        if (FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_PURCHASE_ORDER)) count++;
        if (FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_DEPRECIATION_CALCULATION)) count++;
        if (FeatureFlagReader.isFeatureEnabled(FeatureFlagConstants.ENABLE_ROI_CALCULATION)) count++;
        return count;
    }
}
