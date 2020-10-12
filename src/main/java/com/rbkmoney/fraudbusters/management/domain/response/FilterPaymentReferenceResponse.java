package com.rbkmoney.fraudbusters.management.domain.response;

import com.rbkmoney.fraudbusters.management.domain.ReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentReferenceModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilterPaymentReferenceResponse {

    private List<PaymentReferenceModel> referenceModels;
    private Integer count;

}
