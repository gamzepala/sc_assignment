package com.spritecloud.pages;

import com.microsoft.playwright.Page;
import com.spritecloud.models.ui.CartItem;
import com.spritecloud.models.ui.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Page Object for SauceDemo Shopping Cart Page.
 * Handles viewing cart contents and proceeding to checkout.
 */
public class CartPage extends BasePage {

    private static final Logger logger = LoggerFactory.getLogger(CartPage.class);

    // Locators
    private static final String CART_ITEM = ".cart_item";
    private static final String ITEM_NAME = ".inventory_item_name";
    private static final String ITEM_PRICE = ".inventory_item_price";
    private static final String ITEM_QUANTITY = ".cart_quantity";
    private static final String CHECKOUT_BUTTON = "#checkout";
    private static final String CONTINUE_SHOPPING_BUTTON = "#continue-shopping";
    private static final String REMOVE_BUTTON = "button[id^='remove']";

    public CartPage(Page page) {
        super(page);
    }

    /**
     * Gets all items currently in the cart.
     *
     * @return list of CartItem objects
     */
    public List<CartItem> getCartItems() {
        int itemCount = getElementCount(CART_ITEM);
        List<CartItem> cartItems = new ArrayList<>();

        for (int i = 0; i < itemCount; i++) {
            String name = page.locator(CART_ITEM).nth(i).locator(ITEM_NAME).textContent();
            String priceText = page.locator(CART_ITEM).nth(i).locator(ITEM_PRICE).textContent();
            String quantityText = page.locator(CART_ITEM).nth(i).locator(ITEM_QUANTITY).textContent();

            Double price = parsePrice(priceText);
            int quantity = Integer.parseInt(quantityText);

            Product product = new Product(name, "", price);
            cartItems.add(new CartItem(product, quantity));
        }

        logger.debug("Retrieved {} items from cart", cartItems.size());
        return cartItems;
    }

    /**
     * Gets the number of items in the cart.
     *
     * @return cart item count
     */
    public int getCartItemCount() {
        return getElementCount(CART_ITEM);
    }

    /**
     * Proceeds to checkout.
     */
    public void proceedToCheckout() {
        click(CHECKOUT_BUTTON);
        logger.info("Proceeding to checkout");
    }

    /**
     * Continues shopping (returns to inventory).
     */
    public void continueShopping() {
        click(CONTINUE_SHOPPING_BUTTON);
        logger.info("Continuing shopping");
    }

    /**
     * Removes an item from cart by product name.
     *
     * @param productName name of product to remove
     */
    public void removeItem(String productName) {
        int itemCount = getElementCount(CART_ITEM);

        for (int i = 0; i < itemCount; i++) {
            String name = page.locator(CART_ITEM).nth(i).locator(ITEM_NAME).textContent();
            if (name.equals(productName)) {
                page.locator(CART_ITEM).nth(i).locator(REMOVE_BUTTON).click();
                logger.info("Removed '{}' from cart", productName);
                return;
            }
        }

        logger.error("Product not found in cart: {}", productName);
    }

    /**
     * Checks if cart is empty.
     *
     * @return true if cart has no items
     */
    public boolean isCartEmpty() {
        return getCartItemCount() == 0;
    }

    /**
     * Parses price from text format "$X.XX".
     *
     * @param priceText price with dollar sign
     * @return parsed price
     */
    private Double parsePrice(String priceText) {
        return Double.parseDouble(priceText.replace("$", ""));
    }
}
