package com.rbkmoney.fraudbusters.management.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DefaultReferenceModel {

    private String id;
    private String templateId;
    private String lastUpdateDate;
    private String modifiedByUser;

}
