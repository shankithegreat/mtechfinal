package com.telecom.common;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FeatureFlagReader {

    private static final String FEATURE_FILE = "c:/Users/ivars/Downloads/telecom_microservices/featureflags.json";
    private static final Map<String, Boolean> flags = new HashMap<>();

    static {
        loadFlags();
    }

    private static void loadFlags() {
        try {
            // Read file as text and remove simple inline comment lines and code fences
            String raw = new String(Files.readAllBytes(Paths.get(FEATURE_FILE)));
            StringBuilder cleaned = new StringBuilder();
            String[] lines = raw.split("\\r?\\n");
            for (String line : lines) {
                String t = line.trim();
                if (t.startsWith("//")) continue; // skip comments
                if (t.startsWith("```")) continue; // skip fence
                cleaned.append(line).append(System.lineSeparator());
            }

            ObjectMapper mapper = new ObjectMapper(new JsonFactory());
            JsonNode root = mapper.readTree(cleaned.toString());
            if (root != null && root.isArray()) {
                Iterator<JsonNode> it = root.elements();
                while (it.hasNext()) {
                    JsonNode n = it.next();
                    if (n.has("featureFlagName") && n.has("featureFlagState")) {
                        String name = n.get("featureFlagName").asText();
                        String state = n.get("featureFlagState").asText();
                        flags.put(name, "enabled".equalsIgnoreCase(state));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load feature flags JSON file: " + FEATURE_FILE);
        }
    }

    public static boolean isFeatureEnabled(String key) {
        return flags.getOrDefault(key, false);
    }

    // For tests or runtime reloads
    public static void reload() {
        flags.clear();
        loadFlags();
    }
}