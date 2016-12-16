Feature: Searching ONS website content

  Scenario: When Searching for Retail Price Index I get the same results as RPI
    When a user searches for the term(s) "Retail Price Index"
    Then the user will receive the following documents on the first page
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/june2016     |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/may2016      |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/apr2016      |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/mar2016      |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/feb2016      |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/january2016  |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/december2015 |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/november2015 |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/october2015  |


  Scenario: When Searching for RPI I get the same results as Retail Price Index
    When a user searches for the term(s) "RPI"
    Then the user will receive the following documents on the first page
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/june2016     |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/may2016      |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/apr2016      |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/mar2016      |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/feb2016      |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/january2016  |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/december2015 |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/november2015 |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/october2015  |


  Scenario: When Searching for CPI I get the same results as Consumer Price Index
    When a user searches for the term(s) "CPI"
    Then the user will receive the following documents on the first page
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/june2016     |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/may2016      |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/apr2016      |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/mar2016      |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/feb2016      |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/january2016  |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/december2015 |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/november2015 |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/october2015  |


  Scenario: When Searching for Consumer Price Index I get the same results as Consumer Price Index
    When a user searches for the term(s) "Consumer Price Inflation"
    Then the user will receive the following documents on the first page
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/june2016     |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/may2016      |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/apr2016      |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/mar2016      |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/feb2016      |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/january2016  |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/december2015 |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/november2015 |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/october2015  |


