package com.baeldung.rest.cucumber;

import com.github.tomakehurst.wiremock.WireMockServer;
import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class StepDefinition {

    private static final String CREATE_PATH = "/create";
    private static final String APPLICATION_JSON = "application/json";

    private final InputStream jsonInputStream = this.getClass().getClassLoader().getResourceAsStream("cucumber.json");
    private final String jsonString = new Scanner(jsonInputStream, "UTF-8").useDelimiter("\\Z").next();


    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    String responseString;
    String request;

    @When("^users upload data on a project$")
    public void usersUploadDataOnAProject() throws IOException {
        CucumberIntegrationTest.wireMockServer.start();

        configureFor("localhost", 8080);
        stubFor(post(urlEqualTo(CREATE_PATH))
                .withHeader("content-type", equalTo(APPLICATION_JSON))
                .withRequestBody(containing("testing-framework"))
                .willReturn(aResponse().withStatus(200)));

        HttpPost request = new HttpPost("http://localhost:8080/create");
        StringEntity entity = new StringEntity(jsonString);
        request.addHeader("content-type", APPLICATION_JSON);
        request.setEntity(entity);
        HttpResponse response = httpClient.execute(request);

        assertEquals(200, response.getStatusLine().getStatusCode());
        verify(postRequestedFor(urlEqualTo(CREATE_PATH))
                .withHeader("content-type", equalTo(APPLICATION_JSON)));

        CucumberIntegrationTest.wireMockServer.stop();
    }


    @Given("^I configure 'abc.com/createUser' api with following '(request|response)':$")
    public void configure_api(String configType, DataTable dataTable) throws Throwable {

        if("request".equals(configType)){
            List<Map<String, String>> requestMap = dataTable.asMaps(String.class, String.class);
            request = requestMap.get(0).get("Request");
        }else {
            List<Map<String, String>> requestResponse = dataTable.asMaps(String.class, String.class);
            String testingFramework = requestResponse.get(0).get("testing-framework");
            String testingSupportedLanguage = requestResponse.get(0).get("testing-supported-language");
            String testingWebsite = requestResponse.get(0).get("testing-website");
            //TODO: Bu degiskenleri bir objeye atayip daha sonra JSON'a cevirip response assign edilir.

            String response = jsonString;
            stubFor(get(urlEqualTo("/projects/"+request)).withHeader("accept", equalTo(APPLICATION_JSON))
                    .willReturn(aResponse().withBody(response)));
        }




    }

    @Given("^I do call$")
    public void I_do_call() throws Throwable {

        String projectName = "Cucumber";
        HttpGet request = new HttpGet("http://localhost:8080/projects/" + projectName.toLowerCase());
        request.addHeader("accept", APPLICATION_JSON);
        HttpResponse httpResponse = httpClient.execute(request);
        responseString = convertResponseToString(httpResponse);


    }

    @Given("^The response is:")
    public void the_response(DataTable dataTable) throws Throwable {

        List<Map<String, String>> requestResponse = dataTable.asMaps(String.class, String.class);
        String testingFramework = requestResponse.get(0).get("testing-framework");
        String testingSupportedLanguage = requestResponse.get(0).get("testing-supported-language");
        String testingWebsite = requestResponse.get(0).get("testing-website");
        //TODO: Bu degiskenleri bir objeye atayip daha sonra JSON'a cevirip response assign edilir.

        String response = jsonString;


        assertThat(responseString, containsString("\"testing-framework\": \"cucumber\""));
        assertThat(responseString, containsString("\"website\": \"cucumber.io\""));
        verify(getRequestedFor(urlEqualTo("/projects/cucumber")).withHeader("accept", equalTo(APPLICATION_JSON)));

        CucumberIntegrationTest.wireMockServer.stop();
    }



    @When("^users want to get information on the (.+) project$")
    public void usersGetInformationOnAProject(String projectName) throws IOException {
        CucumberIntegrationTest.wireMockServer.start();

        configureFor("localhost", 8080);
        stubFor(get(urlEqualTo("/projects/cucumber")).withHeader("accept", equalTo(APPLICATION_JSON))
                .willReturn(aResponse().withBody(jsonString)));

        HttpGet request = new HttpGet("http://localhost:8080/projects/" + projectName.toLowerCase());
        request.addHeader("accept", APPLICATION_JSON);
        HttpResponse httpResponse = httpClient.execute(request);
        String responseString = convertResponseToString(httpResponse);

        assertThat(responseString, containsString("\"testing-framework\": \"cucumber\""));
        assertThat(responseString, containsString("\"website\": \"cucumber.io\""));
        verify(getRequestedFor(urlEqualTo("/projects/cucumber")).withHeader("accept", equalTo(APPLICATION_JSON)));

        CucumberIntegrationTest.wireMockServer.stop();
    }

    @Then("^the server should handle it and return a success status$")
    public void theServerShouldReturnASuccessStatus() {
    }

    @Then("^the requested data is returned$")
    public void theRequestedDataIsReturned() {

    }

    private String convertResponseToString(HttpResponse response) throws IOException {
        InputStream responseStream = response.getEntity().getContent();
        Scanner scanner = new Scanner(responseStream, "UTF-8");
        String responseString = scanner.useDelimiter("\\Z").next();
        scanner.close();
        return responseString;
    }
}