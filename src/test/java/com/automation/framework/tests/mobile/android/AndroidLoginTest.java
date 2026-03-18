package com.automation.framework.tests.mobile.android;

import com.automation.framework.core.base.BaseMobileTest;
import com.automation.framework.pages.mobile.android.AndroidHomeScreen;
import com.automation.framework.pages.mobile.android.AndroidLoginScreen;
import com.automation.framework.reporting.ExtentReportManager;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Android Login Test demonstrating mobile testing capabilities.
 */
public class AndroidLoginTest extends BaseMobileTest {

    private AndroidLoginScreen loginScreen;

    @BeforeMethod
    public void initScreen() {
        loginScreen = new AndroidLoginScreen(getAndroidDriver());
        ExtentReportManager.assignDevice("Android");
    }

    @Test(description = "Verify successful login on Android",
            groups = {"smoke", "android", "login"})
    public void testSuccessfulLogin() {
        ExtentReportManager.info("Starting Android login test");

        String username = config.getProperty("test.user.username", "testuser");
        String password = config.getProperty("test.user.password", "password123");

        AndroidHomeScreen homeScreen = loginScreen.login(username, password);

        assertThat(homeScreen.isPageLoaded())
                .as("Home screen should be loaded after login")
                .isTrue();

        ExtentReportManager.pass("Android login successful");
    }

    @Test(description = "Verify login fails with invalid credentials on Android",
            groups = {"smoke", "android", "login", "negative"})
    public void testLoginWithInvalidCredentials() {
        ExtentReportManager.info("Testing Android login with invalid credentials");

        loginScreen.loginExpectingFailure("invalid", "wrong");

        assertThat(loginScreen.isErrorDisplayed())
                .as("Error message should be displayed")
                .isTrue();

        ExtentReportManager.pass("Error displayed as expected on Android");
    }

    @Test(description = "Verify password visibility toggle on Android",
            groups = {"regression", "android", "login"})
    public void testPasswordVisibilityToggle() {
        ExtentReportManager.info("Testing password visibility toggle");

        loginScreen
                .enterPassword("testpassword")
                .togglePasswordVisibility();

        ExtentReportManager.pass("Password visibility toggle works on Android");
    }

    @Test(description = "Verify login screen loads on Android",
            groups = {"smoke", "android", "login"})
    public void testLoginScreenLoads() {
        ExtentReportManager.info("Verifying login screen loads");

        assertThat(loginScreen.isPageLoaded())
                .as("Login screen should be loaded")
                .isTrue();

        ExtentReportManager.pass("Login screen loaded successfully on Android");
    }
}
