package com.spritecloud.utils;

import com.spritecloud.models.api.Cart;
import com.spritecloud.models.api.Product;
import com.spritecloud.models.api.User;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Utility class for API and data validation assertions.
 * Provides reusable assertion methods for common validation scenarios.
 *
 * <p>Key Features:
 * <ul>
 *   <li>HTTP response validation (status codes, content types, response times)</li>
 *   <li>JSON schema validation</li>
 *   <li>Business rule validation</li>
 * </ul>
 */
public class AssertionUtils {

    private static final Logger logger = LoggerFactory.getLogger(AssertionUtils.class);

    /**
     * Validates that the HTTP response has the expected status code.
     *
     * @param response the API response to validate
     * @param expectedStatusCode the expected HTTP status code
     */
    public static void assertStatusCode(Response response, int expectedStatusCode) {
        int actualStatusCode = response.getStatusCode();
        logger.info("Validating status code - Expected: {}, Actual: {}",
                expectedStatusCode, actualStatusCode);

        assertThat(actualStatusCode)
                .as("HTTP Status Code")
                .isEqualTo(expectedStatusCode);
    }

    /**
     * Validates that the response Content-Type header contains the expected value.
     *
     * @param response the API response to validate
     * @param expectedContentType the expected content type (e.g., "application/json")
     */
    public static void assertContentType(Response response, String expectedContentType) {
        String actualContentType = response.getContentType();
        logger.info("Validating content type - Expected: {}, Actual: {}",
                expectedContentType, actualContentType);

        assertThat(actualContentType)
                .as("Content-Type header")
                .contains(expectedContentType);
    }

    /**
     * Validates that the API response time is within acceptable limits.
     *
     * @param response the API response to validate
     * @param maxResponseTimeMs maximum acceptable response time in milliseconds
     */
    public static void assertResponseTime(Response response, long maxResponseTimeMs) {
        long responseTime = response.getTime();
        logger.info("Validating response time - Actual: {}ms, Max: {}ms",
                responseTime, maxResponseTimeMs);

        assertThat(responseTime)
                .as("Response time")
                .isLessThanOrEqualTo(maxResponseTimeMs);
    }

    /**
     * Performs deep validation on a Product object.
     * Validates all critical fields including ID, title, price, category, image, and optional rating.
     *
     * @param product the Product object to validate
     */
    public static void assertProductIsValid(Product product) {
        logger.info("Performing deep validation on Product: {}", product.getId());

        assertThat(product)
                .as("Product object")
                .isNotNull();

        assertThat(product.getId())
                .as("Product ID")
                .isNotNull()
                .isPositive();

        assertThat(product.getTitle())
                .as("Product title")
                .isNotNull()
                .isNotEmpty()
                .hasSizeGreaterThan(3);

        assertThat(product.getPrice())
                .as("Product price")
                .isNotNull()
                .isGreaterThanOrEqualTo(0.0)
                .isLessThan(1000000.0);

        assertThat(product.getCategory())
                .as("Product category")
                .isNotNull()
                .isNotEmpty();

        assertThat(product.getImage())
                .as("Product image URL")
                .isNotNull()
                .startsWith("http");

        if (product.getRating() != null) {
            assertThat(product.getRating().getRate())
                    .as("Product rating")
                    .isGreaterThanOrEqualTo(0.0)
                    .isLessThanOrEqualTo(5.0);

            assertThat(product.getRating().getCount())
                    .as("Rating count")
                    .isGreaterThanOrEqualTo(0);
        }

        logger.info("Product validation passed for ID: {}", product.getId());
    }

    /**
     * Performs deep validation on a User object.
     * Validates all critical fields including ID, email, username, and optional name fields.
     *
     * @param user the User object to validate
     */
    public static void assertUserIsValid(User user) {
        logger.info("Performing deep validation on User: {}", user.getId());

        assertThat(user)
                .as("User object")
                .isNotNull();

        assertThat(user.getId())
                .as("User ID")
                .isNotNull()
                .isPositive();

        assertThat(user.getEmail())
                .as("User email")
                .isNotNull()
                .contains("@");

        assertThat(user.getUsername())
                .as("Username")
                .isNotNull()
                .isNotEmpty()
                .hasSizeGreaterThan(2);

        if (user.getName() != null) {
            assertThat(user.getName().getFirstname())
                    .as("First name")
                    .isNotNull()
                    .isNotEmpty();

            assertThat(user.getName().getLastname())
                    .as("Last name")
                    .isNotNull()
                    .isNotEmpty();
        }

        logger.info("User validation passed for ID: {}", user.getId());
    }

    /**
     * Performs deep validation on a Cart object.
     * Validates cart structure, IDs, and all products within the cart.
     *
     * @param cart the Cart object to validate
     */
    public static void assertCartIsValid(Cart cart) {
        logger.info("Performing deep validation on Cart: {}", cart.getId());

        assertThat(cart)
                .as("Cart object")
                .isNotNull();

        assertThat(cart.getId())
                .as("Cart ID")
                .isNotNull()
                .isPositive();

        assertThat(cart.getUserId())
                .as("Cart user ID")
                .isNotNull()
                .isPositive();

        assertThat(cart.getProducts())
                .as("Cart products")
                .isNotNull()
                .isNotEmpty();

        cart.getProducts().forEach(product -> {
            assertThat(product.getProductId())
                    .as("Product ID in cart")
                    .isNotNull()
                    .isPositive();

            assertThat(product.getQuantity())
                    .as("Product quantity")
                    .isNotNull()
                    .isPositive()
                    .isLessThan(1000);
        });

        logger.info("Cart validation passed for ID: {}", cart.getId());
    }

    /**
     * Validates that a cart contains a specific product.
     *
     * @param cart the Cart object to check
     * @param productId the product ID to look for
     */
    public static void assertCartContainsProduct(Cart cart, Integer productId) {
        logger.info("Validating cart {} contains product {}",
                cart.getId(), productId);

        boolean containsProduct = cart.getProducts().stream()
                .anyMatch(p -> p.getProductId().equals(productId));

        assertThat(containsProduct)
                .as("Cart contains product " + productId)
                .isTrue();
    }

    /**
     * Validates that an authentication token meets basic requirements.
     *
     * @param token the authentication token to validate
     */
    public static void assertTokenIsValid(String token) {
        logger.info("Validating authentication token");

        assertThat(token)
                .as("Auth token")
                .isNotNull()
                .isNotEmpty()
                .hasSizeGreaterThan(10);

        logger.info("Token validation passed");
    }

    /**
     * Validates that a specific JSON path exists in the response.
     *
     * @param response the API response to validate
     * @param jsonPath the JSON path to check (e.g., "user.id")
     */
    public static void assertJsonPathExists(Response response, String jsonPath) {
        logger.info("Validating JSON path exists: {}", jsonPath);

        Object value = response.jsonPath().get(jsonPath);
        assertThat(value)
                .as("JSON path: " + jsonPath)
                .isNotNull();
    }

    /**
     * Validates that a list has at least the minimum required size.
     *
     * @param list the list to validate
     * @param minSize the minimum acceptable size
     */
    public static void assertListSize(List<?> list, int minSize) {
        logger.info("Validating list size - Actual: {}, Min: {}",
                list.size(), minSize);

        assertThat(list)
                .as("Response list")
                .isNotNull()
                .hasSizeGreaterThanOrEqualTo(minSize);
    }

    /**
     * Validates an error response has the expected status code and non-empty body.
     *
     * @param response the error response to validate
     * @param expectedStatusCode the expected HTTP error status code
     */
    public static void assertErrorResponse(Response response, int expectedStatusCode) {
        logger.info("Validating error response");

        assertStatusCode(response, expectedStatusCode);

        assertThat(response.getBody().asString())
                .as("Error response body")
                .isNotNull()
                .isNotEmpty();
    }

    /**
     * Validates that the response matches a predefined JSON schema.
     *
     * @param response the API response to validate
     * @param schemaPath path to the JSON schema file in classpath
     */
    public static void assertJsonSchema(Response response, String schemaPath) {
        logger.info("Validating JSON schema: {}", schemaPath);

        try {
            response.then()
                    .assertThat()
                    .body(JsonSchemaValidator.matchesJsonSchemaInClasspath(schemaPath));
            logger.info("JSON schema validation passed");
        } catch (Exception e) {
            logger.error("JSON schema validation failed: {}", e.getMessage());
            throw e;
        }
    }
}
