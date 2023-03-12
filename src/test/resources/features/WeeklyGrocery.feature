Feature: Some basic grocery validations

  Scenario:
    Given I have a budget of 100 euro's
    And I have the following grocery list
      | name  | amount   | price | expirationDate            |
      | apple   | 5      | 0.4   | (today+15)T00:00:00.000Z  |
      | onion   | 10     | 0.25  | (today+20)T00:00:00.000Z  |
      | potato  | 10     | 0.20  | (today+22)T00:00:00.000Z  |
      | ketchup | 1      | 2.49  | (today+90)T00:00:00.000Z  |
      | rice    | 2      | 4.95  | (today+180)T00:00:00.000Z |
    When I go out and buy everything
    Then I expect to have some money left over
    And I validate the products will not expire at least for the next 14 days
