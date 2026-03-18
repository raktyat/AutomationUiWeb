package com.automation.framework.utils;

import com.automation.framework.config.ConfigurationManager;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Utility class for loading and managing test data.
 */
@Slf4j
public final class TestDataUtils {

    private static final ConfigurationManager config = ConfigurationManager.getInstance();

    private TestDataUtils() {
        // Private constructor
    }

    /**
     * Loads test data from a JSON file.
     *
     * @param fileName The file name (without path)
     * @param clazz The target class
     * @param <T> The type
     * @return The parsed test data
     */
    public static <T> T loadTestData(String fileName, Class<T> clazz) {
        String testDataPath = config.getProperty("testdata.path", "src/test/resources/testdata");
        String filePath = testDataPath + "/" + fileName;
        return JsonUtils.readFromFile(filePath, clazz);
    }

    /**
     * Loads test data from a JSON file as Map.
     *
     * @param fileName The file name (without path)
     * @return The parsed test data map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> loadTestDataAsMap(String fileName) {
        String testDataPath = config.getProperty("testdata.path", "src/test/resources/testdata");
        String filePath = testDataPath + "/" + fileName;
        return (Map<String, Object>) JsonUtils.readFromFile(filePath, Map.class);
    }

    /**
     * Loads test data from a properties file.
     *
     * @param fileName The file name (without path)
     * @return The properties
     */
    public static Properties loadPropertiesTestData(String fileName) {
        String testDataPath = config.getProperty("testdata.path", "src/test/resources/testdata");
        String filePath = testDataPath + "/" + fileName;
        Properties properties = new Properties();

        try (InputStream is = new FileInputStream(filePath)) {
            properties.load(is);
        } catch (IOException e) {
            log.error("Failed to load properties test data: {}", fileName, e);
        }

        return properties;
    }

    /**
     * Generates a random string.
     *
     * @param length The length of the string
     * @return The random string
     */
    public static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }

    /**
     * Generates a random email address.
     *
     * @return The random email
     */
    public static String generateRandomEmail() {
        return "test_" + generateRandomString(8) + "@example.com";
    }

    /**
     * Generates a random phone number.
     *
     * @return The random phone number
     */
    public static String generateRandomPhoneNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        sb.append("+1");
        for (int i = 0; i < 10; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * Generates a random number within range.
     *
     * @param min The minimum value
     * @param max The maximum value
     * @return The random number
     */
    public static int generateRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    /**
     * Generates a UUID string.
     *
     * @return The UUID string
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Gets current timestamp as string.
     *
     * @return The timestamp string
     */
    public static String getCurrentTimestamp() {
        return String.valueOf(System.currentTimeMillis());
    }

    /**
     * Creates test data map with common user fields.
     *
     * @return The user data map
     */
    public static Map<String, Object> createRandomUserData() {
        Map<String, Object> userData = new HashMap<>();
        userData.put("firstName", "Test" + generateRandomString(4));
        userData.put("lastName", "User" + generateRandomString(4));
        userData.put("email", generateRandomEmail());
        userData.put("phone", generateRandomPhoneNumber());
        return userData;
    }

    /**
     * Provides data for data-driven tests.
     *
     * @param data The 2D array of test data
     * @return Iterator for TestNG data provider
     */
    public static Iterator<Object[]> createDataProvider(Object[][] data) {
        return Arrays.asList(data).iterator();
    }
}
