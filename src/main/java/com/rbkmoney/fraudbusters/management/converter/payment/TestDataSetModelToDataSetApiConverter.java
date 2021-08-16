package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.fraudbusters.management.domain.payment.TestDataSetModel;
import com.rbkmoney.fraudbusters.management.utils.DateTimeUtils;
import com.rbkmoney.swag.fraudbusters.management.model.DataSet;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TestDataSetModelToDataSetApiConverter implements Converter<TestDataSetModel, DataSet> {

    private final TestPaymentModelToDataSetRowConverter testPaymentModelToDataSetRowConverter;

    @Override
    public DataSet convert(TestDataSetModel testDataSetModel) {
        return new DataSet()
                .name(testDataSetModel.getName())
                .lastModificationAt(DateTimeUtils.toDate(testDataSetModel.getLastModificationTime()))
                .lastModificationInitiator(testDataSetModel.getLastModificationInitiator())
                .rows(CollectionUtils.isEmpty(testDataSetModel.getTestPaymentModelList())
                        ? List.of()
                        : testDataSetModel.getTestPaymentModelList().stream()
                        .map(testPaymentModelToDataSetRowConverter::convert)
                        .collect(Collectors.toList()));
    }

}
