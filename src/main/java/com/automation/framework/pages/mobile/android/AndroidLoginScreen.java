package com.automation.framework.pages.mobile.android;

import com.automation.framework.core.enums.PlatformType;
import com.automation.framework.pages.BaseMobilePage;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;

/**
 * Android Login Screen demonstrating mobile Page Object Model.
 */
@Slf4j
public class AndroidLoginScreen extends BaseMobilePage {

    @AndroidFindBy(id = "com.example.app:id/username_input")
    private WebElement usernameInput;

    @AndroidFindBy(id = "com.example.app:id/password_input")
    private WebElement passwordInput;

    @AndroidFindBy(id = "com.example.app:id/login_button")
    private WebElement loginButton;

    @AndroidFindBy(id = "com.example.app:id/error_text")
    private WebElement errorText;

    @AndroidFindBy(id = "com.example.app:id/forgot_password")
    private WebElement forgotPasswordLink;

    @AndroidFindBy(id = "com.example.app:id/signup_link")
    private WebElement signUpLink;

    @AndroidFindBy(accessibility = "Show password")
    private WebElement showPasswordButton;

    public AndroidLoginScreen(AppiumDriver driver) {
        super(driver, PlatformType.ANDROID);
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
    public AndroidLoginScreen enterUsername(String username) {
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
    public AndroidLoginScreen enterPassword(String password) {
        type(passwordInput, password);
        hideKeyboard();
        log.info("Entered password");
        return this;
    }

    /**
     * Taps the login button.
     *
     * @return The AndroidHomeScreen
     */
    public AndroidHomeScreen tapLoginButton() {
        click(loginButton);
        log.info("Tapped login button");
        return new AndroidHomeScreen(driver);
    }

    /**
     * Performs complete login.
     *
     * @param username The username
     * @param password The password
     * @return The AndroidHomeScreen
     */
    public AndroidHomeScreen login(String username, String password) {
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
    public AndroidLoginScreen loginExpectingFailure(String username, String password) {
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
        return getText(errorText);
    }

    /**
     * Checks if error is displayed.
     *
     * @return true if displayed, false otherwise
     */
    public boolean isErrorDisplayed() {
        return isElementVisible(errorText);
    }

    /**
     * Toggles password visibility.
     *
     * @return This screen instance for fluent API
     */
    public AndroidLoginScreen togglePasswordVisibility() {
        click(showPasswordButton);
        return this;
    }
}
