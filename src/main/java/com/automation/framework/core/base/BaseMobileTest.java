package com.automation.framework.core.base;

import com.automation.framework.core.driver.DriverManager;
import com.automation.framework.core.enums.PlatformType;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

/**
 * Base class for mobile UI tests.
 * Provides AppiumDriver lifecycle management for mobile tests.
 */
@Slf4j
public abstract class BaseMobileTest extends BaseTest {

    /**
     * Sets up the mobile driver before each test method.
     *
     * @param platform The platform to use (android/ios)
     */
    @BeforeMethod(alwaysRun = true)
    @Parameters({"platform"})
    public void setUpMobileDriver(@Optional("android") String platform) {
        PlatformType platformType = PlatformType.fromString(platform);

        switch (platformType) {
            case ANDROID:
                DriverManager.initAndroidDriver();
                break;
            case IOS:
                DriverManager.initIOSDriver();
                break;
            default:
                throw new IllegalArgumentException("Invalid mobile platform: " + platform);
        }

        log.info("Initialized {} mobile driver", platform);
    }

    /**
     * Tears down the mobile driver after each test method.
     */
    @AfterMethod(alwaysRun = true)
    public void tearDownMobileDriver() {
        DriverManager.quitMobileDriver();
    }

    /**
     * Gets the AppiumDriver instance for the current thread.
     *
     * @return The AppiumDriver instance
     */
    protected AppiumDriver getDriver() {
        return DriverManager.getMobileDriver();
    }

    /**
     * Gets the AndroidDriver instance.
     *
     * @return The AndroidDriver instance
     */
    protected AndroidDriver getAndroidDriver() {
        return DriverManager.getAndroidDriver();
    }

    /**
     * Gets the IOSDriver instance.
     *
     * @return The IOSDriver instance
     */
    protected IOSDriver getIOSDriver() {
        return DriverManager.getIOSDriver();
    }

    /**
     * Gets the current platform type.
     *
     * @return The platform type
     */
    protected PlatformType getCurrentPlatform() {
        return DriverManager.getCurrentPlatform();
    }

    /**
     * Checks if running on Android.
     *
     * @return true if Android, false otherwise
     */
    protected boolean isAndroid() {
        return getCurrentPlatform() == PlatformType.ANDROID;
    }

    /**
     * Checks if running on iOS.
     *
     * @return true if iOS, false otherwise
     */
    protected boolean isIOS() {
        return getCurrentPlatform() == PlatformType.IOS;
    }

    /**
     * Resets the app to initial state.
     */
    protected void resetApp() {
        String appPackage = getAppPackage();
        if (isAndroid()) {
            getAndroidDriver().terminateApp(appPackage);
            getAndroidDriver().activateApp(appPackage);
        } else if (isIOS()) {
            getIOSDriver().terminateApp(appPackage);
            getIOSDriver().activateApp(appPackage);
        }
        log.debug("Reset app");
    }

    /**
     * Gets the app package/bundle ID.
     *
     * @return The app identifier
     */
    protected String getAppPackage() {
        if (isAndroid()) {
            return config.getProperty("android.app.package", "com.example.app");
        } else {
            return config.getProperty("ios.bundle.id", "com.example.app");
        }
    }
}
