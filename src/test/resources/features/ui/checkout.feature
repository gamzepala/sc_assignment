@UI @Checkout @Regression
Feature: SauceDemo Shopping Cart and Checkout
  As a customer
  I want to add products to my cart and complete checkout
  So that I can purchase items

  Background:
    Given I am on the SauceDemo login page
    And I login as a standard user
    And I am on the inventory page

  @E2E @Smoke @C65
  Scenario: Complete checkout with two items and validate final price
    When I add the following products to cart:
      | Sauce Labs Backpack     |
      | Sauce Labs Bike Light   |
    And I navigate to the shopping cart
    Then I should see 2 items in my cart
    When I proceed to checkout
    And I enter checkout information:
      | firstName | lastName | postalCode |
      | John      | Doe      | 12345      |
    And I continue to checkout overview
    Then I should see the correct item total for my cart items
    And I should see tax calculated correctly
    And I should see the correct total price including tax
    When I complete the checkout
    Then I should see order confirmation
    And the confirmation should say "Thank you for your order"

  @E2E @C66
  Scenario: Complete checkout with multiple items
    When I add the following products to cart:
      | Sauce Labs Backpack           |
      | Sauce Labs Bolt T-Shirt       |
      | Sauce Labs Onesie             |
    And I navigate to the shopping cart
    Then I should see 3 items in my cart
    When I proceed to checkout
    And I enter checkout information:
      | firstName | lastName | postalCode |
      | Jane      | Smith    | 67890      |
    And I continue to checkout overview
    Then the total calculation should be mathematically correct
    When I complete the checkout
    Then I should see order confirmation
