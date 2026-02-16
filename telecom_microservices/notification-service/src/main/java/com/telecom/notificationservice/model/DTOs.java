package com.telecom.notificationservice.model;

import java.util.*;

/**
 * Extra DTOs and wrapper types for NotificationService
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
