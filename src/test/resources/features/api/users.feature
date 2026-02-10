@API @Users @Regression
Feature: User Management
  As an API consumer
  I want to manage users
  So that I can perform CRUD operations on user accounts

  Scenario: Delete an existing user
    Given I have an existing user with ID 1
    When I send a DELETE request for the user
    Then the response status code should be 200
    And the response should confirm the deletion
    And the deleted user object should be returned
    And the response time should be less than 3000 milliseconds

  Scenario: Retrieve a user and validate their information
    Given I want to retrieve user with ID 1
    When I send a GET request to users endpoint
    Then the response status code should be 200
    And the user should have a valid ID
    And the user should have a valid email address
    And the user should have a valid username
    And the user should have valid name information
    And the user structure should be valid
