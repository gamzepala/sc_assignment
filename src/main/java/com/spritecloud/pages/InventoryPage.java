package com.spritecloud.pages;

import com.microsoft.playwright.Page;
import com.spritecloud.models.ui.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Page Object for SauceDemo Inventory (Products) Page.
 * Handles product browsing, sorting, and adding items to cart.
 */
public class InventoryPage extends BasePage {

    private static final Logger logger = LoggerFactory.getLogger(InventoryPage.class);

    // Locators
    private static final String INVENTORY_ITEM = ".inventory_item";
    private static final String ITEM_NAME = ".inventory_item_name";
    private static final String ITEM_PRICE = ".inventory_item_price";
    private static final String ITEM_DESCRIPTION = ".inventory_item_desc";
    private static final String ADD_TO_CART_BUTTON = "button[id^='add-to-cart']";
    private static final String REMOVE_BUTTON = "button[id^='remove']";
    private static final String SHOPPING_CART_LINK = ".shopping_cart_link";
    private static final String SHOPPING_CART_BADGE = ".shopping_cart_badge";
    private static final String SORT_DROPDOWN = ".product_sort_container";

    // Sort options
    public enum SortOption {
        NAME_A_TO_Z("az"),
        NAME_Z_TO_A("za"),
        PRICE_LOW_TO_HIGH("lohi"),
        PRICE_HIGH_TO_LOW("hilo");

        private final String value;

        SortOption(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public InventoryPage(Page page) {
        super(page);
    }

    /**
     * Waits for inventory page to load.
     */
    public void waitForInventoryPageLoad() {
        waitForElement(INVENTORY_ITEM);
        logger.info("Inventory page loaded");
    }

    /**
     * Gets all products displayed on the page.
     *
     * @return list of Product objects
     */
    public List<Product> getAllProducts() {
        int productCount = getElementCount(INVENTORY_ITEM);
        List<Product> products = new ArrayList<>();

        for (int i = 0; i < productCount; i++) {
            String name = page.locator(ITEM_NAME).nth(i).textContent();
            String priceText = page.locator(ITEM_PRICE).nth(i).textContent();
            String description = page.locator(ITEM_DESCRIPTION).nth(i).textContent();

            Double price = parsePriceFromText(priceText);
            products.add(new Product(name, description, price));
        }

        logger.debug("Retrieved {} products from inventory", products.size());
        return products;
    }

    /**
     * Gets all product names in their current display order.
     *
     * @return list of product names
     */
    public List<String> getProductNames() {
        String[] names = getAllText(ITEM_NAME);
        logger.debug("Product names: {}", String.join(", ", names));
        return List.of(names);
    }

    /**
     * Adds a product to cart by its name.
     *
     * @param productName name of product to add
     */
    public void addProductToCart(String productName) {
        logger.info("Adding product to cart: {}", productName);

        // Find the product by name and click its "Add to cart" button
        int productCount = getElementCount(INVENTORY_ITEM);
        boolean found = false;

        for (int i = 0; i < productCount; i++) {
            String name = page.locator(ITEM_NAME).nth(i).textContent();
            if (name.equals(productName)) {
                page.locator(INVENTORY_ITEM).nth(i).locator(ADD_TO_CART_BUTTON).click();
                logger.info("Added '{}' to cart", productName);
                found = true;
                break;
            }
        }

        if (!found) {
            logger.error("Product not found: {}", productName);
            throw new IllegalArgumentException("Product not found: " + productName);
        }
    }

    /**
     * Sorts products by selected option.
     *
     * @param sortOption sort option to apply
     */
    public void sortProducts(SortOption sortOption) {
        logger.info("Sorting products: {}", sortOption);
        page.selectOption(SORT_DROPDOWN, sortOption.getValue());

        // Wait a moment for sorting to complete
        page.waitForTimeout(500);
        logger.debug("Products sorted by: {}", sortOption);
    }

    /**
     * Gets the shopping cart badge count.
     *
     * @return number of items in cart, 0 if badge not visible
     */
    public int getCartItemCount() {
        if (!isVisible(SHOPPING_CART_BADGE)) {
            return 0;
        }
        String badgeText = getText(SHOPPING_CART_BADGE);
        return Integer.parseInt(badgeText);
    }

    /**
     * Navigates to shopping cart page.
     */
    public void goToCart() {
        click(SHOPPING_CART_LINK);
        logger.info("Navigated to shopping cart");
    }

    /**
     * Validates product sorting order is correct for Z-A.
     *
     * @return true if products are sorted Z-A, false otherwise
     */
    public boolean isProductsSortedZtoA() {
        List<String> productNames = getProductNames();

        for (int i = 0; i < productNames.size() - 1; i++) {
            String current = productNames.get(i);
            String next = productNames.get(i + 1);

            // For Z-A sorting, current should be >= next (alphabetically)
            if (current.compareToIgnoreCase(next) < 0) {
                logger.error("Sorting error: '{}' should come after '{}'", current, next);
                return false;
            }
        }

        logger.info("Products correctly sorted Z-A");
        return true;
    }

    /**
     * Parses price from text format "$X.XX".
     *
     * @param priceText price text with dollar sign
     * @return parsed price as Double
     */
    private Double parsePriceFromText(String priceText) {
        // Remove "$" and parse
        return Double.parseDouble(priceText.replace("$", ""));
    }
}
