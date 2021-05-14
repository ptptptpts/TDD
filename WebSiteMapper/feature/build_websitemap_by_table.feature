Feature: Build a WebsiteMap by table
  User request to build a WebsiteMap by table format.

  Scenario: User request to build a one depth CSV map with a valid url
    Given Return two depth html document for testing when read from the given url
    When Build WebsiteMap with the given url with 1 depth by "table" format
    Then Output Table format WebsiteMap is same with 1 depth Table solution

  Scenario: User request to build a one depth CSV map with a valid url that contains empty data
    Given Return an empty html document when read from the given url
    When Build WebsiteMap with the given url with 1 depth by "table" format
    Then Output Table format WebsiteMap is same with 0 depth Table solution

  Scenario: User request to build a one depth CSV map with an invalid url
    Given Return a Exception when read from the given url
    When Build WebsiteMap with the given url with 1 depth by "table" format
    Then Output Table format WebsiteMap is same with 0 depth Table solution

  Scenario: User request to build a two depth CSV map with a valid url
    Given Return two depth html document for testing when read from the given url
    When Build WebsiteMap with the given url with 2 depth by "table" format
    Then Output Table format WebsiteMap is same with 2 depth Table solution

  Scenario: User request to build a three depth CSV map with a valid url that contains two depth data
    Given Return two depth html document for testing when read from the given url
    When Build WebsiteMap with the given url with 3 depth by "table" format
    Then Output Table format WebsiteMap is same with 2 depth Table solution