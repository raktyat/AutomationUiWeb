package com.automation.framework.pages.web;

import com.automation.framework.pages.BasePage;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Sign Up Page object.
 */
@Slf4j
public class SignUpPage extends BasePage {

    @FindBy(id = "firstName")
    private WebElement firstNameInput;

    @FindBy(id = "lastName")
    private WebElement lastNameInput;

    @FindBy(id = "email")
    private WebElement emailInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(id = "confirmPassword")
    private WebElement confirmPasswordInput;

    @FindBy(id = "termsCheckbox")
    private WebElement termsCheckbox;

    @FindBy(id = "signup-button")
    private WebElement signUpButton;

    @FindBy(linkText = "Already have an account? Login")
    private WebElement loginLink;

    public SignUpPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageLoaded() {
        try {
            return isDisplayed(emailInput) && isDisplayed(signUpButton);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Fills in the registration form.
     *
     * @param firstName First name
     * @param lastName Last name
     * @param email Email address
     * @param password Password
     * @return This page instance for fluent API
     */
    public SignUpPage fillRegistrationForm(String firstName, String lastName, String email, String password) {
        type(firstNameInput, firstName);
        type(lastNameInput, lastName);
        type(emailInput, email);
        type(passwordInput, password);
        type(confirmPasswordInput, password);
        return this;
    }

    /**
     * Accepts the terms and conditions.
     *
     * @return This page instance for fluent API
     */
    public SignUpPage acceptTerms() {
        if (!isSelected(termsCheckbox)) {
            click(termsCheckbox);
        }
        return this;
    }

    /**
     * Clicks the sign up button.
     *
     * @return The HomePage after successful registration
     */
    public HomePage clickSignUp() {
        click(signUpButton);
        return new HomePage(driver);
    }

    /**
     * Completes the registration process.
     *
     * @param firstName First name
     * @param lastName Last name
     * @param email Email address
     * @param password Password
     * @return The HomePage after successful registration
     */
    public HomePage register(String firstName, String lastName, String email, String password) {
        fillRegistrationForm(firstName, lastName, email, password);
        acceptTerms();
        return clickSignUp();
    }

    /**
     * Goes to the login page.
     *
     * @return The LoginPage
     */
    public LoginPage goToLogin() {
        click(loginLink);
        return new LoginPage(driver);
    }
}
