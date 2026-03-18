package com.automation.framework.pages;

import com.automation.framework.config.ConfigurationManager;
import com.automation.framework.core.enums.PlatformType;
import com.automation.framework.core.interfaces.Scrollable;
import com.automation.framework.core.interfaces.Waitable;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Collections;

/**
 * Base page class for mobile applications (Android and iOS).
 * Supports platform-specific operations while maintaining common interface.
 */
@Slf4j
public abstract class BaseMobilePage implements Waitable, Scrollable {

    protected final AppiumDriver driver;
    protected final WebDriverWait wait;
    protected final ConfigurationManager config;
    protected final PlatformType platform;

    protected BaseMobilePage(AppiumDriver driver, PlatformType platform) {
        this.driver = driver;
        this.platform = platform;
        this.config = ConfigurationManager.getInstance();
        int explicitTimeout = config.getIntProperty("timeout.explicit");
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(explicitTimeout));
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(explicitTimeout)), this);
    }

    // ==================== Waitable Interface Implementation ====================

    @Override
    public WebElement waitForVisible(WebElement element, Duration timeout) {
        return new WebDriverWait(driver, timeout)
                .until(ExpectedConditions.visibilityOf(element));
    }

    @Override
    public WebElement waitForClickable(WebElement element, Duration timeout) {
        return new WebDriverWait(driver, timeout)
                .until(ExpectedConditions.elementToBeClickable(element));
    }

    @Override
    public WebElement waitForPresent(String locator, Duration timeout) {
        return new WebDriverWait(driver, timeout)
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath(locator)));
    }

    @Override
    public boolean waitForInvisible(WebElement element, Duration timeout) {
        try {
            return new WebDriverWait(driver, timeout)
                    .until(ExpectedConditions.invisibilityOf(element));
        } catch (TimeoutException e) {
            return false;
        }
    }

    // ==================== Scrollable Interface Implementation ====================

    @Override
    public void scrollToElement(WebElement element) {
        Dimension size = driver.manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.8);
        int endY = (int) (size.height * 0.2);

        int maxSwipes = 10;
        int swipeCount = 0;

        while (!isElementVisible(element) && swipeCount < maxSwipes) {
            swipe(startX, startY, startX, endY, 500);
            swipeCount++;
        }
    }

    @Override
    public void scrollDown(int amount) {
        Dimension size = driver.manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.8);
        int endY = (int) (size.height * 0.2);
        swipe(startX, startY, startX, endY, 500);
    }

    @Override
    public void scrollUp(int amount) {
        Dimension size = driver.manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.2);
        int endY = (int) (size.height * 0.8);
        swipe(startX, startY, startX, endY, 500);
    }

    @Override
    public void scrollToTop() {
        for (int i = 0; i < 5; i++) {
            scrollUp(0);
        }
    }

    @Override
    public void scrollToBottom() {
        for (int i = 0; i < 5; i++) {
            scrollDown(0);
        }
    }

    // ==================== Mobile-Specific Operations ====================

    /**
     * Performs a swipe gesture.
     *
     * @param startX Starting X coordinate
     * @param startY Starting Y coordinate
     * @param endX Ending X coordinate
     * @param endY Ending Y coordinate
     * @param durationMs Duration in milliseconds
     */
    protected void swipe(int startX, int startY, int endX, int endY, int durationMs) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);

        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(durationMs), PointerInput.Origin.viewport(), endX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Collections.singletonList(swipe));
    }

    /**
     * Swipes left on the screen.
     */
    protected void swipeLeft() {
        Dimension size = driver.manage().window().getSize();
        int startX = (int) (size.width * 0.8);
        int endX = (int) (size.width * 0.2);
        int y = size.height / 2;
        swipe(startX, y, endX, y, 500);
    }

    /**
     * Swipes right on the screen.
     */
    protected void swipeRight() {
        Dimension size = driver.manage().window().getSize();
        int startX = (int) (size.width * 0.2);
        int endX = (int) (size.width * 0.8);
        int y = size.height / 2;
        swipe(startX, y, endX, y, 500);
    }

    /**
     * Taps at specific coordinates.
     *
     * @param x The X coordinate
     * @param y The Y coordinate
     */
    protected void tap(int x, int y) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1);

        tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Collections.singletonList(tap));
    }

    /**
     * Long presses on an element.
     *
     * @param element The element to long press
     * @param durationMs Duration in milliseconds
     */
    protected void longPress(WebElement element, int durationMs) {
        Point location = element.getLocation();
        Dimension size = element.getSize();
        int x = location.getX() + size.getWidth() / 2;
        int y = location.getY() + size.getHeight() / 2;

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence longPress = new Sequence(finger, 1);

        longPress.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
        longPress.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        longPress.addAction(finger.createPointerMove(Duration.ofMillis(durationMs), PointerInput.Origin.viewport(), x, y));
        longPress.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Collections.singletonList(longPress));
    }

    // ==================== Common Page Operations ====================

    /**
     * Clicks on an element.
     *
     * @param element The element to click
     */
    protected void click(WebElement element) {
        waitForClickable(element, getDefaultTimeout());
        element.click();
        log.debug("Clicked element");
    }

    /**
     * Types text into an element.
     *
     * @param element The element to type into
     * @param text The text to type
     */
    protected void type(WebElement element, String text) {
        waitForVisible(element, getDefaultTimeout());
        element.clear();
        element.sendKeys(text);
        log.debug("Typed '{}' into element", text);
    }

    /**
     * Gets the text of an element.
     *
     * @param element The element to get text from
     * @return The element's text
     */
    protected String getText(WebElement element) {
        waitForVisible(element, getDefaultTimeout());
        return element.getText();
    }

    /**
     * Checks if an element is visible.
     *
     * @param element The element to check
     * @return true if visible, false otherwise
     */
    protected boolean isElementVisible(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    /**
     * Checks if currently running on Android.
     *
     * @return true if Android, false otherwise
     */
    protected boolean isAndroid() {
        return platform == PlatformType.ANDROID;
    }

    /**
     * Checks if currently running on iOS.
     *
     * @return true if iOS, false otherwise
     */
    protected boolean isIOS() {
        return platform == PlatformType.IOS;
    }

    /**
     * Gets the Android driver if available.
     *
     * @return The AndroidDriver
     */
    protected AndroidDriver getAndroidDriver() {
        if (!isAndroid()) {
            throw new IllegalStateException("Not running on Android");
        }
        return (AndroidDriver) driver;
    }

    /**
     * Gets the iOS driver if available.
     *
     * @return The IOSDriver
     */
    protected IOSDriver getIOSDriver() {
        if (!isIOS()) {
            throw new IllegalStateException("Not running on iOS");
        }
        return (IOSDriver) driver;
    }

    /**
     * Hides the keyboard if visible.
     */
    protected void hideKeyboard() {
        try {
            if (isAndroid()) {
                getAndroidDriver().hideKeyboard();
            } else if (isIOS()) {
                getIOSDriver().hideKeyboard();
            }
        } catch (Exception e) {
            log.debug("Keyboard not visible or could not be hidden");
        }
    }

    /**
     * Goes back (Android back button or iOS navigation).
     */
    protected void goBack() {
        driver.navigate().back();
    }

    /**
     * Gets the default timeout duration.
     *
     * @return The default timeout
     */
    protected Duration getDefaultTimeout() {
        return Duration.ofSeconds(config.getIntProperty("timeout.explicit"));
    }

    /**
     * Verifies the page/screen is loaded. Subclasses should override this.
     *
     * @return true if page is loaded, false otherwise
     */
    public abstract boolean isPageLoaded();
}
