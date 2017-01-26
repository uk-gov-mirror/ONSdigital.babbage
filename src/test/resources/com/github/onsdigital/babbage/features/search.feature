Feature: Searching ONS website content

  Scenario: When Searching for Retail Price Index I get the same results as RPI
    When a user searches for the term(s) "Retail Price Index"
    Then the user will receive the following documents on the first page
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/dec2016  |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/nov2016  |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/oct2016  |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/sept2016 |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/aug2016  |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/july2016 |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/june2016 |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/may2016  |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/apr2016  |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/mar2016  |

    And the bulletins are order in date descending order

  Scenario: When Searching for RPI I get the same results as Retail Price Index
    When a user searches for the term(s) "RPI"
    Then the user will receive the following documents on the first page
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/dec2016  |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/nov2016  |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/oct2016  |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/sept2016 |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/aug2016  |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/july2016 |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/june2016 |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/may2016  |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/apr2016  |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/mar2016  |

    And the bulletins are order in date descending order

  Scenario: When Searching for CPI I get the same results as Consumer Price Index
    When a user searches for the term(s) "CPI"
    Then the user will receive the following documents on the first page
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/dec2016  |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/nov2016  |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/oct2016  |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/sept2016 |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/aug2016  |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/july2016 |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/june2016 |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/may2016  |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/apr2016  |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/mar2016  |
    And the bulletins are order in date descending order

  Scenario: When Searching for Consumer Price Index I get the same results as Consumer Price Index
    When a user searches for the term(s) "Consumer Price Inflation"
    Then the user will receive the following documents on the first page
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/dec2016  |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/nov2016  |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/oct2016  |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/sept2016 |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/aug2016  |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/july2016 |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/june2016 |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/may2016  |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/apr2016  |
      | /economy/inflationandpriceindices/bulletins/consumerpriceinflation/mar2016  |

    And the bulletins are order in date descending order


  Scenario: When Searching for neighbourhood statistics I get the same results as Consumer Price Index
    When a user searches for the term(s) "neighbourhood statistics"
    Then the user will receive the following documents on the first page
      | /help/localstatistics |
