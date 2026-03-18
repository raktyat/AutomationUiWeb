package com.automation.framework.tests.api;

import com.automation.framework.core.base.BaseApiTest;
import com.automation.framework.reporting.ExtentReportManager;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Example API Test using JSONPlaceholder (free fake API for testing).
 * URL: https://jsonplaceholder.typicode.com
 *
 * This demonstrates the API testing workflow:
 * 1. Setup - Configure base URL
 * 2. Execute - Make HTTP requests
 * 3. Validate - Assert response status and body
 */
public class JsonPlaceholderTest extends BaseApiTest {

    @BeforeClass
    public void setupApi() {
        // Override base URL for this test (using free public API)
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
        RestAssured.basePath = "";  // No version path for this API
    }

    // ===================== GET REQUESTS =====================

    @Test(description = "GET all posts - Basic GET request example",
            groups = {"smoke", "api", "demo"})
    public void testGetAllPosts() {
        ExtentReportManager.info("Step 1: Send GET request to /posts");

        // Execute GET request
        Response response = RestAssured
                .given()
                    .log().uri()           // Log the request URI
                .when()
                    .get("/posts")         // Endpoint
                .then()
                    .log().status()        // Log response status
                    .extract().response();

        // Validate response
        ExtentReportManager.info("Step 2: Validate response");

        // Check status code
        assertThat(response.getStatusCode())
                .as("Status code should be 200")
                .isEqualTo(200);

        // Check response is not empty
        List<Map<String, Object>> posts = response.jsonPath().getList("$");
        assertThat(posts)
                .as("Posts list should not be empty")
                .isNotEmpty();

        // Check we got 100 posts (JSONPlaceholder has 100 posts)
        assertThat(posts.size())
                .as("Should have 100 posts")
                .isEqualTo(100);

        ExtentReportManager.pass("GET all posts successful - Found " + posts.size() + " posts");
    }

    @Test(description = "GET single post by ID",
            groups = {"smoke", "api", "demo"})
    public void testGetPostById() {
        int postId = 1;
        ExtentReportManager.info("Getting post with ID: " + postId);

        Response response = RestAssured
                .given()
                .when()
                    .get("/posts/" + postId)
                .then()
                    .extract().response();

        // Validate
        assertThat(response.getStatusCode()).isEqualTo(200);

        // Extract and validate fields
        int id = response.jsonPath().getInt("id");
        String title = response.jsonPath().getString("title");
        int userId = response.jsonPath().getInt("userId");

        assertThat(id).isEqualTo(postId);
        assertThat(title).isNotEmpty();
        assertThat(userId).isGreaterThan(0);

        ExtentReportManager.info("Post title: " + title);
        ExtentReportManager.pass("GET post by ID successful");
    }

    @Test(description = "GET posts with query parameters",
            groups = {"regression", "api", "demo"})
    public void testGetPostsByUserId() {
        int userId = 1;
        ExtentReportManager.info("Getting posts for user: " + userId);

        Response response = RestAssured
                .given()
                    .queryParam("userId", userId)  // Add query parameter
                .when()
                    .get("/posts")
                .then()
                    .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(200);

        // All posts should belong to userId 1
        List<Integer> userIds = response.jsonPath().getList("userId");
        assertThat(userIds)
                .as("All posts should belong to user " + userId)
                .allMatch(id -> id == userId);

        ExtentReportManager.pass("Found " + userIds.size() + " posts for user " + userId);
    }

    // ===================== POST REQUESTS =====================

    @Test(description = "POST create new post",
            groups = {"smoke", "api", "demo"})
    public void testCreatePost() {
        ExtentReportManager.info("Creating a new post");

        // Prepare request body
        Map<String, Object> postData = new HashMap<>();
        postData.put("title", "Test Post Title");
        postData.put("body", "This is the test post body content");
        postData.put("userId", 1);

        Response response = RestAssured
                .given()
                    .contentType("application/json")
                    .body(postData)
                    .log().body()  // Log request body
                .when()
                    .post("/posts")
                .then()
                    .log().body()  // Log response body
                    .extract().response();

        // Validate - JSONPlaceholder returns 201 for created
        assertThat(response.getStatusCode())
                .as("Status should be 201 Created")
                .isEqualTo(201);

        // Validate response contains our data
        assertThat(response.jsonPath().getString("title"))
                .isEqualTo("Test Post Title");
        assertThat(response.jsonPath().getInt("id"))
                .as("New post should have an ID")
                .isGreaterThan(0);

        ExtentReportManager.pass("POST create successful - New ID: " + response.jsonPath().getInt("id"));
    }

    // ===================== PUT REQUESTS =====================

    @Test(description = "PUT update entire post",
            groups = {"regression", "api", "demo"})
    public void testUpdatePost() {
        int postId = 1;
        ExtentReportManager.info("Updating post: " + postId);

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("id", postId);
        updatedData.put("title", "Updated Title");
        updatedData.put("body", "Updated body content");
        updatedData.put("userId", 1);

        Response response = RestAssured
                .given()
                    .contentType("application/json")
                    .body(updatedData)
                .when()
                    .put("/posts/" + postId)
                .then()
                    .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("title")).isEqualTo("Updated Title");

        ExtentReportManager.pass("PUT update successful");
    }

    // ===================== PATCH REQUESTS =====================

    @Test(description = "PATCH partial update post",
            groups = {"regression", "api", "demo"})
    public void testPatchPost() {
        int postId = 1;
        ExtentReportManager.info("Patching post: " + postId);

        // Only update the title
        Map<String, Object> patchData = new HashMap<>();
        patchData.put("title", "Patched Title Only");

        Response response = RestAssured
                .given()
                    .contentType("application/json")
                    .body(patchData)
                .when()
                    .patch("/posts/" + postId)
                .then()
                    .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("title")).isEqualTo("Patched Title Only");
        // Body should remain unchanged
        assertThat(response.jsonPath().getString("body")).isNotEmpty();

        ExtentReportManager.pass("PATCH partial update successful");
    }

    // ===================== DELETE REQUESTS =====================

    @Test(description = "DELETE remove post",
            groups = {"regression", "api", "demo"})
    public void testDeletePost() {
        int postId = 1;
        ExtentReportManager.info("Deleting post: " + postId);

        Response response = RestAssured
                .given()
                .when()
                    .delete("/posts/" + postId)
                .then()
                    .extract().response();

        // JSONPlaceholder returns 200 for delete
        assertThat(response.getStatusCode()).isEqualTo(200);

        ExtentReportManager.pass("DELETE successful");
    }

    // ===================== NEGATIVE TESTS =====================

    @Test(description = "GET non-existent post returns 404",
            groups = {"negative", "api", "demo"})
    public void testGetNonExistentPost() {
        int invalidId = 99999;
        ExtentReportManager.info("Testing 404 for invalid ID: " + invalidId);

        Response response = RestAssured
                .given()
                .when()
                    .get("/posts/" + invalidId)
                .then()
                    .extract().response();

        assertThat(response.getStatusCode())
                .as("Should return 404 for non-existent resource")
                .isEqualTo(404);

        ExtentReportManager.pass("404 returned as expected");
    }

    // ===================== RESPONSE VALIDATION EXAMPLES =====================

    @Test(description = "Validate response structure",
            groups = {"regression", "api", "demo"})
    public void testResponseStructure() {
        ExtentReportManager.info("Validating post response structure");

        Response response = RestAssured
                .given()
                .when()
                    .get("/posts/1")
                .then()
                    .extract().response();

        // Validate all expected fields exist
        assertThat(response.jsonPath().getInt("id")).isNotNull();
        assertThat(response.jsonPath().getString("title")).isNotNull();
        assertThat(response.jsonPath().getString("body")).isNotNull();
        assertThat(response.jsonPath().getInt("userId")).isNotNull();

        // Validate data types
        assertThat(response.jsonPath().getInt("id")).isInstanceOf(Integer.class);
        assertThat(response.jsonPath().getString("title")).isInstanceOf(String.class);

        ExtentReportManager.pass("Response structure validated");
    }

    @Test(description = "Validate response headers",
            groups = {"regression", "api", "demo"})
    public void testResponseHeaders() {
        ExtentReportManager.info("Validating response headers");

        Response response = RestAssured
                .given()
                .when()
                    .get("/posts/1")
                .then()
                    .extract().response();

        // Check content type
        String contentType = response.getContentType();
        assertThat(contentType)
                .as("Content-Type should be JSON")
                .contains("application/json");

        // Check response time
        long responseTime = response.getTime();
        assertThat(responseTime)
                .as("Response time should be under 5 seconds")
                .isLessThan(5000);

        ExtentReportManager.info("Response time: " + responseTime + "ms");
        ExtentReportManager.pass("Headers validated");
    }
}
