package com.nrdio.authorizationserver.controller;

import com.nrdio.authorizationserver.model.TokenTypeHint;
import org.assertj.core.api.Assertions;
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
public class RevokeTokenControllerIT {

    private static final String CLIENT_ID = "clientx";
    private static final String CLIENT_SECRET = "password";
    private static final String USER_NAME = "userx";
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
    public void revokeRefreshToken() {
        // given
        OAuth2AccessToken token = getAccessTokenUsingPasswordGrant();

        // when
        ResponseEntity<Map<String, String>> response = revokeToken(TokenTypeHint.refresh_token, token.getRefreshToken().getValue());

        // then
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void revokeAccessToken() {
        // given
        OAuth2AccessToken token = getAccessTokenUsingPasswordGrant();

        // when
        ResponseEntity<Map<String, String>> response = revokeToken(TokenTypeHint.access_token, token.getValue());

        // then
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private ResponseEntity<Map<String, String>> revokeToken(TokenTypeHint tokenTypeHint, String token) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.add("token_type_hint", tokenTypeHint.name());
        params.add("token", token);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(params, this.headers);

        return restTemplate.exchange("/oauth/token/revoke",
                HttpMethod.POST,
                request, new ParameterizedTypeReference<Map<String, String>>() {
                });
    }

    private OAuth2AccessToken getAccessTokenUsingPasswordGrant() {
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