package com.spritecloud.models.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Domain model representing a Shopping Cart from FakeStoreAPI.
 * Implements proper encapsulation and validation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cart {

    private Integer id;

    @SerializedName("userId")
    private Integer userId;

    private String date;

    private List<CartProduct> products;

    @SerializedName("__v")
    private Integer version;

    /**
     * Validates cart structure
     * @return true if cart is valid
     */
    public boolean isValid() {
        return id != null && id > 0 &&
               userId != null && userId > 0 &&
               products != null && !products.isEmpty();
    }

    /**
     * Calculates total number of items in cart
     * @return total quantity
     */
    public int getTotalQuantity() {
        return products == null ? 0 : products.stream()
                .mapToInt(CartProduct::getQuantity)
                .sum();
    }

    /**
     * Checks if cart contains a specific product
     * @param productId Product ID to check
     * @return true if cart contains product
     */
    public boolean containsProduct(Integer productId) {
        return products != null && products.stream()
                .anyMatch(p -> p.getProductId().equals(productId));
    }

    /**
     * Nested class representing a product in the cart
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartProduct {
        @SerializedName("productId")
        private Integer productId;

        private Integer quantity;

        public boolean isValid() {
            return productId != null && productId > 0 &&
                   quantity != null && quantity > 0;
        }
    }
}
