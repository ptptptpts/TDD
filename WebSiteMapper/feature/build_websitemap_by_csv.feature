Feature: Build a WebsiteMap by csv
  User request to build a WebsiteMap by CSV format.

  CSV format sample:
  root,<children>...
  child1,<grandchildren>...
  child2,<grandchildren>...
  ...
  grandchildren1,<grandgrandchildren>...
  grandchildren2,<grandgrandchildren>...
  grandchildren3,<grandgrandchildren>...
  ...
  lastchildren1
  lastchildren2
  lastchildren3

  Scenario: User request to build a one depth CSV map with a valid url
    Given Return two depth html document for testing when read from the given url
    When Build WebsiteMap with the given url with 1 depth by "CSV" format
    Then Output CSV format WebsiteMap is same with 1 depth CSV solution

  Scenario: User request to build a one depth CSV map with a valid url that contains empty data
    Given Return an empty html document when read from the given url
    When Build WebsiteMap with the given url with 1 depth by "CSV" format
    Then Output CSV format WebsiteMap is same with 0 depth CSV solution

  Scenario: User request to build a one depth CSV map with an invalid url
    Given Return a Exception when read from the given url
    When Build WebsiteMap with the given url with 1 depth by "CSV" format
    Then Output CSV format WebsiteMap is same with 0 depth CSV solution

  Scenario: User request to build a two depth CSV map with a valid url
    Given Return two depth html document for testing when read from the given url
    When Build WebsiteMap with the given url with 2 depth by "CSV" format
    Then Output CSV format WebsiteMap is same with 2 depth CSV solution

  Scenario: User request to build a three depth CSV map with a valid url that contains two depth data
    Given Return two depth html document for testing when read from the given url
    When Build WebsiteMap with the given url with 3 depth by "CSV" format
    Then Output CSV format WebsiteMap is same with 2 depth CSV solution