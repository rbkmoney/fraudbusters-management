package com.rbkmoney.fraudbusters.management.domain.payment.response;

import com.rbkmoney.fraudbusters.management.domain.payment.PaymentGroupReferenceModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilterPaymentGroupsReferenceResponse {

    private List<PaymentGroupReferenceModel> groupsReferenceModels;
    private Integer count;

}
