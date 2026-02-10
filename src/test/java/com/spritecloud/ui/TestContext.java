package com.spritecloud.ui;

import com.microsoft.playwright.*;
import com.spritecloud.models.ui.CartItem;
import com.spritecloud.models.ui.CheckoutInfo;
import com.spritecloud.models.ui.OrderSummary;
import com.spritecloud.pages.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Test context for UI tests using Cucumber PicoContainer.
 * Manages Playwright browser instances and page objects.
 * Shared across all step definitions within a scenario.
 *
 * Design Decision: Centralized browser and page object management
 * ensures consistent state handling and resource cleanup.
 */
public class TestContext {

    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    // Page Objects
    private LoginPage loginPage;
    private InventoryPage inventoryPage;
    private CartPage cartPage;
    private CheckoutStepOnePage checkoutStepOnePage;
    private CheckoutStepTwoPage checkoutStepTwoPage;
    private CheckoutCompletePage checkoutCompletePage;

    // Test Data
    private List<CartItem> expectedCartItems;
    private CheckoutInfo checkoutInfo;
    private OrderSummary orderSummary;

    public TestContext() {
        this.expectedCartItems = new ArrayList<>();
    }

    /**
     * Initializes Playwright and launches browser.
     */
    public void initializeBrowser() {
        if (playwright == null) {
            playwright = Playwright.create();
            browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(true) // Set to false for debugging
                    .setSlowMo(0));    // Slow down for debugging if needed
        }
    }

    /**
     * Creates a new browser context and page for a test.
     */
    public void createNewContext() {
        if (context != null) {
            context.close();
        }

        context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1920, 1080)
                .setBaseURL("https://www.saucedemo.com"));

        page = context.newPage();
        initializePageObjects();
    }

    /**
     * Initializes all page objects with the current page instance.
     */
    private void initializePageObjects() {
        loginPage = new LoginPage(page);
        inventoryPage = new InventoryPage(page);
        cartPage = new CartPage(page);
        checkoutStepOnePage = new CheckoutStepOnePage(page);
        checkoutStepTwoPage = new CheckoutStepTwoPage(page);
        checkoutCompletePage = new CheckoutCompletePage(page);
    }

    /**
     * Closes the browser context.
     */
    public void closeContext() {
        if (context != null) {
            context.close();
            context = null;
        }
    }

    /**
     * Closes browser and Playwright instance.
     */
    public void closeBrowser() {
        if (browser != null) {
            browser.close();
            browser = null;
        }
        if (playwright != null) {
            playwright.close();
            playwright = null;
        }
    }

    // Getters for page objects
    public Page getPage() {
        return page;
    }

    public LoginPage getLoginPage() {
        return loginPage;
    }

    public InventoryPage getInventoryPage() {
        return inventoryPage;
    }

    public CartPage getCartPage() {
        return cartPage;
    }

    public CheckoutStepOnePage getCheckoutStepOnePage() {
        return checkoutStepOnePage;
    }

    public CheckoutStepTwoPage getCheckoutStepTwoPage() {
        return checkoutStepTwoPage;
    }

    public CheckoutCompletePage getCheckoutCompletePage() {
        return checkoutCompletePage;
    }

    // Test data getters/setters
    public List<CartItem> getExpectedCartItems() {
        return expectedCartItems;
    }

    public void addExpectedCartItem(CartItem item) {
        this.expectedCartItems.add(item);
    }

    public void clearExpectedCartItems() {
        this.expectedCartItems.clear();
    }

    public CheckoutInfo getCheckoutInfo() {
        return checkoutInfo;
    }

    public void setCheckoutInfo(CheckoutInfo checkoutInfo) {
        this.checkoutInfo = checkoutInfo;
    }

    public OrderSummary getOrderSummary() {
        return orderSummary;
    }

    public void setOrderSummary(OrderSummary orderSummary) {
        this.orderSummary = orderSummary;
    }
}
