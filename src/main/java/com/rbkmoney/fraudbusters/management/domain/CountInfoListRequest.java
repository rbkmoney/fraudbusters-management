package com.rbkmoney.fraudbusters.management.domain;

import lombok.Data;

@Data
public class CountInfoListRequest {

    private Long count;
    private String startCountTime;
    private String endCountTime;

    private ListRecord listRecord;
}
