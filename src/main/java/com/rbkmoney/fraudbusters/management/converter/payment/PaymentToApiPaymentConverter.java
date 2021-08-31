package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.fraudbusters.management.utils.DateTimeUtils;
import com.rbkmoney.swag.fraudbusters.management.model.Error;
import com.rbkmoney.swag.fraudbusters.management.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class PaymentToApiPaymentConverter
        implements Converter<com.rbkmoney.damsel.fraudbusters.Payment, Payment> {

    @NonNull
    @Override
    public Payment convert(com.rbkmoney.damsel.fraudbusters.Payment payment) {
        var paymentTool = payment.getPaymentTool();
        var bankCard = paymentTool.getBankCard();
        var cost = payment.getCost();
        var referenceInfo = payment.getReferenceInfo();
        return new Payment()
                .cardToken(bankCard.getToken())
                .amount(cost.getAmount())
                .clientInfo(new ClientInfo()
                        .email(payment.getClientInfo().getEmail())
                        .fingerprint(payment.getClientInfo().getFingerprint())
                        .ip(payment.getClientInfo().getIp())
                )
                .currency(cost.getCurrency().getSymbolicCode())
                .error(new Error()
                        .errorCode(payment.isSetError() ? payment.getError().getErrorCode() : null)
                        .errorReason(payment.isSetError() ? payment.getError().getErrorReason() : null))
                .eventTime(DateTimeUtils.toDate(payment.getEventTime()))
                .id(payment.getId())
                .merchantInfo(new MerchantInfo()
                        .partyId(payment.getReferenceInfo().isSetMerchantInfo()
                                ? referenceInfo.getMerchantInfo().getPartyId()
                                : null)
                        .shopId(payment.getReferenceInfo().isSetMerchantInfo()
                                ? referenceInfo.getMerchantInfo().getShopId()
                                : null)
                )
                .paymentCountry(bankCard.isSetIssuerCountry() ? bankCard.getIssuerCountry().name() : null)
                .paymentSystem(bankCard.isSetPaymentSystem() ? bankCard.getPaymentSystem().getId() : null)
                .paymentTool(paymentTool.getFieldValue().toString())
                .bin(bankCard.getBin())
                .lastDigits(bankCard.getLastDigits())
                .provider(payment.isSetProviderInfo()
                        ? new ProviderInfo().providerId(payment.getProviderInfo().getProviderId())
                        .country(payment.getProviderInfo().getCountry())
                        .terminalId(payment.getProviderInfo().getTerminalId())
                        : new ProviderInfo())
                .status(Payment.StatusEnum.fromValue(payment.getStatus().name()));
    }
}
