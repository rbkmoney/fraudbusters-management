package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.fraudbusters.management.domain.payment.DataSetModel;
import com.rbkmoney.swag.fraudbusters.management.model.DataSet;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DataSetModelToDataSetApiConverter implements Converter<DataSetModel, DataSet> {

    private final PaymentModelToDataSetRowConverter testPaymentModelToDataSetRowConverter;

    @Override
    public DataSet convert(DataSetModel dataSetModel) {
        return new DataSet()
                .id(dataSetModel.getId())
                .name(dataSetModel.getName())
                .lastModificationAt(dataSetModel.getLastModificationTime())
                .lastModificationInitiator(dataSetModel.getLastModificationInitiator())
                .rows(CollectionUtils.isEmpty(dataSetModel.getPaymentModelList())
                        ? List.of()
                        : dataSetModel.getPaymentModelList().stream()
                        .map(testPaymentModelToDataSetRowConverter::convert)
                        .collect(Collectors.toList()));
    }

}
