Feature: ApiProxy

  Scenario: recipes site
    When Run with recipes -site "itv" all
    Then it should return the following site: "itv"

