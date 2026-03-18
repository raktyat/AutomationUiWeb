package com.automation.framework.pages.web;

import com.automation.framework.pages.BasePage;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Forgot Password Page object.
 */
@Slf4j
public class ForgotPasswordPage extends BasePage {

    @FindBy(id = "email")
    private WebElement emailInput;

    @FindBy(id = "reset-button")
    private WebElement resetButton;

    @FindBy(css = ".success-message")
    private WebElement successMessage;

    @FindBy(linkText = "Back to Login")
    private WebElement backToLoginLink;

    public ForgotPasswordPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageLoaded() {
        try {
            return isDisplayed(emailInput) && isDisplayed(resetButton);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Enters the email address.
     *
     * @param email The email address
     * @return This page instance for fluent API
     */
    public ForgotPasswordPage enterEmail(String email) {
        type(emailInput, email);
        return this;
    }

    /**
     * Clicks the reset button.
     *
     * @return This page instance for fluent API
     */
    public ForgotPasswordPage clickReset() {
        click(resetButton);
        return this;
    }

    /**
     * Submits password reset request.
     *
     * @param email The email address
     * @return This page instance for fluent API
     */
    public ForgotPasswordPage requestPasswordReset(String email) {
        enterEmail(email);
        clickReset();
        return this;
    }

    /**
     * Gets the success message.
     *
     * @return The success message text
     */
    public String getSuccessMessage() {
        return getText(successMessage);
    }

    /**
     * Goes back to the login page.
     *
     * @return The LoginPage
     */
    public LoginPage goBackToLogin() {
        click(backToLoginLink);
        return new LoginPage(driver);
    }
}
