package com.nrdio.authorizationserver.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TokenControllerIT {

    private static final String CLIENT_ID = "clienty";
    private static final String CLIENT_SECRET = "password";
    private static final String USER_NAME = "usery";
    private static final String PASSWORD = "password";

    private HttpHeaders headers;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setUp() {
        this.headers = new HttpHeaders();
        headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    }

    @Test
    public void getAccessTokenUsingPasswordGrant() {
        // when
        OAuth2AccessToken token = getAccessToken();

        // then
        assertThat(token.getValue()).isNotEmpty();
        assertThat(token.getRefreshToken().getValue()).isNotEmpty();
    }

    @Test
    public void getAccessTokenUsingRefreshTokenGrant() {
        //given
        OAuth2AccessToken token = getAccessToken();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", CLIENT_ID);
        params.add("refresh_token", token.getRefreshToken().getValue());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(params, this.headers);

        // when
        ResponseEntity<Map<String, String>> response = restTemplate.exchange("/oauth/token",
                HttpMethod.POST,
                request, new ParameterizedTypeReference<Map<String, String>>() {
                });

        OAuth2AccessToken newToken = DefaultOAuth2AccessToken.valueOf(response.getBody());

        //then
        assertThat(newToken.getValue()).isNotEmpty();
        assertThat(newToken.getRefreshToken().getValue()).isNotEmpty();
        assertThat(newToken.getValue()).isNotEqualTo(token.getValue());
        assertThat(newToken.getRefreshToken()).isEqualTo(token.getRefreshToken());
    }

    private OAuth2AccessToken getAccessToken() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.add("grant_type", "password");
        params.add("client_id", CLIENT_ID);
        params.add("username", USER_NAME);
        params.add("password", PASSWORD);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(params, this.headers);

        ResponseEntity<Map<String, String>> response = restTemplate.exchange("/oauth/token",
                HttpMethod.POST,
                request, new ParameterizedTypeReference<Map<String, String>>() {
                });

        return DefaultOAuth2AccessToken.valueOf(response.getBody());
    }

}