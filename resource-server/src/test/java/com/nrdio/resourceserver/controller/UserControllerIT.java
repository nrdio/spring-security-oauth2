package com.nrdio.resourceserver.controller;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerIT {

    private static final String CLIENT_ID = "clientx";
    private static final String CLIENT_SECRET = "password";
    private static final String USER_NAME = "userx";
    private static final String PASSWORD = "password";

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void getUserInfo_shouldSucceedForValidAccessToken() {
        // given
        OAuth2AccessToken token = getAccessTokenUsingPasswordGrant();

        // when
        ResponseEntity<Map<String, String>> response = getUserInfo(token.getValue());

        // then
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getUserInfo_shouldFailForInvalidAccessToken() {
        // when
        ResponseEntity<Map<String, String>> response = getUserInfo("FooAccessToken");

        // then
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        Assertions.assertThat(response.getBody().get("error")).isEqualTo("invalid_token");
    }

    private ResponseEntity<Map<String, String>> getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(headers);

        return restTemplate.exchange("/userinfo",
                HttpMethod.GET,
                request, new ParameterizedTypeReference<Map<String, String>>() {
                });
    }

    private OAuth2AccessToken getAccessTokenUsingPasswordGrant() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.add("grant_type", "password");
        params.add("client_id", CLIENT_ID);
        params.add("username", USER_NAME);
        params.add("password", PASSWORD);

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(params, headers);

        ResponseEntity<Map<String, String>> response = restTemplate.exchange("http://localhost:8085/oauth/token",
                HttpMethod.POST,
                request, new ParameterizedTypeReference<Map<String, String>>() {
                });

        return DefaultOAuth2AccessToken.valueOf(response.getBody());
    }

}