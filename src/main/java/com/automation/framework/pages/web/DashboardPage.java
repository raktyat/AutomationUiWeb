package com.automation.framework.pages.web;

import com.automation.framework.pages.BasePage;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * Sample Dashboard Page object.
 */
@Slf4j
public class DashboardPage extends BasePage {

    @FindBy(css = ".dashboard-title")
    private WebElement dashboardTitle;

    @FindBy(css = ".widget")
    private List<WebElement> widgets;

    @FindBy(id = "add-widget-btn")
    private WebElement addWidgetButton;

    @FindBy(css = ".stats-card")
    private List<WebElement> statsCards;

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageLoaded() {
        try {
            return isDisplayed(dashboardTitle);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets the dashboard title.
     *
     * @return The title text
     */
    public String getDashboardTitle() {
        return getText(dashboardTitle);
    }

    /**
     * Gets the count of visible widgets.
     *
     * @return The widget count
     */
    public int getWidgetCount() {
        return widgets.size();
    }

    /**
     * Clicks the add widget button.
     *
     * @return This page instance for fluent API
     */
    public DashboardPage clickAddWidget() {
        click(addWidgetButton);
        return this;
    }

    /**
     * Gets the count of stats cards.
     *
     * @return The stats card count
     */
    public int getStatsCardCount() {
        return statsCards.size();
    }
}
