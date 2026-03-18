package com.automation.framework.config;

import com.automation.framework.core.enums.ExecutionPlatform;
import com.automation.framework.core.enums.PlatformType;
import com.automation.framework.core.exceptions.ConfigurationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manages loading and processing of capabilities from JSON files.
 * Single Responsibility Principle (SRP): Only handles capabilities management.
 */
@Slf4j
public class CapabilitiesManager {

    private static final String CAPABILITIES_PATH = "capabilities/";
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^}]+)}");
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final ConfigurationManager configManager;

    public CapabilitiesManager() {
        this.configManager = ConfigurationManager.getInstance();
    }

    /**
     * Gets capabilities for the specified platform and execution environment.
     *
     * @param platformType The platform type (android, ios, web)
     * @param executionPlatform The execution platform (local, browserstack, etc.)
     * @return The desired capabilities
     */
    public DesiredCapabilities getCapabilities(PlatformType platformType, ExecutionPlatform executionPlatform) {
        String fileName = buildCapabilitiesFileName(platformType, executionPlatform);
        return loadCapabilitiesFromFile(fileName);
    }

    /**
     * Gets capabilities for mobile platforms.
     *
     * @param platformType The mobile platform type
     * @return The desired capabilities
     */
    public DesiredCapabilities getMobileCapabilities(PlatformType platformType) {
        ExecutionPlatform executionPlatform = ExecutionPlatform.fromString(
                configManager.getProperty("execution.platform", "local"));
        return getCapabilities(platformType, executionPlatform);
    }

    /**
     * Gets capabilities for web platforms.
     *
     * @return The desired capabilities
     */
    public DesiredCapabilities getWebCapabilities() {
        ExecutionPlatform executionPlatform = ExecutionPlatform.fromString(
                configManager.getProperty("execution.platform", "local"));
        return getCapabilities(PlatformType.WEB, executionPlatform);
    }

    private String buildCapabilitiesFileName(PlatformType platformType, ExecutionPlatform executionPlatform) {
        return platformType.getPlatformName() + "-" + executionPlatform.getPlatformName() + ".json";
    }

    private DesiredCapabilities loadCapabilitiesFromFile(String fileName) {
        String filePath = CAPABILITIES_PATH + fileName;
        log.debug("Loading capabilities from: {}", filePath);

        try (InputStream input = getClass().getClassLoader().getResourceAsStream(filePath)) {
            if (input == null) {
                log.warn("Capabilities file not found: {}. Using empty capabilities.", filePath);
                return new DesiredCapabilities();
            }

            JsonNode rootNode = objectMapper.readTree(input);
            Map<String, Object> capsMap = jsonNodeToMap(rootNode);
            resolvePlaceholders(capsMap);

            DesiredCapabilities capabilities = new DesiredCapabilities();
            capsMap.forEach(capabilities::setCapability);

            log.info("Loaded capabilities from: {}", fileName);
            return capabilities;

        } catch (IOException e) {
            throw new ConfigurationException("Failed to load capabilities file: " + fileName, e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> jsonNodeToMap(JsonNode node) {
        Map<String, Object> result = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String key = entry.getKey();
            JsonNode value = entry.getValue();

            if (value.isObject()) {
                result.put(key, jsonNodeToMap(value));
            } else if (value.isArray()) {
                result.put(key, objectMapper.convertValue(value, Object[].class));
            } else if (value.isBoolean()) {
                result.put(key, value.booleanValue());
            } else if (value.isInt()) {
                result.put(key, value.intValue());
            } else if (value.isLong()) {
                result.put(key, value.longValue());
            } else if (value.isDouble()) {
                result.put(key, value.doubleValue());
            } else {
                result.put(key, value.asText());
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private void resolvePlaceholders(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();

            if (value instanceof String) {
                entry.setValue(resolvePlaceholder((String) value));
            } else if (value instanceof Map) {
                resolvePlaceholders((Map<String, Object>) value);
            }
        }
    }

    private String resolvePlaceholder(String value) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(value);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String placeholder = matcher.group(1);
            String resolved = configManager.getProperty(placeholder);

            if (resolved == null) {
                resolved = System.getenv(placeholder);
            }

            if (resolved == null) {
                resolved = matcher.group(0); // Keep original if not resolved
            }

            matcher.appendReplacement(result, Matcher.quoteReplacement(resolved));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    /**
     * Merges additional capabilities with base capabilities.
     *
     * @param base The base capabilities
     * @param additional The additional capabilities to merge
     * @return The merged capabilities
     */
    public MutableCapabilities mergeCapabilities(MutableCapabilities base, Map<String, Object> additional) {
        additional.forEach(base::setCapability);
        return base;
    }
}
