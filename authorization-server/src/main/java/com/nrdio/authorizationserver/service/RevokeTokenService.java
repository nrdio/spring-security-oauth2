package com.nrdio.authorizationserver.service;

import com.nrdio.authorizationserver.model.TokenTypeHint;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class RevokeTokenService {

    DefaultTokenServices tokenServices;

    TokenStore tokenStore;

    public void revokeToken(TokenTypeHint tokenTypeHint, String token, String clientId) {
        if (TokenTypeHint.refresh_token.equals(tokenTypeHint)) {
            revokeRefreshToken(token, clientId);
        } else {
            revokeAccessToken(token, clientId);
        }
    }

    private void revokeRefreshToken(String token, String clientId) {
        Optional<OAuth2RefreshToken> refreshToken = Optional.ofNullable(tokenStore.readRefreshToken(token));
        refreshToken.ifPresent(oAuth2RefreshToken -> Optional.ofNullable(tokenStore.readAuthenticationForRefreshToken(oAuth2RefreshToken))
                .filter(auth -> clientId.equals(auth.getOAuth2Request().getClientId()))
                .ifPresent(tokn -> {
                    tokenStore.removeAccessTokenUsingRefreshToken(oAuth2RefreshToken);
                    tokenStore.removeRefreshToken(oAuth2RefreshToken);
                }));
    }

    private void revokeAccessToken(String token, String clientId) {
        Optional.ofNullable(tokenStore.readAuthentication(token))
                .filter(auth -> clientId.equals(auth.getOAuth2Request().getClientId()))
                .ifPresent(tokn -> tokenServices.revokeToken(token));
    }

}
