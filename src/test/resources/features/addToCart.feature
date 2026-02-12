Feature: Add to Cart on Demoblaze
  As a shopper
  I want to add products to the cart
  So that I can review them before checkout

  Background:
    Given I am on the Demoblaze home page

  @smoke
  Scenario: Add one product to cart and verify it in cart
    When I open category "Phones"
    And I open product "Samsung galaxy s6"
    And I add the product to the cart
    Then I should see "Samsung galaxy s6" in the cart

  @regression
  Scenario: Add two products and verify total items in cart
    When I open category "Laptops"
    And I open product "Sony vaio i5"
    And I add the product to the cart
    And I go back to home
    And I open category "Monitors"
    And I open product "Apple monitor 24"
    And I add the product to the cart
    Then I should see 2 items in the cart

  @delete
  Scenario: Delete a product from cart
    When I open category "Phones"
    And I open product "Nokia lumia 1520"
    And I add the product to the cart
    And I open the cart page
    And I delete product "Nokia lumia 1520" from the cart
    Then I should not see "Nokia lumia 1520" in the cart