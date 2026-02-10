package com.spritecloud.models.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model representing an Authentication Token from FakeStoreAPI.
 * Implements proper encapsulation and validation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthToken {

    private String token;

    /**
     * Validates that token is present and not empty
     * @return true if token is valid
     */
    public boolean isValid() {
        return token != null && !token.isEmpty();
    }

    /**
     * Checks if token is of expected format (JWT typically)
     * @return true if token appears to be valid format
     */
    public boolean hasValidFormat() {
        // JWT tokens typically have 3 parts separated by dots
        return isValid() && token.split("\\.").length >= 2;
    }

    @Override
    public String toString() {
        // Mask token in logs for security
        String maskedToken = token != null && token.length() > 10
                ? token.substring(0, 10) + "..."
                : "***";
        return "AuthToken{token='" + maskedToken + "'}";
    }
}
