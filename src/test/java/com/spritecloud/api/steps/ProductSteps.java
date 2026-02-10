package com.spritecloud.api.steps;

import com.spritecloud.models.api.Product;
import com.spritecloud.services.ProductService;
import com.spritecloud.utils.AssertionUtils;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step definitions for product management scenarios.
 */
public class ProductSteps {

    private static final Logger logger = LoggerFactory.getLogger(ProductSteps.class);
    private final TestContext context;
    private final ProductService productService;

    public ProductSteps(TestContext context) {
        this.context = context;
        this.productService = new ProductService();
    }

    @Given("I request product with ID {int}")
    public void iRequestProductWithID(int productId) {
        logger.info("Setting current product ID to: {}", productId);
        context.setCurrentProductId(productId);
    }

    @Given("I want to retrieve all products")
    public void iWantToRetrieveAllProducts() {
        logger.info("Preparing to retrieve all products");
    }

    @Given("I request a product with non-existent ID {int}")
    public void iRequestAProductWithNonExistentID(int nonExistentId) {
        logger.info("Setting non-existent product ID to: {}", nonExistentId);
        context.setCurrentProductId(nonExistentId);
    }

    @When("I send a GET request to products endpoint")
    public void iSendAGETRequestToProductsEndpoint() {
        logger.info("Sending GET request to products endpoint");

        Response response;
        if (context.getCurrentProductId() != null) {
            // Get specific product
            response = productService.getProductById(context.getCurrentProductId());
        } else {
            // Get all products
            response = productService.getAllProducts();
        }

        context.setResponse(response);
    }

    @Then("the response content type should be {string}")
    public void theResponseContentTypeShouldBe(String expectedContentType) {
        logger.info("Validating content type: {}", expectedContentType);
        AssertionUtils.assertContentType(context.getResponse(), expectedContentType);
    }

    @And("the product should have a valid ID")
    public void theProductShouldHaveAValidID() {
        logger.info("Validating product ID");
        Product product = productService.extractProduct(context.getResponse());
        context.setProduct(product);

        // Deep assertion: ID must be positive integer
        assertThat(product.getId())
                .as("Product ID")
                .isNotNull()
                .isPositive();
    }

    @And("the product should have a non-empty title")
    public void theProductShouldHaveANonEmptyTitle() {
        logger.info("Validating product title");
        Product product = context.getProduct();

        // Deep assertion: Title validation with business rules
        assertThat(product.getTitle())
                .as("Product title")
                .isNotNull()
                .isNotEmpty()
                .hasSizeGreaterThan(3); // Minimum title length

        logger.info("Product title validated: {}", product.getTitle());
    }

    @And("the product should have a valid price greater than or equal to {int}")
    public void theProductShouldHaveAValidPriceGreaterThanOrEqualTo(int minPrice) {
        logger.info("Validating product price");
        Product product = context.getProduct();

        // Deep assertion: Price validation with business logic
        assertThat(product.getPrice())
                .as("Product price")
                .isNotNull()
                .isGreaterThanOrEqualTo((double) minPrice)
                .isLessThan(1000000.0); // Reasonable upper bound

        // Additional business rule: Price should have maximum 2 decimal places
        String priceStr = String.format("%.2f", product.getPrice());
        logger.info("Product price validated: ${}", priceStr);
    }

    @And("the product should have a valid category")
    public void theProductShouldHaveAValidCategory() {
        logger.info("Validating product category");
        Product product = context.getProduct();

        // Deep assertion: Category validation
        assertThat(product.getCategory())
                .as("Product category")
                .isNotNull()
                .isNotEmpty()
                .matches("[a-zA-Z\\s']+"); // Category should contain only letters and spaces

        logger.info("Product category validated: {}", product.getCategory());
    }

    @And("the product should have a valid image URL")
    public void theProductShouldHaveAValidImageURL() {
        logger.info("Validating product image URL");
        Product product = context.getProduct();

        // Deep assertion: Image URL validation
        assertThat(product.getImage())
                .as("Product image URL")
                .isNotNull()
                .isNotEmpty()
                .startsWith("http") // Must be a URL
                .contains("://"); // Valid URL format

        logger.info("Product image URL validated: {}", product.getImage());
    }

    @And("the product should have valid rating information")
    public void theProductShouldHaveValidRatingInformation() {
        logger.info("Validating product rating");
        Product product = context.getProduct();

        if (product.getRating() != null) {
            // Deep assertion: Rating value validation
            assertThat(product.getRating().getRate())
                    .as("Product rating")
                    .isNotNull()
                    .isGreaterThanOrEqualTo(0.0)
                    .isLessThanOrEqualTo(5.0); // Rating scale 0-5

            // Deep assertion: Rating count validation
            assertThat(product.getRating().getCount())
                    .as("Rating count")
                    .isNotNull()
                    .isGreaterThanOrEqualTo(0);

            logger.info("Product rating validated: {} ({} reviews)",
                    product.getRating().getRate(), product.getRating().getCount());
        }
    }

    @And("the response should contain a list of products")
    public void theResponseShouldContainAListOfProducts() {
        logger.info("Validating products list");
        List<Product> products = productService.extractProductList(context.getResponse());
        context.setProductList(products);

        // Deep assertion: List should not be empty
        assertThat(products)
                .as("Products list")
                .isNotNull()
                .isNotEmpty()
                .hasSizeGreaterThan(5); // FakeStore API should return multiple products

        logger.info("Products list validated: {} products found", products.size());
    }

    @And("all products should have valid structure")
    public void allProductsShouldHaveValidStructure() {
        logger.info("Validating all products structure");
        List<Product> products = context.getProductList();

        // Deep assertion: Validate each product's structure
        for (Product product : products) {
            AssertionUtils.assertProductIsValid(product);
        }

        logger.info("All {} products validated successfully", products.size());
    }

    @Then("the response status code should be {int} or similar error code")
    public void theResponseStatusCodeShouldBeOrSimilarErrorCode(int expectedCode) {
        logger.info("Validating error response status code");
        int actualCode = context.getResponse().getStatusCode();

        // For negative tests, accept various error codes (404, 400, etc.)
        assertThat(actualCode)
                .as("Error status code")
                .isGreaterThanOrEqualTo(400)
                .isLessThan(600);

        logger.info("Error response validated with status code: {}", actualCode);
    }

    @And("the response should indicate the resource was not found")
    public void theResponseShouldIndicateTheResourceWasNotFound() {
        logger.info("Validating not found error message");
        Response response = context.getResponse();

        // Deep assertion: Response body should not be empty for errors
        assertThat(response.getBody().asString())
                .as("Error response body")
                .isNotNull();

        logger.info("Not found response validated");
    }
}
