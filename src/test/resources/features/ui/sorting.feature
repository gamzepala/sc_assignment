@UI @Sorting @Regression
Feature: SauceDemo Product Sorting
  As a customer
  I want to sort products by different criteria
  So that I can find items more easily

  Background:
    Given I am on the SauceDemo login page
    And I login as a standard user
    And I am on the inventory page

  Scenario: Sort products by name Z to A
    When I sort products by "Name (Z to A)"
    Then the products should be displayed in reverse alphabetical order
    And the first product should be "Test.allTheThings() T-Shirt (Red)"
    And the last product should be "Sauce Labs Backpack"

  Scenario: Sort products by name A to Z (default)
    When I sort products by "Name (A to Z)"
    Then the products should be displayed in alphabetical order
    And the first product should be "Sauce Labs Backpack"
    And the last product should be "Test.allTheThings() T-Shirt (Red)"

  Scenario: Sort products by price low to high
    When I sort products by "Price (low to high)"
    Then the products should be sorted by price ascending
    And the first product price should be lower than the last product price

  Scenario: Sort products by price high to low
    When I sort products by "Price (high to low)"
    Then the products should be sorted by price descending
    And the first product price should be higher than the last product price
