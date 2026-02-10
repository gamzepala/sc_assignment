package com.spritecloud.ui.steps;

import com.spritecloud.models.ui.CartItem;
import com.spritecloud.models.ui.CheckoutInfo;
import com.spritecloud.models.ui.OrderSummary;
import com.spritecloud.models.ui.Product;
import com.spritecloud.ui.TestContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step definitions for SauceDemo checkout scenarios.
 * Implements E2E shopping and checkout flow with deep price validations.
 */
public class CheckoutSteps {

    private static final Logger logger = LoggerFactory.getLogger(CheckoutSteps.class);
    private final TestContext context;

    public CheckoutSteps(TestContext context) {
        this.context = context;
    }

    @When("I add the following products to cart:")
    public void iAddTheFollowingProductsToCart(List<String> productNames) {
        logger.info("Adding {} products to cart", productNames.size());

        for (String productName : productNames) {
            context.getInventoryPage().addProductToCart(productName);
            logger.debug("Added product: {}", productName);
        }

        logger.info("All products added to cart");
    }

    @When("I navigate to the shopping cart")
    public void iNavigateToTheShoppingCart() {
        logger.info("Navigating to shopping cart");
        context.getInventoryPage().goToCart();
    }

    @Then("I should see {int} items in my cart")
    public void iShouldSeeItemsInMyCart(int expectedCount) {
        logger.info("Validating cart item count: {}", expectedCount);

        int actualCount = context.getCartPage().getCartItemCount();

        assertThat(actualCount)
                .as("Cart should contain %d items", expectedCount)
                .isEqualTo(expectedCount);

        logger.info("Cart item count validation passed: {}", actualCount);
    }

    @When("I proceed to checkout")
    public void iProceedToCheckout() {
        logger.info("Proceeding to checkout");
        context.getCartPage().proceedToCheckout();
    }

    @When("I enter checkout information:")
    public void iEnterCheckoutInformation(DataTable dataTable) {
        logger.info("Entering checkout information");

        // Parse first row of data (skip header row)
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        Map<String, String> data = rows.get(0);

        CheckoutInfo info = new CheckoutInfo(
                data.get("firstName"),
                data.get("lastName"),
                data.get("postalCode")
        );

        context.setCheckoutInfo(info);
        context.getCheckoutStepOnePage().fillCheckoutInformation(info);

        logger.info("Checkout information entered: {}", info);
    }

    @When("I continue to checkout overview")
    public void iContinueToCheckoutOverview() {
        logger.info("Continuing to checkout overview");
        context.getCheckoutStepOnePage().clickContinue();

        // Store order summary for validation
        OrderSummary summary = context.getCheckoutStepTwoPage().getOrderSummary();
        context.setOrderSummary(summary);
        logger.debug("Order summary: {}", summary);
    }

    @Then("I should see the correct item total for my cart items")
    public void iShouldSeeTheCorrectItemTotalForMyCartItems() {
        logger.info("Validating item total calculation");

        OrderSummary summary = context.getOrderSummary();
        Double displayedTotal = summary.getItemTotal();
        Double calculatedTotal = summary.calculateExpectedItemTotal();

        assertThat(displayedTotal)
                .as("Item total should match sum of cart items")
                .isEqualTo(calculatedTotal);

        logger.info("Item total validation passed: ${}", displayedTotal);
    }

    @Then("I should see tax calculated correctly")
    public void iShouldSeeTaxCalculatedCorrectly() {
        logger.info("Validating tax calculation");

        OrderSummary summary = context.getOrderSummary();
        boolean isTaxReasonable = summary.isTaxReasonable();

        assertThat(isTaxReasonable)
                .as("Tax should be calculated within reasonable range (7-9%%)")
                .isTrue();

        Double taxRate = (summary.getTax() / summary.getItemTotal()) * 100;
        logger.info("Tax validation passed: ${} ({:.2f}%%)", summary.getTax(), taxRate);
    }

    @Then("I should see the correct total price including tax")
    public void iShouldSeeTheCorrectTotalPriceIncludingTax() {
        logger.info("Validating total price calculation");

        OrderSummary summary = context.getOrderSummary();
        boolean isCalculationValid = summary.isCalculationValid();

        assertThat(isCalculationValid)
                .as("Total should equal item total + tax")
                .isTrue();

        Double expectedTotal = summary.getItemTotal() + summary.getTax();
        logger.info("Total price validation passed: ${} (Items: ${}, Tax: ${})",
                summary.getTotal(), summary.getItemTotal(), summary.getTax());
    }

    @Then("the total calculation should be mathematically correct")
    public void theTotalCalculationShouldBeMathematicallyCorrect() {
        logger.info("Performing comprehensive total calculation validation");

        OrderSummary summary = context.getOrderSummary();

        // Validate item total
        Double calculatedItemTotal = summary.calculateExpectedItemTotal();
        assertThat(summary.getItemTotal())
                .as("Item total should match calculated sum")
                .isEqualTo(calculatedItemTotal);

        // Validate tax is reasonable
        assertThat(summary.isTaxReasonable())
                .as("Tax should be within reasonable range")
                .isTrue();

        // Validate final total
        assertThat(summary.isCalculationValid())
                .as("Total should equal items + tax")
                .isTrue();

        logger.info("Comprehensive calculation validation passed");
    }

    @When("I complete the checkout")
    public void iCompleteTheCheckout() {
        logger.info("Completing checkout");
        context.getCheckoutStepTwoPage().finishCheckout();
        context.getCheckoutCompletePage().waitForCheckoutComplete();
    }

    @Then("I should see order confirmation")
    public void iShouldSeeOrderConfirmation() {
        logger.info("Validating order confirmation");

        boolean isComplete = context.getCheckoutCompletePage().isOrderComplete();

        assertThat(isComplete)
                .as("Order should be successfully completed")
                .isTrue();

        logger.info("Order confirmation validation passed");
    }

    @Then("the confirmation should say {string}")
    public void theConfirmationShouldSay(String expectedText) {
        logger.info("Validating confirmation message contains: {}", expectedText);

        String confirmationHeader = context.getCheckoutCompletePage().getConfirmationHeader();

        assertThat(confirmationHeader)
                .as("Confirmation header should contain expected text")
                .containsIgnoringCase(expectedText);

        logger.info("Confirmation message validation passed: {}", confirmationHeader);
    }
}
