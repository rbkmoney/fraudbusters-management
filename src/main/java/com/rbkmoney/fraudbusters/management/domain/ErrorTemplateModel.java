package com.rbkmoney.fraudbusters.management.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ErrorTemplateModel {

    private String id;
    private List<String> errors;

}
