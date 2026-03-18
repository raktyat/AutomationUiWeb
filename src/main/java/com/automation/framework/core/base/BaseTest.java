package com.automation.framework.core.base;

import com.automation.framework.config.ConfigurationManager;
import com.automation.framework.core.driver.DriverManager;
import com.automation.framework.listeners.TestListener;
import com.automation.framework.reporting.ExtentReportManager;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import java.lang.reflect.Method;

/**
 * Base test class providing common setup/teardown functionality.
 * Template Method Pattern: Defines skeleton of test lifecycle.
 * Open/Closed Principle (OCP): Subclasses extend behavior without modification.
 */
@Slf4j
@Listeners(TestListener.class)
public abstract class BaseTest {

    protected ConfigurationManager config;

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite(ITestContext context) {
        config = ConfigurationManager.getInstance();
        ExtentReportManager.initReports();
        log.info("Test suite started: {}", context.getSuite().getName());
        log.info("Environment: {}", config.getCurrentEnvironment());
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod(Method method) {
        log.debug("Completed test method: {}", method.getName());
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        ExtentReportManager.flushReports();
        log.info("Test suite completed");
    }

    /**
     * Gets the base URL for the application.
     *
     * @return The base URL
     */
    protected String getBaseUrl() {
        return config.getProperty("web.base.url");
    }

    /**
     * Gets the API base URL.
     *
     * @return The API base URL
     */
    protected String getApiBaseUrl() {
        return config.getProperty("api.base.url");
    }

    /**
     * Gets the current environment name.
     *
     * @return The environment name
     */
    protected String getEnvironment() {
        return config.getCurrentEnvironment();
    }
}
