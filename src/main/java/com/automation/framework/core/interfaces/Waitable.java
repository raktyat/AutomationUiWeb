package com.automation.framework.core.interfaces;

import org.openqa.selenium.WebElement;

import java.time.Duration;

/**
 * Interface Segregation Principle (ISP): Defines contract for wait operations.
 * Clients only depend on the wait functionality they need.
 */
public interface Waitable {

    /**
     * Waits for an element to be visible.
     *
     * @param element The element to wait for
     * @param timeout The maximum wait duration
     * @return The visible element
     */
    WebElement waitForVisible(WebElement element, Duration timeout);

    /**
     * Waits for an element to be clickable.
     *
     * @param element The element to wait for
     * @param timeout The maximum wait duration
     * @return The clickable element
     */
    WebElement waitForClickable(WebElement element, Duration timeout);

    /**
     * Waits for an element to be present in DOM.
     *
     * @param locator The locator string
     * @param timeout The maximum wait duration
     * @return The present element
     */
    WebElement waitForPresent(String locator, Duration timeout);

    /**
     * Waits for an element to disappear.
     *
     * @param element The element to wait for
     * @param timeout The maximum wait duration
     * @return true if element disappeared, false otherwise
     */
    boolean waitForInvisible(WebElement element, Duration timeout);
}
