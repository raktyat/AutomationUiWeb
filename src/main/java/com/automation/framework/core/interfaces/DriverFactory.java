package com.automation.framework.core.interfaces;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Interface Segregation Principle (ISP): Defines contract for driver creation.
 * Single Responsibility: Only handles driver instantiation.
 *
 * @param <T> The type of WebDriver to create
 */
public interface DriverFactory<T extends WebDriver> {

    /**
     * Creates a new driver instance.
     *
     * @return A new WebDriver instance
     */
    T createDriver();

    /**
     * Creates a new driver instance with custom capabilities.
     *
     * @param capabilities The desired capabilities
     * @return A new WebDriver instance
     */
    T createDriver(DesiredCapabilities capabilities);

    /**
     * Checks if this factory supports the given configuration.
     *
     * @return true if supported, false otherwise
     */
    boolean supports();
}
