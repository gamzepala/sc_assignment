@API @Products @Regression
Feature: Product Management
  As an API consumer
  I want to retrieve product information
  So that I can display products to users

  @Smoke
  Scenario: Get a product and validate its content
    Given I request product with ID 1
    When I send a GET request to products endpoint
    Then the response status code should be 200
    And the response content type should be "application/json"
    And the product should have a valid ID
    And the product should have a non-empty title
    And the product should have a valid price greater than or equal to 0
    And the product should have a valid category
    And the product should have a valid image URL
    And the product should have valid rating information
    And the response time should be less than 3000 milliseconds

  Scenario: Retrieve all products and validate list structure
    Given I want to retrieve all products
    When I send a GET request to products endpoint
    Then the response status code should be 200
    And the response should contain a list of products
    And all products should have valid structure
