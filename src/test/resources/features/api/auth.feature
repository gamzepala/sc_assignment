@API @Authentication @Regression
Feature: User Authentication
  As an API consumer
  I want to authenticate users
  So that I can access protected resources

  @Smoke
  Scenario: Successful user login with valid credentials
    Given I have valid user credentials
    When I send a login request
    Then the response status code should be 201
    And the response should contain a valid authentication token
    And the token should have proper format
    And the response time should be less than 3000 milliseconds
