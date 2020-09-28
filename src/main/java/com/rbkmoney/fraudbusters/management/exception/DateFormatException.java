package com.rbkmoney.fraudbusters.management.exception;

public class DateFormatException extends RuntimeException {
    public DateFormatException() {
    }

    public DateFormatException(String message) {
        super(message);
    }

    public DateFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public DateFormatException(Throwable cause) {
        super(cause);
    }

    public DateFormatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
