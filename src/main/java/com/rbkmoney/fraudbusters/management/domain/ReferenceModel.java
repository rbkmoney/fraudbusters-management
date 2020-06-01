package com.rbkmoney.fraudbusters.management.domain;

import lombok.Data;

@Data
public class ReferenceModel {

    private String id;
    private String templateId;
    private Boolean isGlobal;
    private Boolean isDefault;

}
