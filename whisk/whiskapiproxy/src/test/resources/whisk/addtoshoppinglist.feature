Feature: ApiProxy

  Scenario: add to shopping list
    When Add to shopping list with  name: "test" store: "Tesco" and recipe url "http://www.itv.com/food/recipes/salmon-spiced-lentils"
    Then it should return the shopping list with  name "test" and store "Tesco"


