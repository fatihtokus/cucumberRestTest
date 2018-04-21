Feature: Testing a REST API with Karate

  @solo
  Scenario: Do request from currency api
    Given I configure 'abc.com/createUser' api with following 'request':
      | Request  |
      | cucumber |

    And I configure 'abc.com/createUser' api with following 'response':
      | testing-framework | testing-supported-language          | testing-website |
      | cucumber          | Ruby,Java,Javascript,PHP,Python,C++ | cucumber.io     |

    When I do call
    Then The response is:
      | testing-framework | testing-supported-language          | testing-website |
      | cucumber          | Ruby,Java,Javascript,PHP,Python,C++ | cucumber.io     |



  @solo
  Scenario: Do request from currency api
    #Given I configure 'abc.com/createUser' api
    #When I do call 'abc.com/createUser' api with:
     # | Request  |
      #| cucumber |
    Then The response is:
      | testing-framework | testing-supported-language          | testing-website |
      | cucumber          | Ruby,Java,Javascript,PHP,Python,C++ | cucumber.io     |



  Scenario: Testing valid GET endpoint
  Given url 'http://localhost:8080/user/get'
  When method GET
  Then status 200

Scenario: Testing an invalid GET endpoint - 404
  Given url 'http://localhost:8080/user/wrong'
  When method GET
  Then status 404

Scenario: Testing the exact response of a GET endpoint
  Given url 'http://localhost:8080/user/get'
  When method GET
  Then status 200
  And match $ == {id:"1234",name:"John Smith"}

Scenario: Testing the exact response field value of a GET endpoint
  Given url 'http://localhost:8080/user/get'
  When method GET
  Then status 200
  And match $.id == "1234"

Scenario: Testing that GET response contains specific field
  Given url 'http://localhost:8080/user/get'
  When method GET
  Then status 200
  And match $ contains {id:"1234"}

Scenario: Test GET response using markers
  Given url 'http://localhost:8080/user/get'
  When method GET
  Then status 200
  And match $ == {id:"#notnull",name:"John Smith"}

Scenario: Testing a POST endpoint with request body
  Given url 'http://localhost:8080/user/create'
  And request { id: '1234' , name: 'John Smith'}
  When method POST
  Then status 200
  And match $ contains {id:"#notnull"}
