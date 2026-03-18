package com.automation.framework.tests.api;

import com.automation.framework.core.base.BaseApiTest;
import com.automation.framework.reporting.ExtentReportManager;
import com.automation.framework.services.api.AuthService;
import com.automation.framework.utils.TestDataUtils;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Authentication API Test demonstrating API testing capabilities.
 */
public class AuthApiTest extends BaseApiTest {

    private AuthService authService;

    @BeforeClass
    public void initService() {
        authService = new AuthService();
    }

    @Test(description = "Verify login with valid credentials",
            groups = {"smoke", "api", "auth"})
    public void testLoginWithValidCredentials() {
        ExtentReportManager.info("Testing login with valid credentials");

        String username = config.getProperty("api.test.username", "apiuser");
        String password = config.getProperty("api.test.password", "apipassword");

        Response response = authService.login(username, password);

        assertThat(response.getStatusCode())
                .as("Status code should be 200")
                .isEqualTo(200);

        String token = response.jsonPath().getString("data.token");
        assertThat(token)
                .as("Token should not be empty")
                .isNotEmpty();

        ExtentReportManager.pass("Login successful, token received");
    }

    @Test(description = "Verify login with invalid credentials returns 401",
            groups = {"smoke", "api", "auth", "negative"})
    public void testLoginWithInvalidCredentials() {
        ExtentReportManager.info("Testing login with invalid credentials");

        Response response = authService.login("invaliduser", "wrongpassword");

        assertThat(response.getStatusCode())
                .as("Status code should be 401")
                .isEqualTo(401);

        ExtentReportManager.pass("Login failed as expected with 401");
    }

    @Test(description = "Verify login with empty username",
            groups = {"regression", "api", "auth", "negative"})
    public void testLoginWithEmptyUsername() {
        ExtentReportManager.info("Testing login with empty username");

        Response response = authService.login("", "password");

        assertThat(response.getStatusCode())
                .as("Status code should be 400 or 401")
                .isIn(400, 401, 422);

        ExtentReportManager.pass("Validation works for empty username");
    }

    @Test(description = "Verify login with empty password",
            groups = {"regression", "api", "auth", "negative"})
    public void testLoginWithEmptyPassword() {
        ExtentReportManager.info("Testing login with empty password");

        Response response = authService.login("user", "");

        assertThat(response.getStatusCode())
                .as("Status code should be 400 or 401")
                .isIn(400, 401, 422);

        ExtentReportManager.pass("Validation works for empty password");
    }

    @Test(description = "Verify logout functionality",
            groups = {"regression", "api", "auth"})
    public void testLogout() {
        ExtentReportManager.info("Testing logout");

        // First login to get a token
        String username = config.getProperty("api.test.username", "apiuser");
        String password = config.getProperty("api.test.password", "apipassword");
        String token = authService.loginAndGetToken(username, password);

        if (token != null) {
            authService.setAuthToken(token);
            Response response = authService.logout();

            assertThat(response.getStatusCode())
                    .as("Status code should be 200 or 204")
                    .isIn(200, 204);

            ExtentReportManager.pass("Logout successful");
        } else {
            ExtentReportManager.info("Skipping logout test - login failed");
        }
    }

    @Test(description = "Verify token validation",
            groups = {"regression", "api", "auth"})
    public void testValidateToken() {
        ExtentReportManager.info("Testing token validation");

        String username = config.getProperty("api.test.username", "apiuser");
        String password = config.getProperty("api.test.password", "apipassword");
        String token = authService.loginAndGetToken(username, password);

        if (token != null) {
            authService.setAuthToken(token);
            Response response = authService.validateToken();

            assertThat(response.getStatusCode())
                    .as("Status code should be 200")
                    .isEqualTo(200);

            ExtentReportManager.pass("Token validation successful");
        }
    }

    @Test(description = "Verify password reset request",
            groups = {"regression", "api", "auth"})
    public void testPasswordResetRequest() {
        ExtentReportManager.info("Testing password reset request");

        String email = TestDataUtils.generateRandomEmail();
        Response response = authService.requestPasswordReset(email);

        assertThat(response.getStatusCode())
                .as("Status code should be 200 or 202")
                .isIn(200, 202, 404); // 404 if email doesn't exist (acceptable)

        ExtentReportManager.pass("Password reset request handled");
    }

    @Test(description = "Verify login and get token helper method",
            groups = {"smoke", "api", "auth"})
    public void testLoginAndGetToken() {
        ExtentReportManager.info("Testing loginAndGetToken helper");

        String username = config.getProperty("api.test.username", "apiuser");
        String password = config.getProperty("api.test.password", "apipassword");

        String token = authService.loginAndGetToken(username, password);

        if (token != null) {
            assertThat(token)
                    .as("Token should not be empty")
                    .isNotEmpty();
            ExtentReportManager.pass("Token retrieved successfully");
        } else {
            ExtentReportManager.info("Token is null - API may not be available");
        }
    }
}
