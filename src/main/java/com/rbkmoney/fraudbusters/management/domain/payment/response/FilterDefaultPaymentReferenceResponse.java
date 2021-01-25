package com.rbkmoney.fraudbusters.management.domain.payment.response;

import com.rbkmoney.fraudbusters.management.domain.payment.DefaultPaymentReferenceModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilterDefaultPaymentReferenceResponse {

    private List<DefaultPaymentReferenceModel> referenceModels;
    private Integer count;

}
