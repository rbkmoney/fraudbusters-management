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

        return TestPaymentModel.builder()
                .currency(payment.getCost().getCurrency().getSymbolicCode())
                .amount(payment.getCost().getAmount())
                .eventTime(payment.getEventTime())
                .paymentId(payment.getId())
                .status(payment.getStatus().name())
                .paymentCountry(payment.getPaymentTool().getBankCard().getIssuerCountry().name())
                .partyId(payment.getReferenceInfo() != null
                        ? payment.getReferenceInfo().getMerchantInfo().getPartyId()
                        : null)
                .shopId(payment.getReferenceInfo() != null
                        ? payment.getReferenceInfo().getMerchantInfo().getShopId()
                        : null)
                .terminalId(payment.getProviderInfo() != null
                        ? payment.getProviderInfo().getTerminalId()
                        : null)
                .providerId(payment.getProviderInfo() != null
                        ? payment.getProviderInfo().getProviderId()
                        : null)
                .country(payment.getProviderInfo() != null
                        ? payment.getProviderInfo().getCountry()
                        : null)
                .paymentSystem(payment.getPaymentTool().getBankCard() != null
                        ? payment.getPaymentTool().getBankCard().getPaymentSystem().getId()
                        : null)
                .cardToken(payment.getPaymentTool().getBankCard() != null
                        ? payment.getPaymentTool().getBankCard().getToken()
                        : null)
                .bin(payment.getPaymentTool().getBankCard() != null
                        ? payment.getPaymentTool().getBankCard().getBin()
                        : null)
                .lastDigits(payment.getPaymentTool().getBankCard() != null
                        ? payment.getPaymentTool().getBankCard().getLastDigits()
                        : null)
                .paymentTool(payment.getPaymentTool().getFieldValue().toString())
                .ip(payment.getClientInfo() != null ? payment.getClientInfo().getIp() : null)
                .fingerprint(payment.getClientInfo() != null ? payment.getClientInfo().getFingerprint() : null)
                .email(payment.getClientInfo() != null ? payment.getClientInfo().getEmail() : null)
                .payerType(payment.getPayerType().name())
                .errorCode(payment.getError() != null ? payment.getError().getErrorCode() : null)
                .errorReason(payment.getError() != null ? payment.getError().getErrorReason() : null)
                .build();
    }
}
