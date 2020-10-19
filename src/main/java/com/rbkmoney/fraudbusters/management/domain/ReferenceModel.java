package com.rbkmoney.fraudbusters.management.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReferenceModel {

    protected String id;
    protected String templateId;
    protected Boolean isGlobal;
    protected Boolean isDefault;

}
