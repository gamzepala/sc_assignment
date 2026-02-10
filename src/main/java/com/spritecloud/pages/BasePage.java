package com.spritecloud.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides common functionality for interacting with web pages using Playwright.
 *
 * <p>Key Features:
 * <ul>
 *   <li>Automatic waiting for elements to be visible before interaction</li>
 *   <li>Consistent logging for debugging and traceability</li>
 *   <li>Default timeout configuration</li>
 *   <li>Safe element visibility checks with exception handling</li>
 * </ul>
 */
public abstract class BasePage {

    protected static final Logger logger = LoggerFactory.getLogger(BasePage.class);
    protected final Page page;
    protected static final int DEFAULT_TIMEOUT = 30000;

    /**
     * Constructs a BasePage with the given Playwright page instance.
     *
     * @param page the Playwright page instance to interact with
     */
    public BasePage(Page page) {
        this.page = page;
    }

    /**
     * Waits for an element to become visible on the page.
     *
     * @param selector the CSS selector of the element to wait for
     * @throws com.microsoft.playwright.TimeoutError if element doesn't become visible within timeout
     */
    protected void waitForElement(String selector) {
        page.waitForSelector(selector, new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(DEFAULT_TIMEOUT));
        logger.debug("Element visible: {}", selector);
    }

    /**
     * Clicks an element after waiting for it to become visible.
     *
     * @param selector the CSS selector of the element to click
     */
    protected void click(String selector) {
        waitForElement(selector);
        page.click(selector);
        logger.debug("Clicked: {}", selector);
    }

    /**
     * Fills a form field with the specified text after waiting for it to become visible.
     *
     * @param selector the CSS selector of the input element
     * @param text the text to fill into the field
     */
    protected void fill(String selector, String text) {
        waitForElement(selector);
        page.fill(selector, text);
        logger.debug("Filled '{}' into: {}", text, selector);
    }

    /**
     * Gets the text content of an element after waiting for it to become visible.
     *
     * @param selector the CSS selector of the element
     * @return the text content of the element
     */
    protected String getText(String selector) {
        waitForElement(selector);
        return page.textContent(selector);
    }

    /**
     * Checks if an element is visible on the page without waiting.
     *
     * @param selector the CSS selector of the element to check
     * @return true if the element is visible, false otherwise (including if it doesn't exist)
     */
    protected boolean isVisible(String selector) {
        try {
            return page.isVisible(selector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets the current URL of the page.
     *
     * @return the current page URL
     */
    public String getCurrentUrl() {
        return page.url();
    }

    /**
     * Waits for the URL to match the expected path pattern.
     *
     * @param expectedPath the expected path to wait for (will be matched with wildcard prefix)
     */
    protected void waitForUrl(String expectedPath) {
        page.waitForURL("**/" + expectedPath);
        logger.debug("URL changed to path: {}", expectedPath);
    }

    /**
     * Gets all text contents from all elements matching the selector.
     *
     * @param selector the CSS selector to match elements
     * @return an array of text contents from all matching elements
     */
    protected String[] getAllText(String selector) {
        return page.locator(selector).allTextContents().toArray(new String[0]);
    }

    /**
     * Counts the number of elements matching the selector.
     *
     * @param selector the CSS selector to match elements
     * @return the count of matching elements
     */
    protected int getElementCount(String selector) {
        return page.locator(selector).count();
    }

    /**
     * Navigates to the specified URL.
     *
     * @param url the URL to navigate to
     */
    public void navigateTo(String url) {
        page.navigate(url);
        logger.info("Navigated to: {}", url);
    }
}
