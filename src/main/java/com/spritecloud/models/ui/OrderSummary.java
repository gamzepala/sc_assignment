package com.spritecloud.models.ui;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Domain model representing the order summary on checkout overview page.
 * Contains price calculations including subtotal, tax, and total.
 *
 * Design Decision: Encapsulates all price-related data with
 * validation logic to ensure correct calculations and business rules.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummary {
    private List<CartItem> items;
    private Double itemTotal;
    private Double tax;
    private Double total;

    /**
     * Calculates expected item total from cart items.
     *
     * @return sum of all cart item subtotals
     */
    public Double calculateExpectedItemTotal() {
        if (items == null || items.isEmpty()) {
            return 0.0;
        }
        return items.stream()
                .map(CartItem::getSubtotal)
                .reduce(0.0, Double::sum);
    }

    /**
     * Validates that the displayed total matches calculated values.
     * Allows small rounding differences (within $0.01).
     *
     * @return true if calculations are correct, false otherwise
     */
    public boolean isCalculationValid() {
        if (itemTotal == null || tax == null || total == null) {
            return false;
        }

        Double expectedTotal = itemTotal + tax;
        Double difference = Math.abs(expectedTotal - total);

        // Allow rounding difference of up to 1 cent
        return difference < 0.01;
    }

    /**
     * Validates tax calculation is reasonable (typically 8% for SauceDemo).
     *
     * @return true if tax is within expected range
     */
    public boolean isTaxReasonable() {
        if (itemTotal == null || tax == null || itemTotal == 0) {
            return false;
        }

        double taxRate = (tax / itemTotal) * 100;
        // SauceDemo uses 8% tax rate, allow small variance
        return taxRate >= 7.0 && taxRate <= 9.0;
    }

    @Override
    public String toString() {
        return String.format("OrderSummary{items=%d, itemTotal=$%.2f, tax=$%.2f, total=$%.2f}",
                items != null ? items.size() : 0, itemTotal, tax, total);
    }
}
