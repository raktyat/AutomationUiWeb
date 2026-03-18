package com.automation.framework.listeners;

import com.automation.framework.config.ConfigurationManager;
import com.automation.framework.core.driver.DriverManager;
import com.automation.framework.reporting.ExtentReportManager;
import com.automation.framework.utils.ScreenshotUtils;
import com.aventstack.extentreports.Status;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG Listener for handling test events and reporting.
 * Single Responsibility: Handles test lifecycle events for reporting.
 */
@Slf4j
public class TestListener implements ITestListener {

    private final ConfigurationManager config = ConfigurationManager.getInstance();

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String testDescription = result.getMethod().getDescription();

        if (testDescription != null && !testDescription.isEmpty()) {
            ExtentReportManager.createTest(testName, testDescription);
        } else {
            ExtentReportManager.createTest(testName);
        }

        // Assign categories based on test groups
        String[] groups = result.getMethod().getGroups();
        if (groups.length > 0) {
            ExtentReportManager.assignCategory(groups);
        }

        log.info("Test Started: {}", testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        ExtentReportManager.pass("Test passed successfully");

        if (config.getBooleanProperty("screenshot.on.pass")) {
            captureScreenshot(result, "Pass");
        }

        log.info("Test Passed: {}", testName);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        Throwable throwable = result.getThrowable();

        ExtentReportManager.fail(throwable);

        if (config.getBooleanProperty("screenshot.on.failure")) {
            captureScreenshot(result, "Failure");
        }

        log.error("Test Failed: {} - {}", testName, throwable.getMessage());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        Throwable throwable = result.getThrowable();

        if (throwable != null) {
            ExtentReportManager.skip("Test skipped: " + throwable.getMessage());
        } else {
            ExtentReportManager.skip("Test skipped");
        }

        log.warn("Test Skipped: {}", testName);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        ExtentReportManager.warning("Test failed but within success percentage");
        log.warn("Test failed within success percentage: {}", testName);
    }

    @Override
    public void onStart(ITestContext context) {
        log.info("Test Suite Started: {}", context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        log.info("Test Suite Finished: {}", context.getName());
        log.info("Passed: {}, Failed: {}, Skipped: {}",
                context.getPassedTests().size(),
                context.getFailedTests().size(),
                context.getSkippedTests().size());
    }

    /**
     * Captures and attaches screenshot to the report.
     *
     * @param result The test result
     * @param status The status for naming
     */
    private void captureScreenshot(ITestResult result, String status) {
        try {
            if (DriverManager.hasDriver()) {
                WebDriver driver = null;
                try {
                    driver = DriverManager.getWebDriver();
                } catch (Exception e) {
                    try {
                        driver = DriverManager.getMobileDriver();
                    } catch (Exception ex) {
                        log.debug("No driver available for screenshot");
                        return;
                    }
                }

                if (driver != null) {
                    String base64Screenshot = ScreenshotUtils.captureBase64Screenshot(driver);
                    String screenshotTitle = result.getMethod().getMethodName() + "_" + status;
                    ExtentReportManager.addScreenshot(base64Screenshot, screenshotTitle);
                    log.debug("Screenshot captured for: {}", result.getMethod().getMethodName());
                }
            }
        } catch (Exception e) {
            log.error("Failed to capture screenshot", e);
        }
    }
}
