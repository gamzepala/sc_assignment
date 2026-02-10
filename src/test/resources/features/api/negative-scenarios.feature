@API @NegativeTests @Regression
Feature: API Negative Scenarios
  As an API consumer
  I want to handle edge cases properly
  So that my application can handle unusual inputs gracefully

  @NegativeTest @C63
  Scenario: Attempt to retrieve a non-existent product
    Given I request a product with non-existent ID 999999
    When I send a GET request to products endpoint
    Then the response status code should be 200
    And the response time should be less than 3000 milliseconds

  @NegativeTest @C64
  Scenario: Attempt to create a cart with incomplete data
    Given I have invalid cart data with missing required fields
    When I send a POST request to create a cart
    Then the response status code should be 201
    And the response time should be less than 3000 milliseconds
