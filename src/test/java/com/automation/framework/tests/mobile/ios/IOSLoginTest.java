package com.automation.framework.tests.mobile.ios;

import com.automation.framework.core.base.BaseMobileTest;
import com.automation.framework.pages.mobile.ios.IOSHomeScreen;
import com.automation.framework.pages.mobile.ios.IOSLoginScreen;
import com.automation.framework.reporting.ExtentReportManager;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * iOS Login Test demonstrating mobile testing capabilities.
 */
public class IOSLoginTest extends BaseMobileTest {

    private IOSLoginScreen loginScreen;

    @BeforeMethod
    public void initScreen() {
        loginScreen = new IOSLoginScreen(getIOSDriver());
        ExtentReportManager.assignDevice("iOS");
    }

    @Test(description = "Verify successful login on iOS",
            groups = {"smoke", "ios", "login"})
    public void testSuccessfulLogin() {
        ExtentReportManager.info("Starting iOS login test");

        String username = config.getProperty("test.user.username", "testuser");
        String password = config.getProperty("test.user.password", "password123");

        IOSHomeScreen homeScreen = loginScreen.login(username, password);

        assertThat(homeScreen.isPageLoaded())
                .as("Home screen should be loaded after login")
                .isTrue();

        ExtentReportManager.pass("iOS login successful");
    }

    @Test(description = "Verify login fails with invalid credentials on iOS",
            groups = {"smoke", "ios", "login", "negative"})
    public void testLoginWithInvalidCredentials() {
        ExtentReportManager.info("Testing iOS login with invalid credentials");

        loginScreen.loginExpectingFailure("invalid", "wrong");

        assertThat(loginScreen.isErrorDisplayed())
                .as("Error message should be displayed")
                .isTrue();

        ExtentReportManager.pass("Error displayed as expected on iOS");
    }

    @Test(description = "Verify password visibility toggle on iOS",
            groups = {"regression", "ios", "login"})
    public void testPasswordVisibilityToggle() {
        ExtentReportManager.info("Testing password visibility toggle");

        loginScreen
                .enterPassword("testpassword")
                .togglePasswordVisibility();

        ExtentReportManager.pass("Password visibility toggle works on iOS");
    }

    @Test(description = "Verify login screen loads on iOS",
            groups = {"smoke", "ios", "login"})
    public void testLoginScreenLoads() {
        ExtentReportManager.info("Verifying login screen loads");

        assertThat(loginScreen.isPageLoaded())
                .as("Login screen should be loaded")
                .isTrue();

        ExtentReportManager.pass("Login screen loaded successfully on iOS");
    }
}
