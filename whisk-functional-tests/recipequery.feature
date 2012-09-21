Feature: Recipe Query

  Scenario: Top Chefs
    When I create a new session
    And I ask it to search for "topchefs"
    Then it should return the following chefs:
      """
      Jamie Oliver
      Gordon Ramsey
      """

  Scenario: Search for recipe
    When I create a new session
    And I ask it to search for the text "salmon lentils"
    And the site "itv"
    # The search should be /recipes/query?searchText=salmon+lentils&site=itv
    Then it should return the recipe with Url "http://www.itv.com/food/recipes/salmon-spiced-lentils"

  Scenario: Search for recipe and add to favourites
    When I create a new session
    And I ask it to search for the text "salmon lentils"
    And the site "itv"
    And then add to favourites the recipe with Url "http://www.itv.com/food/recipes/salmon-spiced-lentils"
    Then my favourites should include the recipe with Url "http://www.itv.com/food/recipes/salmon-spiced-lentils"

  Scenario: Get shopping list options and add to shopping list
    When I create a new session
    And I ask it to get the shopping list options for "http://www.jamieoliver.com/recipes/meat-recipes/the-best-sausages-with-braised-lentils-a"
    And I change the option for "1 kg sausages" to "Butchers Choice 8 Half Fat Pork Sausages 454G"
    And I add the recipe to a shopping list at "Tesco"
    The shopping list should include "Butchers Choice 8 Half Fat Pork Sausages 454G"