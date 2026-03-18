package com.automation.framework.services.api;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * User Service for handling user-related API operations.
 * Single Responsibility: Only handles user CRUD operations.
 */
@Slf4j
public class UserService extends BaseApiService {

    private static final String BASE_PATH = "/users";

    @Override
    protected String getBasePath() {
        return BASE_PATH;
    }

    /**
     * Gets all users.
     *
     * @return The Response containing user list
     */
    public Response getAllUsers() {
        return get(BASE_PATH);
    }

    /**
     * Gets all users with pagination.
     *
     * @param page The page number
     * @param size The page size
     * @return The Response containing paginated user list
     */
    public Response getAllUsers(int page, int size) {
        Map<String, Integer> params = new HashMap<>();
        params.put("page", page);
        params.put("size", size);
        return get(BASE_PATH, params);
    }

    /**
     * Gets a user by ID.
     *
     * @param userId The user ID
     * @return The Response containing user details
     */
    public Response getUserById(String userId) {
        return get(BASE_PATH + "/" + userId);
    }

    /**
     * Gets a user by ID.
     *
     * @param userId The user ID
     * @return The Response containing user details
     */
    public Response getUserById(int userId) {
        return getUserById(String.valueOf(userId));
    }

    /**
     * Creates a new user.
     *
     * @param userData The user data map
     * @return The Response containing created user
     */
    public Response createUser(Map<String, Object> userData) {
        log.info("Creating user: {}", userData.get("email"));
        return post(BASE_PATH, userData);
    }

    /**
     * Creates a new user.
     *
     * @param firstName First name
     * @param lastName Last name
     * @param email Email address
     * @param password Password
     * @return The Response containing created user
     */
    public Response createUser(String firstName, String lastName, String email, String password) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("firstName", firstName);
        userData.put("lastName", lastName);
        userData.put("email", email);
        userData.put("password", password);
        return createUser(userData);
    }

    /**
     * Updates a user.
     *
     * @param userId The user ID
     * @param userData The user data to update
     * @return The Response containing updated user
     */
    public Response updateUser(String userId, Map<String, Object> userData) {
        log.info("Updating user: {}", userId);
        return put(BASE_PATH + "/" + userId, userData);
    }

    /**
     * Partially updates a user.
     *
     * @param userId The user ID
     * @param userData The user data to update
     * @return The Response containing updated user
     */
    public Response patchUser(String userId, Map<String, Object> userData) {
        log.info("Patching user: {}", userId);
        return patch(BASE_PATH + "/" + userId, userData);
    }

    /**
     * Deletes a user.
     *
     * @param userId The user ID
     * @return The Response
     */
    public Response deleteUser(String userId) {
        log.info("Deleting user: {}", userId);
        return delete(BASE_PATH + "/" + userId);
    }

    /**
     * Deletes a user.
     *
     * @param userId The user ID
     * @return The Response
     */
    public Response deleteUser(int userId) {
        return deleteUser(String.valueOf(userId));
    }

    /**
     * Searches for users.
     *
     * @param query The search query
     * @return The Response containing matching users
     */
    public Response searchUsers(String query) {
        Map<String, String> params = new HashMap<>();
        params.put("q", query);
        return get(BASE_PATH + "/search", params);
    }

    /**
     * Gets the current user's profile.
     *
     * @return The Response containing current user details
     */
    public Response getCurrentUser() {
        return get(BASE_PATH + "/me");
    }

    /**
     * Updates the current user's profile.
     *
     * @param profileData The profile data to update
     * @return The Response containing updated profile
     */
    public Response updateCurrentUser(Map<String, Object> profileData) {
        return put(BASE_PATH + "/me", profileData);
    }

    /**
     * Changes the current user's password.
     *
     * @param currentPassword The current password
     * @param newPassword The new password
     * @return The Response
     */
    public Response changePassword(String currentPassword, String newPassword) {
        Map<String, String> body = new HashMap<>();
        body.put("currentPassword", currentPassword);
        body.put("newPassword", newPassword);
        return post(BASE_PATH + "/me/password", body);
    }
}
