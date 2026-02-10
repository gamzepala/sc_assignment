@API @Cart @Regression
Feature: Shopping Cart Management
  As an API consumer
  I want to manage shopping carts
  So that users can add products to their carts

  Scenario: Create a new cart with an existing product
    Given I have a valid user ID 1
    And I have an existing product ID 1
    When I create a new cart with the product
    Then the response status code should be 201
    And the response should contain a valid cart ID
    And the cart should belong to the correct user
    And the cart should contain the specified product
    And the product quantity should be correct
    And the cart structure should be valid
    And the response time should be less than 3000 milliseconds

  Scenario: Retrieve user's cart and validate structure
    Given I have a user with ID 1
    When I retrieve carts for the user
    Then the response status code should be 200
    And the response should contain cart information
    And each cart should have valid structure
