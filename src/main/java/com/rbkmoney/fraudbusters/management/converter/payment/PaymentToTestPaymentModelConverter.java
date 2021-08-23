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
        var bankCard = payment.getPaymentTool().getBankCard();
        var referenceInfo = payment.getReferenceInfo();
        var providerInfo = payment.getProviderInfo();
        var clientInfo = payment.getClientInfo();
        var error = payment.getError();
        return TestPaymentModel.builder()
                .currency(payment.getCost().getCurrency().getSymbolicCode())
                .amount(payment.getCost().getAmount())
                .eventTime(payment.getEventTime())
                .paymentId(payment.getId())
                .status(payment.getStatus().name())
                .partyId(referenceInfo != null ? referenceInfo.getMerchantInfo().getPartyId() : null)
                .shopId(referenceInfo != null ? referenceInfo.getMerchantInfo().getShopId() : null)
                .terminalId(providerInfo != null ? providerInfo.getTerminalId() : null)
                .providerId(providerInfo != null ? providerInfo.getProviderId() : null)
                .country(providerInfo != null ? providerInfo.getCountry() : null)
                .paymentSystem(bankCard != null ? bankCard.getPaymentSystem().getId() : null)
                .paymentCountry(bankCard != null ? bankCard.getIssuerCountry().name() : null)
                .cardToken(bankCard != null ? bankCard.getToken() : null)
                .bin(bankCard != null ? bankCard.getBin() : null)
                .lastDigits(bankCard != null ? bankCard.getLastDigits() : null)
                .paymentTool(payment.getPaymentTool().getFieldValue().toString())
                .ip(clientInfo != null ? clientInfo.getIp() : null)
                .fingerprint(clientInfo != null ? clientInfo.getFingerprint() : null)
                .email(clientInfo != null ? clientInfo.getEmail() : null)
                .payerType(payment.getPayerType().name())
                .errorCode(error != null ? error.getErrorCode() : null)
                .errorReason(error != null ? error.getErrorReason() : null)
                .build();
    }
}
