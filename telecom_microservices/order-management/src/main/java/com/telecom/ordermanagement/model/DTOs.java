package com.telecom.ordermanagement.model;

import java.util.*;

/**
 * Domain Transfer Objects (DTOs) for Order Management
 * Comprehensive telecom-specific order models including customers, products, fulfillment, and provisioning
 */

// ==================== REQUEST/RESPONSE WRAPPERS ====================
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

// ==================== TELECOM ORDER DOMAIN MODELS ====================

/**
 * Core telecommunications order entity
 * Represents a complete order for telecom services and devices
 */
public class TelecomOrder {
    private String orderId;
    private String customerId;
    private String orderNumber;
    private OrderStatus status;
    private OrderType orderType;
    private List<OrderLineItem> lineItems;
    private OrderPricing pricing;
    private FulfillmentDetails fulfillment;
    private ServiceProvisioningDetails provisioning;
    private OrderTimeline timeline;
    private CustomerInfo customerInfo;
    private PaymentInfo paymentInfo;
    private List<OrderNote> notes;
    private long createdAt;
    private long updatedAt;

    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    
    public OrderType getOrderType() { return orderType; }
    public void setOrderType(OrderType orderType) { this.orderType = orderType; }
    
    public List<OrderLineItem> getLineItems() { return lineItems; }
    public void setLineItems(List<OrderLineItem> lineItems) { this.lineItems = lineItems; }
    
    public OrderPricing getPricing() { return pricing; }
    public void setPricing(OrderPricing pricing) { this.pricing = pricing; }
    
    public FulfillmentDetails getFulfillment() { return fulfillment; }
    public void setFulfillment(FulfillmentDetails fulfillment) { this.fulfillment = fulfillment; }
    
    public ServiceProvisioningDetails getProvisioning() { return provisioning; }
    public void setProvisioning(ServiceProvisioningDetails provisioning) { this.provisioning = provisioning; }
    
    public OrderTimeline getTimeline() { return timeline; }
    public void setTimeline(OrderTimeline timeline) { this.timeline = timeline; }
    
    public CustomerInfo getCustomerInfo() { return customerInfo; }
    public void setCustomerInfo(CustomerInfo customerInfo) { this.customerInfo = customerInfo; }
    
    public PaymentInfo getPaymentInfo() { return paymentInfo; }
    public void setPaymentInfo(PaymentInfo paymentInfo) { this.paymentInfo = paymentInfo; }
    
    public List<OrderNote> getNotes() { return notes; }
    public void setNotes(List<OrderNote> notes) { this.notes = notes; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}

/**
 * Order status enumeration
 */
public enum OrderStatus {
    PENDING, VALIDATED, APPROVED, IN_FULFILLMENT, FULFILLED, PROVISIONING, ACTIVE, 
    ON_HOLD, BACKORDERED, CANCELLED, FAILED, EXPIRED
}

/**
 * Order type enumeration - telecom specific
 */
public enum OrderType {
    NEW_SERVICE, UPGRADE, DOWNGRADE, ADDON, RENEWAL, DEVICE_ONLY, SERVICE_BUNDLE, MIGRATION, FAMILY_PLAN
}

/**
 * Individual line item in an order
 */
public class OrderLineItem {
    private String lineItemId;
    private String productId;
    private String productName;
    private ProductType productType;
    private int quantity;
    private double unitPrice;
    private double totalPrice;
    private String deviceImei;  // For device orders
    private String simCardNumber;  // For SIM orders
    private String serviceType;  // e.g., VOICE, DATA, SMS, VIDEO
    private ContractDetails contractDetails;
    private long createdAt;

    // Getters and Setters
    public String getLineItemId() { return lineItemId; }
    public void setLineItemId(String lineItemId) { this.lineItemId = lineItemId; }
    
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public ProductType getProductType() { return productType; }
    public void setProductType(ProductType productType) { this.productType = productType; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    
    public String getDeviceImei() { return deviceImei; }
    public void setDeviceImei(String deviceImei) { this.deviceImei = deviceImei; }
    
    public String getSimCardNumber() { return simCardNumber; }
    public void setSimCardNumber(String simCardNumber) { this.simCardNumber = simCardNumber; }
    
    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    
    public ContractDetails getContractDetails() { return contractDetails; }
    public void setContractDetails(ContractDetails contractDetails) { this.contractDetails = contractDetails; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}

/**
 * Product type enumeration
 */
public enum ProductType {
    DEVICE, SIM_CARD, SERVICE_PLAN, ADD_ON, EQUIPMENT, ACCESSORY
}

/**
 * Contract details for service subscriptions
 */
public class ContractDetails {
    private String contractId;
    private int durationMonths;
    private double monthlyCharge;
    private double setupFee;
    private double earlyTerminationFee;
    private boolean autoRenewal;
    private String renewalTermMonths;
    private long startDate;
    private long endDate;

    // Getters and Setters
    public String getContractId() { return contractId; }
    public void setContractId(String contractId) { this.contractId = contractId; }
    
    public int getDurationMonths() { return durationMonths; }
    public void setDurationMonths(int durationMonths) { this.durationMonths = durationMonths; }
    
    public double getMonthlyCharge() { return monthlyCharge; }
    public void setMonthlyCharge(double monthlyCharge) { this.monthlyCharge = monthlyCharge; }
    
    public double getSetupFee() { return setupFee; }
    public void setSetupFee(double setupFee) { this.setupFee = setupFee; }
    
    public double getEarlyTerminationFee() { return earlyTerminationFee; }
    public void setEarlyTerminationFee(double earlyTerminationFee) { this.earlyTerminationFee = earlyTerminationFee; }
    
    public boolean isAutoRenewal() { return autoRenewal; }
    public void setAutoRenewal(boolean autoRenewal) { this.autoRenewal = autoRenewal; }
    
    public String getRenewalTermMonths() { return renewalTermMonths; }
    public void setRenewalTermMonths(String renewalTermMonths) { this.renewalTermMonths = renewalTermMonths; }
    
    public long getStartDate() { return startDate; }
    public void setStartDate(long startDate) { this.startDate = startDate; }
    
    public long getEndDate() { return endDate; }
    public void setEndDate(long endDate) { this.endDate = endDate; }
}

/**
 * Order pricing information
 */
public class OrderPricing {
    private double subtotal;
    private double discount;
    private double discountPercentage;
    private String promotionCode;
    private double tax;
    private double taxRate;
    private double shippingCost;
    private double total;
    private String currency;
    private List<ChargeItem> charges;

    // Getters and Setters
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    
    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }
    
    public double getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(double discountPercentage) { this.discountPercentage = discountPercentage; }
    
    public String getPromotionCode() { return promotionCode; }
    public void setPromotionCode(String promotionCode) { this.promotionCode = promotionCode; }
    
    public double getTax() { return tax; }
    public void setTax(double tax) { this.tax = tax; }
    
    public double getTaxRate() { return taxRate; }
    public void setTaxRate(double taxRate) { this.taxRate = taxRate; }
    
    public double getShippingCost() { return shippingCost; }
    public void setShippingCost(double shippingCost) { this.shippingCost = shippingCost; }
    
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public List<ChargeItem> getCharges() { return charges; }
    public void setCharges(List<ChargeItem> charges) { this.charges = charges; }
}

/**
 * Individual charge item in pricing
 */
public class ChargeItem {
    private String chargeType;
    private double amount;
    private String description;

    public ChargeItem() {}
    public ChargeItem(String chargeType, double amount, String description) {
        this.chargeType = chargeType;
        this.amount = amount;
        this.description = description;
    }

    public String getChargeType() { return chargeType; }
    public void setChargeType(String chargeType) { this.chargeType = chargeType; }
    
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

/**
 * Fulfillment details for order delivery and logistics
 */
public class FulfillmentDetails {
    private String fulfillmentId;
    private String warehouseId;
    private String warehouseName;
    private FulfillmentStatus status;
    private ShippingAddress shippingAddress;
    private String trackingNumber;
    private String carrier;
    private long estimatedDeliveryDate;
    private long actualDeliveryDate;
    private List<FulfillmentEvent> events;

    // Getters and Setters
    public String getFulfillmentId() { return fulfillmentId; }
    public void setFulfillmentId(String fulfillmentId) { this.fulfillmentId = fulfillmentId; }
    
    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }
    
    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }
    
    public FulfillmentStatus getStatus() { return status; }
    public void setStatus(FulfillmentStatus status) { this.status = status; }
    
    public ShippingAddress getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(ShippingAddress shippingAddress) { this.shippingAddress = shippingAddress; }
    
    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    
    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }
    
    public long getEstimatedDeliveryDate() { return estimatedDeliveryDate; }
    public void setEstimatedDeliveryDate(long estimatedDeliveryDate) { this.estimatedDeliveryDate = estimatedDeliveryDate; }
    
    public long getActualDeliveryDate() { return actualDeliveryDate; }
    public void setActualDeliveryDate(long actualDeliveryDate) { this.actualDeliveryDate = actualDeliveryDate; }
    
    public List<FulfillmentEvent> getEvents() { return events; }
    public void setEvents(List<FulfillmentEvent> events) { this.events = events; }
}

/**
 * Fulfillment status enumeration
 */
public enum FulfillmentStatus {
    PENDING, PICKED, PACKED, SHIPPED, IN_TRANSIT, DELIVERED, FAILED, RETURNED
}

/**
 * Shipping address
 */
public class ShippingAddress {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String addressType;

    // Getters and Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public String getAddressType() { return addressType; }
    public void setAddressType(String addressType) { this.addressType = addressType; }
}

/**
 * Individual fulfillment event
 */
public class FulfillmentEvent {
    private String eventType;
    private long timestamp;
    private String location;
    private String description;

    public FulfillmentEvent() {}
    public FulfillmentEvent(String eventType, long timestamp, String location, String description) {
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.location = location;
        this.description = description;
    }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

/**
 * Service provisioning details for network and device activation
 */
public class ServiceProvisioningDetails {
    private String provisioningId;
    private ProvisioningStatus status;
    private String msisdn;  // Mobile Subscriber Integrated Services Digital Network (phone number)
    private String simIccid;  // SIM card ICCID
    private String deviceImei;
    private List<ActivationRecord> activationRecords;
    private NetworkConfiguration networkConfig;
    private long provisioningStartTime;
    private long provisioningEndTime;

    // Getters and Setters
    public String getProvisioningId() { return provisioningId; }
    public void setProvisioningId(String provisioningId) { this.provisioningId = provisioningId; }
    
    public ProvisioningStatus getStatus() { return status; }
    public void setStatus(ProvisioningStatus status) { this.status = status; }
    
    public String getMsisdn() { return msisdn; }
    public void setMsisdn(String msisdn) { this.msisdn = msisdn; }
    
    public String getSimIccid() { return simIccid; }
    public void setSimIccid(String simIccid) { this.simIccid = simIccid; }
    
    public String getDeviceImei() { return deviceImei; }
    public void setDeviceImei(String deviceImei) { this.deviceImei = deviceImei; }
    
    public List<ActivationRecord> getActivationRecords() { return activationRecords; }
    public void setActivationRecords(List<ActivationRecord> activationRecords) { this.activationRecords = activationRecords; }
    
    public NetworkConfiguration getNetworkConfig() { return networkConfig; }
    public void setNetworkConfig(NetworkConfiguration networkConfig) { this.networkConfig = networkConfig; }
    
    public long getProvisioningStartTime() { return provisioningStartTime; }
    public void setProvisioningStartTime(long provisioningStartTime) { this.provisioningStartTime = provisioningStartTime; }
    
    public long getProvisioningEndTime() { return provisioningEndTime; }
    public void setProvisioningEndTime(long provisioningEndTime) { this.provisioningEndTime = provisioningEndTime; }
}

/**
 * Provisioning status enumeration
 */
public enum ProvisioningStatus {
    PENDING, IN_PROGRESS, ACTIVATED, FAILED, SUSPENDED, DEACTIVATED
}

/**
 * Activation record for tracking provisioning events
 */
public class ActivationRecord {
    private String activationType;  // SIM_ACTIVATION, DEVICE_REGISTRATION, SERVICE_ACTIVATION
    private long timestamp;
    private String status;
    private String details;
    private String errorMessage;

    public ActivationRecord() {}
    public ActivationRecord(String activationType, long timestamp, String status, String details) {
        this.activationType = activationType;
        this.timestamp = timestamp;
        this.status = status;
        this.details = details;
    }

    public String getActivationType() { return activationType; }
    public void setActivationType(String activationType) { this.activationType = activationType; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}

/**
 * Network configuration for provisioning
 */
public class NetworkConfiguration {
    private String apn;  // Access Point Name
    private String dnsServer;
    private String gatewayIp;
    private String networkTechnology;  // 4G, 5G, LTE
    private Map<String, String> voiceSettings;
    private Map<String, String> dataSettings;

    public String getApn() { return apn; }
    public void setApn(String apn) { this.apn = apn; }
    
    public String getDnsServer() { return dnsServer; }
    public void setDnsServer(String dnsServer) { this.dnsServer = dnsServer; }
    
    public String getGatewayIp() { return gatewayIp; }
    public void setGatewayIp(String gatewayIp) { this.gatewayIp = gatewayIp; }
    
    public String getNetworkTechnology() { return networkTechnology; }
    public void setNetworkTechnology(String networkTechnology) { this.networkTechnology = networkTechnology; }
    
    public Map<String, String> getVoiceSettings() { return voiceSettings; }
    public void setVoiceSettings(Map<String, String> voiceSettings) { this.voiceSettings = voiceSettings; }
    
    public Map<String, String> getDataSettings() { return dataSettings; }
    public void setDataSettings(Map<String, String> dataSettings) { this.dataSettings = dataSettings; }
}

/**
 * Order timeline tracking
 */
public class OrderTimeline {
    private long orderedAt;
    private long validatedAt;
    private long approvedAt;
    private long fulfillmentStartedAt;
    private long fulfillmentCompletedAt;
    private long provisioningStartedAt;
    private long provisioningCompletedAt;
    private long activatedAt;
    private long cancelledAt;

    // Getters and Setters
    public long getOrderedAt() { return orderedAt; }
    public void setOrderedAt(long orderedAt) { this.orderedAt = orderedAt; }
    
    public long getValidatedAt() { return validatedAt; }
    public void setValidatedAt(long validatedAt) { this.validatedAt = validatedAt; }
    
    public long getApprovedAt() { return approvedAt; }
    public void setApprovedAt(long approvedAt) { this.approvedAt = approvedAt; }
    
    public long getFulfillmentStartedAt() { return fulfillmentStartedAt; }
    public void setFulfillmentStartedAt(long fulfillmentStartedAt) { this.fulfillmentStartedAt = fulfillmentStartedAt; }
    
    public long getFulfillmentCompletedAt() { return fulfillmentCompletedAt; }
    public void setFulfillmentCompletedAt(long fulfillmentCompletedAt) { this.fulfillmentCompletedAt = fulfillmentCompletedAt; }
    
    public long getProvisioningStartedAt() { return provisioningStartedAt; }
    public void setProvisioningStartedAt(long provisioningStartedAt) { this.provisioningStartedAt = provisioningStartedAt; }
    
    public long getProvisioningCompletedAt() { return provisioningCompletedAt; }
    public void setProvisioningCompletedAt(long provisioningCompletedAt) { this.provisioningCompletedAt = provisioningCompletedAt; }
    
    public long getActivatedAt() { return activatedAt; }
    public void setActivatedAt(long activatedAt) { this.activatedAt = activatedAt; }
    
    public long getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(long cancelledAt) { this.cancelledAt = cancelledAt; }
}

/**
 * Customer information on order
 */
public class CustomerInfo {
    private String customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String msisdn;
    private String customerSegment;  // INDIVIDUAL, SME, ENTERPRISE
    private String customerTier;  // BRONZE, SILVER, GOLD, PLATINUM
    private double creditLimit;
    private double accountBalance;

    // Getters and Setters
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getMsisdn() { return msisdn; }
    public void setMsisdn(String msisdn) { this.msisdn = msisdn; }
    
    public String getCustomerSegment() { return customerSegment; }
    public void setCustomerSegment(String customerSegment) { this.customerSegment = customerSegment; }
    
    public String getCustomerTier() { return customerTier; }
    public void setCustomerTier(String customerTier) { this.customerTier = customerTier; }
    
    public double getCreditLimit() { return creditLimit; }
    public void setCreditLimit(double creditLimit) { this.creditLimit = creditLimit; }
    
    public double getAccountBalance() { return accountBalance; }
    public void setAccountBalance(double accountBalance) { this.accountBalance = accountBalance; }
}

/**
 * Payment information
 */
public class PaymentInfo {
    private String paymentMethod;  // CREDIT_CARD, BANK_TRANSFER, WALLET, CASH
    private String paymentStatus;  // PENDING, PROCESSED, FAILED, REFUNDED
    private double amountPaid;
    private double amountDue;
    private long paymentDate;
    private String transactionId;
    private String receiptNumber;
    private String invoiceNumber;

    // Getters and Setters
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }
    
    public double getAmountDue() { return amountDue; }
    public void setAmountDue(double amountDue) { this.amountDue = amountDue; }
    
    public long getPaymentDate() { return paymentDate; }
    public void setPaymentDate(long paymentDate) { this.paymentDate = paymentDate; }
    
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    
    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }
    
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
}

/**
 * Order note/comment
 */
public class OrderNote {
    private String noteId;
    private String author;
    private String content;
    private long timestamp;
    private String noteType;  // INTERNAL, CUSTOMER_VISIBLE

    public OrderNote() {}
    public OrderNote(String author, String content, long timestamp, String noteType) {
        this.author = author;
        this.content = content;
        this.timestamp = timestamp;
        this.noteType = noteType;
    }

    public String getNoteId() { return noteId; }
    public void setNoteId(String noteId) { this.noteId = noteId; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    public String getNoteType() { return noteType; }
    public void setNoteType(String noteType) { this.noteType = noteType; }
}
