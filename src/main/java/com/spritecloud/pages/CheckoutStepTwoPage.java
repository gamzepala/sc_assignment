package com.spritecloud.pages;

import com.microsoft.playwright.Page;
import com.spritecloud.models.ui.CartItem;
import com.spritecloud.models.ui.OrderSummary;
import com.spritecloud.models.ui.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Page Object for SauceDemo Checkout Step 2 (Order Overview).
 * Handles reviewing order summary and completing purchase.
 */
public class CheckoutStepTwoPage extends BasePage {

    private static final Logger logger = LoggerFactory.getLogger(CheckoutStepTwoPage.class);

    // Locators
    private static final String CART_ITEM = ".cart_item";
    private static final String ITEM_NAME = ".inventory_item_name";
    private static final String ITEM_PRICE = ".inventory_item_price";
    private static final String ITEM_QUANTITY = ".cart_quantity";
    private static final String SUBTOTAL_LABEL = ".summary_subtotal_label";
    private static final String TAX_LABEL = ".summary_tax_label";
    private static final String TOTAL_LABEL = ".summary_total_label";
    private static final String FINISH_BUTTON = "#finish";
    private static final String CANCEL_BUTTON = "#cancel";

    public CheckoutStepTwoPage(Page page) {
        super(page);
    }

    /**
     * Gets complete order summary with all price calculations.
     *
     * @return OrderSummary object with items and prices
     */
    public OrderSummary getOrderSummary() {
        List<CartItem> items = getOrderItems();
        Double itemTotal = getItemTotal();
        Double tax = getTax();
        Double total = getTotal();

        OrderSummary summary = new OrderSummary(items, itemTotal, tax, total);
        logger.debug("Order summary: {}", summary);
        return summary;
    }

    /**
     * Gets all items in the order.
     *
     * @return list of cart items
     */
    private List<CartItem> getOrderItems() {
        int itemCount = getElementCount(CART_ITEM);
        List<CartItem> items = new ArrayList<>();

        for (int i = 0; i < itemCount; i++) {
            String name = page.locator(CART_ITEM).nth(i).locator(ITEM_NAME).textContent();
            String priceText = page.locator(CART_ITEM).nth(i).locator(ITEM_PRICE).textContent();
            String quantityText = page.locator(CART_ITEM).nth(i).locator(ITEM_QUANTITY).textContent();

            Double price = parsePrice(priceText);
            int quantity = Integer.parseInt(quantityText);

            Product product = new Product(name, "", price);
            items.add(new CartItem(product, quantity));
        }

        return items;
    }

    /**
     * Gets the item subtotal (before tax).
     *
     * @return item total
     */
    public Double getItemTotal() {
        String text = getText(SUBTOTAL_LABEL);
        // Format: "Item total: $XX.XX"
        return parsePriceFromLabel(text);
    }

    /**
     * Gets the tax amount.
     *
     * @return tax amount
     */
    public Double getTax() {
        String text = getText(TAX_LABEL);
        // Format: "Tax: $X.XX"
        return parsePriceFromLabel(text);
    }

    /**
     * Gets the total amount (items + tax).
     *
     * @return total amount
     */
    public Double getTotal() {
        String text = getText(TOTAL_LABEL);
        // Format: "Total: $XX.XX"
        return parsePriceFromLabel(text);
    }

    /**
     * Completes the purchase by clicking finish.
     */
    public void finishCheckout() {
        click(FINISH_BUTTON);
        logger.info("Completed checkout");
    }

    /**
     * Cancels checkout and returns to inventory.
     */
    public void cancelCheckout() {
        click(CANCEL_BUTTON);
        logger.info("Cancelled checkout");
    }

    /**
     * Validates that the total price matches item total + tax.
     *
     * @return true if calculation is correct
     */
    public boolean isTotalCalculationCorrect() {
        OrderSummary summary = getOrderSummary();
        boolean isValid = summary.isCalculationValid();
        logger.debug("Total calculation valid: {}", isValid);
        return isValid;
    }

    /**
     * Parses price from label text that contains format "Label: $X.XX".
     *
     * @param labelText label text with price
     * @return parsed price
     */
    private Double parsePriceFromLabel(String labelText) {
        // Extract price portion after the colon
        String pricePart = labelText.substring(labelText.indexOf('$'));
        return parsePrice(pricePart);
    }

    /**
     * Parses price from text format "$X.XX".
     *
     * @param priceText price with dollar sign
     * @return parsed price
     */
    private Double parsePrice(String priceText) {
        return Double.parseDouble(priceText.replace("$", "").trim());
    }
}
