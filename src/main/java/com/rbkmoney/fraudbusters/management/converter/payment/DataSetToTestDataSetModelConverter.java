package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.fraudbusters.management.domain.payment.DataSetModel;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentModel;
import com.rbkmoney.swag.fraudbusters.management.model.DataSet;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DataSetToTestDataSetModelConverter implements Converter<DataSet, DataSetModel> {

    private final DataSetRowToTestPaymentModelConverter dataSetRowToTestPaymentModelConverter;

    @Override
    public DataSetModel convert(DataSet dataSet) {
        return  DataSetModel.builder()
                .id(dataSet.getId() != null ? dataSet.getId() : null)
                .name(dataSet.getName())
                .lastModificationInitiator(dataSet.getLastModificationInitiator())
                .lastModificationTime(dataSet.getLastModificationAt())
                .paymentModelList(CollectionUtils.isEmpty(dataSet.getRows())
                        ? null
                        : mapDataSetToTestPaymentModels(dataSet))
                .build();
    }

    private List<PaymentModel> mapDataSetToTestPaymentModels(DataSet dataSet) {
        return dataSet.getRows().stream()
                .map(dataSetRowToTestPaymentModelConverter::convert)
                .collect(Collectors.toList());
    }

}
