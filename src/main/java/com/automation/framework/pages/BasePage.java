package com.automation.framework.pages;

import com.automation.framework.config.ConfigurationManager;
import com.automation.framework.core.interfaces.Scrollable;
import com.automation.framework.core.interfaces.Waitable;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Base page class providing common page operations.
 * Single Responsibility Principle (SRP): Handles common UI interactions.
 * Open/Closed Principle (OCP): Subclasses extend without modifying this class.
 * Liskov Substitution Principle (LSP): All page objects can be used where BasePage is expected.
 */
@Slf4j
public abstract class BasePage implements Waitable, Scrollable {

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final ConfigurationManager config;
    protected final Actions actions;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.config = ConfigurationManager.getInstance();
        int explicitTimeout = config.getIntProperty("timeout.explicit");
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(explicitTimeout));
        this.actions = new Actions(driver);
        PageFactory.initElements(driver, this);
    }

    // ==================== Waitable Interface Implementation ====================

    @Override
    public WebElement waitForVisible(WebElement element, Duration timeout) {
        return new WebDriverWait(driver, timeout)
                .until(ExpectedConditions.visibilityOf(element));
    }

    @Override
    public WebElement waitForClickable(WebElement element, Duration timeout) {
        return new WebDriverWait(driver, timeout)
                .until(ExpectedConditions.elementToBeClickable(element));
    }

    @Override
    public WebElement waitForPresent(String locator, Duration timeout) {
        return new WebDriverWait(driver, timeout)
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath(locator)));
    }

    @Override
    public boolean waitForInvisible(WebElement element, Duration timeout) {
        try {
            return new WebDriverWait(driver, timeout)
                    .until(ExpectedConditions.invisibilityOf(element));
        } catch (TimeoutException e) {
            return false;
        }
    }

    // ==================== Scrollable Interface Implementation ====================

    @Override
    public void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
    }

    @Override
    public void scrollDown(int pixels) {
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, " + pixels + ");");
    }

    @Override
    public void scrollUp(int pixels) {
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, -" + pixels + ");");
    }

    @Override
    public void scrollToTop() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
    }

    @Override
    public void scrollToBottom() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    // ==================== Common Page Operations ====================

    /**
     * Clicks on an element with wait.
     *
     * @param element The element to click
     */
    protected void click(WebElement element) {
        waitForClickable(element, getDefaultTimeout());
        element.click();
        log.debug("Clicked element: {}", getElementDescription(element));
    }

    /**
     * Clicks on an element using JavaScript.
     *
     * @param element The element to click
     */
    protected void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        log.debug("JS clicked element: {}", getElementDescription(element));
    }

    /**
     * Types text into an element.
     *
     * @param element The element to type into
     * @param text The text to type
     */
    protected void type(WebElement element, String text) {
        waitForVisible(element, getDefaultTimeout());
        element.clear();
        element.sendKeys(text);
        log.debug("Typed '{}' into element: {}", text, getElementDescription(element));
    }

    /**
     * Types text without clearing the field first.
     *
     * @param element The element to type into
     * @param text The text to append
     */
    protected void typeWithoutClear(WebElement element, String text) {
        waitForVisible(element, getDefaultTimeout());
        element.sendKeys(text);
    }

    /**
     * Gets the text of an element.
     *
     * @param element The element to get text from
     * @return The element's text
     */
    protected String getText(WebElement element) {
        waitForVisible(element, getDefaultTimeout());
        return element.getText();
    }

    /**
     * Gets an attribute value from an element.
     *
     * @param element The element
     * @param attribute The attribute name
     * @return The attribute value
     */
    protected String getAttribute(WebElement element, String attribute) {
        waitForVisible(element, getDefaultTimeout());
        return element.getAttribute(attribute);
    }

    /**
     * Checks if an element is displayed.
     *
     * @param element The element to check
     * @return true if displayed, false otherwise
     */
    protected boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    /**
     * Checks if an element is enabled.
     *
     * @param element The element to check
     * @return true if enabled, false otherwise
     */
    protected boolean isEnabled(WebElement element) {
        return element.isEnabled();
    }

    /**
     * Checks if a checkbox/radio is selected.
     *
     * @param element The element to check
     * @return true if selected, false otherwise
     */
    protected boolean isSelected(WebElement element) {
        return element.isSelected();
    }

    /**
     * Selects a dropdown option by visible text.
     *
     * @param dropdown The dropdown element
     * @param text The text to select
     */
    protected void selectByText(WebElement dropdown, String text) {
        new Select(dropdown).selectByVisibleText(text);
        log.debug("Selected '{}' from dropdown", text);
    }

    /**
     * Selects a dropdown option by value.
     *
     * @param dropdown The dropdown element
     * @param value The value to select
     */
    protected void selectByValue(WebElement dropdown, String value) {
        new Select(dropdown).selectByValue(value);
    }

    /**
     * Selects a dropdown option by index.
     *
     * @param dropdown The dropdown element
     * @param index The index to select
     */
    protected void selectByIndex(WebElement dropdown, int index) {
        new Select(dropdown).selectByIndex(index);
    }

    /**
     * Hovers over an element.
     *
     * @param element The element to hover over
     */
    protected void hover(WebElement element) {
        actions.moveToElement(element).perform();
        log.debug("Hovered over element: {}", getElementDescription(element));
    }

    /**
     * Double-clicks an element.
     *
     * @param element The element to double-click
     */
    protected void doubleClick(WebElement element) {
        actions.doubleClick(element).perform();
    }

    /**
     * Right-clicks an element.
     *
     * @param element The element to right-click
     */
    protected void rightClick(WebElement element) {
        actions.contextClick(element).perform();
    }

    /**
     * Drags and drops an element.
     *
     * @param source The source element
     * @param target The target element
     */
    protected void dragAndDrop(WebElement source, WebElement target) {
        actions.dragAndDrop(source, target).perform();
    }

    /**
     * Switches to a frame by element.
     *
     * @param frameElement The frame element
     */
    protected void switchToFrame(WebElement frameElement) {
        driver.switchTo().frame(frameElement);
    }

    /**
     * Switches to the default content.
     */
    protected void switchToDefaultContent() {
        driver.switchTo().defaultContent();
    }

    /**
     * Accepts an alert.
     *
     * @return The alert text
     */
    protected String acceptAlert() {
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String text = alert.getText();
        alert.accept();
        return text;
    }

    /**
     * Dismisses an alert.
     *
     * @return The alert text
     */
    protected String dismissAlert() {
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String text = alert.getText();
        alert.dismiss();
        return text;
    }

    /**
     * Gets the current page URL.
     *
     * @return The current URL
     */
    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Gets the page title.
     *
     * @return The page title
     */
    protected String getPageTitle() {
        return driver.getTitle();
    }

    /**
     * Gets the default timeout duration.
     *
     * @return The default timeout
     */
    protected Duration getDefaultTimeout() {
        return Duration.ofSeconds(config.getIntProperty("timeout.explicit"));
    }

    /**
     * Gets a description of an element for logging.
     *
     * @param element The element
     * @return A description string
     */
    private String getElementDescription(WebElement element) {
        try {
            String tagName = element.getTagName();
            String id = element.getAttribute("id");
            String text = element.getText();
            if (id != null && !id.isEmpty()) {
                return tagName + "#" + id;
            } else if (text != null && !text.isEmpty()) {
                return tagName + "[" + text.substring(0, Math.min(text.length(), 20)) + "]";
            }
            return tagName;
        } catch (Exception e) {
            return "unknown";
        }
    }

    /**
     * Verifies the page is loaded. Subclasses should override this.
     *
     * @return true if page is loaded, false otherwise
     */
    public abstract boolean isPageLoaded();
}
