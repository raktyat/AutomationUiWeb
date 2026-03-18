package com.automation.framework.pages.mobile.ios;

import com.automation.framework.core.enums.PlatformType;
import com.automation.framework.pages.BaseMobilePage;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * iOS Home Screen demonstrating mobile Page Object Model for iOS.
 */
@Slf4j
public class IOSHomeScreen extends BaseMobilePage {

    @iOSXCUITFindBy(accessibility = "welcomeLabel")
    private WebElement welcomeLabel;

    @iOSXCUITFindBy(accessibility = "menuButton")
    private WebElement menuButton;

    @iOSXCUITFindBy(accessibility = "logoutButton")
    private WebElement logoutButton;

    @iOSXCUITFindBy(iOSClassChain = "**/XCUIElementTypeCell")
    private List<WebElement> itemCells;

    @iOSXCUITFindBy(accessibility = "searchButton")
    private WebElement searchButton;

    @iOSXCUITFindBy(accessibility = "notificationButton")
    private WebElement notificationButton;

    @iOSXCUITFindBy(accessibility = "notificationBadge")
    private WebElement notificationBadge;

    public IOSHomeScreen(AppiumDriver driver) {
        super(driver, PlatformType.IOS);
    }

    @Override
    public boolean isPageLoaded() {
        try {
            return isElementVisible(welcomeLabel) || isElementVisible(menuButton);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets the welcome text.
     *
     * @return The welcome text
     */
    public String getWelcomeText() {
        return getText(welcomeLabel);
    }

    /**
     * Opens the menu.
     *
     * @return This screen instance for fluent API
     */
    public IOSHomeScreen openMenu() {
        click(menuButton);
        return this;
    }

    /**
     * Logs out of the application.
     *
     * @return The IOSLoginScreen
     */
    public IOSLoginScreen logout() {
        openMenu();
        click(logoutButton);
        log.info("Logged out");
        return new IOSLoginScreen(driver);
    }

    /**
     * Gets the count of item cells.
     *
     * @return The item count
     */
    public int getItemCount() {
        return itemCells.size();
    }

    /**
     * Taps on an item cell by index.
     *
     * @param index The item index (0-based)
     * @return This screen instance for fluent API
     */
    public IOSHomeScreen tapItem(int index) {
        if (index < itemCells.size()) {
            click(itemCells.get(index));
        }
        return this;
    }

    /**
     * Opens the search screen.
     *
     * @return This screen instance for fluent API
     */
    public IOSHomeScreen openSearch() {
        click(searchButton);
        return this;
    }

    /**
     * Gets the notification count.
     *
     * @return The notification count
     */
    public int getNotificationCount() {
        try {
            return Integer.parseInt(getText(notificationBadge));
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
        return isElementVisible(notificationBadge);
    }
}
