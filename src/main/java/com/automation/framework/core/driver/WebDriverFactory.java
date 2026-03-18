package com.automation.framework.core.driver;

import com.automation.framework.config.CapabilitiesManager;
import com.automation.framework.config.ConfigurationManager;
import com.automation.framework.core.enums.BrowserType;
import com.automation.framework.core.enums.ExecutionPlatform;
import com.automation.framework.core.exceptions.DriverInitializationException;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * Factory for creating WebDriver instances.
 * Open/Closed Principle (OCP): Open for extension (new browsers) without modifying existing code.
 * Liskov Substitution Principle (LSP): All drivers can be used interchangeably as WebDriver.
 */
@Slf4j
public class WebDriverFactory {

    private static final ConfigurationManager config = ConfigurationManager.getInstance();
    private static final CapabilitiesManager capabilitiesManager = new CapabilitiesManager();

    private WebDriverFactory() {
        // Private constructor
    }

    /**
     * Creates a WebDriver based on browser type and execution platform.
     *
     * @param browserType The browser type
     * @param executionPlatform The execution platform
     * @return The WebDriver instance
     */
    public static WebDriver createDriver(BrowserType browserType, ExecutionPlatform executionPlatform) {
        WebDriver driver;

        switch (executionPlatform) {
            case LOCAL:
                driver = createLocalDriver(browserType);
                break;
            case GRID:
                driver = createGridDriver(browserType);
                break;
            case BROWSERSTACK:
                driver = createBrowserStackDriver(browserType);
                break;
            case SAUCELABS:
                driver = createSauceLabsDriver(browserType);
                break;
            default:
                throw new DriverInitializationException("Unsupported execution platform: " + executionPlatform);
        }

        configureDriver(driver);
        return driver;
    }

    private static WebDriver createLocalDriver(BrowserType browserType) {
        boolean headless = config.getBooleanProperty("browser.headless");

        switch (browserType) {
            case CHROME:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                if (headless) {
                    chromeOptions.addArguments("--headless=new");
                }
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--disable-gpu");
                chromeOptions.addArguments("--window-size=1920,1080");
                return new ChromeDriver(chromeOptions);

            case FIREFOX:
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (headless) {
                    firefoxOptions.addArguments("--headless");
                }
                return new FirefoxDriver(firefoxOptions);

            case EDGE:
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                if (headless) {
                    edgeOptions.addArguments("--headless=new");
                }
                return new EdgeDriver(edgeOptions);

            case SAFARI:
                return new SafariDriver(new SafariOptions());

            default:
                throw new DriverInitializationException("Unsupported browser: " + browserType);
        }
    }

    private static WebDriver createGridDriver(BrowserType browserType) {
        String gridUrl = config.getProperty("grid.url");
        DesiredCapabilities capabilities = getCapabilitiesForBrowser(browserType);

        try {
            log.info("Creating RemoteWebDriver on Grid: {}", gridUrl);
            return new RemoteWebDriver(new URL(gridUrl), capabilities);
        } catch (MalformedURLException e) {
            throw new DriverInitializationException("Invalid Grid URL: " + gridUrl, e);
        }
    }

    private static WebDriver createBrowserStackDriver(BrowserType browserType) {
        String username = config.getProperty("browserstack.username");
        String accessKey = config.getProperty("browserstack.accesskey");
        String url = String.format("https://%s:%s@hub-cloud.browserstack.com/wd/hub", username, accessKey);

        DesiredCapabilities capabilities = capabilitiesManager.getWebCapabilities();
        capabilities.merge(getCapabilitiesForBrowser(browserType));

        try {
            log.info("Creating RemoteWebDriver on BrowserStack");
            return new RemoteWebDriver(new URL(url), capabilities);
        } catch (MalformedURLException e) {
            throw new DriverInitializationException("Invalid BrowserStack URL", e);
        }
    }

    private static WebDriver createSauceLabsDriver(BrowserType browserType) {
        String username = config.getProperty("saucelabs.username");
        String accessKey = config.getProperty("saucelabs.accesskey");
        String url = String.format("https://%s:%s@ondemand.us-west-1.saucelabs.com/wd/hub", username, accessKey);

        DesiredCapabilities capabilities = getCapabilitiesForBrowser(browserType);
        capabilities.setCapability("sauce:options", buildSauceOptions());

        try {
            log.info("Creating RemoteWebDriver on Sauce Labs");
            return new RemoteWebDriver(new URL(url), capabilities);
        } catch (MalformedURLException e) {
            throw new DriverInitializationException("Invalid Sauce Labs URL", e);
        }
    }

    private static DesiredCapabilities getCapabilitiesForBrowser(BrowserType browserType) {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName(browserType.getBrowserName());
        return capabilities;
    }

    private static java.util.Map<String, Object> buildSauceOptions() {
        java.util.Map<String, Object> sauceOptions = new java.util.HashMap<>();
        sauceOptions.put("build", config.getProperty("saucelabs.build", "Automation Build"));
        sauceOptions.put("name", config.getProperty("saucelabs.testname", "Automated Test"));
        return sauceOptions;
    }

    private static void configureDriver(WebDriver driver) {
        int implicitWait = config.getIntProperty("timeout.implicit");
        int pageLoadTimeout = config.getIntProperty("timeout.page.load");
        int scriptTimeout = config.getIntProperty("timeout.script");

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(scriptTimeout));

        driver.manage().window().maximize();
        log.debug("Configured driver timeouts - implicit: {}s, pageLoad: {}s, script: {}s",
                implicitWait, pageLoadTimeout, scriptTimeout);
    }
}
