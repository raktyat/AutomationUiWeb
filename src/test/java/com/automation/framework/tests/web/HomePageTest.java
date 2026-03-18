package com.automation.framework.tests.web;

import com.automation.framework.core.base.BaseWebTest;
import com.automation.framework.pages.web.DashboardPage;
import com.automation.framework.pages.web.HomePage;
import com.automation.framework.pages.web.LoginPage;
import com.automation.framework.reporting.ExtentReportManager;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Sample Home Page Test demonstrating navigation and user actions.
 */
public class HomePageTest extends BaseWebTest {

    private HomePage homePage;

    @BeforeMethod
    public void loginAndNavigateToHome() {
        LoginPage loginPage = new LoginPage(getDriver());
        String username = config.getProperty("test.user.username", "testuser");
        String password = config.getProperty("test.user.password", "password123");

        homePage = loginPage.login(username, password);
    }

    @Test(description = "Verify home page loads after login",
            groups = {"smoke", "homepage"})
    public void testHomePageLoads() {
        ExtentReportManager.info("Verifying home page loads correctly");

        assertThat(homePage.isPageLoaded())
                .as("Home page should be loaded")
                .isTrue();

        ExtentReportManager.pass("Home page loaded successfully");
    }

    @Test(description = "Verify welcome message is displayed",
            groups = {"regression", "homepage"})
    public void testWelcomeMessageDisplayed() {
        ExtentReportManager.info("Verifying welcome message");

        String welcomeMessage = homePage.getWelcomeMessage();

        assertThat(welcomeMessage)
                .as("Welcome message should not be empty")
                .isNotEmpty();

        ExtentReportManager.pass("Welcome message: " + welcomeMessage);
    }

    @Test(description = "Verify navigation to dashboard",
            groups = {"regression", "homepage", "navigation"})
    public void testNavigateToDashboard() {
        ExtentReportManager.info("Testing navigation to dashboard");

        DashboardPage dashboardPage = homePage.goToDashboard();

        assertThat(dashboardPage.isPageLoaded())
                .as("Dashboard page should be loaded")
                .isTrue();

        ExtentReportManager.pass("Navigated to dashboard successfully");
    }

    @Test(description = "Verify logout functionality",
            groups = {"smoke", "homepage", "logout"})
    public void testLogout() {
        ExtentReportManager.info("Testing logout functionality");

        LoginPage loginPage = homePage.logout();

        assertThat(loginPage.isPageLoaded())
                .as("Should be redirected to login page after logout")
                .isTrue();

        ExtentReportManager.pass("Logout successful");
    }

    @Test(description = "Verify notification badge",
            groups = {"regression", "homepage"})
    public void testNotificationBadge() {
        ExtentReportManager.info("Checking notification badge");

        boolean hasNotifications = homePage.hasNotifications();

        if (hasNotifications) {
            int notificationCount = homePage.getNotificationCount();
            ExtentReportManager.info("Notification count: " + notificationCount);
            assertThat(notificationCount).isGreaterThanOrEqualTo(0);
        }

        ExtentReportManager.pass("Notification check completed");
    }
}
