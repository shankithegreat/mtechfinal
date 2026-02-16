package com.telecom.authservice.controller;

import com.telecom.authservice.model.*;
import com.telecom.authservice.service.*;
import com.telecom.authservice.util.AuthServiceFeatureFlagConstants;
import com.telecom.common.FeatureFlagReader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.*;
/**
 * REST controllers for AuthService endpoints.
 * Contains multiple endpoints to manage the core domain for this microservice.
 */
@RestController
@RequestMapping("/api/auth-service")
public class AuthServiceController {

    @Autowired
    private AuthServiceService service;

    // 1. List (paged simple)
    @GetMapping
    public ResponseEntity<List<Object>> listAll() {
        return ResponseEntity.ok(service.listAll());
    }

    // 2. Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable String id) {
        Object obj = service.getById(id);
        if (obj == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(obj);
    }

    // 3. Create
    @PostMapping
    public ResponseEntity<Object> create(@RequestBody Map<String, Object> payload) {
        // Block registration if disabled globally
        if (!FeatureFlagReader.isFeatureEnabled(AuthServiceFeatureFlagConstants.AUTH_ENABLE_REGISTRATION)) {
            return ResponseEntity.status(403).body("Registrations are disabled by feature flag");
        }
        Object created = service.create(payload);
        return ResponseEntity.ok(created);
    }

    // 4. Update
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        // If profile edits are disabled, return 403
        if (!FeatureFlagReader.isFeatureEnabled(AuthServiceFeatureFlagConstants.AUTH_ENABLE_USER_PROFILE_EDIT)) {
            return ResponseEntity.status(403).body("Profile editing is disabled");
        }
        Object updated = service.update(id, payload);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    // 5. Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        boolean ok = service.delete(id);
        if (!ok) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }

    // 6. Search / filter
    @GetMapping("/search")
    public ResponseEntity<List<Object>> search(@RequestParam Map<String,String> params) {
        return ResponseEntity.ok(service.search(params));
    }

    // 7. Bulk create
    @PostMapping("/bulk")
    public ResponseEntity<List<Object>> bulkCreate(@RequestBody List<Map<String,Object>> payloads) {
        return ResponseEntity.ok(service.bulkCreate(payloads));
    }

    // 8. Health / diagnostics for this service
    @GetMapping("/health")
    public ResponseEntity<Map<String,Object>> health() {
        Map<String,Object> m = new HashMap<>();
        m.put("service", "auth-service");
        m.put("status", "UP");
        m.put("items", service.count());
        return ResponseEntity.ok(m);
    }

    // Feature flag example for 2FA
    private void checkTwoFactorAuthentication() {
        if (FeatureFlagReader.isFeatureEnabled("auth_enable_2fa")) {
            // Logic for 2FA
        }
    }
}
