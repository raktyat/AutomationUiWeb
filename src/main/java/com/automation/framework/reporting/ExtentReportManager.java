package com.automation.framework.reporting;

import com.automation.framework.config.ConfigurationManager;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Manages Extent Reports creation and test reporting.
 * Singleton pattern for report management across tests.
 */
@Slf4j
public class ExtentReportManager {

    private static ExtentReports extentReports;
    private static final ThreadLocal<ExtentTest> extentTestThreadLocal = new ThreadLocal<>();
    private static final ConfigurationManager config = ConfigurationManager.getInstance();

    private ExtentReportManager() {
        // Private constructor
    }

    /**
     * Initializes the Extent Reports.
     */
    public static synchronized void initReports() {
        if (extentReports == null) {
            String reportPath = config.getProperty("report.path", "target/extent-reports");
            String reportName = config.getProperty("report.name", "Automation Report");
            String reportTitle = config.getProperty("report.title", "Test Execution Report");

            // Create report directory
            File reportDir = new File(reportPath);
            if (!reportDir.exists()) {
                reportDir.mkdirs();
            }

            // Generate timestamped report filename
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String reportFile = reportPath + "/TestReport_" + timestamp + ".html";

            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportFile);
            sparkReporter.config().setDocumentTitle(reportTitle);
            sparkReporter.config().setReportName(reportName);
            sparkReporter.config().setTheme(Theme.STANDARD);
            sparkReporter.config().setEncoding("UTF-8");
            sparkReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");

            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);
            extentReports.setSystemInfo("Environment", config.getCurrentEnvironment());
            extentReports.setSystemInfo("Browser", config.getProperty("browser", "chrome"));
            extentReports.setSystemInfo("Platform", System.getProperty("os.name"));
            extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));

            log.info("Extent Reports initialized: {}", reportFile);
        }
    }

    /**
     * Gets the ExtentReports instance.
     *
     * @return The ExtentReports instance
     */
    public static ExtentReports getExtentReports() {
        if (extentReports == null) {
            initReports();
        }
        return extentReports;
    }

    /**
     * Creates a new test in the report.
     *
     * @param testName The test name
     * @return The ExtentTest instance
     */
    public static ExtentTest createTest(String testName) {
        ExtentTest test = getExtentReports().createTest(testName);
        extentTestThreadLocal.set(test);
        log.debug("Created test in report: {}", testName);
        return test;
    }

    /**
     * Creates a new test in the report with description.
     *
     * @param testName The test name
     * @param description The test description
     * @return The ExtentTest instance
     */
    public static ExtentTest createTest(String testName, String description) {
        ExtentTest test = getExtentReports().createTest(testName, description);
        extentTestThreadLocal.set(test);
        log.debug("Created test in report: {} - {}", testName, description);
        return test;
    }

    /**
     * Gets the current test for the thread.
     *
     * @return The ExtentTest instance
     */
    public static ExtentTest getTest() {
        return extentTestThreadLocal.get();
    }

    /**
     * Logs a pass status.
     *
     * @param message The message to log
     */
    public static void pass(String message) {
        ExtentTest test = getTest();
        if (test != null) {
            test.pass(message);
        }
    }

    /**
     * Logs a fail status.
     *
     * @param message The message to log
     */
    public static void fail(String message) {
        ExtentTest test = getTest();
        if (test != null) {
            test.fail(message);
        }
    }

    /**
     * Logs a fail status with throwable.
     *
     * @param throwable The throwable
     */
    public static void fail(Throwable throwable) {
        ExtentTest test = getTest();
        if (test != null) {
            test.fail(throwable);
        }
    }

    /**
     * Logs a skip status.
     *
     * @param message The message to log
     */
    public static void skip(String message) {
        ExtentTest test = getTest();
        if (test != null) {
            test.skip(message);
        }
    }

    /**
     * Logs an info status.
     *
     * @param message The message to log
     */
    public static void info(String message) {
        ExtentTest test = getTest();
        if (test != null) {
            test.info(message);
        }
    }

    /**
     * Logs a warning status.
     *
     * @param message The message to log
     */
    public static void warning(String message) {
        ExtentTest test = getTest();
        if (test != null) {
            test.warning(message);
        }
    }

    /**
     * Logs a status with message.
     *
     * @param status The status
     * @param message The message
     */
    public static void log(Status status, String message) {
        ExtentTest test = getTest();
        if (test != null) {
            test.log(status, message);
        }
    }

    /**
     * Adds a screenshot to the report.
     *
     * @param base64Screenshot The base64 encoded screenshot
     * @param title The screenshot title
     */
    public static void addScreenshot(String base64Screenshot, String title) {
        ExtentTest test = getTest();
        if (test != null) {
            test.addScreenCaptureFromBase64String(base64Screenshot, title);
        }
    }

    /**
     * Adds a screenshot from path to the report.
     *
     * @param screenshotPath The screenshot file path
     * @param title The screenshot title
     */
    public static void addScreenshotFromPath(String screenshotPath, String title) {
        ExtentTest test = getTest();
        if (test != null) {
            try {
                test.addScreenCaptureFromPath(screenshotPath, title);
            } catch (Exception e) {
                log.error("Failed to add screenshot to report", e);
            }
        }
    }

    /**
     * Assigns a category to the current test.
     *
     * @param categories The categories to assign
     */
    public static void assignCategory(String... categories) {
        ExtentTest test = getTest();
        if (test != null) {
            test.assignCategory(categories);
        }
    }

    /**
     * Assigns an author to the current test.
     *
     * @param authors The authors to assign
     */
    public static void assignAuthor(String... authors) {
        ExtentTest test = getTest();
        if (test != null) {
            test.assignAuthor(authors);
        }
    }

    /**
     * Assigns a device to the current test.
     *
     * @param devices The devices to assign
     */
    public static void assignDevice(String... devices) {
        ExtentTest test = getTest();
        if (test != null) {
            test.assignDevice(devices);
        }
    }

    /**
     * Flushes the reports to disk.
     */
    public static synchronized void flushReports() {
        if (extentReports != null) {
            extentReports.flush();
            log.info("Extent Reports flushed");
        }
    }

    /**
     * Removes the test from thread local.
     */
    public static void removeTest() {
        extentTestThreadLocal.remove();
    }
}
