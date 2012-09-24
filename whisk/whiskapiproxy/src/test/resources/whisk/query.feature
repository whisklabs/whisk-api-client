Feature: Query
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