Feature: Searching ONS website content

  Scenario: When Searching for Retail Price Index I get the same results as RPI
    When a user searches for the term(s) "Retail Price Index"
    Then the user will receive the following documents on the first page
    | /economy/inflationandpriceindices/datasets/consumerpriceinflation |
    | /economy/inflationandpriceindices/timeseries/czbh/mm23 |
    | /economy/inflationandpriceindices/timeseries/chaw/mm23 |
    | /economy/inflationandpriceindices/timeseries/cznb/mm23 |
    | /economy/inflationandpriceindices/timeseries/cznb/mm23 |
    | /economy/inflationandpriceindices/timeseries/dohn/mm23 |
    | /economy/inflationandpriceindices/timeseries/czng/mm23 |
    | /economy/inflationandpriceindices/timeseries/choh/mm23 |
    | /economy/inflationandpriceindices/timeseries/chbf/mm23 |


  Scenario: When Searching for RPI I get the same results as Retail Price Index
    When a user searches for the term(s) "RPI"
    Then the user will receive the following documents on the first page
      | /economy/inflationandpriceindices/datasets/consumerpriceinflation |
      | /economy/inflationandpriceindices/timeseries/czbh/mm23 |
      | /economy/inflationandpriceindices/timeseries/chaw/mm23 |
      | /economy/inflationandpriceindices/timeseries/cznb/mm23 |
      | /economy/inflationandpriceindices/timeseries/dohn/mm23 |
      | /economy/inflationandpriceindices/timeseries/czng/mm23 |
      | /economy/inflationandpriceindices/timeseries/choh/mm23 |
      | /economy/inflationandpriceindices/timeseries/chbf/mm23 |



  Scenario: When Searching for CPI I get the same results as Consumer Price Index
    When a user searches for the term(s) "CPI"
    Then the user will receive the following documents on the first page
      | /economy/inflationandpriceindices/datasets/consumerpriceinflation |


  Scenario: When Searching for Consumer Price Index I get the same results as Consumer Price Index
    When a user searches for the term(s) "Consumer Price Inflation"
    Then the user will receive the following documents on the first page
      | /economy/inflationandpriceindices/datasets/consumerpriceinflation |


