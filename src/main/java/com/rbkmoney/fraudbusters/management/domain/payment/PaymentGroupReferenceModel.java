package com.rbkmoney.fraudbusters.management.domain.payment;

import com.rbkmoney.fraudbusters.management.domain.GroupReferenceModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PaymentGroupReferenceModel extends GroupReferenceModel {

    private String partyId;
    private String shopId;

}
