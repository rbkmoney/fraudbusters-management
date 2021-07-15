package com.rbkmoney.fraudbusters.management.domain.payment;

import com.rbkmoney.fraudbusters.management.domain.DefaultReferenceModel;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DefaultPaymentReferenceModel extends DefaultReferenceModel {

    private String partyId;
    private String shopId;

}
