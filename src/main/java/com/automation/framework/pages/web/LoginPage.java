package com.automation.framework.pages.web;

import com.automation.framework.pages.BasePage;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Sample Login Page object demonstrating Page Object Model pattern.
 * Single Responsibility: Only handles login page interactions.
 */
@Slf4j
public class LoginPage extends BasePage {

    @FindBy(id = "username")
    private WebElement usernameInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(id = "login-button")
    private WebElement loginButton;

    @FindBy(css = ".error-message")
    private WebElement errorMessage;

    @FindBy(id = "remember-me")
    private WebElement rememberMeCheckbox;

    @FindBy(linkText = "Forgot Password?")
    private WebElement forgotPasswordLink;

    @FindBy(linkText = "Sign Up")
    private WebElement signUpLink;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageLoaded() {
        try {
            return isDisplayed(usernameInput) && isDisplayed(passwordInput);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Enters the username.
     *
     * @param username The username to enter
     * @return This page instance for fluent API
     */
    public LoginPage enterUsername(String username) {
        type(usernameInput, username);
        log.info("Entered username: {}", username);
        return this;
    }

    /**
     * Enters the password.
     *
     * @param password The password to enter
     * @return This page instance for fluent API
     */
    public LoginPage enterPassword(String password) {
        type(passwordInput, password);
        log.info("Entered password");
        return this;
    }

    /**
     * Clicks the login button.
     *
     * @return The HomePage if login is successful
     */
    public HomePage clickLoginButton() {
        click(loginButton);
        log.info("Clicked login button");
        return new HomePage(driver);
    }

    /**
     * Performs a complete login action.
     *
     * @param username The username
     * @param password The password
     * @return The HomePage if login is successful
     */
    public HomePage login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        return clickLoginButton();
    }

    /**
     * Performs login expecting failure.
     *
     * @param username The username
     * @param password The password
     * @return This page instance (login fails)
     */
    public LoginPage loginExpectingFailure(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        click(loginButton);
        return this;
    }

    /**
     * Gets the error message text.
     *
     * @return The error message
     */
    public String getErrorMessage() {
        return getText(errorMessage);
    }

    /**
     * Checks if error message is displayed.
     *
     * @return true if displayed, false otherwise
     */
    public boolean isErrorMessageDisplayed() {
        return isDisplayed(errorMessage);
    }

    /**
     * Checks the remember me checkbox.
     *
     * @return This page instance for fluent API
     */
    public LoginPage checkRememberMe() {
        if (!isSelected(rememberMeCheckbox)) {
            click(rememberMeCheckbox);
        }
        return this;
    }

    /**
     * Clicks the forgot password link.
     *
     * @return The ForgotPasswordPage
     */
    public ForgotPasswordPage clickForgotPassword() {
        click(forgotPasswordLink);
        return new ForgotPasswordPage(driver);
    }

    /**
     * Clicks the sign up link.
     *
     * @return The SignUpPage
     */
    public SignUpPage clickSignUp() {
        click(signUpLink);
        return new SignUpPage(driver);
    }
}
