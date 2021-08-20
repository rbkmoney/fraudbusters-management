package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.fraudbusters.management.domain.payment.TestPaymentModel;
import com.rbkmoney.fraudbusters.management.utils.DateTimeUtils;
import com.rbkmoney.swag.fraudbusters.management.model.Error;
import com.rbkmoney.swag.fraudbusters.management.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class TestPaymentModelToDataSetRowConverter implements Converter<TestPaymentModel, DataSetRow> {

    @Override
    public DataSetRow convert(TestPaymentModel testDataSetModel) {
        return new DataSetRow()
                .id(String.valueOf(testDataSetModel.getTestDataSetId()))
                .payment(new Payment()
                        .id(testDataSetModel.getPaymentId())
                        .eventTime(DateTimeUtils.toDate(testDataSetModel.getEventTime()))
                        .amount(testDataSetModel.getAmount())
                        .currency(testDataSetModel.getCurrency())
                        .cardToken(testDataSetModel.getCardToken())
                        .bin(testDataSetModel.getBin())
                        .lastDigits(testDataSetModel.getLastDigits())
                        .paymentCountry(testDataSetModel.getPaymentCountry())
                        .clientInfo(new ClientInfo()
                                .email(testDataSetModel.getEmail())
                                .ip(testDataSetModel.getIp())
                                .fingerprint(testDataSetModel.getFingerprint()))
                        .error(StringUtils.hasText(testDataSetModel.getErrorCode())
                                ? new Error()
                                .errorReason(testDataSetModel.getErrorReason())
                                .errorCode(testDataSetModel.getErrorCode())
                                : null)
                        .payerType(testDataSetModel.getPayerType())
                        .paymentSystem(testDataSetModel.getPaymentSystem())
                        .paymentTool(testDataSetModel.getPaymentTool())
                        .mobile(testDataSetModel.getMobile())
                        .recurrent(testDataSetModel.getRecurrent())
                        .merchantInfo(new MerchantInfo()
                                .shopId(testDataSetModel.getShopId())
                                .partyId(testDataSetModel.getPartyId()))
                        .provider(new ProviderInfo()
                                .terminalId(testDataSetModel.getTerminalId())
                                .providerId(testDataSetModel.getProviderId())
                                .country(testDataSetModel.getCountry()))
                        .status(Payment.StatusEnum.valueOf(testDataSetModel.getStatus()))
                );
    }

}
