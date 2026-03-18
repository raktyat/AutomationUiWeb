package com.automation.framework.services.api;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Authentication Service for handling auth-related API operations.
 * Single Responsibility: Only handles authentication operations.
 */
@Slf4j
public class AuthService extends BaseApiService {

    private static final String BASE_PATH = "/auth";

    @Override
    protected String getBasePath() {
        return BASE_PATH;
    }

    /**
     * Performs user login.
     *
     * @param username The username
     * @param password The password
     * @return The Response containing auth token
     */
    public Response login(String username, String password) {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("password", password);

        Response response = post(BASE_PATH + "/login", credentials);
        log.info("Login attempt for user: {}, status: {}", username, response.getStatusCode());
        return response;
    }

    /**
     * Performs user logout.
     *
     * @return The Response
     */
    public Response logout() {
        Response response = post(BASE_PATH + "/logout");
        log.info("Logout performed, status: {}", response.getStatusCode());
        return response;
    }

    /**
     * Refreshes the authentication token.
     *
     * @param refreshToken The refresh token
     * @return The Response containing new tokens
     */
    public Response refreshToken(String refreshToken) {
        Map<String, String> body = new HashMap<>();
        body.put("refreshToken", refreshToken);

        return post(BASE_PATH + "/refresh", body);
    }

    /**
     * Requests password reset.
     *
     * @param email The user's email
     * @return The Response
     */
    public Response requestPasswordReset(String email) {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);

        return post(BASE_PATH + "/password-reset", body);
    }

    /**
     * Resets password with token.
     *
     * @param token The reset token
     * @param newPassword The new password
     * @return The Response
     */
    public Response resetPassword(String token, String newPassword) {
        Map<String, String> body = new HashMap<>();
        body.put("token", token);
        body.put("newPassword", newPassword);

        return post(BASE_PATH + "/password-reset/confirm", body);
    }

    /**
     * Validates the current token.
     *
     * @return The Response with token validity info
     */
    public Response validateToken() {
        return get(BASE_PATH + "/validate");
    }

    /**
     * Performs login and extracts the auth token.
     *
     * @param username The username
     * @param password The password
     * @return The auth token or null if login failed
     */
    public String loginAndGetToken(String username, String password) {
        Response response = login(username, password);
        if (response.getStatusCode() == 200) {
            String token = extractFromResponse(response, "data.token");
            setAuthToken(token);
            return token;
        }
        return null;
    }
}
