package com.automation.framework.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Utility class for JSON operations.
 */
@Slf4j
public final class JsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private JsonUtils() {
        // Private constructor
    }

    /**
     * Converts an object to JSON string.
     *
     * @param object The object to convert
     * @return The JSON string
     */
    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert object to JSON", e);
            return null;
        }
    }

    /**
     * Converts an object to pretty-printed JSON string.
     *
     * @param object The object to convert
     * @return The pretty-printed JSON string
     */
    public static String toPrettyJson(Object object) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert object to pretty JSON", e);
            return null;
        }
    }

    /**
     * Parses JSON string to object.
     *
     * @param json The JSON string
     * @param clazz The target class
     * @param <T> The type
     * @return The parsed object
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse JSON to {}", clazz.getSimpleName(), e);
            return null;
        }
    }

    /**
     * Parses JSON string to typed object.
     *
     * @param json The JSON string
     * @param typeReference The type reference
     * @param <T> The type
     * @return The parsed object
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse JSON", e);
            return null;
        }
    }

    /**
     * Reads JSON from file.
     *
     * @param filePath The file path
     * @param clazz The target class
     * @param <T> The type
     * @return The parsed object
     */
    public static <T> T readFromFile(String filePath, Class<T> clazz) {
        try {
            return objectMapper.readValue(new File(filePath), clazz);
        } catch (IOException e) {
            log.error("Failed to read JSON from file: {}", filePath, e);
            return null;
        }
    }

    /**
     * Reads JSON from resource file.
     *
     * @param resourcePath The resource path
     * @param clazz The target class
     * @param <T> The type
     * @return The parsed object
     */
    public static <T> T readFromResource(String resourcePath, Class<T> clazz) {
        try (InputStream is = JsonUtils.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                log.error("Resource not found: {}", resourcePath);
                return null;
            }
            return objectMapper.readValue(is, clazz);
        } catch (IOException e) {
            log.error("Failed to read JSON from resource: {}", resourcePath, e);
            return null;
        }
    }

    /**
     * Writes object to JSON file.
     *
     * @param object The object to write
     * @param filePath The file path
     * @return true if successful, false otherwise
     */
    public static boolean writeToFile(Object object, String filePath) {
        try {
            objectMapper.writeValue(new File(filePath), object);
            return true;
        } catch (IOException e) {
            log.error("Failed to write JSON to file: {}", filePath, e);
            return false;
        }
    }

    /**
     * Parses JSON string to JsonNode.
     *
     * @param json The JSON string
     * @return The JsonNode
     */
    public static JsonNode parseToNode(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse JSON to node", e);
            return null;
        }
    }

    /**
     * Extracts value from JSON using JSONPath.
     *
     * @param json The JSON string
     * @param jsonPath The JSONPath expression
     * @param <T> The type
     * @return The extracted value
     */
    public static <T> T extractByPath(String json, String jsonPath) {
        return JsonPath.read(json, jsonPath);
    }

    /**
     * Extracts list from JSON using JSONPath.
     *
     * @param json The JSON string
     * @param jsonPath The JSONPath expression
     * @param <T> The element type
     * @return The extracted list
     */
    public static <T> List<T> extractListByPath(String json, String jsonPath) {
        return JsonPath.read(json, jsonPath);
    }

    /**
     * Converts object to Map.
     *
     * @param object The object to convert
     * @return The Map representation
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(Object object) {
        return objectMapper.convertValue(object, Map.class);
    }

    /**
     * Converts Map to object.
     *
     * @param map The map to convert
     * @param clazz The target class
     * @param <T> The type
     * @return The converted object
     */
    public static <T> T mapToObject(Map<String, Object> map, Class<T> clazz) {
        return objectMapper.convertValue(map, clazz);
    }

    /**
     * Gets the ObjectMapper instance.
     *
     * @return The ObjectMapper
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
