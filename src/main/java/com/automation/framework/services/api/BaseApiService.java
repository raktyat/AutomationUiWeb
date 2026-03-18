package com.automation.framework.services.api;

import com.automation.framework.config.ConfigurationManager;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Base API Service providing common REST operations.
 * Single Responsibility Principle (SRP): Handles HTTP operations only.
 * Open/Closed Principle (OCP): Subclasses extend without modifying this class.
 * Dependency Inversion Principle (DIP): Depends on abstractions (interfaces).
 */
@Slf4j
public abstract class BaseApiService {

    protected final ConfigurationManager config;
    protected final RequestSpecification requestSpec;
    protected String authToken;

    protected BaseApiService() {
        this.config = ConfigurationManager.getInstance();
        this.requestSpec = buildBaseRequestSpec();
    }

    /**
     * Builds the base request specification.
     *
     * @return The base RequestSpecification
     */
    private RequestSpecification buildBaseRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(config.getProperty("api.base.url"))
                .setBasePath(config.getProperty("api.version", "v1"))
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }

    /**
     * Sets the authentication token.
     *
     * @param token The auth token
     */
    public void setAuthToken(String token) {
        this.authToken = token;
    }

    /**
     * Gets a request specification with authentication if token is set.
     *
     * @return The RequestSpecification
     */
    protected RequestSpecification getRequest() {
        RequestSpecification spec = RestAssured.given().spec(requestSpec);
        if (authToken != null && !authToken.isEmpty()) {
            spec.header("Authorization", "Bearer " + authToken);
        }
        return spec;
    }

    // ==================== HTTP Methods ====================

    /**
     * Performs a GET request.
     *
     * @param endpoint The API endpoint
     * @return The Response
     */
    protected Response get(String endpoint) {
        log.debug("GET {}", endpoint);
        return getRequest().get(endpoint);
    }

    /**
     * Performs a GET request with query parameters.
     *
     * @param endpoint The API endpoint
     * @param queryParams The query parameters
     * @return The Response
     */
    protected Response get(String endpoint, Map<String, ?> queryParams) {
        log.debug("GET {} with params: {}", endpoint, queryParams);
        return getRequest().queryParams(queryParams).get(endpoint);
    }

    /**
     * Performs a GET request with path parameters.
     *
     * @param endpoint The API endpoint with placeholders
     * @param pathParams The path parameters
     * @return The Response
     */
    protected Response getWithPathParams(String endpoint, Map<String, ?> pathParams) {
        log.debug("GET {} with path params: {}", endpoint, pathParams);
        return getRequest().pathParams(pathParams).get(endpoint);
    }

    /**
     * Performs a POST request.
     *
     * @param endpoint The API endpoint
     * @param body The request body
     * @return The Response
     */
    protected Response post(String endpoint, Object body) {
        log.debug("POST {} with body: {}", endpoint, body);
        return getRequest().body(body).post(endpoint);
    }

    /**
     * Performs a POST request without body.
     *
     * @param endpoint The API endpoint
     * @return The Response
     */
    protected Response post(String endpoint) {
        log.debug("POST {}", endpoint);
        return getRequest().post(endpoint);
    }

    /**
     * Performs a PUT request.
     *
     * @param endpoint The API endpoint
     * @param body The request body
     * @return The Response
     */
    protected Response put(String endpoint, Object body) {
        log.debug("PUT {} with body: {}", endpoint, body);
        return getRequest().body(body).put(endpoint);
    }

    /**
     * Performs a PATCH request.
     *
     * @param endpoint The API endpoint
     * @param body The request body
     * @return The Response
     */
    protected Response patch(String endpoint, Object body) {
        log.debug("PATCH {} with body: {}", endpoint, body);
        return getRequest().body(body).patch(endpoint);
    }

    /**
     * Performs a DELETE request.
     *
     * @param endpoint The API endpoint
     * @return The Response
     */
    protected Response delete(String endpoint) {
        log.debug("DELETE {}", endpoint);
        return getRequest().delete(endpoint);
    }

    /**
     * Performs a DELETE request with body.
     *
     * @param endpoint The API endpoint
     * @param body The request body
     * @return The Response
     */
    protected Response delete(String endpoint, Object body) {
        log.debug("DELETE {} with body: {}", endpoint, body);
        return getRequest().body(body).delete(endpoint);
    }

    // ==================== Response Helpers ====================

    /**
     * Extracts a value from the response using JSONPath.
     *
     * @param response The Response
     * @param jsonPath The JSONPath expression
     * @param <T> The expected type
     * @return The extracted value
     */
    protected <T> T extractFromResponse(Response response, String jsonPath) {
        return response.jsonPath().get(jsonPath);
    }

    /**
     * Extracts the response body as a specific class.
     *
     * @param response The Response
     * @param clazz The target class
     * @param <T> The type
     * @return The deserialized object
     */
    protected <T> T extractAs(Response response, Class<T> clazz) {
        return response.as(clazz);
    }

    /**
     * Gets the endpoint path. Subclasses should define their base path.
     *
     * @return The base endpoint path
     */
    protected abstract String getBasePath();
}
