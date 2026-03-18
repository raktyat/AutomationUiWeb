package com.automation.framework.utils;

import com.automation.framework.config.ConfigurationManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

/**
 * Utility class for capturing screenshots.
 */
@Slf4j
public final class ScreenshotUtils {

    private static final ConfigurationManager config = ConfigurationManager.getInstance();

    private ScreenshotUtils() {
        // Private constructor
    }

    /**
     * Captures a screenshot and saves it to the configured path.
     *
     * @param driver The WebDriver instance
     * @param screenshotName The name for the screenshot
     * @return The path to the saved screenshot
     */
    public static String captureScreenshot(WebDriver driver, String screenshotName) {
        String screenshotPath = config.getProperty("screenshot.path", "target/screenshots");
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = screenshotName + "_" + timestamp + ".png";
        String fullPath = screenshotPath + "/" + fileName;

        try {
            File screenshotDir = new File(screenshotPath);
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }

            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destFile = new File(fullPath);
            FileUtils.copyFile(srcFile, destFile);

            log.debug("Screenshot saved: {}", fullPath);
            return fullPath;

        } catch (IOException e) {
            log.error("Failed to capture screenshot", e);
            return null;
        }
    }

    /**
     * Captures a screenshot as Base64 encoded string.
     *
     * @param driver The WebDriver instance
     * @return The Base64 encoded screenshot
     */
    public static String captureBase64Screenshot(WebDriver driver) {
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            log.error("Failed to capture Base64 screenshot", e);
            return null;
        }
    }

    /**
     * Captures a screenshot as byte array.
     *
     * @param driver The WebDriver instance
     * @return The screenshot as byte array
     */
    public static byte[] captureScreenshotAsBytes(WebDriver driver) {
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            log.error("Failed to capture screenshot as bytes", e);
            return new byte[0];
        }
    }

    /**
     * Converts a file to Base64 string.
     *
     * @param filePath The file path
     * @return The Base64 encoded string
     */
    public static String fileToBase64(String filePath) {
        try {
            byte[] fileContent = FileUtils.readFileToByteArray(new File(filePath));
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            log.error("Failed to convert file to Base64", e);
            return null;
        }
    }
}
