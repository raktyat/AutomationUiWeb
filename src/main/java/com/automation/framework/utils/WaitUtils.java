package com.automation.framework.utils;

import com.automation.framework.config.ConfigurationManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

/**
 * Utility class for various wait operations.
 */
@Slf4j
public final class WaitUtils {

    private static final ConfigurationManager config = ConfigurationManager.getInstance();
    private static final int DEFAULT_TIMEOUT = config.getIntProperty("timeout.explicit");

    private WaitUtils() {
        // Private constructor
    }

    /**
     * Waits for an element to be visible.
     *
     * @param driver The WebDriver instance
     * @param element The element to wait for
     * @return The visible element
     */
    public static WebElement waitForVisible(WebDriver driver, WebElement element) {
        return waitForVisible(driver, element, DEFAULT_TIMEOUT);
    }

    /**
     * Waits for an element to be visible with custom timeout.
     *
     * @param driver The WebDriver instance
     * @param element The element to wait for
     * @param timeoutSeconds The timeout in seconds
     * @return The visible element
     */
    public static WebElement waitForVisible(WebDriver driver, WebElement element, int timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Waits for an element to be clickable.
     *
     * @param driver The WebDriver instance
     * @param element The element to wait for
     * @return The clickable element
     */
    public static WebElement waitForClickable(WebDriver driver, WebElement element) {
        return waitForClickable(driver, element, DEFAULT_TIMEOUT);
    }

    /**
     * Waits for an element to be clickable with custom timeout.
     *
     * @param driver The WebDriver instance
     * @param element The element to wait for
     * @param timeoutSeconds The timeout in seconds
     * @return The clickable element
     */
    public static WebElement waitForClickable(WebDriver driver, WebElement element, int timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Waits for an element to be present by locator.
     *
     * @param driver The WebDriver instance
     * @param locator The element locator
     * @return The present element
     */
    public static WebElement waitForPresence(WebDriver driver, By locator) {
        return waitForPresence(driver, locator, DEFAULT_TIMEOUT);
    }

    /**
     * Waits for an element to be present by locator with custom timeout.
     *
     * @param driver The WebDriver instance
     * @param locator The element locator
     * @param timeoutSeconds The timeout in seconds
     * @return The present element
     */
    public static WebElement waitForPresence(WebDriver driver, By locator, int timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Waits for an element to be invisible.
     *
     * @param driver The WebDriver instance
     * @param element The element to wait for
     * @return true if element became invisible
     */
    public static boolean waitForInvisible(WebDriver driver, WebElement element) {
        return waitForInvisible(driver, element, DEFAULT_TIMEOUT);
    }

    /**
     * Waits for an element to be invisible with custom timeout.
     *
     * @param driver The WebDriver instance
     * @param element The element to wait for
     * @param timeoutSeconds The timeout in seconds
     * @return true if element became invisible
     */
    public static boolean waitForInvisible(WebDriver driver, WebElement element, int timeoutSeconds) {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                    .until(ExpectedConditions.invisibilityOf(element));
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Waits for text to be present in element.
     *
     * @param driver The WebDriver instance
     * @param element The element
     * @param text The text to wait for
     * @return true if text is present
     */
    public static boolean waitForTextPresent(WebDriver driver, WebElement element, String text) {
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT))
                .until(ExpectedConditions.textToBePresentInElement(element, text));
    }

    /**
     * Waits for URL to contain specific text.
     *
     * @param driver The WebDriver instance
     * @param urlPart The URL part to wait for
     * @return true if URL contains the text
     */
    public static boolean waitForUrlContains(WebDriver driver, String urlPart) {
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT))
                .until(ExpectedConditions.urlContains(urlPart));
    }

    /**
     * Waits for page title to contain text.
     *
     * @param driver The WebDriver instance
     * @param title The title text to wait for
     * @return true if title contains the text
     */
    public static boolean waitForTitleContains(WebDriver driver, String title) {
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT))
                .until(ExpectedConditions.titleContains(title));
    }

    /**
     * Waits for all elements to be visible.
     *
     * @param driver The WebDriver instance
     * @param elements The elements to wait for
     * @return The list of visible elements
     */
    public static List<WebElement> waitForAllVisible(WebDriver driver, List<WebElement> elements) {
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT))
                .until(ExpectedConditions.visibilityOfAllElements(elements));
    }

    /**
     * Waits for an alert to be present.
     *
     * @param driver The WebDriver instance
     * @return The Alert
     */
    public static Alert waitForAlert(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT))
                .until(ExpectedConditions.alertIsPresent());
    }

    /**
     * Waits for frame to be available and switches to it.
     *
     * @param driver The WebDriver instance
     * @param frameElement The frame element
     * @return The WebDriver switched to frame
     */
    public static WebDriver waitForFrameAndSwitch(WebDriver driver, WebElement frameElement) {
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT))
                .until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameElement));
    }

    /**
     * Waits for a custom condition with fluent wait.
     *
     * @param driver The WebDriver instance
     * @param condition The expected condition
     * @param timeoutSeconds The timeout in seconds
     * @param pollingMs The polling interval in milliseconds
     * @param <T> The return type
     * @return The result of the condition
     */
    public static <T> T fluentWait(WebDriver driver, Function<WebDriver, T> condition,
                                    int timeoutSeconds, int pollingMs) {
        return new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeoutSeconds))
                .pollingEvery(Duration.ofMillis(pollingMs))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class)
                .until(condition);
    }

    /**
     * Waits for page to be fully loaded.
     *
     * @param driver The WebDriver instance
     */
    public static void waitForPageLoad(WebDriver driver) {
        new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT))
                .until((ExpectedCondition<Boolean>) d ->
                        ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete"));
    }

    /**
     * Waits for jQuery AJAX calls to complete.
     *
     * @param driver The WebDriver instance
     */
    public static void waitForAjax(WebDriver driver) {
        new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT))
                .until((ExpectedCondition<Boolean>) d -> {
                    JavascriptExecutor js = (JavascriptExecutor) d;
                    return (Boolean) js.executeScript("return jQuery.active == 0");
                });
    }

    /**
     * Hard wait (use sparingly).
     *
     * @param milliseconds The time to wait in milliseconds
     */
    public static void hardWait(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Hard wait interrupted");
        }
    }
}
