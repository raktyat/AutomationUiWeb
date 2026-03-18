package com.automation.framework.pages.mobile.android;

import com.automation.framework.core.enums.PlatformType;
import com.automation.framework.pages.BaseMobilePage;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Android Home Screen demonstrating mobile Page Object Model.
 */
@Slf4j
public class AndroidHomeScreen extends BaseMobilePage {

    @AndroidFindBy(id = "com.example.app:id/welcome_text")
    private WebElement welcomeText;

    @AndroidFindBy(id = "com.example.app:id/menu_button")
    private WebElement menuButton;

    @AndroidFindBy(id = "com.example.app:id/logout_option")
    private WebElement logoutOption;

    @AndroidFindBy(id = "com.example.app:id/item_card")
    private List<WebElement> itemCards;

    @AndroidFindBy(id = "com.example.app:id/search_button")
    private WebElement searchButton;

    @AndroidFindBy(id = "com.example.app:id/notification_icon")
    private WebElement notificationIcon;

    @AndroidFindBy(id = "com.example.app:id/notification_badge")
    private WebElement notificationBadge;

    public AndroidHomeScreen(AppiumDriver driver) {
        super(driver, PlatformType.ANDROID);
    }

    @Override
    public boolean isPageLoaded() {
        try {
            return isElementVisible(welcomeText) || isElementVisible(menuButton);
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
        return getText(welcomeText);
    }

    /**
     * Opens the menu.
     *
     * @return This screen instance for fluent API
     */
    public AndroidHomeScreen openMenu() {
        click(menuButton);
        return this;
    }

    /**
     * Logs out of the application.
     *
     * @return The AndroidLoginScreen
     */
    public AndroidLoginScreen logout() {
        openMenu();
        click(logoutOption);
        log.info("Logged out");
        return new AndroidLoginScreen(driver);
    }

    /**
     * Gets the count of item cards.
     *
     * @return The item count
     */
    public int getItemCount() {
        return itemCards.size();
    }

    /**
     * Taps on an item card by index.
     *
     * @param index The item index (0-based)
     * @return This screen instance for fluent API
     */
    public AndroidHomeScreen tapItem(int index) {
        if (index < itemCards.size()) {
            click(itemCards.get(index));
        }
        return this;
    }

    /**
     * Opens the search screen.
     *
     * @return This screen instance for fluent API
     */
    public AndroidHomeScreen openSearch() {
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
