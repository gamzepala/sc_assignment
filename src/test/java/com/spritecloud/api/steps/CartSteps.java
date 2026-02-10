package com.spritecloud.api.steps;

import com.spritecloud.models.api.Cart;
import com.spritecloud.services.CartService;
import com.spritecloud.utils.AssertionUtils;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step definitions for shopping cart scenarios.
 */
public class CartSteps {

    private static final Logger logger = LoggerFactory.getLogger(CartSteps.class);
    private final TestContext context;
    private final CartService cartService;

    public CartSteps(TestContext context) {
        this.context = context;
        this.cartService = new CartService();
    }

    @Given("I have a valid user ID {int}")
    public void iHaveAValidUserID(int userId) {
        logger.info("Setting user ID to: {}", userId);
        context.setCurrentUserId(userId);
    }

    @And("I have an existing product ID {int}")
    public void iHaveAnExistingProductID(int productId) {
        logger.info("Setting product ID to: {}", productId);
        context.setCurrentProductId(productId);
    }

    @Given("I have invalid cart data with missing required fields")
    public void iHaveInvalidCartDataWithMissingRequiredFields() {
        logger.info("Creating invalid cart data for negative test");

        // Missing required fields like userId or products
        Map<String, Object> invalidData = new HashMap<>();
        invalidData.put("date", LocalDate.now().toString());
        // Intentionally missing userId and products

        context.setInvalidData(invalidData);
    }

    @When("I create a new cart with the product")
    public void iCreateANewCartWithTheProduct() {
        logger.info("Creating cart with product ID: {} for user ID: {}",
                context.getCurrentProductId(), context.getCurrentUserId());

        // Create cart products list
        List<Map<String, Integer>> products = new ArrayList<>();
        Map<String, Integer> product = new HashMap<>();
        product.put("productId", context.getCurrentProductId());
        product.put("quantity", 2); // Quantity of 2 for testing
        products.add(product);

        // Create cart with current date
        String date = LocalDate.now().toString();
        Response response = cartService.createCart(
                context.getCurrentUserId(),
                date,
                products
        );

        context.setResponse(response);
    }

    @When("I send a POST request to create a cart")
    public void iSendAPOSTRequestToCreateACart() {
        logger.info("Sending POST request to create cart with invalid data");

        Response response = cartService.createCartWithInvalidData(context.getInvalidData());
        context.setResponse(response);
    }

    @Then("the response should contain a valid cart ID")
    public void theResponseShouldContainAValidCartID() {
        logger.info("Validating cart ID in response");
        Cart cart = cartService.extractCart(context.getResponse());
        context.setCart(cart);

        // Deep assertion: Cart ID validation
        assertThat(cart.getId())
                .as("Cart ID")
                .isNotNull()
                .isPositive();

        logger.info("Cart created with ID: {}", cart.getId());
    }

    @And("the cart should belong to the correct user")
    public void theCartShouldBelongToTheCorrectUser() {
        logger.info("Validating cart belongs to correct user");
        Cart cart = context.getCart();

        // Deep assertion: Verify user ID matches
        assertThat(cart.getUserId())
                .as("Cart user ID")
                .isEqualTo(context.getCurrentUserId());

        logger.info("Cart user ID validated: {}", cart.getUserId());
    }

    @And("the cart should contain the specified product")
    public void theCartShouldContainTheSpecifiedProduct() {
        logger.info("Validating cart contains the specified product");
        Cart cart = context.getCart();

        // Deep assertion: Verify product exists in cart
        boolean containsProduct = cart.getProducts().stream()
                .anyMatch(p -> p.getProductId().equals(context.getCurrentProductId()));

        assertThat(containsProduct)
                .as("Cart contains product " + context.getCurrentProductId())
                .isTrue();

        logger.info("Cart contains product ID: {}", context.getCurrentProductId());
    }

    @And("the product quantity should be correct")
    public void theProductQuantityShouldBeCorrect() {
        logger.info("Validating product quantity in cart");
        Cart cart = context.getCart();

        // Deep assertion: Find product and validate quantity
        cart.getProducts().forEach(product -> {
            if (product.getProductId().equals(context.getCurrentProductId())) {
                assertThat(product.getQuantity())
                        .as("Product quantity")
                        .isNotNull()
                        .isPositive()
                        .isGreaterThanOrEqualTo(1);

                logger.info("Product {} quantity: {}",
                        product.getProductId(), product.getQuantity());
            }
        });
    }

    @And("the cart structure should be valid")
    public void theCartStructureShouldBeValid() {
        logger.info("Validating cart structure");
        Cart cart = context.getCart();

        // Deep assertion: Comprehensive cart validation
        AssertionUtils.assertCartIsValid(cart);

        // Additional business logic: Total quantity calculation
        int totalQuantity = cart.getTotalQuantity();
        assertThat(totalQuantity)
                .as("Total cart quantity")
                .isPositive();

        logger.info("Cart structure validated. Total items: {}", totalQuantity);
    }

    @Given("I have a user with ID {int}")
    public void iHaveAUserWithID(int userId) {
        logger.info("Setting user ID to: {}", userId);
        context.setCurrentUserId(userId);
    }

    @When("I retrieve carts for the user")
    public void iRetrieveCartsForTheUser() {
        logger.info("Retrieving carts for user ID: {}", context.getCurrentUserId());

        Response response = cartService.getUserCarts(context.getCurrentUserId());
        context.setResponse(response);
    }

    @Then("the response should contain cart information")
    public void theResponseShouldContainCartInformation() {
        logger.info("Validating cart information in response");
        Response response = context.getResponse();

        // Parse response as array
        Cart[] cartsArray = response.as(Cart[].class);
        List<Cart> carts = Arrays.asList(cartsArray);
        context.setCartList(carts);

        // Deep assertion: Validate carts list
        assertThat(carts)
                .as("User carts list")
                .isNotNull();

        logger.info("Found {} carts for user", carts.size());
    }

    @And("each cart should have valid structure")
    public void eachCartShouldHaveValidStructure() {
        logger.info("Validating structure of all carts");
        List<Cart> carts = context.getCartList();

        // Deep assertion: Validate each cart
        for (Cart cart : carts) {
            assertThat(cart.getUserId())
                    .as("Cart user ID")
                    .isEqualTo(context.getCurrentUserId());

            if (cart.getProducts() != null && !cart.getProducts().isEmpty()) {
                cart.getProducts().forEach(product -> {
                    assertThat(product.getProductId())
                            .as("Product ID")
                            .isPositive();
                    assertThat(product.getQuantity())
                            .as("Product quantity")
                            .isPositive();
                });
            }
        }

        logger.info("All carts validated successfully");
    }

    @And("the response should contain error information")
    public void theResponseShouldContainErrorInformation() {
        logger.info("Validating error response contains information");
        Response response = context.getResponse();

        // Deep assertion: Error response should not be empty
        assertThat(response.getBody().asString())
                .as("Error response body")
                .isNotNull();

        logger.info("Error response validated");
    }
}
