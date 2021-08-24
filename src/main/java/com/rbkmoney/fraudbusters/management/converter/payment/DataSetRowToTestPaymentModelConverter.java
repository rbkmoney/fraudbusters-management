package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.fraudbusters.management.domain.payment.TestPaymentModel;
import com.rbkmoney.swag.fraudbusters.management.model.DataSetRow;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class DataSetRowToTestPaymentModelConverter implements Converter<DataSetRow, TestPaymentModel> {

    @Override
    public TestPaymentModel convert(DataSetRow dataSetRow) {
        var payment = dataSetRow.getPayment();
        if (payment == null) {
            throw new RuntimeException("Unknown payment");
        }
        var clientInfo = payment.getClientInfo();
        var error = payment.getError();
        var merchantInfo = payment.getMerchantInfo();
        return TestPaymentModel.builder()
                .id(dataSetRow.getId() != null ? Long.valueOf(dataSetRow.getId()) : null)
                .eventTime(payment.getEventTime())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .cardToken(payment.getCardToken())
                .country(payment.getPaymentCountry())
                .email(clientInfo != null ? clientInfo.getEmail() : null)
                .fingerprint(clientInfo != null ? clientInfo.getFingerprint() : null)
                .errorCode(error != null ? error.getErrorCode() : null)
                .errorReason(error != null ? error.getErrorReason() : null)
                .ip(clientInfo != null ? clientInfo.getIp() : null)
                .paymentId(payment.getId())
                .payerType(payment.getPayerType())
                .paymentSystem(payment.getPaymentSystem())
                .paymentTool(payment.getPaymentTool())
                .mobile(payment.getMobile())
                .recurrent(payment.getRecurrent())
                .partyId(merchantInfo != null ? merchantInfo.getPartyId() : null)
                .shopId(merchantInfo != null ? merchantInfo.getShopId() : null)
                .providerId(payment.getProvider() != null ? payment.getProvider().getProviderId() : null)
                .terminalId(payment.getProvider() != null ? payment.getProvider().getTerminalId() : null)
                .paymentCountry(payment.getPaymentCountry())
                .status(payment.getStatus().name())
                .testDataSetId(dataSetRow.getId() != null ? Long.valueOf(dataSetRow.getId()) : null)
                .bin(payment.getBin())
                .lastDigits(payment.getLastDigits())
                .build();
    }

}
