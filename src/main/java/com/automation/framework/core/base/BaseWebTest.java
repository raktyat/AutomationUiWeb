package com.automation.framework.core.base;

import com.automation.framework.core.driver.DriverManager;
import com.automation.framework.core.enums.BrowserType;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

/**
 * Base class for web UI tests.
 * Provides WebDriver lifecycle management for web tests.
 */
@Slf4j
public abstract class BaseWebTest extends BaseTest {

    /**
     * Sets up WebDriver before each test method.
     *
     * @param browser The browser to use (default: from config)
     */
    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser"})
    public void setUpWebDriver(@Optional String browser) {
        String browserName = browser != null ? browser : config.getProperty("browser", "chrome");
        BrowserType browserType = BrowserType.fromString(browserName);

        DriverManager.initWebDriver(browserType);
        getDriver().get(getBaseUrl());

        log.info("Initialized {} browser and navigated to {}", browserType, getBaseUrl());
    }

    /**
     * Tears down WebDriver after each test method.
     */
    @AfterMethod(alwaysRun = true)
    public void tearDownWebDriver() {
        DriverManager.quitWebDriver();
    }

    /**
     * Gets the WebDriver instance for the current thread.
     *
     * @return The WebDriver instance
     */
    protected WebDriver getDriver() {
        return DriverManager.getWebDriver();
    }

    /**
     * Navigates to a specific URL.
     *
     * @param url The URL to navigate to
     */
    protected void navigateTo(String url) {
        getDriver().get(url);
        log.debug("Navigated to: {}", url);
    }

    /**
     * Navigates to a path relative to the base URL.
     *
     * @param path The path to append to base URL
     */
    protected void navigateToPath(String path) {
        String fullUrl = getBaseUrl() + path;
        navigateTo(fullUrl);
    }

    /**
     * Gets the current page title.
     *
     * @return The page title
     */
    protected String getPageTitle() {
        return getDriver().getTitle();
    }

    /**
     * Gets the current URL.
     *
     * @return The current URL
     */
    protected String getCurrentUrl() {
        return getDriver().getCurrentUrl();
    }
}
