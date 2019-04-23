package com.rbkmoney.fraudbusters.management.domain.request;

import lombok.Data;

@Data
public class AddReferencesRequest {

    private String partyId;
    private String shopId;
    private String templateId;

}
