Feature: Check Recipe
  Scenario: Check Recipe
    When I  create a new session
    And I  ask it to check for  recipe url recipe "http://www.itv.com/food/recipes/salmon-spiced-lentils"
    Then it  should return the recipe with Url "http://www.itv.com/food/recipes/salmon-spiced-lentils"
