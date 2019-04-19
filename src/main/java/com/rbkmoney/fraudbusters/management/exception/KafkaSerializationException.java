package com.rbkmoney.fraudbusters.management.exception;

public class KafkaSerializationException extends RuntimeException {
    public KafkaSerializationException(String message) {
        super(message);
    }

    public KafkaSerializationException(Throwable cause) {
        super(cause);
    }

}
