package com.rbkmoney.fraudbusters.management.exception;

public class UnknownReferenceException extends RuntimeException {
    public UnknownReferenceException() {
    }

    public UnknownReferenceException(String message) {
        super(message);
    }

    public UnknownReferenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownReferenceException(Throwable cause) {
        super(cause);
    }

    public UnknownReferenceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
