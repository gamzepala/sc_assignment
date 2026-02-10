package com.spritecloud.pages;

import com.microsoft.playwright.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page Object for SauceDemo Login Page.
 * Handles login actions and error message validation.
 */
public class LoginPage extends BasePage {

    private static final Logger logger = LoggerFactory.getLogger(LoginPage.class);

    // Locators
    private static final String USERNAME_INPUT = "#user-name";
    private static final String PASSWORD_INPUT = "#password";
    private static final String LOGIN_BUTTON = "#login-button";
    private static final String ERROR_MESSAGE = "[data-test='error']";
    private static final String ERROR_BUTTON = ".error-button";

    // Valid test users from SauceDemo
    public static final String STANDARD_USER = "standard_user";
    public static final String LOCKED_USER = "locked_out_user";
    public static final String PROBLEM_USER = "problem_user";
    public static final String PERFORMANCE_GLITCH_USER = "performance_glitch_user";
    public static final String VALID_PASSWORD = "secret_sauce";

    public LoginPage(Page page) {
        super(page);
    }

    /**
     * Performs login with provided credentials.
     *
     * @param username username to enter
     * @param password password to enter
     */
    public void login(String username, String password) {
        logger.info("Attempting login with username: {}", username);
        fill(USERNAME_INPUT, username);
        fill(PASSWORD_INPUT, password);
        click(LOGIN_BUTTON);
    }

    /**
     * Performs login with standard user.
     */
    public void loginAsStandardUser() {
        login(STANDARD_USER, VALID_PASSWORD);
        logger.info("Logged in as standard user");
    }

    /**
     * Checks if error message is displayed.
     *
     * @return true if error message visible, false otherwise
     */
    public boolean isErrorMessageDisplayed() {
        return isVisible(ERROR_MESSAGE);
    }

    /**
     * Gets the error message text.
     *
     * @return error message text
     */
    public String getErrorMessage() {
        if (!isErrorMessageDisplayed()) {
            return "";
        }
        String error = getText(ERROR_MESSAGE);
        logger.debug("Error message: {}", error);
        return error;
    }

    /**
     * Validates that specific error message is displayed.
     *
     * @param expectedError expected error text (partial match)
     * @return true if expected error is shown
     */
    public boolean hasErrorMessage(String expectedError) {
        String actualError = getErrorMessage();
        return actualError.contains(expectedError);
    }

    /**
     * Checks if user is still on login page (failed login).
     *
     * @return true if still on login page
     */
    public boolean isOnLoginPage() {
        return getCurrentUrl().contains("saucedemo.com") &&
               !getCurrentUrl().contains("inventory.html");
    }

    /**
     * Clears error message by clicking the X button.
     */
    public void clearErrorMessage() {
        if (isVisible(ERROR_BUTTON)) {
            click(ERROR_BUTTON);
            logger.debug("Cleared error message");
        }
    }
}
