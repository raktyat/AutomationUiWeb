package com.automation.framework.tests.web;

import com.automation.framework.core.base.BaseWebTest;
import com.automation.framework.listeners.RetryAnalyzer;
import com.automation.framework.pages.web.HomePage;
import com.automation.framework.pages.web.LoginPage;
import com.automation.framework.reporting.ExtentReportManager;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Sample Web Login Test demonstrating the framework capabilities.
 */
public class LoginTest extends BaseWebTest {

    private LoginPage loginPage;

    @BeforeMethod
    public void initPage() {
        loginPage = new LoginPage(getDriver());
    }

    @Test(description = "Verify successful login with valid credentials",
            groups = {"smoke", "login"},
            retryAnalyzer = RetryAnalyzer.class)
    public void testSuccessfulLogin() {
        ExtentReportManager.info("Starting login test with valid credentials");

        // Test data
        String username = config.getProperty("test.user.username", "testuser");
        String password = config.getProperty("test.user.password", "password123");

        // Perform login
        HomePage homePage = loginPage
                .enterUsername(username)
                .enterPassword(password)
                .clickLoginButton();

        // Verify successful login
        assertThat(homePage.isPageLoaded())
                .as("Home page should be loaded after successful login")
                .isTrue();

        ExtentReportManager.pass("Login successful");
    }

    @Test(description = "Verify login fails with invalid credentials",
            groups = {"smoke", "login", "negative"})
    public void testLoginWithInvalidCredentials() {
        ExtentReportManager.info("Testing login with invalid credentials");

        // Perform login with invalid credentials
        loginPage.loginExpectingFailure("invaliduser", "wrongpassword");

        // Verify error message
        assertThat(loginPage.isErrorMessageDisplayed())
                .as("Error message should be displayed")
                .isTrue();

        ExtentReportManager.pass("Error message displayed as expected");
    }

    @Test(description = "Verify login with empty username",
            groups = {"validation", "login", "negative"})
    public void testLoginWithEmptyUsername() {
        ExtentReportManager.info("Testing login with empty username");

        loginPage.loginExpectingFailure("", "password123");

        assertThat(loginPage.isErrorMessageDisplayed())
                .as("Error message should be displayed for empty username")
                .isTrue();

        ExtentReportManager.pass("Validation works for empty username");
    }

    @Test(description = "Verify login with empty password",
            groups = {"validation", "login", "negative"})
    public void testLoginWithEmptyPassword() {
        ExtentReportManager.info("Testing login with empty password");

        loginPage.loginExpectingFailure("testuser", "");

        assertThat(loginPage.isErrorMessageDisplayed())
                .as("Error message should be displayed for empty password")
                .isTrue();

        ExtentReportManager.pass("Validation works for empty password");
    }

    @Test(description = "Verify remember me functionality",
            groups = {"regression", "login"})
    public void testRememberMe() {
        ExtentReportManager.info("Testing remember me checkbox");

        String username = config.getProperty("test.user.username", "testuser");
        String password = config.getProperty("test.user.password", "password123");

        loginPage
                .enterUsername(username)
                .enterPassword(password)
                .checkRememberMe()
                .clickLoginButton();

        ExtentReportManager.pass("Remember me functionality tested");
    }

    @Test(description = "Data-driven login test",
            groups = {"regression", "login"},
            dataProvider = "loginDataProvider")
    public void testLoginWithMultipleCredentials(String username, String password, boolean shouldSucceed) {
        ExtentReportManager.info("Testing login with username: " + username);

        if (shouldSucceed) {
            HomePage homePage = loginPage.login(username, password);
            assertThat(homePage.isPageLoaded()).isTrue();
        } else {
            loginPage.loginExpectingFailure(username, password);
            assertThat(loginPage.isErrorMessageDisplayed()).isTrue();
        }

        ExtentReportManager.pass("Data-driven test completed");
    }

    @DataProvider(name = "loginDataProvider")
    public Object[][] loginDataProvider() {
        return new Object[][]{
                {"validuser1", "validpass1", true},
                {"validuser2", "validpass2", true},
                {"invaliduser", "invalidpass", false},
                {"", "password", false},
                {"username", "", false}
        };
    }
}
