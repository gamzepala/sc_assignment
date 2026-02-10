package com.spritecloud.pages;

import com.microsoft.playwright.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page Object for SauceDemo Checkout Complete Page.
 * Handles order confirmation validation.
 */
public class CheckoutCompletePage extends BasePage {

    private static final Logger logger = LoggerFactory.getLogger(CheckoutCompletePage.class);

    // Locators
    private static final String COMPLETE_HEADER = ".complete-header";
    private static final String COMPLETE_TEXT = ".complete-text";
    private static final String PONY_EXPRESS_IMAGE = ".pony_express";
    private static final String BACK_HOME_BUTTON = "#back-to-products";

    public CheckoutCompletePage(Page page) {
        super(page);
    }

    /**
     * Gets the order confirmation header text.
     *
     * @return header text
     */
    public String getConfirmationHeader() {
        String header = getText(COMPLETE_HEADER);
        logger.debug("Confirmation header: {}", header);
        return header;
    }

    /**
     * Gets the order confirmation message.
     *
     * @return confirmation message
     */
    public String getConfirmationMessage() {
        return getText(COMPLETE_TEXT);
    }

    /**
     * Validates that order was successfully completed.
     *
     * @return true if success message displayed
     */
    public boolean isOrderComplete() {
        boolean isComplete = isVisible(COMPLETE_HEADER) &&
                             isVisible(PONY_EXPRESS_IMAGE) &&
                             getConfirmationHeader().contains("Thank you");

        logger.info("Order complete: {}", isComplete);
        return isComplete;
    }

    /**
     * Returns to products page.
     */
    public void backToProducts() {
        click(BACK_HOME_BUTTON);
        logger.info("Returned to products page");
    }

    /**
     * Waits for checkout complete page to load.
     */
    public void waitForCheckoutComplete() {
        waitForElement(COMPLETE_HEADER);
        logger.info("Checkout complete page loaded");
    }
}
