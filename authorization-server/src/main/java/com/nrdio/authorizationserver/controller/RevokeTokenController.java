package com.nrdio.authorizationserver.controller;

import com.nrdio.authorizationserver.model.TokenTypeHint;
import com.nrdio.authorizationserver.service.RevokeTokenService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.security.Principal;

@RestController
@Slf4j
@AllArgsConstructor
@Validated
public class RevokeTokenController {

    RevokeTokenService revokeTokenService;

    @RequestMapping(method = RequestMethod.POST, value = "/oauth/token/revoke")
    @ResponseBody
    public void revokeToken(Principal principal, @RequestParam(value = "token_type_hint") TokenTypeHint tokenTypeHint, @NotNull @RequestParam(value = "token") String token) {
        revokeTokenService.revokeToken(tokenTypeHint, token, principal.getName());
    }


}