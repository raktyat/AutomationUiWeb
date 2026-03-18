package com.automation.framework.pages.web;

import com.automation.framework.pages.BasePage;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Sample Home Page object demonstrating navigation after login.
 */
@Slf4j
public class HomePage extends BasePage {

    @FindBy(css = ".welcome-message")
    private WebElement welcomeMessage;

    @FindBy(id = "user-menu")
    private WebElement userMenu;

    @FindBy(id = "logout-link")
    private WebElement logoutLink;

    @FindBy(id = "dashboard-link")
    private WebElement dashboardLink;

    @FindBy(id = "profile-link")
    private WebElement profileLink;

    @FindBy(id = "settings-link")
    private WebElement settingsLink;

    @FindBy(css = ".notification-badge")
    private WebElement notificationBadge;

    public HomePage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageLoaded() {
        try {
            return isDisplayed(welcomeMessage) || isDisplayed(userMenu);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets the welcome message text.
     *
     * @return The welcome message
     */
    public String getWelcomeMessage() {
        return getText(welcomeMessage);
    }

    /**
     * Opens the user menu dropdown.
     *
     * @return This page instance for fluent API
     */
    public HomePage openUserMenu() {
        click(userMenu);
        return this;
    }

    /**
     * Logs out of the application.
     *
     * @return The LoginPage
     */
    public LoginPage logout() {
        openUserMenu();
        click(logoutLink);
        log.info("Logged out");
        return new LoginPage(driver);
    }

    /**
     * Navigates to the dashboard.
     *
     * @return The DashboardPage
     */
    public DashboardPage goToDashboard() {
        click(dashboardLink);
        return new DashboardPage(driver);
    }

    /**
     * Gets the notification count.
     *
     * @return The notification count
     */
    public int getNotificationCount() {
        try {
            String count = getText(notificationBadge);
            return Integer.parseInt(count);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Checks if notifications are present.
     *
     * @return true if notifications exist, false otherwise
     */
    public boolean hasNotifications() {
        return isDisplayed(notificationBadge);
    }
}
