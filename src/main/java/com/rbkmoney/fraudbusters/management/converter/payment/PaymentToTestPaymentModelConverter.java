package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.fraudbusters.management.domain.payment.TestPaymentModel;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class PaymentToTestPaymentModelConverter
        implements Converter<com.rbkmoney.damsel.fraudbusters.Payment, TestPaymentModel> {

    @NonNull
    @Override
    public TestPaymentModel convert(com.rbkmoney.damsel.fraudbusters.Payment payment) {
        TestPaymentModel.TestPaymentModelBuilder builder = TestPaymentModel.builder();
        var referenceInfo = payment.getReferenceInfo();
        if (referenceInfo != null) {
            builder.partyId(referenceInfo.getMerchantInfo().getPartyId())
                    .shopId(referenceInfo.getMerchantInfo().getShopId());
        }
        var bankCard = payment.getPaymentTool().getBankCard();
        if (bankCard != null) {
            builder.paymentSystem(bankCard.isSetPaymentSystem() ? bankCard.getPaymentSystem().getId() : null)
                    .paymentCountry(bankCard.isSetIssuerCountry() ? bankCard.getIssuerCountry().name() : null)
                    .cardToken(bankCard.getToken())
                    .bin(bankCard.getBin())
                    .lastDigits(bankCard.getLastDigits());
        }
        var providerInfo = payment.getProviderInfo();
        if (providerInfo != null) {
            builder.terminalId(providerInfo.getTerminalId())
                    .providerId(providerInfo.getProviderId())
                    .country(providerInfo.getCountry());
        }
        var clientInfo = payment.getClientInfo();
        if (clientInfo != null) {
            builder.ip(clientInfo.getIp())
                    .fingerprint(clientInfo.getFingerprint())
                    .email(clientInfo.getEmail());
        }
        var error = payment.getError();
        if (error != null) {
            builder.errorCode(error.getErrorCode())
                    .errorReason(error.getErrorReason());
        }
        return builder
                .currency(payment.getCost().getCurrency().getSymbolicCode())
                .amount(payment.getCost().getAmount())
                .eventTime(payment.getEventTime())
                .paymentId(payment.getId())
                .status(payment.getStatus().name())
                .paymentTool(payment.getPaymentTool().getFieldValue().toString())
                .payerType(payment.getPayerType().name())
                .build();
    }
}
