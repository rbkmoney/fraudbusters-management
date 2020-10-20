package com.rbkmoney.fraudbusters.management.domain.payment;

import com.rbkmoney.fraudbusters.management.domain.ReferenceModel;
import lombok.*;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PaymentReferenceModel extends ReferenceModel {

    private String partyId;
    private String shopId;

}
