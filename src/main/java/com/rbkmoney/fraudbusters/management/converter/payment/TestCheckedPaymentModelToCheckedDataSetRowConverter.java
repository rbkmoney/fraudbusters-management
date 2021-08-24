package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.fraudbusters.management.domain.payment.TestCheckedPaymentModel;
import com.rbkmoney.fraudbusters.management.utils.DateTimeUtils;
import com.rbkmoney.swag.fraudbusters.management.model.Error;
import com.rbkmoney.swag.fraudbusters.management.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TestCheckedPaymentModelToCheckedDataSetRowConverter
        implements Converter<TestCheckedPaymentModel, CheckedDataSetRow> {

    @Override
    public CheckedDataSetRow convert(TestCheckedPaymentModel testDataSetModel) {
        var testPaymentModel = testDataSetModel.getTestPaymentModel();
        return new CheckedDataSetRow()
                .id(String.valueOf(testDataSetModel.getTestDataSetCheckingResultId()))
                .payment(new Payment()
                        .id(String.valueOf(testPaymentModel.getId()))
                        .paymentId(testPaymentModel.getPaymentId())
                        .eventTime(
                                LocalDateTime.parse(testPaymentModel.getEventTime(), DateTimeUtils.DATE_TIME_FORMATTER))
                        .amount(testPaymentModel.getAmount())
                        .currency(testPaymentModel.getCurrency())
                        .cardToken(testPaymentModel.getCardToken())
                        .bin(testPaymentModel.getBin())
                        .lastDigits(testPaymentModel.getLastDigits())
                        .paymentCountry(testPaymentModel.getPaymentCountry())
                        .clientInfo(new ClientInfo()
                                .email(testPaymentModel.getEmail())
                                .ip(testPaymentModel.getIp())
                                .fingerprint(testPaymentModel.getFingerprint()))
                        .error(StringUtils.hasText(testPaymentModel.getErrorCode())
                                ? new Error()
                                .errorReason(testPaymentModel.getErrorReason())
                                .errorCode(testPaymentModel.getErrorCode())
                                : null)
                        .payerType(testPaymentModel.getPayerType())
                        .paymentSystem(testPaymentModel.getPaymentSystem())
                        .paymentTool(testPaymentModel.getPaymentTool())
                        .mobile(testPaymentModel.getMobile())
                        .recurrent(testPaymentModel.getRecurrent())
                        .merchantInfo(new MerchantInfo()
                                .shopId(testPaymentModel.getShopId())
                                .partyId(testPaymentModel.getPartyId()))
                        .provider(new ProviderInfo()
                                .terminalId(testPaymentModel.getTerminalId())
                                .providerId(testPaymentModel.getProviderId())
                                .country(testPaymentModel.getCountry()))
                        .status(Payment.StatusEnum.valueOf(testPaymentModel.getStatus()))
                );
    }

}
