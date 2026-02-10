package com.spritecloud.api.steps;

import com.spritecloud.models.api.AuthToken;
import com.spritecloud.services.AuthService;
import com.spritecloud.utils.AssertionUtils;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Cucumber step definitions for authentication API tests.
 * Handles login scenarios and authentication token validation.

 */
public class AuthSteps {

    private static final Logger logger = LoggerFactory.getLogger(AuthSteps.class);
    private final TestContext context;
    private final AuthService authService;

    private static final String VALID_USERNAME = "mor_2314";
    private static final String VALID_PASSWORD = "83r5^_";

    /**
     * Constructs AuthSteps with shared test context.
     *
     * @param context shared context for storing test data
     */
    public AuthSteps(TestContext context) {
        this.context = context;
        this.authService = new AuthService();
    }

    /**
     * Sets up valid user credentials for authentication testing.
     * Corresponds to Gherkin: "Given I have valid user credentials"
     */
    @Given("I have valid user credentials")
    public void iHaveValidUserCredentials() {
        logger.info("Using valid credentials for authentication test");
    }

    /**
     * Sends a login request to the authentication endpoint.
     * Corresponds to Gherkin: "When I send a login request"
     */
    @When("I send a login request")
    public void iSendALoginRequest() {
        logger.info("Sending login request with username: {}", VALID_USERNAME);
        Response response = authService.login(VALID_USERNAME, VALID_PASSWORD);
        context.setResponse(response);
    }

    /**
     * Validates the HTTP response status code.
     * Corresponds to Gherkin: "Then the response status code should be {int}"
     *
     * @param expectedStatusCode the expected HTTP status code
     */
    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int expectedStatusCode) {
        logger.info("Validating response status code: {}", expectedStatusCode);
        AssertionUtils.assertStatusCode(context.getResponse(), expectedStatusCode);
    }

    /**
     * Validates that the response contains a valid authentication token.
     * Corresponds to Gherkin: "And the response should contain a valid authentication token"
     */
    @And("the response should contain a valid authentication token")
    public void theResponseShouldContainAValidAuthenticationToken() {
        logger.info("Validating authentication token presence");
        Response response = context.getResponse();

        AuthToken authToken = authService.extractAuthToken(response);
        context.setAuthToken(authToken);

        assertThat(authToken).isNotNull();
        assertThat(authToken.getToken())
                .as("Authentication token")
                .isNotNull()
                .isNotEmpty();

        assertThat(authToken.getToken().length())
                .as("Token length")
                .isGreaterThan(10);

        logger.info("Authentication token validated successfully");
    }

    /**
     * Validates that the authentication token has the expected format.
     * Corresponds to Gherkin: "And the token should have proper format"
     */
    @And("the token should have proper format")
    public void theTokenShouldHaveProperFormat() {
        logger.info("Validating token format");
        AuthToken authToken = context.getAuthToken();

        assertThat(authToken.getToken())
                .as("Token format")
                .contains(".");

        assertThat(authToken.getToken())
                .as("Token characters")
                .doesNotContain(" ", "\n", "\t");

        logger.info("Token format validation passed");
    }

    /**
     * Validates that the API response time is within acceptable limits.
     * Corresponds to Gherkin: "And the response time should be less than {int} milliseconds"
     *
     * @param maxResponseTime maximum acceptable response time in milliseconds
     */
    @And("the response time should be less than {int} milliseconds")
    public void theResponseTimeShouldBeLessThanMilliseconds(int maxResponseTime) {
        logger.info("Validating response time is under {} ms", maxResponseTime);
        AssertionUtils.assertResponseTime(context.getResponse(), maxResponseTime);
    }
}
