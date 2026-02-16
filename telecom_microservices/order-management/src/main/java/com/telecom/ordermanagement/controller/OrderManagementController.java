package com.telecom.ordermanagement.controller;

import com.telecom.common.FeatureFlagReader;
import com.telecom.ordermanagement.model.*;
import com.telecom.ordermanagement.service.*;
import com.telecom.ordermanagement.config.OrderManagementFeatureFlagConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * REST controllers for OrderManagement endpoints.
 * Comprehensive telecom order management with feature flags integrated across all operations.
 * Supports order creation, fulfillment, provisioning, billing, and status tracking.
 */
@RestController
@RequestMapping("/api/order-management")
public class OrderManagementController {

    @Autowired
    private OrderManagementService service;

    // ==================== ORDER LIFECYCLE ENDPOINTS ====================

    /**
     * Create a new telecom order
     */
    @PostMapping("/orders")
    public ResponseEntity<?> createTelecomOrder(@RequestBody TelecomOrder orderRequest) {
        try {
            if (!FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_CREATION)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Order creation feature is disabled"));
            }
            
            TelecomOrder createdOrder = service.createOrder(orderRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Approve an order
     */
    @PostMapping("/orders/{orderId}/approve")
    public ResponseEntity<?> approveOrder(@PathVariable String orderId) {
        try {
            if (!FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_APPROVAL_WORKFLOW)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Order approval workflow is disabled"));
            }
            
            TelecomOrder approvedOrder = service.approveOrder(orderId);
            return ResponseEntity.ok(approvedOrder);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get order status with complete tracking information
     */
    @GetMapping("/orders/{orderId}/status")
    public ResponseEntity<?> getOrderStatus(@PathVariable String orderId) {
        try {
            if (!FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_STATUS_TRACKING)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Order status tracking is disabled"));
            }
            
            Map<String, Object> status = service.getOrderStatus(orderId);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Cancel an order
     */
    @PostMapping("/orders/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable String orderId) {
        try {
            if (!FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_CANCELLATION)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Order cancellation is disabled"));
            }
            
            Map<String, Object> cancellationResult = service.cancelOrder(orderId);
            return ResponseEntity.ok(cancellationResult);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== FULFILLMENT ENDPOINTS ====================

    /**
     * Process fulfillment for an order
     */
    @PostMapping("/orders/{orderId}/fulfill")
    public ResponseEntity<?> processFulfillment(@PathVariable String orderId) {
        try {
            if (!FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_FULFILLMENT)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Fulfillment processing is disabled"));
            }
            
            FulfillmentDetails fulfillment = service.processFulfillment(orderId);
            return ResponseEntity.ok(fulfillment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Update fulfillment status with event tracking
     */
    @PutMapping("/fulfillment/{fulfillmentId}/status")
    public ResponseEntity<?> updateFulfillmentStatus(
            @PathVariable String fulfillmentId,
            @RequestParam String status) {
        try {
            if (!FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_DELIVERY_TRACKING)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Delivery tracking is disabled"));
            }
            
            FulfillmentDetails updated = service.updateFulfillmentStatus(fulfillmentId, status);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== SERVICE PROVISIONING ENDPOINTS ====================

    /**
     * Provision services and activate devices/SIM cards
     */
    @PostMapping("/orders/{orderId}/provision")
    public ResponseEntity<?> provisionServices(@PathVariable String orderId) {
        try {
            if (!FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_SERVICE_PROVISIONING)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Service provisioning is disabled"));
            }
            
            ServiceProvisioningDetails provisioning = service.provisionServices(orderId);
            return ResponseEntity.ok(provisioning);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== BILLING ENDPOINTS ====================

    /**
     * Create billing account for order
     */
    @PostMapping("/orders/{orderId}/billing-account")
    public ResponseEntity<?> createBillingAccount(@PathVariable String orderId) {
        try {
            if (!FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_BILLING_ACCOUNT)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Billing account creation is disabled"));
            }
            
            Map<String, Object> billingAccount = service.createBillingAccount(orderId);
            return ResponseEntity.status(HttpStatus.CREATED).body(billingAccount);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== LEGACY GENERIC ENDPOINTS ====================

    /**
     * List all orders
     */
    @GetMapping
    public ResponseEntity<List<Object>> listAll() {
        return ResponseEntity.ok(service.listAll());
    }

    /**
     * Get order by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable String id) {
        Object obj = service.getById(id);
        if (obj == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(obj);
    }

    /**
     * Create generic order (legacy endpoint)
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
     * Update order by ID
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
     * Delete order by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        boolean ok = service.delete(id);
        if (!ok) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }

    /**
     * Search orders by criteria
     */
    @GetMapping("/search")
    public ResponseEntity<List<Object>> search(@RequestParam Map<String,String> params) {
        return ResponseEntity.ok(service.search(params));
    }

    /**
     * Bulk create orders
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
        health.put("service", "order-management");
        health.put("status", "UP");
        health.put("orders", service.count());
        health.put("featureFlagsEnabled", getEnabledFeatureFlags());
        return ResponseEntity.ok(health);
    }

    /**
     * Get information about enabled feature flags
     */
    @GetMapping("/features")
    public ResponseEntity<Map<String, Object>> getFeaturesStatus() {
        Map<String, Object> features = new HashMap<>();
        
        features.put("orderLifecycle", Map.of(
            "creationEnabled", FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_CREATION),
            "validationEnabled", FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_VALIDATION),
            "approvalEnabled", FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_APPROVAL_WORKFLOW),
            "statusTrackingEnabled", FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_STATUS_TRACKING),
            "cancellationEnabled", FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_CANCELLATION)
        ));
        
        features.put("fulfillment", Map.of(
            "fulfillmentEnabled", FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_FULFILLMENT),
            "inventoryAllocationEnabled", FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_INVENTORY_ALLOCATION),
            "deliveryTrackingEnabled", FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_DELIVERY_TRACKING),
            "backorderEnabled", FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_BACKORDER),
            "multiWarehouseEnabled", FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_MULTIWAREHOUSE_FULFILLMENT)
        ));
        
        features.put("provisioning", Map.of(
            "serviceProvisioningEnabled", FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_SERVICE_PROVISIONING),
            "simActivationEnabled", FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_SIM_ACTIVATION),
            "deviceProvisioningEnabled", FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_DEVICE_PROVISIONING),
            "networkConfigEnabled", FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_NETWORK_CONFIG)
        ));
        
        features.put("pricing", Map.of(
            "dynamicPricingEnabled", FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_DYNAMIC_PRICING),
            "promotionsEnabled", FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_PROMOTIONS),
            "taxCalculationEnabled", FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_TAX_CALCULATION),
            "paymentProcessingEnabled", FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_PAYMENT_PROCESSING),
            "billingAccountEnabled", FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_BILLING_ACCOUNT)
        ));
        
        return ResponseEntity.ok(features);
    }

    private Map<String, Boolean> getEnabledFeatureFlags() {
        Map<String, Boolean> flags = new HashMap<>();
        flags.put("orderCreation", FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_CREATION));
        flags.put("orderApproval", FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_APPROVAL_WORKFLOW));
        flags.put("fulfillment", FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_FULFILLMENT));
        flags.put("provisioning", FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_SERVICE_PROVISIONING));
        flags.put("billing", FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_BILLING_ACCOUNT));
        return flags;
    }
}
