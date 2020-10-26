package com.rbkmoney.fraudbusters.management.domain.response;

import com.rbkmoney.fraudbusters.management.domain.payment.PaymentGroupReferenceModel;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilterPaymentGroupsReferenceResponse {

    private List<PaymentGroupReferenceModel> groupsReferenceModels;
    private Integer count;

}
