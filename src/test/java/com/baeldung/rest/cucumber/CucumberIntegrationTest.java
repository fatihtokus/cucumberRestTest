package com.baeldung.rest.cucumber;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:karate",tags = "@solo")
public class CucumberIntegrationTest {
    public static final WireMockServer wireMockServer = new WireMockServer();



    @BeforeClass
    public static void setUp() throws Exception {
        wireMockServer.start();
        configureFor("localhost", 8080);
    }
}