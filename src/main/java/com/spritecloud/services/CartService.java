package com.spritecloud.services;

import com.spritecloud.models.api.Cart;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Service class for shopping cart operations.
 * Handles cart creation, retrieval, and management.
 */
public class CartService extends BaseApiService {

    /**
     * Creates a new cart with products.
     *
     * @param userId User ID
     * @param date Cart date
     * @param products List of products with quantities
     * @return Response containing created cart
     */
    public Response createCart(Integer userId, String date, List<Map<String, Integer>> products) {
        logRequest("POST", CARTS_ENDPOINT);

        Map<String, Object> cartData = new HashMap<>();
        cartData.put("userId", userId);
        cartData.put("date", date);
        cartData.put("products", products);

        Response response = given()
                .spec(getRequestSpec())
                .body(cartData)
                .when()
                .post(CARTS_ENDPOINT);

        logResponse(response.getStatusCode(), CARTS_ENDPOINT);
        return response;
    }

    /**
     * Retrieves all carts.
     *
     * @return Response containing list of carts
     */
    public Response getAllCarts() {
        logRequest("GET", CARTS_ENDPOINT);

        Response response = given()
                .spec(getRequestSpec())
                .when()
                .get(CARTS_ENDPOINT);

        logResponse(response.getStatusCode(), CARTS_ENDPOINT);
        return response;
    }

    /**
     * Retrieves a specific cart by ID.
     *
     * @param cartId Cart ID to retrieve
     * @return Response containing cart details
     */
    public Response getCartById(Integer cartId) {
        String endpoint = CARTS_ENDPOINT + "/" + cartId;
        logRequest("GET", endpoint);

        Response response = given()
                .spec(getRequestSpec())
                .when()
                .get(endpoint);

        logResponse(response.getStatusCode(), endpoint);
        return response;
    }

    /**
     * Retrieves carts for a specific user.
     *
     * @param userId User ID
     * @return Response containing user's carts
     */
    public Response getUserCarts(Integer userId) {
        String endpoint = CARTS_ENDPOINT + "/user/" + userId;
        logRequest("GET", endpoint);

        Response response = given()
                .spec(getRequestSpec())
                .when()
                .get(endpoint);

        logResponse(response.getStatusCode(), endpoint);
        return response;
    }

    /**
     * Updates an existing cart.
     *
     * @param cartId Cart ID to update
     * @param userId User ID
     * @param date Cart date
     * @param products List of products with quantities
     * @return Response containing updated cart
     */
    public Response updateCart(Integer cartId, Integer userId, String date,
                               List<Map<String, Integer>> products) {
        String endpoint = CARTS_ENDPOINT + "/" + cartId;
        logRequest("PUT", endpoint);

        Map<String, Object> cartData = new HashMap<>();
        cartData.put("userId", userId);
        cartData.put("date", date);
        cartData.put("products", products);

        Response response = given()
                .spec(getRequestSpec())
                .body(cartData)
                .when()
                .put(endpoint);

        logResponse(response.getStatusCode(), endpoint);
        return response;
    }

    /**
     * Deletes a cart.
     *
     * @param cartId Cart ID to delete
     * @return Response confirming deletion
     */
    public Response deleteCart(Integer cartId) {
        String endpoint = CARTS_ENDPOINT + "/" + cartId;
        logRequest("DELETE", endpoint);

        Response response = given()
                .spec(getRequestSpec())
                .when()
                .delete(endpoint);

        logResponse(response.getStatusCode(), endpoint);
        return response;
    }

    /**
     * Extracts Cart object from response.
     *
     * @param response API response
     * @return Cart object
     */
    public Cart extractCart(Response response) {
        Cart cart = response.as(Cart.class);
        logger.info("Cart extracted: {}", cart);
        return cart;
    }

    /**
     * Creates a cart with invalid data (negative test).
     *
     * @param invalidData Invalid cart data
     * @return Response with error details
     */
    public Response createCartWithInvalidData(Map<String, Object> invalidData) {
        logRequest("POST", CARTS_ENDPOINT + " (Invalid Data)");

        Response response = given()
                .spec(getRequestSpec())
                .body(invalidData)
                .when()
                .post(CARTS_ENDPOINT);

        logResponse(response.getStatusCode(), CARTS_ENDPOINT);
        return response;
    }
}
