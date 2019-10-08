package com.nrdio.resourceserver.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
public class UserInfoController {

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @RequestMapping(method = RequestMethod.GET, value = "/userinfo", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Map<String, String> getUserInfo(Authentication authentication) {
        return Map.ofEntries(Map.entry("name", authentication.getName()));
    }

}