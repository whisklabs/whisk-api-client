Feature: ApiProxy

  Scenario: shopping list options
    When Request shopping list options with store: "Tesco" and recipe url: "http://www.itv.com/food/recipes/salmon-spiced-lentils"
    Then it should return the shopping list with   store "Tesco"


