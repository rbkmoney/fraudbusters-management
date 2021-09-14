package com.rbkmoney.fraudbusters.management.exception;

import lombok.Getter;

@Getter
public class NotificatorCallException extends RuntimeException {

    private int code;

    public NotificatorCallException(String s) {
        super(s);
    }

    public NotificatorCallException(int code, String s) {
        super(s);
        this.code = code;
    }


}
