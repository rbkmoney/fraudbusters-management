package com.rbkmoney.fraudbusters.management.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DefaultReferenceModel {

    private String id;
    private String templateId;
    private String lastUpdateDate;
    private String modifiedByUser;

}
