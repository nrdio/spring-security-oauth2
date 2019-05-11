package com.nrdio.apigateway.config;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class RouteLocatorConfigurationTest {

    @Value("${local.server.port}")
    int port;

    private WebTestClient webClient;

    @Before
    public void contextLoads() {
        this.webClient = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    public void tokenRouteMappingShouldSucceed() {
        WireMock.stubFor(post(urlEqualTo("/oauth/token"))
                .willReturn(aResponse()
                        .withBody("{\n" +
                                "    \"access_token\": \"c9f1486d-4eaf-461c-9872-a2a9a8a1375d\",\n" +
                                "    \"token_type\": \"bearer\",\n" +
                                "    \"refresh_token\": \"ef886592-8f9e-45a1-8e83-a95e4d43f503\",\n" +
                                "    \"expires_in\": 28799,\n" +
                                "    \"scope\": \"read write\"\n" +
                                "}")
                        .withHeader("Content-Type", "application/json")));

        webClient
                .post().uri("/oauth/token")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.access_token").isEqualTo("c9f1486d-4eaf-461c-9872-a2a9a8a1375d");

    }

    @Test
    public void tokenRouteMappingShouldResultInTimeoutError() {
        WireMock.stubFor(post(urlEqualTo("/oauth/token"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.REQUEST_TIMEOUT.value())
                        .withFixedDelay(3000)));

        webClient
                .post().uri("/oauth/token")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Gateway Timeout");

    }

    @Test
    public void tokenRevokeRouteMappingShouldSucceed() {
        WireMock.stubFor(post(urlEqualTo("/oauth/token/revoke"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())));

        webClient
                .post().uri("/oauth/token/revoke")
                .exchange()
                .expectStatus().isOk();

    }

    @Test
    public void getUserInfoRouteMappingShouldSucceed() {
        //Stubs
        WireMock.stubFor(get(urlEqualTo("/userinfo"))
                .willReturn(aResponse()
                        .withBody("{\n" +
                                "    \"name\": \"userx\"\n" +
                                "}")
                        .withHeader("Content-Type", "application/json")));

        webClient
                .get().uri("/userinfo")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("userx");

    }
}