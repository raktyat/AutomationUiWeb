# Enterprise Automation Framework

A comprehensive, enterprise-grade test automation framework supporting Web, Mobile (Android/iOS), and API testing in a unified architecture.

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Quick Start](#quick-start)
- [Configuration](#configuration)
- [Writing Tests](#writing-tests)
- [Execution](#execution)
- [Reporting](#reporting)
- [SOLID Principles](#solid-principles)
- [Best Practices](#best-practices)

## Features

- **Unified Framework**: Single framework for Web, Mobile, and API testing
- **Multi-Browser Support**: Chrome, Firefox, Edge, Safari
- **Mobile Platforms**: Android (UiAutomator2) and iOS (XCUITest)
- **Cloud Integration**: BrowserStack, Sauce Labs, Selenium Grid
- **Parallel Execution**: Thread-safe driver management with TestNG
- **Multi-Environment**: Dev, QA, Prod configuration profiles
- **Rich Reporting**: Extent Reports with screenshots
- **Retry Mechanism**: Automatic retry for flaky tests
- **Page Object Model**: Clean separation of test logic and page interactions
- **Service Layer Pattern**: Structured API testing with RestAssured

## Tech Stack

| Component | Technology | Version |
|-----------|------------|---------|
| Language | Java | 11+ |
| Build Tool | Maven | 3.8+ |
| Test Runner | TestNG | 7.8.0 |
| Web Automation | Selenium WebDriver | 4.15.0 |
| Mobile Automation | Appium Java Client | 9.0.0 |
| API Testing | RestAssured | 5.3.2 |
| Reporting | Extent Reports | 5.1.1 |
| Logging | Log4j2 | 2.21.1 |
| Assertions | AssertJ | 3.24.2 |

## Project Structure

```
automation-framework/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/automation/framework/
│   │   │   ├── config/                 # Configuration management
│   │   │   │   ├── ConfigurationManager.java
│   │   │   │   └── CapabilitiesManager.java
│   │   │   ├── core/
│   │   │   │   ├── base/               # Base test classes
│   │   │   │   │   ├── BaseTest.java
│   │   │   │   │   ├── BaseWebTest.java
│   │   │   │   │   ├── BaseMobileTest.java
│   │   │   │   │   └── BaseApiTest.java
│   │   │   │   ├── driver/             # Driver factories
│   │   │   │   │   ├── DriverManager.java
│   │   │   │   │   ├── WebDriverFactory.java
│   │   │   │   │   └── MobileDriverFactory.java
│   │   │   │   ├── enums/              # Enumerations
│   │   │   │   ├── exceptions/         # Custom exceptions
│   │   │   │   └── interfaces/         # Contracts/Interfaces
│   │   │   ├── pages/                  # Page Object Model
│   │   │   │   ├── BasePage.java
│   │   │   │   ├── BaseMobilePage.java
│   │   │   │   ├── web/                # Web page objects
│   │   │   │   └── mobile/             # Mobile screen objects
│   │   │   │       ├── android/
│   │   │   │       └── ios/
│   │   │   ├── services/api/           # API service layer
│   │   │   ├── reporting/              # Extent Reports
│   │   │   ├── listeners/              # TestNG listeners
│   │   │   └── utils/                  # Utilities
│   │   └── resources/
│   │       ├── config/                 # Property files
│   │       │   ├── framework.properties
│   │       │   ├── dev.properties
│   │       │   ├── qa.properties
│   │       │   └── prod.properties
│   │       ├── capabilities/           # Mobile capabilities
│   │       └── log4j2.xml
│   └── test/
│       ├── java/com/automation/framework/tests/
│       │   ├── web/                    # Web tests
│       │   ├── mobile/                 # Mobile tests
│       │   │   ├── android/
│       │   │   └── ios/
│       │   └── api/                    # API tests
│       └── resources/
│           └── testng/                 # TestNG suites
```

## Quick Start

### Prerequisites

- Java JDK 11 or higher
- Maven 3.8 or higher
- Chrome/Firefox browser (for web tests)
- Appium server (for mobile tests)
- Android SDK / Xcode (for mobile tests)

### Installation

```bash
# Clone the repository
git clone <repository-url>
cd automation-framework

# Install dependencies
mvn clean install -DskipTests

# Run tests
mvn test
```

### First Test Run

```bash
# Run smoke tests
mvn test -Dtestng.suite=src/test/resources/testng/testng-smoke.xml

# Run web tests only
mvn test -Pweb

# Run API tests only
mvn test -Papi
```

## Configuration

### Environment Configuration

The framework supports multiple environments. Set the environment using Maven profiles:

```bash
mvn test -Pdev    # Development environment
mvn test -Pqa     # QA environment (default)
mvn test -Pprod   # Production environment
```

Or via system property:

```bash
mvn test -Denvironment=qa
```

### Framework Properties

Edit `src/main/resources/config/framework.properties`:

```properties
# Execution Platform: local, grid, browserstack, saucelabs
execution.platform=local

# Browser Configuration
browser=chrome
browser.headless=false

# Timeouts (in seconds)
timeout.implicit=10
timeout.explicit=30
timeout.page.load=60

# Screenshots
screenshot.on.failure=true
screenshot.on.pass=false

# Retry Configuration
retry.count=2
```

### Environment-Specific Properties

Edit `src/main/resources/config/{env}.properties`:

```properties
# QA Environment (qa.properties)
web.base.url=https://qa.example.com
api.base.url=https://qa-api.example.com
```

### Mobile Capabilities

Edit JSON files in `src/main/resources/capabilities/`:

```json
// android-local.json
{
  "platformName": "Android",
  "appium:automationName": "UiAutomator2",
  "appium:deviceName": "Android Emulator",
  "appium:platformVersion": "13.0",
  "appium:app": "${android.app.path}"
}
```

## Writing Tests

### Web Test Example

```java
package com.automation.framework.tests.web;

import com.automation.framework.core.base.BaseWebTest;
import com.automation.framework.pages.web.LoginPage;
import com.automation.framework.pages.web.HomePage;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class LoginTest extends BaseWebTest {

    @Test(description = "Verify successful login", groups = {"smoke", "login"})
    public void testSuccessfulLogin() {
        // Initialize page object
        LoginPage loginPage = new LoginPage(getDriver());

        // Perform login using fluent API
        HomePage homePage = loginPage
            .enterUsername("testuser")
            .enterPassword("password123")
            .clickLoginButton();

        // Verify successful login
        assertThat(homePage.isPageLoaded())
            .as("Home page should be loaded after login")
            .isTrue();
    }

    @Test(description = "Verify login validation", groups = {"regression", "negative"})
    public void testLoginWithInvalidCredentials() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.loginExpectingFailure("invalid", "wrong");

        assertThat(loginPage.isErrorMessageDisplayed()).isTrue();
        assertThat(loginPage.getErrorMessage()).contains("Invalid credentials");
    }
}
```

### Creating a Page Object

```java
package com.automation.framework.pages.web;

import com.automation.framework.pages.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ProductPage extends BasePage {

    @FindBy(id = "product-title")
    private WebElement productTitle;

    @FindBy(id = "add-to-cart")
    private WebElement addToCartButton;

    @FindBy(css = ".price-value")
    private WebElement priceElement;

    public ProductPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageLoaded() {
        return isDisplayed(productTitle);
    }

    public String getProductTitle() {
        return getText(productTitle);
    }

    public String getPrice() {
        return getText(priceElement);
    }

    public CartPage addToCart() {
        click(addToCartButton);
        return new CartPage(driver);
    }

    public ProductPage waitForPriceUpdate() {
        waitForVisible(priceElement, getDefaultTimeout());
        return this;
    }
}
```

### Mobile Test Example (Android)

```java
package com.automation.framework.tests.mobile.android;

import com.automation.framework.core.base.BaseMobileTest;
import com.automation.framework.pages.mobile.android.AndroidLoginScreen;
import com.automation.framework.pages.mobile.android.AndroidHomeScreen;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class AndroidLoginTest extends BaseMobileTest {

    @Test(description = "Verify Android login", groups = {"smoke", "android"})
    public void testAndroidLogin() {
        AndroidLoginScreen loginScreen = new AndroidLoginScreen(getAndroidDriver());

        AndroidHomeScreen homeScreen = loginScreen
            .enterUsername("testuser")
            .enterPassword("password123")
            .tapLoginButton();

        assertThat(homeScreen.isPageLoaded()).isTrue();
        assertThat(homeScreen.getWelcomeText()).contains("Welcome");
    }
}
```

### Creating a Mobile Screen Object

```java
package com.automation.framework.pages.mobile.android;

import com.automation.framework.core.enums.PlatformType;
import com.automation.framework.pages.BaseMobilePage;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

public class ProductScreen extends BaseMobilePage {

    @AndroidFindBy(id = "com.example.app:id/product_name")
    private WebElement productName;

    @AndroidFindBy(id = "com.example.app:id/add_to_cart_btn")
    private WebElement addToCartButton;

    @AndroidFindBy(accessibility = "Product Image")
    private WebElement productImage;

    public ProductScreen(AppiumDriver driver) {
        super(driver, PlatformType.ANDROID);
    }

    @Override
    public boolean isPageLoaded() {
        return isElementVisible(productName);
    }

    public String getProductName() {
        return getText(productName);
    }

    public CartScreen addToCart() {
        click(addToCartButton);
        return new CartScreen(driver);
    }

    public ProductScreen swipeToNextImage() {
        swipeLeft();
        return this;
    }
}
```

### API Test Example

```java
package com.automation.framework.tests.api;

import com.automation.framework.core.base.BaseApiTest;
import com.automation.framework.services.api.UserService;
import com.automation.framework.services.api.AuthService;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

public class UserApiTest extends BaseApiTest {

    private UserService userService;
    private AuthService authService;

    @BeforeClass
    public void initServices() {
        userService = new UserService();
        authService = new AuthService();

        // Authenticate
        String token = authService.loginAndGetToken("apiuser", "password");
        userService.setAuthToken(token);
    }

    @Test(description = "Get all users", groups = {"smoke", "api"})
    public void testGetAllUsers() {
        Response response = userService.getAllUsers();

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getList("data")).isNotEmpty();
    }

    @Test(description = "Create new user", groups = {"regression", "api"})
    public void testCreateUser() {
        Map<String, Object> userData = Map.of(
            "firstName", "John",
            "lastName", "Doe",
            "email", "john.doe@example.com",
            "password", "securePassword123"
        );

        Response response = userService.createUser(userData);

        assertThat(response.getStatusCode()).isIn(200, 201);
        assertThat(response.jsonPath().getString("data.email"))
            .isEqualTo("john.doe@example.com");
    }

    @Test(description = "Get user by ID", groups = {"smoke", "api"})
    public void testGetUserById() {
        Response response = userService.getUserById(1);

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getInt("data.id")).isEqualTo(1);
    }
}
```

### Creating an API Service

```java
package com.automation.framework.services.api;

import io.restassured.response.Response;
import java.util.Map;

public class ProductService extends BaseApiService {

    private static final String BASE_PATH = "/products";

    @Override
    protected String getBasePath() {
        return BASE_PATH;
    }

    public Response getAllProducts() {
        return get(BASE_PATH);
    }

    public Response getProductById(int id) {
        return get(BASE_PATH + "/" + id);
    }

    public Response createProduct(Map<String, Object> productData) {
        return post(BASE_PATH, productData);
    }

    public Response updateProduct(int id, Map<String, Object> productData) {
        return put(BASE_PATH + "/" + id, productData);
    }

    public Response deleteProduct(int id) {
        return delete(BASE_PATH + "/" + id);
    }

    public Response searchProducts(String query, int page, int size) {
        Map<String, Object> params = Map.of(
            "q", query,
            "page", page,
            "size", size
        );
        return get(BASE_PATH + "/search", params);
    }
}
```

### Data-Driven Test Example

```java
@Test(dataProvider = "loginCredentials", groups = {"regression"})
public void testLoginWithMultipleUsers(String username, String password, boolean shouldPass) {
    LoginPage loginPage = new LoginPage(getDriver());

    if (shouldPass) {
        HomePage homePage = loginPage.login(username, password);
        assertThat(homePage.isPageLoaded()).isTrue();
    } else {
        loginPage.loginExpectingFailure(username, password);
        assertThat(loginPage.isErrorMessageDisplayed()).isTrue();
    }
}

@DataProvider(name = "loginCredentials")
public Object[][] loginCredentials() {
    return new Object[][] {
        {"validuser1", "validpass1", true},
        {"validuser2", "validpass2", true},
        {"invaliduser", "wrongpass", false},
        {"", "password", false},
        {"username", "", false}
    };
}
```

## Execution

### Maven Commands

```bash
# Run all tests
mvn test

# Run with specific TestNG suite
mvn test -Dtestng.suite=src/test/resources/testng/testng-web.xml

# Run specific test class
mvn test -Dtest=LoginTest

# Run specific test method
mvn test -Dtest=LoginTest#testSuccessfulLogin

# Run tests by group
mvn test -Dgroups=smoke

# Run with specific browser
mvn test -Dbrowser=firefox

# Run in headless mode
mvn test -Dbrowser.headless=true

# Run with parallel threads
mvn test -Dthread.count=8
```

### Profile-Based Execution

```bash
# Environment profiles
mvn test -Pdev              # Development
mvn test -Pqa               # QA (default)
mvn test -Pprod             # Production

# Test type profiles
mvn test -Pweb              # Web tests only
mvn test -Pmobile           # Mobile tests only
mvn test -Papi              # API tests only

# Cloud platform profiles
mvn test -Pbrowserstack     # Run on BrowserStack
mvn test -Psaucelabs        # Run on Sauce Labs
mvn test -Pgrid             # Run on Selenium Grid

# Combined profiles
mvn test -Pqa,web,browserstack
```

### Cross-Browser Testing

```bash
# Run on multiple browsers
mvn test -Dtestng.suite=src/test/resources/testng/testng-crossbrowser.xml
```

### Mobile Testing

```bash
# Start Appium server first
appium

# Run Android tests
mvn test -Pmobile -Dplatform=android

# Run iOS tests
mvn test -Pmobile -Dplatform=ios

# Run on BrowserStack
mvn test -Pmobile,browserstack
```

## Reporting

### Extent Reports

Reports are generated in `target/extent-reports/` after test execution.

```bash
# Open report after tests
open target/extent-reports/TestReport_*.html
```

### Adding Custom Logs to Reports

```java
import com.automation.framework.reporting.ExtentReportManager;

@Test
public void testWithCustomLogs() {
    ExtentReportManager.info("Starting test execution");

    // Test steps...
    ExtentReportManager.info("Navigated to login page");

    // On success
    ExtentReportManager.pass("Login successful");

    // Add categories
    ExtentReportManager.assignCategory("Smoke", "Login");

    // Add author
    ExtentReportManager.assignAuthor("QA Team");
}
```

### Screenshot Capture

Screenshots are automatically captured on test failure. Manual capture:

```java
import com.automation.framework.utils.ScreenshotUtils;

// Capture and save screenshot
String path = ScreenshotUtils.captureScreenshot(driver, "test_screenshot");

// Capture as Base64 for report
String base64 = ScreenshotUtils.captureBase64Screenshot(driver);
ExtentReportManager.addScreenshot(base64, "Step Screenshot");
```

## SOLID Principles

This framework demonstrates all five SOLID principles:

### Single Responsibility Principle (SRP)
Each class has one clear responsibility:
- `DriverManager` - Manages driver lifecycle
- `ConfigurationManager` - Handles configuration loading
- `ExtentReportManager` - Manages reporting

### Open/Closed Principle (OCP)
Classes are open for extension, closed for modification:
- `BasePage` can be extended for new page objects
- `BaseApiService` can be extended for new API services
- New browser types can be added to `WebDriverFactory`

### Liskov Substitution Principle (LSP)
Subtypes are substitutable for their base types:
- All page objects extend `BasePage` and can be used interchangeably
- All drivers implement `WebDriver` interface

### Interface Segregation Principle (ISP)
Clients depend only on interfaces they use:
- `Waitable` - Wait operations only
- `Scrollable` - Scroll operations only
- `Configurable` - Configuration operations only

### Dependency Inversion Principle (DIP)
High-level modules depend on abstractions:
- `DriverFactory` interface for driver creation
- `Configurable` interface for configuration

## Best Practices

### Test Organization
```java
@Test(
    description = "Clear description of what the test verifies",
    groups = {"smoke", "login"},           // Categorize tests
    retryAnalyzer = RetryAnalyzer.class,   // Handle flaky tests
    dependsOnMethods = "testPrerequisite"  // Define dependencies
)
public void testMethodName() {
    // Arrange
    LoginPage loginPage = new LoginPage(getDriver());

    // Act
    HomePage homePage = loginPage.login("user", "pass");

    // Assert
    assertThat(homePage.isPageLoaded()).isTrue();
}
```

### Page Object Guidelines
1. Use fluent API pattern for method chaining
2. Return page objects from action methods
3. Keep element locators private
4. Implement `isPageLoaded()` for verification
5. Use meaningful method names

### API Testing Guidelines
1. Use service classes for API operations
2. Separate authentication from business logic
3. Use data transfer objects (DTOs) for complex payloads
4. Verify both status codes and response body

### Configuration Guidelines
1. Never hardcode credentials
2. Use environment variables for sensitive data
3. Keep environment-specific config in separate files
4. Use property placeholders (`${VAR}`) for dynamic values

## Troubleshooting

### Common Issues

**Driver not found:**
```bash
# WebDriverManager handles driver downloads automatically
# If issues persist, check browser version compatibility
```

**Mobile tests failing:**
```bash
# Ensure Appium server is running
appium

# Check device/emulator is connected
adb devices          # Android
xcrun simctl list    # iOS
```

**Cloud platform connection issues:**
```bash
# Verify credentials in environment variables
export BROWSERSTACK_USERNAME=your_username
export BROWSERSTACK_ACCESS_KEY=your_key
```

## Contributing

1. Follow the existing code structure
2. Add tests for new functionality
3. Update documentation
4. Run all tests before submitting

## License

This project is licensed under the MIT License.
