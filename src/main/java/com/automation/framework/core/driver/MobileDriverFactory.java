package com.automation.framework.core.driver;

import com.automation.framework.config.CapabilitiesManager;
import com.automation.framework.config.ConfigurationManager;
import com.automation.framework.core.enums.ExecutionPlatform;
import com.automation.framework.core.enums.PlatformType;
import com.automation.framework.core.exceptions.DriverInitializationException;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * Factory for creating mobile drivers (Android and iOS).
 * Open/Closed Principle (OCP): Open for extension with new mobile platforms.
 * Single Responsibility Principle (SRP): Only handles mobile driver creation.
 */
@Slf4j
public class MobileDriverFactory {

    private static final ConfigurationManager config = ConfigurationManager.getInstance();
    private static final CapabilitiesManager capabilitiesManager = new CapabilitiesManager();

    private MobileDriverFactory() {
        // Private constructor
    }

    // ==================== Android Driver Methods ====================

    /**
     * Creates an Android driver.
     *
     * @param executionPlatform The execution platform
     * @return The AndroidDriver instance
     */
    public static AndroidDriver createAndroidDriver(ExecutionPlatform executionPlatform) {
        try {
            URL appiumUrl = getAppiumUrl(executionPlatform);
            DesiredCapabilities capabilities = capabilitiesManager.getCapabilities(
                    PlatformType.ANDROID, executionPlatform);

            UiAutomator2Options options = new UiAutomator2Options();
            capabilities.asMap().forEach(options::setCapability);

            AndroidDriver driver = new AndroidDriver(appiumUrl, options);
            configureTimeouts(driver);

            log.info("Created Android driver on {} platform", executionPlatform);
            return driver;

        } catch (MalformedURLException e) {
            throw new DriverInitializationException("Invalid Appium URL", e);
        }
    }

    /**
     * Creates an Android driver with custom capabilities.
     *
     * @param executionPlatform The execution platform
     * @param additionalCaps Additional capabilities to merge
     * @return The AndroidDriver instance
     */
    public static AndroidDriver createAndroidDriver(ExecutionPlatform executionPlatform,
                                                    DesiredCapabilities additionalCaps) {
        try {
            URL appiumUrl = getAppiumUrl(executionPlatform);
            DesiredCapabilities capabilities = capabilitiesManager.getCapabilities(
                    PlatformType.ANDROID, executionPlatform);
            capabilities.merge(additionalCaps);

            UiAutomator2Options options = new UiAutomator2Options();
            capabilities.asMap().forEach(options::setCapability);

            AndroidDriver driver = new AndroidDriver(appiumUrl, options);
            configureTimeouts(driver);

            return driver;

        } catch (MalformedURLException e) {
            throw new DriverInitializationException("Invalid Appium URL", e);
        }
    }

    // ==================== iOS Driver Methods ====================

    /**
     * Creates an iOS driver.
     *
     * @param executionPlatform The execution platform
     * @return The IOSDriver instance
     */
    public static IOSDriver createIOSDriver(ExecutionPlatform executionPlatform) {
        try {
            URL appiumUrl = getAppiumUrl(executionPlatform);
            DesiredCapabilities capabilities = capabilitiesManager.getCapabilities(
                    PlatformType.IOS, executionPlatform);

            XCUITestOptions options = new XCUITestOptions();
            capabilities.asMap().forEach(options::setCapability);

            IOSDriver driver = new IOSDriver(appiumUrl, options);
            configureTimeouts(driver);

            log.info("Created iOS driver on {} platform", executionPlatform);
            return driver;

        } catch (MalformedURLException e) {
            throw new DriverInitializationException("Invalid Appium URL", e);
        }
    }

    /**
     * Creates an iOS driver with custom capabilities.
     *
     * @param executionPlatform The execution platform
     * @param additionalCaps Additional capabilities to merge
     * @return The IOSDriver instance
     */
    public static IOSDriver createIOSDriver(ExecutionPlatform executionPlatform,
                                            DesiredCapabilities additionalCaps) {
        try {
            URL appiumUrl = getAppiumUrl(executionPlatform);
            DesiredCapabilities capabilities = capabilitiesManager.getCapabilities(
                    PlatformType.IOS, executionPlatform);
            capabilities.merge(additionalCaps);

            XCUITestOptions options = new XCUITestOptions();
            capabilities.asMap().forEach(options::setCapability);

            IOSDriver driver = new IOSDriver(appiumUrl, options);
            configureTimeouts(driver);

            return driver;

        } catch (MalformedURLException e) {
            throw new DriverInitializationException("Invalid Appium URL", e);
        }
    }

    // ==================== Helper Methods ====================

    private static URL getAppiumUrl(ExecutionPlatform executionPlatform) throws MalformedURLException {
        String urlString;

        switch (executionPlatform) {
            case LOCAL:
                urlString = config.getProperty("appium.url", "http://localhost:4723");
                break;
            case BROWSERSTACK:
                String bsUsername = config.getProperty("browserstack.username");
                String bsAccessKey = config.getProperty("browserstack.accesskey");
                urlString = String.format("https://%s:%s@hub-cloud.browserstack.com/wd/hub",
                        bsUsername, bsAccessKey);
                break;
            case SAUCELABS:
                String sauceUsername = config.getProperty("saucelabs.username");
                String sauceAccessKey = config.getProperty("saucelabs.accesskey");
                urlString = String.format("https://%s:%s@ondemand.us-west-1.saucelabs.com/wd/hub",
                        sauceUsername, sauceAccessKey);
                break;
            default:
                throw new DriverInitializationException("Unsupported execution platform for mobile: " + executionPlatform);
        }

        return new URL(urlString);
    }

    private static void configureTimeouts(io.appium.java_client.AppiumDriver driver) {
        int implicitWait = config.getIntProperty("timeout.implicit");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
    }
}
