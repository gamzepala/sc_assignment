package com.spritecloud.models.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model representing a User from FakeStoreAPI.
 * Implements proper encapsulation and validation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    private Integer id;
    private String email;
    private String username;
    private String password;

    @SerializedName("name")
    private Name name;

    @SerializedName("address")
    private Address address;

    private String phone;

    /**
     * Validates critical user fields
     * @return true if user is valid
     */
    public boolean isValid() {
        return id != null && id > 0 &&
               email != null && !email.isEmpty() &&
               username != null && !username.isEmpty();
    }

    /**
     * Nested Name class for user's full name
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Name {
        private String firstname;
        private String lastname;

        public String getFullName() {
            return firstname + " " + lastname;
        }
    }

    /**
     * Nested Address class for user's address
     */
    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Address {
        private String city;
        private String street;
        private Integer number;
        private String zipcode;

        @SerializedName("geolocation")
        private GeoLocation geoLocation;
    }

    /**
     * Nested GeoLocation class for coordinates
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeoLocation {
        private String lat;
        private String lng;
    }
}
