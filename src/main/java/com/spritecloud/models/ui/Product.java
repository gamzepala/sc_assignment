package com.spritecloud.models.ui;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model representing a product in the UI.
 * Used for SauceDemo inventory items.
 *
 * Design Decision: Separate UI model from API model to maintain
 * clear boundaries between UI and API domains. UI products have
 * different properties and validation rules than API products.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private String name;
    private String description;
    private Double price;
    private String imageUrl;

    public Product(String name, String description, Double price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    /**
     * Validates that the product has all required fields.
     *
     * @return true if product is valid, false otherwise
     */
    public boolean isValid() {
        return name != null && !name.isEmpty() &&
               price != null && price > 0;
    }

    @Override
    public String toString() {
        return String.format("Product{name='%s', price=$%.2f}", name, price);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return name.equals(product.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
