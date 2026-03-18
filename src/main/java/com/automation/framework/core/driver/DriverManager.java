package com.automation.framework.core.driver;

import com.automation.framework.config.ConfigurationManager;
import com.automation.framework.core.enums.BrowserType;
import com.automation.framework.core.enums.ExecutionPlatform;
import com.automation.framework.core.enums.PlatformType;
import com.automation.framework.core.exceptions.DriverInitializationException;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;

/**
 * Thread-safe driver manager using ThreadLocal pattern.
 * Single Responsibility Principle (SRP): Manages driver lifecycle.
 * Dependency Inversion Principle (DIP): Depends on abstractions (WebDriver interface).
 */
@Slf4j
public class DriverManager {

    private static final ThreadLocal<WebDriver> webDriverThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<AppiumDriver> appiumDriverThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<PlatformType> platformTypeThreadLocal = new ThreadLocal<>();

    private static final ConfigurationManager config = ConfigurationManager.getInstance();

    private DriverManager() {
        // Private constructor to prevent instantiation
    }

    // ==================== Web Driver Methods ====================

    /**
     * Initializes WebDriver for the current thread.
     *
     * @param browserType The browser type to use
     */
    public static void initWebDriver(BrowserType browserType) {
        ExecutionPlatform executionPlatform = ExecutionPlatform.fromString(
                config.getProperty("execution.platform", "local"));
        initWebDriver(browserType, executionPlatform);
    }

    /**
     * Initializes WebDriver for the current thread with specific execution platform.
     *
     * @param browserType The browser type to use
     * @param executionPlatform The execution platform
     */
    public static void initWebDriver(BrowserType browserType, ExecutionPlatform executionPlatform) {
        if (webDriverThreadLocal.get() != null) {
            log.warn("WebDriver already initialized for this thread. Quitting existing driver.");
            quitWebDriver();
        }

        WebDriver driver = WebDriverFactory.createDriver(browserType, executionPlatform);
        webDriverThreadLocal.set(driver);
        platformTypeThreadLocal.set(PlatformType.WEB);

        log.info("Initialized {} WebDriver on {} platform", browserType, executionPlatform);
    }

    /**
     * Gets the WebDriver instance for the current thread.
     *
     * @return The WebDriver instance
     */
    public static WebDriver getWebDriver() {
        WebDriver driver = webDriverThreadLocal.get();
        if (driver == null) {
            throw new DriverInitializationException("WebDriver not initialized for current thread");
        }
        return driver;
    }

    /**
     * Quits the WebDriver for the current thread.
     */
    public static void quitWebDriver() {
        WebDriver driver = webDriverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
                log.info("WebDriver quit successfully");
            } catch (Exception e) {
                log.error("Error quitting WebDriver", e);
            } finally {
                webDriverThreadLocal.remove();
                platformTypeThreadLocal.remove();
            }
        }
    }

    // ==================== Mobile Driver Methods ====================

    /**
     * Initializes Android driver for the current thread.
     */
    public static void initAndroidDriver() {
        ExecutionPlatform executionPlatform = ExecutionPlatform.fromString(
                config.getProperty("execution.platform", "local"));
        initAndroidDriver(executionPlatform);
    }

    /**
     * Initializes Android driver for the current thread with specific execution platform.
     *
     * @param executionPlatform The execution platform
     */
    public static void initAndroidDriver(ExecutionPlatform executionPlatform) {
        if (appiumDriverThreadLocal.get() != null) {
            log.warn("AppiumDriver already initialized for this thread. Quitting existing driver.");
            quitMobileDriver();
        }

        AndroidDriver driver = MobileDriverFactory.createAndroidDriver(executionPlatform);
        appiumDriverThreadLocal.set(driver);
        platformTypeThreadLocal.set(PlatformType.ANDROID);

        log.info("Initialized Android driver on {} platform", executionPlatform);
    }

    /**
     * Initializes iOS driver for the current thread.
     */
    public static void initIOSDriver() {
        ExecutionPlatform executionPlatform = ExecutionPlatform.fromString(
                config.getProperty("execution.platform", "local"));
        initIOSDriver(executionPlatform);
    }

    /**
     * Initializes iOS driver for the current thread with specific execution platform.
     *
     * @param executionPlatform The execution platform
     */
    public static void initIOSDriver(ExecutionPlatform executionPlatform) {
        if (appiumDriverThreadLocal.get() != null) {
            log.warn("AppiumDriver already initialized for this thread. Quitting existing driver.");
            quitMobileDriver();
        }

        IOSDriver driver = MobileDriverFactory.createIOSDriver(executionPlatform);
        appiumDriverThreadLocal.set(driver);
        platformTypeThreadLocal.set(PlatformType.IOS);

        log.info("Initialized iOS driver on {} platform", executionPlatform);
    }

    /**
     * Gets the AppiumDriver instance for the current thread.
     *
     * @return The AppiumDriver instance
     */
    public static AppiumDriver getMobileDriver() {
        AppiumDriver driver = appiumDriverThreadLocal.get();
        if (driver == null) {
            throw new DriverInitializationException("Mobile driver not initialized for current thread");
        }
        return driver;
    }

    /**
     * Gets the Android driver instance.
     *
     * @return The AndroidDriver instance
     */
    public static AndroidDriver getAndroidDriver() {
        AppiumDriver driver = getMobileDriver();
        if (!(driver instanceof AndroidDriver)) {
            throw new DriverInitializationException("Current driver is not an Android driver");
        }
        return (AndroidDriver) driver;
    }

    /**
     * Gets the iOS driver instance.
     *
     * @return The IOSDriver instance
     */
    public static IOSDriver getIOSDriver() {
        AppiumDriver driver = getMobileDriver();
        if (!(driver instanceof IOSDriver)) {
            throw new DriverInitializationException("Current driver is not an iOS driver");
        }
        return (IOSDriver) driver;
    }

    /**
     * Quits the mobile driver for the current thread.
     */
    public static void quitMobileDriver() {
        AppiumDriver driver = appiumDriverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
                log.info("Mobile driver quit successfully");
            } catch (Exception e) {
                log.error("Error quitting mobile driver", e);
            } finally {
                appiumDriverThreadLocal.remove();
                platformTypeThreadLocal.remove();
            }
        }
    }

    // ==================== Utility Methods ====================

    /**
     * Gets the current platform type for the thread.
     *
     * @return The platform type
     */
    public static PlatformType getCurrentPlatform() {
        return platformTypeThreadLocal.get();
    }

    /**
     * Checks if a driver is initialized for the current thread.
     *
     * @return true if driver exists, false otherwise
     */
    public static boolean hasDriver() {
        return webDriverThreadLocal.get() != null || appiumDriverThreadLocal.get() != null;
    }

    /**
     * Quits any active driver for the current thread.
     */
    public static void quitDriver() {
        if (webDriverThreadLocal.get() != null) {
            quitWebDriver();
        }
        if (appiumDriverThreadLocal.get() != null) {
            quitMobileDriver();
        }
    }
}
