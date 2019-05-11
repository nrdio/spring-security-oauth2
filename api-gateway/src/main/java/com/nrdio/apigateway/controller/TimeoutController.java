package com.nrdio.apigateway.controller;

import com.nrdio.apigateway.exception.RequestTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TimeoutController {

    @RequestMapping(method = RequestMethod.GET, value = "/timeout", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity getTimeOutResponse() {
        throw new RequestTimeoutException();
    }

}
