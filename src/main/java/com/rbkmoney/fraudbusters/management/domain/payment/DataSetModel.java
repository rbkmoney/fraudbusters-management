package com.rbkmoney.fraudbusters.management.domain.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataSetModel {

    private String id;
    private String name;
    private String template;
    private LocalDateTime lastModificationTime;
    private String lastModificationInitiator;
    private List<PaymentModel> paymentModelList;

}
