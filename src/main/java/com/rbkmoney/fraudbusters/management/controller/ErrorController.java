package com.rbkmoney.fraudbusters.management.controller;

import com.rbkmoney.dao.DaoException;
import com.rbkmoney.fraudbusters.management.domain.response.ErrorResponse;
import com.rbkmoney.fraudbusters.management.exception.KafkaProduceException;
import com.rbkmoney.fraudbusters.management.exception.KafkaSerializationException;
import com.rbkmoney.fraudbusters.management.exception.NotificatorCallException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import static com.rbkmoney.damsel.fraudbusters_notificator.fraudbusters_notificatorConstants.VALIDATION_ERROR;

@Slf4j
@ControllerAdvice
@RestController
@RequiredArgsConstructor
public class ErrorController {

    public static final String FORMAT_DATA_EXCEPTION = "formatDataException";
    public static final String INVALID_PARAMETERS = "invalidParameters";
    public static final String DATA_BASE_INVOCATION_EXCEPTION = "dataBaseInvocationException";
    public static final String KAFKA_PRODUCE_ERROR = "kafkaProduceError";
    public static final String NOTIFICATOR_CALL_EXCEPTION = "notificatorCallException";

    @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public void handleUnauthorized(HttpClientErrorException.Unauthorized e) {
        log.error("HttpClientErrorException.Unauthorized exception e: ", e);
    }

    @ExceptionHandler(KafkaSerializationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleBadRequest(KafkaSerializationException e) {
        log.error("KafkaSerializationException exception e: ", e);
        return ErrorResponse.builder()
                .code(FORMAT_DATA_EXCEPTION)
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(DaoException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleBadRequest(DaoException e) {
        log.error("DaoException exception e: ", e);
        return ErrorResponse.builder()
                .code(DATA_BASE_INVOCATION_EXCEPTION)
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler({HttpClientErrorException.BadRequest.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleBadRequest(HttpClientErrorException.BadRequest e) {
        log.error("HttpClientErrorException.BadRequest exception e: ", e);
        return ErrorResponse.builder()
                .code(INVALID_PARAMETERS)
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler({KafkaProduceException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleBadRequest(KafkaProduceException e) {
        log.error("KafkaProduceException exception e: ", e);
        return ErrorResponse.builder()
                .code(KAFKA_PRODUCE_ERROR)
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(NotificatorCallException.class)
    public ResponseEntity<ErrorResponse> handleNotificationException(NotificatorCallException e) {
        log.error("NotificatorCallException exception e: ", e);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(NOTIFICATOR_CALL_EXCEPTION)
                .message(e.getMessage())
                .build();
        HttpStatus status = getStatus(e.getCode());
        return new ResponseEntity<>(errorResponse, status);
    }

    private HttpStatus getStatus(int code) {
        if (code == VALIDATION_ERROR) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.BAD_GATEWAY;
    }

}
