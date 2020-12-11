package com.rbkmoney.fraudbusters.management.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateModel {

    private String id;
    private String template;
    private String lastUpdateDate;
    private String modifiedByUser;

}
