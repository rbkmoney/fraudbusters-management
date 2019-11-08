package com.rbkmoney.fraudbusters.management.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PaymentListRecord extends ListRecord {

    private String partyId;
    private String shopId;

}
