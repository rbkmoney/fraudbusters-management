package com.rbkmoney.fraudbusters.management.exception;

public class UnknownPaymentException extends RuntimeException {

    public UnknownPaymentException() {
    }

    public UnknownPaymentException(String message) {
        super(message);
    }

    public UnknownPaymentException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownPaymentException(Throwable cause) {
        super(cause);
    }

    public UnknownPaymentException(String message, Throwable cause, boolean enableSuppression,
                                   boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
