package com.rbkmoney.fraudbusters.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.fraudbusters.management.domain.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@ControllerAdvice
@RestController
@RequiredArgsConstructor
public class ErrorController {

    public static final String INVALID_REQUEST = "invalidRequest";

    @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public void handleUnauthorized(HttpClientErrorException.Unauthorized e) {
        log.error("HttpClientErrorException.Unauthorized exception e: ", e);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleBadRequest(MethodArgumentNotValidException e) {
        log.error("HttpClientErrorException.BadRequest exception e: ", e);
        return ErrorResponse.builder()
                .code(INVALID_REQUEST)
                .message(e.getMessage())
                .build();
    }

}