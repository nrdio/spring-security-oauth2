package com.nrdio.apigateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.REQUEST_TIMEOUT, reason = "Request timed out")
public class RequestTimeoutException extends RuntimeException {
}
