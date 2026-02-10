package com.spritecloud.services;

import com.spritecloud.models.api.Product;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;

/**
 * Service class for product operations.
 * Handles all product-related API interactions.
 */
public class ProductService extends BaseApiService {

    /**
     * Retrieves all products.
     *
     * @return Response containing list of products
     */
    public Response getAllProducts() {
        logRequest("GET", PRODUCTS_ENDPOINT);

        Response response = given()
                .spec(getRequestSpec())
                .when()
                .get(PRODUCTS_ENDPOINT);

        logResponse(response.getStatusCode(), PRODUCTS_ENDPOINT);
        return response;
    }

    /**
     * Retrieves a specific product by ID.
     *
     * @param productId Product ID to retrieve
     * @return Response containing product details
     */
    public Response getProductById(Integer productId) {
        String endpoint = PRODUCTS_ENDPOINT + "/" + productId;
        logRequest("GET", endpoint);

        Response response = given()
                .spec(getRequestSpec())
                .when()
                .get(endpoint);

        logResponse(response.getStatusCode(), endpoint);
        return response;
    }

    /**
     * Extracts Product object from response with validation.
     *
     * @param response API response
     * @return Product object
     */
    public Product extractProduct(Response response) {
        Product product = response.as(Product.class);
        logger.info("Product extracted: {}", product);
        return product;
    }

    /**
     * Extracts list of products from response.
     *
     * @param response API response
     * @return List of Product objects
     */
    public List<Product> extractProductList(Response response) {
        Product[] productsArray = response.as(Product[].class);
        List<Product> products = Arrays.asList(productsArray);
        logger.info("Extracted {} products", products.size());
        return products;
    }

    /**
     * Retrieves products by category.
     *
     * @param category Product category
     * @return Response containing filtered products
     */
    public Response getProductsByCategory(String category) {
        String endpoint = PRODUCTS_ENDPOINT + "/category/" + category;
        logRequest("GET", endpoint);

        Response response = given()
                .spec(getRequestSpec())
                .when()
                .get(endpoint);

        logResponse(response.getStatusCode(), endpoint);
        return response;
    }

    /**
     * Retrieves all product categories.
     *
     * @return Response containing list of categories
     */
    public Response getAllCategories() {
        String endpoint = PRODUCTS_ENDPOINT + "/categories";
        logRequest("GET", endpoint);

        Response response = given()
                .spec(getRequestSpec())
                .when()
                .get(endpoint);

        logResponse(response.getStatusCode(), endpoint);
        return response;
    }

    /**
     * Attempts to retrieve product with invalid ID (negative test).
     *
     * @param invalidId Invalid product ID
     * @return Response with error details
     */
    public Response getProductWithInvalidId(String invalidId) {
        String endpoint = PRODUCTS_ENDPOINT + "/" + invalidId;
        logRequest("GET", endpoint + " (Invalid ID)");

        Response response = given()
                .spec(getRequestSpec())
                .when()
                .get(endpoint);

        logResponse(response.getStatusCode(), endpoint);
        return response;
    }

    /**
     * Attempts to retrieve non-existent product (negative test).
     *
     * @param nonExistentId Non-existent product ID
     * @return Response with error details
     */
    public Response getNonExistentProduct(Integer nonExistentId) {
        String endpoint = PRODUCTS_ENDPOINT + "/" + nonExistentId;
        logRequest("GET", endpoint + " (Non-existent)");

        Response response = given()
                .spec(getRequestSpec())
                .when()
                .get(endpoint);

        logResponse(response.getStatusCode(), endpoint);
        return response;
    }
}
