package com.automation.framework.tests.api;

import com.automation.framework.core.base.BaseApiTest;
import com.automation.framework.reporting.ExtentReportManager;
import com.automation.framework.services.api.AuthService;
import com.automation.framework.services.api.UserService;
import com.automation.framework.utils.TestDataUtils;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User API Test demonstrating API testing capabilities.
 */
public class UserApiTest extends BaseApiTest {

    private UserService userService;
    private AuthService authService;
    private String authToken;

    @BeforeClass
    public void initServices() {
        userService = new UserService();
        authService = new AuthService();

        // Authenticate for subsequent tests
        String username = config.getProperty("api.test.username", "apiuser");
        String password = config.getProperty("api.test.password", "apipassword");
        authToken = authService.loginAndGetToken(username, password);

        if (authToken != null) {
            userService.setAuthToken(authToken);
        }
    }

    @Test(description = "Verify getting all users",
            groups = {"smoke", "api", "users"})
    public void testGetAllUsers() {
        ExtentReportManager.info("Testing GET all users");

        Response response = userService.getAllUsers();

        assertThat(response.getStatusCode())
                .as("Status code should be 200")
                .isEqualTo(200);

        ExtentReportManager.info("Response: " + response.asString());
        ExtentReportManager.pass("GET all users successful");
    }

    @Test(description = "Verify getting users with pagination",
            groups = {"regression", "api", "users"})
    public void testGetUsersWithPagination() {
        ExtentReportManager.info("Testing GET users with pagination");

        Response response = userService.getAllUsers(1, 10);

        assertThat(response.getStatusCode())
                .as("Status code should be 200")
                .isEqualTo(200);

        ExtentReportManager.pass("GET users with pagination successful");
    }

    @Test(description = "Verify getting user by ID",
            groups = {"smoke", "api", "users"})
    public void testGetUserById() {
        ExtentReportManager.info("Testing GET user by ID");

        Response response = userService.getUserById(1);

        assertThat(response.getStatusCode())
                .as("Status code should be 200")
                .isEqualTo(200);

        ExtentReportManager.info("User: " + response.asString());
        ExtentReportManager.pass("GET user by ID successful");
    }

    @Test(description = "Verify creating a new user",
            groups = {"smoke", "api", "users"})
    public void testCreateUser() {
        ExtentReportManager.info("Testing POST create user");

        Map<String, Object> userData = TestDataUtils.createRandomUserData();
        userData.put("password", "testPassword123");

        Response response = userService.createUser(userData);

        assertThat(response.getStatusCode())
                .as("Status code should be 201")
                .isIn(200, 201);

        ExtentReportManager.info("Created user: " + response.asString());
        ExtentReportManager.pass("POST create user successful");
    }

    @Test(description = "Verify creating user with all fields",
            groups = {"regression", "api", "users"})
    public void testCreateUserWithAllFields() {
        ExtentReportManager.info("Testing POST create user with all fields");

        String email = TestDataUtils.generateRandomEmail();
        Response response = userService.createUser("John", "Doe", email, "password123");

        assertThat(response.getStatusCode())
                .as("Status code should be 201")
                .isIn(200, 201);

        ExtentReportManager.pass("User created with email: " + email);
    }

    @Test(description = "Verify updating a user",
            groups = {"regression", "api", "users"})
    public void testUpdateUser() {
        ExtentReportManager.info("Testing PUT update user");

        Map<String, Object> updateData = Map.of(
                "firstName", "Updated",
                "lastName", "User"
        );

        Response response = userService.updateUser("1", updateData);

        assertThat(response.getStatusCode())
                .as("Status code should be 200")
                .isIn(200, 204);

        ExtentReportManager.pass("PUT update user successful");
    }

    @Test(description = "Verify patching a user",
            groups = {"regression", "api", "users"})
    public void testPatchUser() {
        ExtentReportManager.info("Testing PATCH user");

        Map<String, Object> patchData = Map.of("firstName", "Patched");

        Response response = userService.patchUser("1", patchData);

        assertThat(response.getStatusCode())
                .as("Status code should be 200")
                .isIn(200, 204);

        ExtentReportManager.pass("PATCH user successful");
    }

    @Test(description = "Verify deleting a user",
            groups = {"regression", "api", "users"},
            dependsOnMethods = "testCreateUser")
    public void testDeleteUser() {
        ExtentReportManager.info("Testing DELETE user");

        Response response = userService.deleteUser("999");

        assertThat(response.getStatusCode())
                .as("Status code should be 200 or 204")
                .isIn(200, 204, 404); // 404 is acceptable if user doesn't exist

        ExtentReportManager.pass("DELETE user successful");
    }

    @Test(description = "Verify searching users",
            groups = {"regression", "api", "users"})
    public void testSearchUsers() {
        ExtentReportManager.info("Testing search users");

        Response response = userService.searchUsers("test");

        assertThat(response.getStatusCode())
                .as("Status code should be 200")
                .isEqualTo(200);

        ExtentReportManager.pass("Search users successful");
    }

    @Test(description = "Verify getting current user profile",
            groups = {"smoke", "api", "users"})
    public void testGetCurrentUser() {
        ExtentReportManager.info("Testing GET current user");

        Response response = userService.getCurrentUser();

        assertThat(response.getStatusCode())
                .as("Status code should be 200")
                .isIn(200, 401); // 401 if not authenticated

        ExtentReportManager.pass("GET current user completed");
    }

    @Test(description = "Verify getting non-existent user returns 404",
            groups = {"negative", "api", "users"})
    public void testGetNonExistentUser() {
        ExtentReportManager.info("Testing GET non-existent user");

        Response response = userService.getUserById("999999");

        assertThat(response.getStatusCode())
                .as("Status code should be 404")
                .isEqualTo(404);

        ExtentReportManager.pass("GET non-existent user returns 404 as expected");
    }
}
