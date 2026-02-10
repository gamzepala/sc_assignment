package com.spritecloud.models.ui;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model representing a cart item in the shopping cart.
 * Combines a product with its quantity and calculates subtotal.
 *
 * <p>Design Decision: Encapsulates cart line item logic including
 * subtotal calculation to maintain single responsibility principle.
 * Validation ensures data integrity before checkout operations.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private Product product;
    private int quantity;

    /**
     * Calculates the subtotal price for this cart item.
     *
     * @return the product price multiplied by quantity, or 0.0 if product/price is null
     */
    public Double getSubtotal() {
        if (product == null || product.getPrice() == null) {
            return 0.0;
        }
        return product.getPrice() * quantity;
    }

    /**
     * Validates that the cart item is in a valid state.
     *
     * @return true if product is valid and quantity is positive, false otherwise
     */
    public boolean isValid() {
        return product != null && product.isValid() && quantity > 0;
    }

    @Override
    public String toString() {
        return String.format("CartItem{product=%s, quantity=%d, subtotal=$%.2f}",
                product, quantity, getSubtotal());
    }
}
