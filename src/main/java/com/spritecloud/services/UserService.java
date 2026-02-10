package com.spritecloud.services;

import com.spritecloud.models.api.User;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Service class for user operations.
 * Handles user CRUD operations.
 */
public class UserService extends BaseApiService {

    /**
     * Retrieves all users.
     *
     * @return Response containing list of users
     */
    public Response getAllUsers() {
        logRequest("GET", USERS_ENDPOINT);

        Response response = given()
                .spec(getRequestSpec())
                .when()
                .get(USERS_ENDPOINT);

        logResponse(response.getStatusCode(), USERS_ENDPOINT);
        return response;
    }

    /**
     * Retrieves a specific user by ID.
     *
     * @param userId User ID to retrieve
     * @return Response containing user details
     */
    public Response getUserById(Integer userId) {
        String endpoint = USERS_ENDPOINT + "/" + userId;
        logRequest("GET", endpoint);

        Response response = given()
                .spec(getRequestSpec())
                .when()
                .get(endpoint);

        logResponse(response.getStatusCode(), endpoint);
        return response;
    }

    /**
     * Creates a new user.
     *
     * @param userData User data map
     * @return Response containing created user
     */
    public Response createUser(Map<String, Object> userData) {
        logRequest("POST", USERS_ENDPOINT);

        Response response = given()
                .spec(getRequestSpec())
                .body(userData)
                .when()
                .post(USERS_ENDPOINT);

        logResponse(response.getStatusCode(), USERS_ENDPOINT);
        return response;
    }

    /**
     * Updates an existing user.
     *
     * @param userId User ID to update
     * @param userData Updated user data
     * @return Response containing updated user
     */
    public Response updateUser(Integer userId, Map<String, Object> userData) {
        String endpoint = USERS_ENDPOINT + "/" + userId;
        logRequest("PUT", endpoint);

        Response response = given()
                .spec(getRequestSpec())
                .body(userData)
                .when()
                .put(endpoint);

        logResponse(response.getStatusCode(), endpoint);
        return response;
    }

    /**
     * Deletes a user.
     *
     * @param userId User ID to delete
     * @return Response confirming deletion
     */
    public Response deleteUser(Integer userId) {
        String endpoint = USERS_ENDPOINT + "/" + userId;
        logRequest("DELETE", endpoint);

        Response response = given()
                .spec(getRequestSpec())
                .when()
                .delete(endpoint);

        logResponse(response.getStatusCode(), endpoint);
        return response;
    }

    /**
     * Extracts User object from response.
     *
     * @param response API response
     * @return User object
     */
    public User extractUser(Response response) {
        User user = response.as(User.class);
        logger.info("User extracted: {}", user);
        return user;
    }

    /**
     * Extracts list of users from response.
     *
     * @param response API response
     * @return List of User objects
     */
    public List<User> extractUserList(Response response) {
        User[] usersArray = response.as(User[].class);
        List<User> users = Arrays.asList(usersArray);
        logger.info("Extracted {} users", users.size());
        return users;
    }

    /**
     * Retrieves users with pagination.
     *
     * @param limit Number of users to retrieve
     * @return Response containing limited users
     */
    public Response getUsersWithLimit(Integer limit) {
        logRequest("GET", USERS_ENDPOINT + "?limit=" + limit);

        Response response = given()
                .spec(getRequestSpec())
                .queryParam("limit", limit)
                .when()
                .get(USERS_ENDPOINT);

        logResponse(response.getStatusCode(), USERS_ENDPOINT);
        return response;
    }

    /**
     * Retrieves users sorted by specific order.
     *
     * @param sort Sort order (desc/asc)
     * @return Response containing sorted users
     */
    public Response getUsersSorted(String sort) {
        logRequest("GET", USERS_ENDPOINT + "?sort=" + sort);

        Response response = given()
                .spec(getRequestSpec())
                .queryParam("sort", sort)
                .when()
                .get(USERS_ENDPOINT);

        logResponse(response.getStatusCode(), USERS_ENDPOINT);
        return response;
    }

    /**
     * Attempts to delete non-existent user (negative test).
     *
     * @param nonExistentId Non-existent user ID
     * @return Response with operation result
     */
    public Response deleteNonExistentUser(Integer nonExistentId) {
        String endpoint = USERS_ENDPOINT + "/" + nonExistentId;
        logRequest("DELETE", endpoint + " (Non-existent)");

        Response response = given()
                .spec(getRequestSpec())
                .when()
                .delete(endpoint);

        logResponse(response.getStatusCode(), endpoint);
        return response;
    }

    /**
     * Attempts to get user with invalid ID format (negative test).
     *
     * @param invalidId Invalid user ID
     * @return Response with error details
     */
    public Response getUserWithInvalidId(String invalidId) {
        String endpoint = USERS_ENDPOINT + "/" + invalidId;
        logRequest("GET", endpoint + " (Invalid ID)");

        Response response = given()
                .spec(getRequestSpec())
                .when()
                .get(endpoint);

        logResponse(response.getStatusCode(), endpoint);
        return response;
    }
}
