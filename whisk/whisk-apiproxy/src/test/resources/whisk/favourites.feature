Feature: Favourites

  Scenario: Search for recipe and add to favourites
    When I Create  a  new session
    And I ask it to search for the text "salmon lentils"
    And the site name "itv"
    And then add to favourites the recipe with Url "http://www.itv.com/food/recipes/salmon-spiced-lentils"
    Then my favourites should include the recipe with Url "http://www.itv.com/food/recipes/salmon-spiced-lentils"