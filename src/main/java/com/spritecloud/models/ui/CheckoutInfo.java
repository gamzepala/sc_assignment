package com.spritecloud.models.ui;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model representing checkout information in SauceDemo.
 * Contains customer details required for completing a purchase.
 *
 * Design Decision: Encapsulates checkout form data with validation
 * logic, ensuring data integrity before submitting orders.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutInfo {
    private String firstName;
    private String lastName;
    private String postalCode;

    /**
     * Validates that all required checkout fields are populated.
     *
     * @return true if all fields are valid, false otherwise
     */
    public boolean isValid() {
        return firstName != null && !firstName.trim().isEmpty() &&
               lastName != null && !lastName.trim().isEmpty() &&
               postalCode != null && !postalCode.trim().isEmpty();
    }

    @Override
    public String toString() {
        return String.format("CheckoutInfo{firstName='%s', lastName='%s', postalCode='%s'}",
                firstName, lastName, postalCode);
    }
}
