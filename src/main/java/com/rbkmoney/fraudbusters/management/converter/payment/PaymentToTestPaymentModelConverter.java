package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.fraudbusters.management.domain.payment.PaymentModel;
import com.rbkmoney.fraudbusters.management.utils.DateTimeUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static com.rbkmoney.fraudbusters.management.domain.payment.PaymentModel.*;


@Component
public class PaymentToTestPaymentModelConverter
        implements Converter<com.rbkmoney.damsel.fraudbusters.Payment, PaymentModel> {

    @NonNull
    @Override
    public PaymentModel convert(com.rbkmoney.damsel.fraudbusters.Payment payment) {
        PaymentModelBuilder builder = builder();
        if (payment.isSetReferenceInfo()) {
            updateReferenceInfo(builder, payment.getReferenceInfo());
        }
        if (payment.getPaymentTool().isSetBankCard()) {
            updateBankCardData(builder, payment.getPaymentTool().getBankCard());
        }
        if (payment.isSetProviderInfo()) {
            updateProviderInfo(builder, payment.getProviderInfo());
        }
        if (payment.isSetClientInfo()) {
            updateClientInfo(builder, payment.getClientInfo());
        }
        if (payment.isSetError()) {
            updateError(builder, payment.getError());
        }
        return builder
                .currency(payment.getCost().getCurrency().getSymbolicCode())
                .amount(payment.getCost().getAmount())
                .eventTime(payment.getEventTime() != null
                        ? DateTimeUtils.toDate(payment.getEventTime())
                        : null)
                .paymentId(payment.getId())
                .status(payment.getStatus() != null
                        ? payment.getStatus().name()
                        : null)
                .paymentTool(payment.getPaymentTool().getFieldValue().toString())
                .payerType(payment.getPayerType().name())
                .build();
    }

    private void updateError(PaymentModelBuilder builder, com.rbkmoney.damsel.fraudbusters.Error error) {
        builder.errorCode(error.getErrorCode())
                .errorReason(error.getErrorReason());
    }

    private void updateClientInfo(PaymentModelBuilder builder,
                                  com.rbkmoney.damsel.fraudbusters.ClientInfo clientInfo) {
        builder.ip(clientInfo.getIp())
                .fingerprint(clientInfo.getFingerprint())
                .email(clientInfo.getEmail());
    }

    private void updateProviderInfo(PaymentModelBuilder builder,
                                    com.rbkmoney.damsel.fraudbusters.ProviderInfo providerInfo) {
        builder.terminalId(providerInfo.getTerminalId())
                .providerId(providerInfo.getProviderId())
                .country(providerInfo.getCountry());
    }

    private void updateBankCardData(PaymentModelBuilder builder,
                                    com.rbkmoney.damsel.domain.BankCard bankCard) {
        builder.paymentSystem(bankCard.isSetPaymentSystem() ? bankCard.getPaymentSystem().getId() : null)
                .paymentCountry(bankCard.isSetIssuerCountry() ? bankCard.getIssuerCountry().name() : null)
                .cardToken(bankCard.getToken())
                .bin(bankCard.getBin())
                .lastDigits(bankCard.getLastDigits());
    }

    private void updateReferenceInfo(PaymentModelBuilder builder,
                                     com.rbkmoney.damsel.fraudbusters.ReferenceInfo referenceInfo) {
        builder.partyId(referenceInfo.getMerchantInfo().getPartyId())
                .shopId(referenceInfo.getMerchantInfo().getShopId());
    }
}
