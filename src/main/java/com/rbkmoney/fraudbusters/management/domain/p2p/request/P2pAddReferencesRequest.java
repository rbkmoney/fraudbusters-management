package com.rbkmoney.fraudbusters.management.domain.p2p.request;

import lombok.Data;

@Data
public class P2pAddReferencesRequest {

    private String identityId;
    private String templateId;

}
