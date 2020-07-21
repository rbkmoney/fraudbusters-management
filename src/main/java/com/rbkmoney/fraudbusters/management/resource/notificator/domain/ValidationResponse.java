package com.rbkmoney.fraudbusters.management.resource.notificator.domain;

import lombok.Data;

import java.util.List;

@Data
public class ValidationResponse {

    private List<ValidationError> errors;
    private String result;

}
