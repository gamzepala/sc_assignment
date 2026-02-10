package com.spritecloud.api.steps;

import com.spritecloud.models.api.User;
import com.spritecloud.services.UserService;
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
 * Step definitions for user management scenarios.
 */
public class UserSteps {

    private static final Logger logger = LoggerFactory.getLogger(UserSteps.class);
    private final TestContext context;
    private final UserService userService;

    public UserSteps(TestContext context) {
        this.context = context;
        this.userService = new UserService();
    }

    @Given("I have an existing user with ID {int}")
    public void iHaveAnExistingUserWithID(int userId) {
        logger.info("Setting user ID to: {}", userId);
        context.setCurrentUserId(userId);
    }

    @Given("I want to retrieve user with ID {int}")
    public void iWantToRetrieveUserWithID(int userId) {
        logger.info("Setting user ID for retrieval: {}", userId);
        context.setCurrentUserId(userId);
    }

    @When("I send a DELETE request for the user")
    public void iSendADELETERequestForTheUser() {
        logger.info("Sending DELETE request for user ID: {}", context.getCurrentUserId());

        Response response = userService.deleteUser(context.getCurrentUserId());
        context.setResponse(response);
    }

    @When("I send a GET request to users endpoint")
    public void iSendAGETRequestToUsersEndpoint() {
        logger.info("Sending GET request to users endpoint");

        Response response = userService.getUserById(context.getCurrentUserId());
        context.setResponse(response);
    }

    @Then("the response should confirm the deletion")
    public void theResponseShouldConfirmTheDeletion() {
        logger.info("Validating deletion confirmation");
        Response response = context.getResponse();

        // Deep assertion: Response should indicate successful deletion
        AssertionUtils.assertStatusCode(response, 200);

        // Validate response is not empty
        assertThat(response.getBody().asString())
                .as("Deletion response")
                .isNotNull()
                .isNotEmpty();

        logger.info("Deletion confirmed");
    }

    @And("the deleted user object should be returned")
    public void theDeletedUserObjectShouldBeReturned() {
        logger.info("Validating deleted user object in response");
        User user = userService.extractUser(context.getResponse());
        context.setUser(user);

        // Deep assertion: Returned user should have the correct ID
        assertThat(user.getId())
                .as("Deleted user ID")
                .isEqualTo(context.getCurrentUserId());

        logger.info("Deleted user object validated with ID: {}", user.getId());
    }

    @And("the user should have a valid ID")
    public void theUserShouldHaveAValidID() {
        logger.info("Validating user ID");
        User user = userService.extractUser(context.getResponse());
        context.setUser(user);

        // Deep assertion: ID validation
        assertThat(user.getId())
                .as("User ID")
                .isNotNull()
                .isPositive()
                .isEqualTo(context.getCurrentUserId());

        logger.info("User ID validated: {}", user.getId());
    }

    @And("the user should have a valid email address")
    public void theUserShouldHaveAValidEmailAddress() {
        logger.info("Validating user email address");
        User user = context.getUser();

        // Deep assertion: Email validation with format check
        assertThat(user.getEmail())
                .as("User email")
                .isNotNull()
                .isNotEmpty()
                .contains("@")  // Basic email format
                .contains("."); // Must have domain extension

        // Additional validation: Email should not have whitespace
        assertThat(user.getEmail())
                .as("Email format")
                .doesNotContain(" ", "\n", "\t");

        logger.info("User email validated: {}", user.getEmail());
    }

    @And("the user should have a valid username")
    public void theUserShouldHaveAValidUsername() {
        logger.info("Validating username");
        User user = context.getUser();

        // Deep assertion: Username validation
        assertThat(user.getUsername())
                .as("Username")
                .isNotNull()
                .isNotEmpty()
                .hasSizeGreaterThan(2) // Minimum username length
                .hasSizeLessThan(50);  // Maximum username length

        logger.info("Username validated: {}", user.getUsername());
    }

    @And("the user should have valid name information")
    public void theUserShouldHaveValidNameInformation() {
        logger.info("Validating user name information");
        User user = context.getUser();

        // Deep assertion: Name object validation
        if (user.getName() != null) {
            assertThat(user.getName().getFirstname())
                    .as("First name")
                    .isNotNull()
                    .isNotEmpty()
                    .matches("[a-zA-Z]+"); // Only letters in name

            assertThat(user.getName().getLastname())
                    .as("Last name")
                    .isNotNull()
                    .isNotEmpty()
                    .matches("[a-zA-Z]+"); // Only letters in name

            // Validate full name can be constructed
            String fullName = user.getName().getFullName();
            assertThat(fullName)
                    .as("Full name")
                    .isNotEmpty()
                    .contains(" "); // Should have space between names

            logger.info("User name validated: {}", fullName);
        }
    }

    @And("the user structure should be valid")
    public void theUserStructureShouldBeValid() {
        logger.info("Validating complete user structure");
        User user = context.getUser();

        // Deep assertion: Comprehensive user validation
        AssertionUtils.assertUserIsValid(user);

        // Additional validations
        if (user.getPhone() != null) {
            assertThat(user.getPhone())
                    .as("Phone number")
                    .isNotEmpty();
        }

        // Address validation if present
        if (user.getAddress() != null) {
            assertThat(user.getAddress().getCity())
                    .as("City")
                    .isNotNull();

            if (user.getAddress().getZipcode() != null) {
                assertThat(user.getAddress().getZipcode())
                        .as("Zipcode")
                        .isNotEmpty();
            }
        }

        logger.info("Complete user structure validated for: {}", user.getUsername());
    }
}
