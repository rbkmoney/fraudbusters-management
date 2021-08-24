package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.fraudbusters.management.domain.payment.TestCheckedDataSetModel;
import com.rbkmoney.fraudbusters.management.utils.DateTimeUtils;
import com.rbkmoney.swag.fraudbusters.management.model.CheckedDataSet;
import com.rbkmoney.swag.fraudbusters.management.model.MerchantInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TestCheckedDataSetModelToCheckedDataSetApiConverter
        implements Converter<TestCheckedDataSetModel, CheckedDataSet> {

    private final TestCheckedPaymentModelToCheckedDataSetRowConverter paymentModelToCheckedDataSetRowConverter;

    @Override
    public CheckedDataSet convert(TestCheckedDataSetModel testDataSetModel) {
        return new CheckedDataSet()
                .id(String.valueOf(testDataSetModel.getId()))
                .checkingTimestamp(DateTimeUtils.toDate(testDataSetModel.getCheckingTimestamp()))
                .createdAt(LocalDateTime.parse(testDataSetModel.getCreatedAt(), DateTimeUtils.DATE_TIME_FORMATTER))
                .testDataSetId(String.valueOf(testDataSetModel.getTestDataSetId()))
                .initiator(testDataSetModel.getInitiator())
                .merchantInfo(new MerchantInfo()
                        .partyId(testDataSetModel.getPartyId())
                        .shopId(testDataSetModel.getShopId()))
                .template(testDataSetModel.getTemplate())
                .rows(CollectionUtils.isEmpty(testDataSetModel.getTestCheckedPaymentModels())
                        ? List.of()
                        : testDataSetModel.getTestCheckedPaymentModels().stream()
                        .map(paymentModelToCheckedDataSetRowConverter::convert)
                        .collect(Collectors.toList()));
    }

}
