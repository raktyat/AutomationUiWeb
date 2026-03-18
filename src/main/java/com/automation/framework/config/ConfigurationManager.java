package com.automation.framework.config;

import com.automation.framework.core.exceptions.ConfigurationException;
import com.automation.framework.core.interfaces.Configurable;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Singleton pattern for configuration management.
 * Single Responsibility Principle (SRP): Only handles configuration loading and retrieval.
 * Open/Closed Principle (OCP): Open for extension via environment-specific properties.
 */
@Slf4j
public class ConfigurationManager implements Configurable {

    private static final String FRAMEWORK_CONFIG = "config/framework.properties";
    private static final Pattern ENV_VAR_PATTERN = Pattern.compile("\\$\\{([^}]+)}");

    private static volatile ConfigurationManager instance;
    private final Properties properties;
    private String currentEnvironment;

    private ConfigurationManager() {
        this.properties = new Properties();
        loadFrameworkConfig();
        loadEnvironmentConfig();
    }

    /**
     * Gets the singleton instance (thread-safe double-checked locking).
     *
     * @return The ConfigurationManager instance
     */
    public static ConfigurationManager getInstance() {
        if (instance == null) {
            synchronized (ConfigurationManager.class) {
                if (instance == null) {
                    instance = new ConfigurationManager();
                }
            }
        }
        return instance;
    }

    private void loadFrameworkConfig() {
        loadPropertiesFile(FRAMEWORK_CONFIG);
        log.info("Loaded framework configuration");
    }

    private void loadEnvironmentConfig() {
        currentEnvironment = System.getProperty("environment",
                properties.getProperty("default.environment", "qa"));
        String envConfigFile = "config/" + currentEnvironment + ".properties";
        loadPropertiesFile(envConfigFile);
        log.info("Loaded {} environment configuration", currentEnvironment);
    }

    private void loadPropertiesFile(String fileName) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (input != null) {
                properties.load(input);
            } else {
                log.warn("Configuration file not found: {}", fileName);
            }
        } catch (IOException e) {
            throw new ConfigurationException("Failed to load configuration: " + fileName, e);
        }
    }

    @Override
    public String getProperty(String key) {
        String value = System.getProperty(key);
        if (value == null) {
            value = properties.getProperty(key);
        }
        return resolveEnvironmentVariables(value);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }

    @Override
    public int getIntProperty(String key) {
        String value = getProperty(key);
        if (value == null) {
            throw new ConfigurationException("Property not found: " + key);
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Invalid integer value for property: " + key, e);
        }
    }

    @Override
    public boolean getBooleanProperty(String key) {
        String value = getProperty(key);
        return Boolean.parseBoolean(value);
    }

    /**
     * Gets the current environment.
     *
     * @return The current environment name
     */
    public String getCurrentEnvironment() {
        return currentEnvironment;
    }

    /**
     * Resolves environment variables in property values.
     * Supports ${ENV_VAR} syntax.
     */
    private String resolveEnvironmentVariables(String value) {
        if (value == null) {
            return null;
        }

        Matcher matcher = ENV_VAR_PATTERN.matcher(value);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String envVar = matcher.group(1);
            String envValue = System.getenv(envVar);
            if (envValue == null) {
                envValue = System.getProperty(envVar, matcher.group(0));
            }
            matcher.appendReplacement(result, Matcher.quoteReplacement(envValue));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    /**
     * Reloads configuration (useful for tests).
     */
    public void reload() {
        properties.clear();
        loadFrameworkConfig();
        loadEnvironmentConfig();
    }
}
