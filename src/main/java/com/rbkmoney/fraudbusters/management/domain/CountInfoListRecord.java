package com.rbkmoney.fraudbusters.management.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class CountInfoListRecord extends ListRecord {

    private Long count;
    private String startCountTime;
    private String endCountTime;

}
