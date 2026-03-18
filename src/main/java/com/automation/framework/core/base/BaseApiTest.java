package com.automation.framework.core.base;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * Base class for API tests.
 * Provides RestAssured setup and common configurations.
 */
@Slf4j
public abstract class BaseApiTest extends BaseTest {

    protected RequestSpecification requestSpec;
    protected ResponseSpecification responseSpec;

    @BeforeClass(alwaysRun = true)
    public void setUpApiTest() {
        configureRestAssured();
        requestSpec = buildRequestSpec();
        responseSpec = buildResponseSpec();
        log.info("Configured RestAssured for API tests");
    }

    @BeforeMethod(alwaysRun = true)
    public void logTestStart(Method method) {
        log.info("Starting API test: {}", method.getName());
    }

    /**
     * Configures RestAssured global settings.
     */
    private void configureRestAssured() {
        RestAssured.baseURI = getApiBaseUrl();
        RestAssured.basePath = config.getProperty("api.version", "v1");
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        if (config.getBooleanProperty("api.log.request")) {
            RestAssured.filters(new io.restassured.filter.log.RequestLoggingFilter());
        }
        if (config.getBooleanProperty("api.log.response")) {
            RestAssured.filters(new io.restassured.filter.log.ResponseLoggingFilter());
        }
    }

    /**
     * Builds the default request specification.
     *
     * @return The RequestSpecification
     */
    protected RequestSpecification buildRequestSpec() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();
    }

    /**
     * Builds the default response specification.
     *
     * @return The ResponseSpecification
     */
    protected ResponseSpecification buildResponseSpec() {
        return new ResponseSpecBuilder()
                .expectResponseTime(lessThanOrEqualTo(
                        config.getIntProperty("api.connection.timeout"),
                        TimeUnit.MILLISECONDS))
                .build();
    }

    private org.hamcrest.Matcher<Long> lessThanOrEqualTo(long value, TimeUnit unit) {
        return org.hamcrest.Matchers.lessThanOrEqualTo(value);
    }

    /**
     * Gets a RequestSpecification with authentication.
     *
     * @param token The auth token
     * @return The authenticated RequestSpecification
     */
    protected RequestSpecification authenticatedRequest(String token) {
        return RestAssured.given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token);
    }

    /**
     * Gets a RequestSpecification with basic authentication.
     *
     * @param username The username
     * @param password The password
     * @return The authenticated RequestSpecification
     */
    protected RequestSpecification basicAuthRequest(String username, String password) {
        return RestAssured.given()
                .spec(requestSpec)
                .auth().basic(username, password);
    }
}
