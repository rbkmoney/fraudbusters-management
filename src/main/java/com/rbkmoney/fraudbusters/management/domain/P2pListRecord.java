package com.rbkmoney.fraudbusters.management.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class P2pListRecord extends ListRecord {

    private String identityId;
}
