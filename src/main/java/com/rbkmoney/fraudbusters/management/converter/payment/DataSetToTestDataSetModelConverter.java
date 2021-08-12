package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.fraudbusters.management.domain.payment.TestDataSetModel;
import com.rbkmoney.swag.fraudbusters.management.model.DataSet;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DataSetToTestDataSetModelConverter implements Converter<DataSet, TestDataSetModel> {

    private final DataSetRowToTestPaymentModelConverter dataSetRowToTestPaymentModelConverter;

    @Override
    public TestDataSetModel convert(DataSet testDataSetModel) {
        TestDataSetModel.TestDataSetModelBuilder builder = TestDataSetModel.builder();
        return builder
                .id(testDataSetModel.getId())
                .name(testDataSetModel.getName())
                .lastModificationInitiator(testDataSetModel.getLastModificationInitiator())
                .lastModificationTime(testDataSetModel.getLastModificationAt().toString())
                .testPaymentModelList(testDataSetModel.getRows().stream()
                        .map(dataSetRowToTestPaymentModelConverter::convert)
                        .collect(Collectors.toList()))
                .build();
    }

}
