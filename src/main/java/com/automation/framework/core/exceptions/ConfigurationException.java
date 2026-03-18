package com.automation.framework.core.exceptions;

/**
 * Exception thrown when configuration loading or parsing fails.
 */
public class ConfigurationException extends FrameworkException {

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
