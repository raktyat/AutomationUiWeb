package com.automation.framework.pages.mobile.ios;

import com.automation.framework.core.enums.PlatformType;
import com.automation.framework.pages.BaseMobilePage;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;

/**
 * iOS Login Screen demonstrating mobile Page Object Model for iOS.
 */
@Slf4j
public class IOSLoginScreen extends BaseMobilePage {

    @iOSXCUITFindBy(accessibility = "usernameTextField")
    private WebElement usernameInput;

    @iOSXCUITFindBy(accessibility = "passwordTextField")
    private WebElement passwordInput;

    @iOSXCUITFindBy(accessibility = "loginButton")
    private WebElement loginButton;

    @iOSXCUITFindBy(accessibility = "errorLabel")
    private WebElement errorLabel;

    @iOSXCUITFindBy(accessibility = "forgotPasswordButton")
    private WebElement forgotPasswordButton;

    @iOSXCUITFindBy(accessibility = "signUpButton")
    private WebElement signUpButton;

    @iOSXCUITFindBy(accessibility = "showPasswordButton")
    private WebElement showPasswordButton;

    public IOSLoginScreen(AppiumDriver driver) {
        super(driver, PlatformType.IOS);
    }

    @Override
    public boolean isPageLoaded() {
        try {
            return isElementVisible(usernameInput) && isElementVisible(loginButton);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Enters the username.
     *
     * @param username The username
     * @return This screen instance for fluent API
     */
    public IOSLoginScreen enterUsername(String username) {
        type(usernameInput, username);
        hideKeyboard();
        log.info("Entered username: {}", username);
        return this;
    }

    /**
     * Enters the password.
     *
     * @param password The password
     * @return This screen instance for fluent API
     */
    public IOSLoginScreen enterPassword(String password) {
        type(passwordInput, password);
        hideKeyboard();
        log.info("Entered password");
        return this;
    }

    /**
     * Taps the login button.
     *
     * @return The IOSHomeScreen
     */
    public IOSHomeScreen tapLoginButton() {
        click(loginButton);
        log.info("Tapped login button");
        return new IOSHomeScreen(driver);
    }

    /**
     * Performs complete login.
     *
     * @param username The username
     * @param password The password
     * @return The IOSHomeScreen
     */
    public IOSHomeScreen login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        return tapLoginButton();
    }

    /**
     * Performs login expecting failure.
     *
     * @param username The username
     * @param password The password
     * @return This screen instance
     */
    public IOSLoginScreen loginExpectingFailure(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        click(loginButton);
        return this;
    }

    /**
     * Gets the error message.
     *
     * @return The error message text
     */
    public String getErrorMessage() {
        return getText(errorLabel);
    }

    /**
     * Checks if error is displayed.
     *
     * @return true if displayed, false otherwise
     */
    public boolean isErrorDisplayed() {
        return isElementVisible(errorLabel);
    }

    /**
     * Toggles password visibility.
     *
     * @return This screen instance for fluent API
     */
    public IOSLoginScreen togglePasswordVisibility() {
        click(showPasswordButton);
        return this;
    }
}
