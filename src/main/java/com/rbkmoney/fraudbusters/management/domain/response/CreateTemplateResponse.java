package com.rbkmoney.fraudbusters.management.domain.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CreateTemplateResponse {

    private String id;
    private String template;
    private List<String> errors;

}
