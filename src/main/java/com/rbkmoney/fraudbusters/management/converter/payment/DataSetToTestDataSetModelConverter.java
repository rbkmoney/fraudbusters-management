package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.fraudbusters.management.domain.payment.TestDataSetModel;
import com.rbkmoney.swag.fraudbusters.management.model.DataSet;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DataSetToTestDataSetModelConverter implements Converter<DataSet, TestDataSetModel> {

    private final DataSetRowToTestPaymentModelConverter dataSetRowToTestPaymentModelConverter;

    @Override
    public TestDataSetModel convert(DataSet dataSet) {
        TestDataSetModel.TestDataSetModelBuilder builder = TestDataSetModel.builder();
        return builder
                .id(dataSet.getId())
                .name(dataSet.getName())
                .lastModificationInitiator(dataSet.getLastModificationInitiator())
                .lastModificationTime(dataSet.getLastModificationAt() != null
                        ? dataSet.getLastModificationAt().toString()
                        : null)
                .testPaymentModelList(CollectionUtils.isEmpty(dataSet.getRows())
                        ? null
                        : dataSet.getRows().stream()
                        .map(dataSetRowToTestPaymentModelConverter::convert)
                        .collect(Collectors.toList()))
                .build();
    }

}
