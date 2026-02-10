package com.spritecloud.ui.steps;

import com.spritecloud.ui.TestContext;
import com.spritecloud.utils.AssertionUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step definitions for SauceDemo login scenarios.
 * Implements BDD steps using Cucumber with deep validations.
 */
public class LoginSteps {

    private static final Logger logger = LoggerFactory.getLogger(LoginSteps.class);
    private final TestContext context;

    public LoginSteps(TestContext context) {
        this.context = context;
    }

    @Given("I am on the SauceDemo login page")
    public void iAmOnTheSauceDemoLoginPage() {
        logger.info("Navigating to SauceDemo login page");
        context.getLoginPage().navigateTo("https://www.saucedemo.com");
    }

    @Given("I login as a standard user")
    public void iLoginAsAStandardUser() {
        logger.info("Logging in as standard user");
        context.getLoginPage().loginAsStandardUser();
        context.getInventoryPage().waitForInventoryPageLoad();
    }

    @When("I attempt to login with username {string} and password {string}")
    public void iAttemptToLoginWithUsernameAndPassword(String username, String password) {
        logger.info("Attempting login with username: {}", username);
        context.getLoginPage().login(username, password);
    }

    @Then("I should see an error message containing {string}")
    public void iShouldSeeAnErrorMessageContaining(String expectedError) {
        logger.info("Validating error message contains: {}", expectedError);

        boolean hasError = context.getLoginPage().hasErrorMessage(expectedError);
        String actualError = context.getLoginPage().getErrorMessage();

        assertThat(hasError)
                .as("Error message should contain: '%s'. Actual: '%s'", expectedError, actualError)
                .isTrue();

        logger.info("Error message validation passed");
    }

    @Then("I should remain on the login page")
    public void iShouldRemainOnTheLoginPage() {
        logger.info("Validating user remains on login page");

        boolean isOnLoginPage = context.getLoginPage().isOnLoginPage();

        assertThat(isOnLoginPage)
                .as("User should remain on login page after failed login")
                .isTrue();

        logger.info("Login page validation passed");
    }

    @Given("I am on the inventory page")
    public void iAmOnTheInventoryPage() {
        String currentUrl = context.getPage().url();
        assertThat(currentUrl)
                .as("Should be on inventory page")
                .contains("inventory.html");
        logger.info("Confirmed on inventory page");
    }
}
