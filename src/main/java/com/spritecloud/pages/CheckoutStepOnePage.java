package com.spritecloud.pages;

import com.microsoft.playwright.Page;
import com.spritecloud.models.ui.CheckoutInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page Object for SauceDemo Checkout Step 1 (Customer Information).
 * Handles entering customer details for checkout.
 */
public class CheckoutStepOnePage extends BasePage {

    private static final Logger logger = LoggerFactory.getLogger(CheckoutStepOnePage.class);

    // Locators
    private static final String FIRST_NAME_INPUT = "#first-name";
    private static final String LAST_NAME_INPUT = "#last-name";
    private static final String POSTAL_CODE_INPUT = "#postal-code";
    private static final String CONTINUE_BUTTON = "#continue";
    private static final String CANCEL_BUTTON = "#cancel";
    private static final String ERROR_MESSAGE = "[data-test='error']";

    public CheckoutStepOnePage(Page page) {
        super(page);
    }

    /**
     * Fills checkout information form.
     *
     * @param info checkout information to enter
     */
    public void fillCheckoutInformation(CheckoutInfo info) {
        logger.info("Filling checkout information: {}", info);

        if (!info.isValid()) {
            logger.error("Invalid checkout info provided: {}", info);
            throw new IllegalArgumentException("CheckoutInfo is not valid");
        }

        fill(FIRST_NAME_INPUT, info.getFirstName());
        fill(LAST_NAME_INPUT, info.getLastName());
        fill(POSTAL_CODE_INPUT, info.getPostalCode());
    }

    /**
     * Clicks continue button to proceed to checkout step 2.
     */
    public void clickContinue() {
        click(CONTINUE_BUTTON);
        logger.info("Clicked continue to checkout step 2");
    }

    /**
     * Completes checkout step 1 by filling form and continuing.
     *
     * @param info checkout information
     */
    public void completeCheckoutStepOne(CheckoutInfo info) {
        fillCheckoutInformation(info);
        clickContinue();
        logger.info("Completed checkout step 1");
    }

    /**
     * Cancels checkout and returns to cart.
     */
    public void cancelCheckout() {
        click(CANCEL_BUTTON);
        logger.info("Cancelled checkout");
    }

    /**
     * Checks if error message is displayed.
     *
     * @return true if error visible
     */
    public boolean hasError() {
        return isVisible(ERROR_MESSAGE);
    }

    /**
     * Gets error message text.
     *
     * @return error message
     */
    public String getErrorMessage() {
        if (!hasError()) {
            return "";
        }
        return getText(ERROR_MESSAGE);
    }
}
