@UI @Login @Regression
Feature: SauceDemo Login
  As a user of SauceDemo
  I want to log in to the application
  So that I can access the product inventory

  Background:
    Given I am on the SauceDemo login page

  @NegativeTest
  Scenario: Failed login with invalid credentials
    When I attempt to login with username "invalid_user" and password "wrong_password"
    Then I should see an error message containing "Username and password do not match"
    And I should remain on the login page

  @NegativeTest
  Scenario: Failed login with locked out user
    When I attempt to login with username "locked_out_user" and password "secret_sauce"
    Then I should see an error message containing "locked out"
    And I should remain on the login page

  @NegativeTest
  Scenario: Failed login with empty credentials
    When I attempt to login with username "" and password ""
    Then I should see an error message containing "Username is required"
    And I should remain on the login page
