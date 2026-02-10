package com.spritecloud.services;

import com.spritecloud.models.api.AuthToken;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Service class for authentication operations.
 * Handles login and authentication token management.
 */
public class AuthService extends BaseApiService {

    /**
     * Performs user login and returns authentication token.
     *
     * @param username User's username
     * @param password User's password
     * @return Response containing auth token
     */
    public Response login(String username, String password) {
        logRequest("POST", AUTH_ENDPOINT);

        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("password", password);

        Response response = given()
                .spec(getRequestSpec())
                .body(credentials)
                .when()
                .post(AUTH_ENDPOINT);

        logResponse(response.getStatusCode(), AUTH_ENDPOINT);
        return response;
    }

    /**
     * Extracts and validates AuthToken from response.
     *
     * @param response API response
     * @return AuthToken object
     */
    public AuthToken extractAuthToken(Response response) {
        AuthToken authToken = response.as(AuthToken.class);
        logger.info("Auth token extracted successfully");
        return authToken;
    }

    /**
     * Performs login and returns token directly.
     *
     * @param username User's username
     * @param password User's password
     * @return AuthToken string
     */
    public String loginAndGetToken(String username, String password) {
        Response response = login(username, password);
        AuthToken authToken = extractAuthToken(response);

        if (!authToken.isValid()) {
            throw new IllegalStateException("Invalid authentication token received");
        }

        return authToken.getToken();
    }

    /**
     * Performs login with invalid credentials (for negative testing).
     *
     * @param username Invalid username
     * @param password Invalid password
     * @return Response with error details
     */
    public Response loginWithInvalidCredentials(String username, String password) {
        logRequest("POST", AUTH_ENDPOINT + " (Invalid Credentials)");

        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("password", password);

        Response response = given()
                .spec(getRequestSpec())
                .body(credentials)
                .when()
                .post(AUTH_ENDPOINT);

        logResponse(response.getStatusCode(), AUTH_ENDPOINT);
        return response;
    }
}
