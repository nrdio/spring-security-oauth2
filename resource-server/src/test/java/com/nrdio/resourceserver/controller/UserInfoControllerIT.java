package com.nrdio.resourceserver.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWireMock(port = 8885)
public class UserInfoControllerIT {

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
    public void getUserInfo_shouldSucceedForValidAccessToken() {
        WireMock.stubFor(post(urlEqualTo("/oauth/check_token"))
                .willReturn(aResponse()
                        .withBody("{\n" +
                                "    \"exp\": 1570496813,\n" +
                                "    \"user_name\": \"userx\",\n" +
                                "    \"authorities\": [\n" +
                                "        \"ROLE_ADMIN\",\n" +
                                "        \"ROLE_USER\"\n" +
                                "    ],\n" +
                                "    \"client_id\": \"clientx\",\n" +
                                "    \"scope\": [\n" +
                                "        \"payments\",\n" +
                                "        \"accounts\"\n" +
                                "    ]\n" +
                                "}")
                        .withHeader("Content-Type", "application/json")));

        webClient
                .get().uri("/userinfo")
                .header("Authorization", "Bearer c9f1486d-4eaf-461c-9872-a2a9a8a1375d")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("userx");

    }

    @Test
    public void getUserInfo_shouldFailForInvalidAccessToken() {
        WireMock.stubFor(post(urlEqualTo("/oauth/check_token"))
                .willReturn(aResponse()
                        .withBody("{\n" +
                                "    \"error\": \"invalid_token\",\n" +
                                "    \"error_description\": \"Token was not recognised\"\n" +
                                "}")
                        .withHeader("Content-Type", "application/json")));

        webClient
                .get().uri("/userinfo")
                .header("Authorization", "Bearer c9f1486d-4eaf-461c-9872-a2a9a8a1375d")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.error").isEqualTo("invalid_token");
    }

}