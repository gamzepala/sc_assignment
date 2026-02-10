package com.spritecloud.ui.steps;

import com.spritecloud.models.ui.Product;
import com.spritecloud.pages.InventoryPage;
import com.spritecloud.ui.TestContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step definitions for SauceDemo product sorting scenarios.
 * Validates sorting functionality with deep order validations.
 */
public class SortingSteps {

    private static final Logger logger = LoggerFactory.getLogger(SortingSteps.class);
    private final TestContext context;

    public SortingSteps(TestContext context) {
        this.context = context;
    }

    @When("I sort products by {string}")
    public void iSortProductsBy(String sortOption) {
        logger.info("Sorting products by: {}", sortOption);

        InventoryPage.SortOption option = mapSortOption(sortOption);
        context.getInventoryPage().sortProducts(option);

        logger.debug("Products sorted by: {}", option);
    }

    @Then("the products should be displayed in reverse alphabetical order")
    public void theProductsShouldBeDisplayedInReverseAlphabeticalOrder() {
        logger.info("Validating reverse alphabetical order (Z-A)");

        boolean isCorrectlySorted = context.getInventoryPage().isProductsSortedZtoA();

        assertThat(isCorrectlySorted)
                .as("Products should be sorted Z to A")
                .isTrue();

        logger.info("Z-A sorting validation passed");
    }

    @Then("the first product should be {string}")
    public void theFirstProductShouldBe(String expectedProductName) {
        logger.info("Validating first product is: {}", expectedProductName);

        List<String> productNames = context.getInventoryPage().getProductNames();
        String firstProduct = productNames.get(0);

        assertThat(firstProduct)
                .as("First product should be '%s'", expectedProductName)
                .isEqualTo(expectedProductName);

        logger.info("First product validation passed: {}", firstProduct);
    }

    @Then("the last product should be {string}")
    public void theLastProductShouldBe(String expectedProductName) {
        logger.info("Validating last product is: {}", expectedProductName);

        List<String> productNames = context.getInventoryPage().getProductNames();
        String lastProduct = productNames.get(productNames.size() - 1);

        assertThat(lastProduct)
                .as("Last product should be '%s'", expectedProductName)
                .isEqualTo(expectedProductName);

        logger.info("Last product validation passed: {}", lastProduct);
    }

    @Then("the products should be displayed in alphabetical order")
    public void theProductsShouldBeDisplayedInAlphabeticalOrder() {
        logger.info("Validating alphabetical order (A-Z)");

        List<String> productNames = context.getInventoryPage().getProductNames();

        for (int i = 0; i < productNames.size() - 1; i++) {
            String current = productNames.get(i);
            String next = productNames.get(i + 1);

            assertThat(current.compareToIgnoreCase(next))
                    .as("Product '%s' should come before '%s'", current, next)
                    .isLessThanOrEqualTo(0);
        }

        logger.info("A-Z sorting validation passed");
    }

    @Then("the products should be sorted by price ascending")
    public void theProductsShouldBeSortedByPriceAscending() {
        logger.info("Validating price sorting (low to high)");

        List<Product> products = context.getInventoryPage().getAllProducts();

        for (int i = 0; i < products.size() - 1; i++) {
            Product current = products.get(i);
            Product next = products.get(i + 1);

            assertThat(current.getPrice())
                    .as("Product '%s' ($%.2f) should cost <= '%s' ($%.2f)",
                            current.getName(), current.getPrice(),
                            next.getName(), next.getPrice())
                    .isLessThanOrEqualTo(next.getPrice());
        }

        logger.info("Price ascending validation passed");
    }

    @Then("the first product price should be lower than the last product price")
    public void theFirstProductPriceShouldBeLowerThanTheLastProductPrice() {
        logger.info("Validating first product is cheaper than last");

        List<Product> products = context.getInventoryPage().getAllProducts();
        Double firstPrice = products.get(0).getPrice();
        Double lastPrice = products.get(products.size() - 1).getPrice();

        assertThat(firstPrice)
                .as("First product price ($%.2f) should be <= last price ($%.2f)",
                        firstPrice, lastPrice)
                .isLessThanOrEqualTo(lastPrice);

        logger.info("Price range validation passed: ${} to ${}", firstPrice, lastPrice);
    }

    @Then("the products should be sorted by price descending")
    public void theProductsShouldBeSortedByPriceDescending() {
        logger.info("Validating price sorting (high to low)");

        List<Product> products = context.getInventoryPage().getAllProducts();

        for (int i = 0; i < products.size() - 1; i++) {
            Product current = products.get(i);
            Product next = products.get(i + 1);

            assertThat(current.getPrice())
                    .as("Product '%s' ($%.2f) should cost >= '%s' ($%.2f)",
                            current.getName(), current.getPrice(),
                            next.getName(), next.getPrice())
                    .isGreaterThanOrEqualTo(next.getPrice());
        }

        logger.info("Price descending validation passed");
    }

    @Then("the first product price should be higher than the last product price")
    public void theFirstProductPriceShouldBeHigherThanTheLastProductPrice() {
        logger.info("Validating first product is more expensive than last");

        List<Product> products = context.getInventoryPage().getAllProducts();
        Double firstPrice = products.get(0).getPrice();
        Double lastPrice = products.get(products.size() - 1).getPrice();

        assertThat(firstPrice)
                .as("First product price ($%.2f) should be >= last price ($%.2f)",
                        firstPrice, lastPrice)
                .isGreaterThanOrEqualTo(lastPrice);

        logger.info("Price range validation passed: ${} to ${}", firstPrice, lastPrice);
    }

    /**
     * Maps human-readable sort option to enum value.
     */
    private InventoryPage.SortOption mapSortOption(String optionText) {
        switch (optionText.toLowerCase()) {
            case "name (a to z)":
                return InventoryPage.SortOption.NAME_A_TO_Z;
            case "name (z to a)":
                return InventoryPage.SortOption.NAME_Z_TO_A;
            case "price (low to high)":
                return InventoryPage.SortOption.PRICE_LOW_TO_HIGH;
            case "price (high to low)":
                return InventoryPage.SortOption.PRICE_HIGH_TO_LOW;
            default:
                throw new IllegalArgumentException("Unknown sort option: " + optionText);
        }
    }
}
