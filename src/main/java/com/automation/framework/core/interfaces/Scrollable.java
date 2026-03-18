package com.automation.framework.core.interfaces;

import org.openqa.selenium.WebElement;

/**
 * Interface Segregation Principle (ISP): Defines contract for scroll operations.
 * Useful for both web and mobile platforms.
 */
public interface Scrollable {

    /**
     * Scrolls to the specified element.
     *
     * @param element The element to scroll to
     */
    void scrollToElement(WebElement element);

    /**
     * Scrolls down by the specified pixels or percentage.
     *
     * @param amount The amount to scroll
     */
    void scrollDown(int amount);

    /**
     * Scrolls up by the specified pixels or percentage.
     *
     * @param amount The amount to scroll
     */
    void scrollUp(int amount);

    /**
     * Scrolls to the top of the page/screen.
     */
    void scrollToTop();

    /**
     * Scrolls to the bottom of the page/screen.
     */
    void scrollToBottom();
}
