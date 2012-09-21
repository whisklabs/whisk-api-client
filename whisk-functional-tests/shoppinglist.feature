Feature: Shopping list

  Scenario: Get shopping list options and add to shopping list
    When I create a new session
    And I ask it to get the shopping list options for "http://www.jamieoliver.com/recipes/meat-recipes/the-best-sausages-with-braised-lentils-a"
    And I change the option for "1 kg sausages" to "Butchers Choice 8 Half Fat Pork Sausages 454G"
    And I add the recipe to a shopping list at "Tesco"
    The shopping list should include "Butchers Choice 8 Half Fat Pork Sausages 454G"