package com.rbkmoney.fraudbusters.management.exception;

public class KafkaProduceException extends RuntimeException {
    public KafkaProduceException() {
    }

    public KafkaProduceException(String message) {
        super(message);
    }

    public KafkaProduceException(String message, Throwable cause) {
        super(message, cause);
    }

    public KafkaProduceException(Throwable cause) {
        super(cause);
    }

    public KafkaProduceException(String message, Throwable cause, boolean enableSuppression,
                                 boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
