package com.automation.framework.core.interfaces;

/**
 * Interface Segregation Principle (ISP): Defines contract for configurable components.
 * Dependency Inversion Principle (DIP): High-level modules depend on this abstraction.
 */
public interface Configurable {

    /**
     * Gets a configuration property value.
     *
     * @param key The property key
     * @return The property value
     */
    String getProperty(String key);

    /**
     * Gets a configuration property value with a default.
     *
     * @param key The property key
     * @param defaultValue The default value if key not found
     * @return The property value or default
     */
    String getProperty(String key, String defaultValue);

    /**
     * Gets an integer property value.
     *
     * @param key The property key
     * @return The integer value
     */
    int getIntProperty(String key);

    /**
     * Gets a boolean property value.
     *
     * @param key The property key
     * @return The boolean value
     */
    boolean getBooleanProperty(String key);
}
