                        // Cross-service: If allocation rules are enabled in product-catalog, apply rules
                        if (FeatureFlagReader.isFeatureEnabled("catalog_enable_allocation_rules")) {
                            System.out.println("[ORDER] Allocation rules applied (catalog_enable_allocation_rules)");
                        }
                // Cross-service: If cross warehouse transfer is enabled in inventory, allow transfer
                if (!FeatureFlagReader.isFeatureEnabled("inventory_enable_cross_warehouse_transfer")) {
                    throw new IllegalStateException("Cross warehouse transfer is disabled (inventory_enable_cross_warehouse_transfer)");
                }
        // Cross-service: If audit trail is enabled in billing, log audit info
        if (FeatureFlagReader.isFeatureEnabled("billing_enable_audit_trail")) {
            System.out.println("[AUDIT] Order created: " + request.getOrderId() + " (billing_enable_audit_trail)");
        }
package com.telecom.ordermanagement.service;

import org.springframework.stereotype.Service;
import com.telecom.common.FeatureFlagReader;
import com.telecom.ordermanagement.model.*;
import com.telecom.ordermanagement.config.OrderManagementFeatureFlagConstants;
import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service layer for OrderManagement with comprehensive telecom-specific business logic.
 * Implements complex order processing with inventory management concepts, feature flags,
 * fulfillment, provisioning, and billing calculations.
 */
@Service
public class OrderManagementService {

    // In-memory stores for different entities
    private final Map<String, TelecomOrder> orderStore = new ConcurrentHashMap<>();
    private final Map<String, FulfillmentDetails> fulfillmentStore = new ConcurrentHashMap<>();
    private final Map<String, ServiceProvisioningDetails> provisioningStore = new ConcurrentHashMap<>();
    private final Map<String, Map<String,Object>> genericStore = new ConcurrentHashMap<>();
    private final Map<String, Integer> inventoryStock = new ConcurrentHashMap<>();  // Product ID -> Quantity
    private final Map<String, Double> productPrices = new ConcurrentHashMap<>();  // Product ID -> Price

    @PostConstruct
    public void init() {
        // Initialize sample inventory stock
        inventoryStock.put("DEVICE-IPHONE-15", 50);
        inventoryStock.put("DEVICE-SAMSUNG-S24", 40);
        inventoryStock.put("SIM-CARD-4G", 200);
        inventoryStock.put("SIM-CARD-5G", 150);
        inventoryStock.put("PLAN-VOICE-100", 999);
        inventoryStock.put("PLAN-DATA-20GB", 999);
        inventoryStock.put("ADDON-HOTSPOT", 999);

        // Initialize product prices
        productPrices.put("DEVICE-IPHONE-15", 999.99);
        productPrices.put("DEVICE-SAMSUNG-S24", 899.99);
        productPrices.put("SIM-CARD-4G", 5.99);
        productPrices.put("SIM-CARD-5G", 9.99);
        productPrices.put("PLAN-VOICE-100", 29.99);
        productPrices.put("PLAN-DATA-20GB", 49.99);
        productPrices.put("ADDON-HOTSPOT", 14.99);

        // Populate with sample orders
        for (int i = 1; i <= 5; i++) {
            createSampleOrder(i);
        }
    }

    private void createSampleOrder(int index) {
        TelecomOrder order = new TelecomOrder();
        order.setOrderId(UUID.randomUUID().toString());
        order.setOrderNumber("ORD-2024-" + String.format("%05d", index));
        order.setStatus(index % 2 == 0 ? OrderStatus.PENDING : OrderStatus.APPROVED);
        order.setOrderType(OrderType.NEW_SERVICE);
        order.setCustomerId("CUST-" + String.format("%05d", index));
        order.setCreatedAt(System.currentTimeMillis());
        order.setUpdatedAt(System.currentTimeMillis());

        // Initialize with sample line items
        List<OrderLineItem> lineItems = new ArrayList<>();
        OrderLineItem item = new OrderLineItem();
        item.setLineItemId(UUID.randomUUID().toString());
        item.setProductId("DEVICE-IPHONE-15");
        item.setProductName("iPhone 15");
        item.setProductType(ProductType.DEVICE);
        item.setQuantity(1);
        item.setUnitPrice(999.99);
        item.setTotalPrice(999.99);
        lineItems.add(item);
        order.setLineItems(lineItems);

        orderStore.put(order.getOrderId(), order);
        genericStore.put(order.getOrderId(), convertToMap(order));
    }

    // ==================== ORDER CREATION & VALIDATION ====================

    /**
     * Create a new telecom order with comprehensive validation and feature flag checks
     */
    public TelecomOrder createOrder(TelecomOrder orderRequest) {
        if (!FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_CREATION)) {
            throw new RuntimeException("Order creation feature is disabled");
        }

        // Validate order data
        if (FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_VALIDATION)) {
            validateOrderData(orderRequest);
        }

        // Check inventory availability
        if (FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_INVENTORY_ALLOCATION)) {
            checkAndReserveInventory(orderRequest);
        }

        TelecomOrder order = new TelecomOrder();
        order.setOrderId(UUID.randomUUID().toString());
        order.setOrderNumber(generateOrderNumber());
        order.setStatus(OrderStatus.PENDING);
        order.setCustomerId(orderRequest.getCustomerId());
        order.setCustomerInfo(orderRequest.getCustomerInfo());
        order.setLineItems(orderRequest.getLineItems());
        order.setOrderType(orderRequest.getOrderType());
        order.setCreatedAt(System.currentTimeMillis());
        order.setUpdatedAt(System.currentTimeMillis());

        // Initialize timeline
        OrderTimeline timeline = new OrderTimeline();
        timeline.setOrderedAt(System.currentTimeMillis());
        order.setTimeline(timeline);

        orderStore.put(order.getOrderId(), order);
        genericStore.put(order.getOrderId(), convertToMap(order));

        return order;
    }

    /**
     * Validate order data with complex business rules
     */
    private void validateOrderData(TelecomOrder order) {
        if (order.getLineItems() == null || order.getLineItems().isEmpty()) {
            throw new RuntimeException("Order must contain at least one line item");
        }

        for (OrderLineItem item : order.getLineItems()) {
            if (item.getQuantity() <= 0) {
                throw new RuntimeException("Item quantity must be positive");
            }
            if (item.getProductId() == null || item.getProductId().isEmpty()) {
                throw new RuntimeException("Product ID is required");
            }
        }

        // Validate customer information
        if (order.getCustomerInfo() == null || order.getCustomerInfo().getCustomerId() == null) {
            throw new RuntimeException("Valid customer information is required");
        }
    }

    /**
     * Check and reserve inventory for order items
     */
    private void checkAndReserveInventory(TelecomOrder order) {
        for (OrderLineItem item : order.getLineItems()) {
            String productId = item.getProductId();
            int requiredQuantity = item.getQuantity();
            Integer availableStock = inventoryStock.getOrDefault(productId, 0);

            if (availableStock < requiredQuantity) {
                if (FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_BACKORDER)) {
                    // Allow backorder
                    item.setQuantity(requiredQuantity);  // Mark for backorder processing
                } else {
                    throw new RuntimeException("Insufficient inventory for product: " + productId);
                }
            } else {
                // Reserve the inventory
                inventoryStock.put(productId, availableStock - requiredQuantity);
            }
        }
    }

    // ==================== ORDER APPROVAL & PRICING ====================

    /**
     * Approve order and calculate pricing with promotions and tax
     */
    public TelecomOrder approveOrder(String orderId) {
        TelecomOrder order = orderStore.get(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found: " + orderId);
        }

        if (!FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_APPROVAL_WORKFLOW)) {
            throw new RuntimeException("Order approval workflow is disabled");
        }

        // Calculate pricing
        if (FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_DYNAMIC_PRICING)) {
            calculateOrderPricing(order);
        }

        order.setStatus(OrderStatus.APPROVED);
        order.getTimeline().setApprovedAt(System.currentTimeMillis());
        orderStore.put(orderId, order);

        return order;
    }

    /**
     * Calculate order pricing with dynamic pricing, promotions, and tax
     */
    private void calculateOrderPricing(TelecomOrder order) {
        OrderPricing pricing = new OrderPricing();
        double subtotal = 0;
        List<ChargeItem> charges = new ArrayList<>();

        // Calculate line item totals
        for (OrderLineItem item : order.getLineItems()) {
            double itemPrice = productPrices.getOrDefault(item.getProductId(), 0.0);
            double itemTotal = itemPrice * item.getQuantity();
            subtotal += itemTotal;
            item.setUnitPrice(itemPrice);
            item.setTotalPrice(itemTotal);

            // Add contract charges if applicable
            if (item.getContractDetails() != null) {
                ContractDetails contract = item.getContractDetails();
                subtotal += contract.getSetupFee();
                charges.add(new ChargeItem("SETUP_FEE", contract.getSetupFee(), "Contract setup fee"));
            }
        }

        pricing.setSubtotal(subtotal);

        // Apply promotions if enabled
        if (FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_PROMOTIONS)) {
            applyPromotions(pricing, charges);
        }

        // Calculate tax if enabled
        if (FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_TAX_CALCULATION)) {
            calculateTax(pricing, order.getCustomerInfo());
        }

        // Add shipping costs
        pricing.setShippingCost(5.0);  // Standard shipping
        charges.add(new ChargeItem("SHIPPING", 5.0, "Standard shipping charge"));

        // Calculate total
        double total = pricing.getSubtotal() - pricing.getDiscount() + pricing.getTax() + pricing.getShippingCost();
        pricing.setTotal(total);
        pricing.setCharges(charges);
        pricing.setCurrency("USD");

        order.setPricing(pricing);
    }

    /**
     * Apply promotional discounts and codes
     */
    private void applyPromotions(OrderPricing pricing, List<ChargeItem> charges) {
        // Example promotion logic: Apply 10% discount for new customers
        double discountPercentage = 0;
        String promoCode = pricing.getPromotionCode();

        if (promoCode != null) {
            if (promoCode.equals("NEWCUSTOMER")) {
                discountPercentage = 0.10;  // 10% discount
            } else if (promoCode.equals("SEASONAL")) {
                discountPercentage = 0.15;  // 15% discount
            } else if (promoCode.equals("BULK")) {
                discountPercentage = 0.20;  // 20% discount
            }
        }

        if (discountPercentage > 0) {
            double discountAmount = pricing.getSubtotal() * discountPercentage;
            pricing.setDiscount(discountAmount);
            pricing.setDiscountPercentage(discountPercentage);
            charges.add(new ChargeItem("DISCOUNT", -discountAmount, "Promotional discount - " + promoCode));
        }
    }

    /**
     * Calculate tax based on jurisdiction and customer type
     */
    private void calculateTax(OrderPricing pricing, CustomerInfo customerInfo) {
        double taxRate = 0.08;  // Default 8% sales tax

        // Adjust tax rate based on customer segment and location
        if (customerInfo != null && "ENTERPRISE".equalsIgnoreCase(customerInfo.getCustomerSegment())) {
            taxRate = 0.0;  // Enterprise customers exempt from sales tax
        }

        double taxAmount = (pricing.getSubtotal() - pricing.getDiscount()) * taxRate;
        pricing.setTax(taxAmount);
        pricing.setTaxRate(taxRate);
    }

    // ==================== FULFILLMENT PROCESSING ====================

    /**
     * Process fulfillment for an approved order
     */
    public FulfillmentDetails processFulfillment(String orderId) {
        TelecomOrder order = orderStore.get(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found: " + orderId);
        }

        if (!FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_FULFILLMENT)) {
            throw new RuntimeException("Fulfillment processing is disabled");
        }

        // Select warehouse for fulfillment
        String warehouseId = selectOptimalWarehouse(order);

        FulfillmentDetails fulfillment = new FulfillmentDetails();
        fulfillment.setFulfillmentId(UUID.randomUUID().toString());
        fulfillment.setWarehouseId(warehouseId);
        fulfillment.setWarehouseName("Warehouse-" + warehouseId);
        fulfillment.setStatus(FulfillmentStatus.PENDING);
        fulfillment.setShippingAddress(createShippingAddress(order.getCustomerInfo()));

        // Generate tracking number
        fulfillment.setTrackingNumber("TRK-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
        fulfillment.setCarrier("StandardCarrier");

        // Calculate estimated delivery date (3-5 business days)
        long estimatedDelivery = System.currentTimeMillis() + (4 * 24 * 60 * 60 * 1000);
        fulfillment.setEstimatedDeliveryDate(estimatedDelivery);

        // Initialize fulfillment events
        List<FulfillmentEvent> events = new ArrayList<>();
        events.add(new FulfillmentEvent("PENDING", System.currentTimeMillis(), warehouseId, "Order received in warehouse"));
        fulfillment.setEvents(events);

        fulfillmentStore.put(fulfillment.getFulfillmentId(), fulfillment);
        order.setFulfillment(fulfillment);
        order.setStatus(OrderStatus.IN_FULFILLMENT);
        order.getTimeline().setFulfillmentStartedAt(System.currentTimeMillis());

        orderStore.put(orderId, order);
        return fulfillment;
    }

    /**
     * Select optimal warehouse based on inventory and location
     */
    private String selectOptimalWarehouse(TelecomOrder order) {
        if (FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_MULTIWAREHOUSE_FULFILLMENT)) {
            // Multi-warehouse optimization logic
            String[] warehouses = {"WH-EAST", "WH-CENTRAL", "WH-WEST"};
            // Simple round-robin for demo
            return warehouses[(int)(System.currentTimeMillis() % warehouses.length)];
        }
        return "WH-CENTRAL";  // Default warehouse
    }

    /**
     * Create shipping address from customer info
     */
    private ShippingAddress createShippingAddress(CustomerInfo customerInfo) {
        ShippingAddress address = new ShippingAddress();
        if (customerInfo != null) {
            address.setFirstName(customerInfo.getFirstName());
            address.setLastName(customerInfo.getLastName());
            address.setEmail(customerInfo.getEmail());
            address.setPhoneNumber(customerInfo.getPhoneNumber());
        }
        address.setCity("New York");
        address.setState("NY");
        address.setPostalCode("10001");
        address.setCountry("USA");
        address.setAddressType("RESIDENTIAL");
        return address;
    }

    /**
     * Update fulfillment status with event tracking
     */
    public FulfillmentDetails updateFulfillmentStatus(String fulfillmentId, String newStatus) {
        FulfillmentDetails fulfillment = fulfillmentStore.get(fulfillmentId);
        if (fulfillment == null) {
            throw new RuntimeException("Fulfillment not found: " + fulfillmentId);
        }

        fulfillment.setStatus(FulfillmentStatus.valueOf(newStatus));
        List<FulfillmentEvent> events = fulfillment.getEvents();
        events.add(new FulfillmentEvent(newStatus, System.currentTimeMillis(), fulfillment.getWarehouseId(), 
                  "Status updated to: " + newStatus));

        if (FulfillmentStatus.DELIVERED.toString().equals(newStatus)) {
            fulfillment.setActualDeliveryDate(System.currentTimeMillis());
        }

        fulfillmentStore.put(fulfillmentId, fulfillment);
        return fulfillment;
    }

    // ==================== SERVICE PROVISIONING ====================

    /**
     * Provision services on order completion with device and SIM activation
     */
    public ServiceProvisioningDetails provisionServices(String orderId) {
        TelecomOrder order = orderStore.get(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found: " + orderId);
        }

        if (!FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_SERVICE_PROVISIONING)) {
            throw new RuntimeException("Service provisioning is disabled");
        }

        ServiceProvisioningDetails provisioning = new ServiceProvisioningDetails();
        provisioning.setProvisioningId(UUID.randomUUID().toString());
        provisioning.setStatus(ProvisioningStatus.PENDING);
        provisioning.setProvisioningStartTime(System.currentTimeMillis());

        List<ActivationRecord> activationRecords = new ArrayList<>();

        // Process SIM card activation if enabled
        for (OrderLineItem item : order.getLineItems()) {
            if (ProductType.SIM_CARD.equals(item.getProductType())) {
                if (FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_SIM_ACTIVATION)) {
                    activateSIMCard(provisioning, item, activationRecords);
                }
            }

            // Process device provisioning if enabled
            if (ProductType.DEVICE.equals(item.getProductType())) {
                if (FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_DEVICE_PROVISIONING)) {
                    provisionDevice(provisioning, item, activationRecords);
                }
            }
        }

        // Configure network settings if enabled
        if (FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_NETWORK_CONFIG)) {
            configureNetworkSettings(provisioning);
        }

        provisioning.setActivationRecords(activationRecords);
        provisioning.setStatus(ProvisioningStatus.ACTIVATED);
        provisioning.setProvisioningEndTime(System.currentTimeMillis());

        provisioningStore.put(provisioning.getProvisioningId(), provisioning);
        order.setProvisioning(provisioning);
        order.setStatus(OrderStatus.ACTIVE);
        order.getTimeline().setProvisioningCompletedAt(System.currentTimeMillis());
        order.getTimeline().setActivatedAt(System.currentTimeMillis());

        orderStore.put(orderId, order);
        return provisioning;
    }

    /**
     * Activate SIM card and assign MSISDN (phone number)
     */
    private void activateSIMCard(ServiceProvisioningDetails provisioning, OrderLineItem item, 
                                   List<ActivationRecord> records) {
        String simIccid = "8944" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
        String msisdn = "1" + (1000000000 + (int)(Math.random() * 9000000000L));

        provisioning.setSimIccid(simIccid);
        provisioning.setMsisdn(msisdn);

        if (item.getSimCardNumber() == null) {
            item.setSimCardNumber(simIccid);
        }

        records.add(new ActivationRecord("SIM_ACTIVATION", System.currentTimeMillis(), "SUCCESS",
                "SIM card activated with ICCID: " + simIccid + ", MSISDN: " + msisdn));
    }

    /**
     * Provision device and register IMEI
     */
    private void provisionDevice(ServiceProvisioningDetails provisioning, OrderLineItem item, 
                                  List<ActivationRecord> records) {
        String deviceImei = "35" + (100000000000000L + (long)(Math.random() * 900000000000000L));
        provisioning.setDeviceImei(String.valueOf(deviceImei));

        if (item.getDeviceImei() == null) {
            item.setDeviceImei(String.valueOf(deviceImei));
        }

        records.add(new ActivationRecord("DEVICE_REGISTRATION", System.currentTimeMillis(), "SUCCESS",
                "Device provisioned with IMEI: " + deviceImei));
    }

    /**
     * Configure network settings for provisioned services
     */
    private void configureNetworkSettings(ServiceProvisioningDetails provisioning) {
        NetworkConfiguration netConfig = new NetworkConfiguration();
        netConfig.setApn("telecom.apn.main");
        netConfig.setDnsServer("8.8.8.8");
        netConfig.setGatewayIp("10.0.0.1");
        netConfig.setNetworkTechnology("5G");

        Map<String, String> voiceSettings = new HashMap<>();
        voiceSettings.put("codec", "AMR-WB");
        voiceSettings.put("qos_priority", "high");
        netConfig.setVoiceSettings(voiceSettings);

        Map<String, String> dataSettings = new HashMap<>();
        dataSettings.put("max_bandwidth", "100Mbps");
        dataSettings.put("quality_of_service", "priority");
        netConfig.setDataSettings(dataSettings);

        provisioning.setNetworkConfig(netConfig);
    }

    // ==================== BILLING ACCOUNT CREATION ====================

    /**
     * Create billing account for order with subscription management
     */
    public Map<String, Object> createBillingAccount(String orderId) {
        TelecomOrder order = orderStore.get(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found: " + orderId);
        }

        if (!FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_BILLING_ACCOUNT)) {
            throw new RuntimeException("Billing account creation is disabled");
        }

        Map<String, Object> billingAccount = new HashMap<>();
        billingAccount.put("billingAccountId", "BA-" + UUID.randomUUID().toString());
        billingAccount.put("customerId", order.getCustomerId());
        billingAccount.put("orderNumber", order.getOrderNumber());
        billingAccount.put("createdAt", System.currentTimeMillis());
        billingAccount.put("status", "ACTIVE");

        if (order.getPricing() != null) {
            billingAccount.put("monthlyCharges", calculateMonthlyCharges(order));
            billingAccount.put("billingCycle", "MONTHLY");
            billingAccount.put("currency", order.getPricing().getCurrency());
        }

        return billingAccount;
    }

    /**
     * Calculate monthly recurring charges from order
     */
    private double calculateMonthlyCharges(TelecomOrder order) {
        double monthlyCharges = 0;
        for (OrderLineItem item : order.getLineItems()) {
            if (item.getContractDetails() != null) {
                monthlyCharges += item.getContractDetails().getMonthlyCharge();
            }
        }
        return monthlyCharges;
    }

    // ==================== ORDER TRACKING & STATUS ====================

    /**
     * Get order status with complete timeline and tracking information
     */
    public Map<String, Object> getOrderStatus(String orderId) {
        TelecomOrder order = orderStore.get(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found: " + orderId);
        }

        if (!FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_STATUS_TRACKING)) {
            throw new RuntimeException("Order status tracking is disabled");
        }

        Map<String, Object> statusMap = new HashMap<>();
        statusMap.put("orderId", order.getOrderId());
        statusMap.put("orderNumber", order.getOrderNumber());
        statusMap.put("status", order.getStatus());
        statusMap.put("currentStage", getCurrentStage(order));
        statusMap.put("timeline", convertTimeline(order.getTimeline()));

        if (order.getFulfillment() != null) {
            statusMap.put("fulfillment", convertFulfillment(order.getFulfillment()));
        }

        if (order.getProvisioning() != null) {
            statusMap.put("provisioning", convertProvisioning(order.getProvisioning()));
        }

        return statusMap;
    }

    private String getCurrentStage(TelecomOrder order) {
        switch(order.getStatus()) {
            case PENDING: return "PENDING_APPROVAL";
            case VALIDATED: return "VALIDATED";
            case APPROVED: return "READY_FOR_FULFILLMENT";
            case IN_FULFILLMENT: return "BEING_SHIPPED";
            case FULFILLED: return "DELIVERED_AWAITING_ACTIVATION";
            case PROVISIONING: return "ACTIVATING_SERVICES";
            case ACTIVE: return "ACTIVE_AND_RUNNING";
            default: return order.getStatus().toString();
        }
    }

    private Map<String, Object> convertTimeline(OrderTimeline timeline) {
        Map<String, Object> timelineMap = new HashMap<>();
        timelineMap.put("orderedAt", timeline.getOrderedAt());
        timelineMap.put("validatedAt", timeline.getValidatedAt());
        timelineMap.put("approvedAt", timeline.getApprovedAt());
        timelineMap.put("fulfillmentStartedAt", timeline.getFulfillmentStartedAt());
        timelineMap.put("fulfillmentCompletedAt", timeline.getFulfillmentCompletedAt());
        timelineMap.put("provisioningStartedAt", timeline.getProvisioningStartedAt());
        timelineMap.put("provisioningCompletedAt", timeline.getProvisioningCompletedAt());
        timelineMap.put("activatedAt", timeline.getActivatedAt());
        return timelineMap;
    }

    private Map<String, Object> convertFulfillment(FulfillmentDetails fulfillment) {
        Map<String, Object> fulfillmentMap = new HashMap<>();
        fulfillmentMap.put("fulfillmentId", fulfillment.getFulfillmentId());
        fulfillmentMap.put("status", fulfillment.getStatus());
        fulfillmentMap.put("trackingNumber", fulfillment.getTrackingNumber());
        fulfillmentMap.put("carrier", fulfillment.getCarrier());
        fulfillmentMap.put("estimatedDeliveryDate", fulfillment.getEstimatedDeliveryDate());
        fulfillmentMap.put("actualDeliveryDate", fulfillment.getActualDeliveryDate());
        return fulfillmentMap;
    }

    private Map<String, Object> convertProvisioning(ServiceProvisioningDetails provisioning) {
        Map<String, Object> provisioningMap = new HashMap<>();
        provisioningMap.put("provisioningId", provisioning.getProvisioningId());
        provisioningMap.put("status", provisioning.getStatus());
        provisioningMap.put("msisdn", provisioning.getMsisdn());
        provisioningMap.put("simIccid", provisioning.getSimIccid());
        provisioningMap.put("deviceImei", provisioning.getDeviceImei());
        return provisioningMap;
    }

    // ==================== ORDER CANCELLATION ====================

    /**
     * Cancel order with fee calculations
     */
    public Map<String, Object> cancelOrder(String orderId) {
        TelecomOrder order = orderStore.get(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found: " + orderId);
        }

        if (!FeatureFlagReader.isFeatureEnabled(OrderManagementFeatureFlagConstants.ORDER_ENABLE_CANCELLATION)) {
            throw new RuntimeException("Order cancellation is disabled");
        }

        Map<String, Object> cancellationResult = new HashMap<>();
        double cancellationFee = 0;

        // Calculate early termination fee based on order status and contracts
        if (order.getStatus().equals(OrderStatus.ACTIVE)) {
            for (OrderLineItem item : order.getLineItems()) {
                if (item.getContractDetails() != null) {
                    cancellationFee += item.getContractDetails().getEarlyTerminationFee();
                }
            }
        }

        cancellationResult.put("orderId", order.getOrderId());
        cancellationResult.put("cancellationTime", System.currentTimeMillis());
        cancellationResult.put("cancellationFee", cancellationFee);
        cancellationResult.put("refundAmount", order.getPricing() != null ? 
                             order.getPricing().getTotal() - cancellationFee : 0);

        order.setStatus(OrderStatus.CANCELLED);
        order.getTimeline().setCancelledAt(System.currentTimeMillis());
        orderStore.put(orderId, order);

        return cancellationResult;
    }

    // ==================== LEGACY GENERIC METHODS ====================

    public List<Object> listAll() {
        return new ArrayList<>(genericStore.values());
    }

    public Object getById(String id) {
        return genericStore.get(id);
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
        return orderStore.size();
    }

    // ==================== UTILITY METHODS ====================

    private String generateOrderNumber() {
        String timestamp = String.format("%010d", System.currentTimeMillis() % 10000000000L);
        String random = String.format("%05d", (int)(Math.random() * 100000));
        return "ORD-" + timestamp + "-" + random;
    }

    private Map<String, Object> convertToMap(TelecomOrder order) {
        Map<String, Object> map = new HashMap<>();
        map.put("orderId", order.getOrderId());
        map.put("orderNumber", order.getOrderNumber());
        map.put("status", order.getStatus());
        map.put("customerId", order.getCustomerId());
        map.put("orderType", order.getOrderType());
        map.put("createdAt", order.getCreatedAt());
        return map;
    }

    public int computeChecksum(String s) {
        int c = 0;
        for (char ch : s.toCharArray()) c = (c * 31) + ch;
        return Math.abs(c);
    }

    public String humanReadableId(String id) {
        return id.substring(0, Math.min(8, id.length())).toUpperCase();
    }
}
