package com.rbkmoney.fraudbusters.management.controller;

import com.rbkmoney.fraudbusters.management.domain.response.ErrorResponse;
import com.rbkmoney.fraudbusters.management.exception.DaoException;
import com.rbkmoney.fraudbusters.management.exception.KafkaSerializationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@ControllerAdvice
@RestController
@RequiredArgsConstructor
public class ErrorController {

    public static final String FORMAT_DATA_EXCEPTION = "formatDataException";
    public static final String DATA_BASE_INVOCATION_EXCEPTION = "dataBaseInvocationException";

    @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public void handleUnauthorized(HttpClientErrorException.Unauthorized e) {
        log.error("HttpClientErrorException.Unauthorized exception e: ", e);
    }

    @ExceptionHandler({KafkaSerializationException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleBadRequest(KafkaSerializationException e) {
        return ErrorResponse.builder()
                .code(FORMAT_DATA_EXCEPTION)
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler({DaoException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleBadRequest(DaoException e) {
        return ErrorResponse.builder()
                .code(DATA_BASE_INVOCATION_EXCEPTION)
                .message(e.getMessage())
                .build();
    }

}