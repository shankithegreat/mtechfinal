package com.telecom.inventorymanagement.repo;

import org.springframework.stereotype.Repository;
import java.util.*;
/**
 * Very small in-memory repository used for demonstration and local testing.
 * Replace with JPA, JDBC, or any real datastore for production.
 */
@Repository
public class InMemoryRepo {

    private final Map<String, Map<String,Object>> db = new HashMap<>();

    public void save(String id, Map<String,Object> item) {
        db.put(id, item);
    }

    public Map<String,Object> find(String id) {
        return db.get(id);
    }

    public List<Map<String,Object>> findAll() {
        return new ArrayList<>(db.values());
    }

    public void delete(String id) {
        db.remove(id);
    }

    public void clear() { db.clear(); }

    public int size() { return db.size(); }
}
