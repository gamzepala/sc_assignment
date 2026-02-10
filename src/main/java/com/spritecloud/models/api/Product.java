package com.spritecloud.models.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model representing a Product from FakeStoreAPI.
 * Implements proper encapsulation and validation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {

    private Integer id;
    private String title;
    private Double price;
    private String description;
    private String category;
    private String image;

    @SerializedName("rating")
    private Rating rating;

    /**
     * Validates that all critical fields are present
     * @return true if product is valid
     */
    public boolean isValid() {
        return id != null && id > 0 &&
               title != null && !title.isEmpty() &&
               price != null && price >= 0 &&
               category != null && !category.isEmpty();
    }

    /**
     * Nested Rating class for product ratings
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rating {
        private Double rate;
        private Integer count;

        public boolean isValid() {
            return rate != null && rate >= 0 && rate <= 5 &&
                   count != null && count >= 0;
        }
    }
}
