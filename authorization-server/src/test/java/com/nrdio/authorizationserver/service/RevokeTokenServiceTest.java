package com.nrdio.authorizationserver.service;

import com.nrdio.authorizationserver.model.TokenTypeHint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class RevokeTokenServiceTest {

    private final String clientId = "clientId";
    @MockBean
    DefaultTokenServices tokenServices;
    @MockBean
    TokenStore tokenStore;
    @Autowired
    RevokeTokenService revokeTokenService;
    private OAuth2Authentication authentication;

    @Before
    public void setUp() {
        this.authentication = new OAuth2Authentication(new OAuth2Request(null, clientId, null, true, null, null, null, null, null), null);
    }

    @Test
    public void revokeToken_shouldRevokeRefreshToken() {
        // given
        String token = "refreshToken";
        OAuth2RefreshToken refreshToken = () -> token;

        doReturn(refreshToken).when(tokenStore).readRefreshToken(token);
        doReturn(this.authentication).when(tokenStore).readAuthenticationForRefreshToken(refreshToken);

        // when
        revokeTokenService.revokeToken(TokenTypeHint.refresh_token, token, this.clientId);

        // then
        verify(tokenStore).readRefreshToken(token);
        verify(tokenStore).readAuthenticationForRefreshToken(refreshToken);
        verify(tokenStore).removeAccessTokenUsingRefreshToken(refreshToken);
        verify(tokenStore).removeRefreshToken(refreshToken);
    }

    @Test
    public void revokeToken_shouldRevokeAccessToken() {
        // given
        String token = "accessToken";

        doReturn(this.authentication).when(tokenStore).readAuthentication(token);

        // when
        revokeTokenService.revokeToken(TokenTypeHint.access_token, token, clientId);

        // then
        verify(tokenStore).readAuthentication(token);
        verify(tokenServices).revokeToken(token);
    }

}