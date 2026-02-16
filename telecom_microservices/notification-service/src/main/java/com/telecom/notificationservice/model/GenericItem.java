package com.telecom.notificationservice.model;

import java.util.Map;
import java.util.HashMap;

/**
 * Flexible DTO used across controllers/services in NotificationService.
 * This lightweight DTO stores arbitrary attributes as a map.
 */
public class GenericItem {
    private String id;
    private String name;
    private Map<String,Object> attributes = new HashMap<>();

    public GenericItem() {}
    public GenericItem(String id, String name) {
        this.id = id; this.name = name;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Map<String,Object> getAttributes() { return attributes; }
    public void setAttributes(Map<String,Object> attributes) { this.attributes = attributes; }
}
