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
public class PaymentInfoToPaymentConverter
        implements Converter<com.rbkmoney.damsel.fraudbusters.Payment, Payment> {

    @NonNull
    @Override
    public Payment convert(com.rbkmoney.damsel.fraudbusters.Payment payment) {
        var paymentTool = payment.getPaymentTool();
        var bankCard = paymentTool.getBankCard();
        var cost = payment.getCost();
        var merchantInfo = payment.getReferenceInfo().getMerchantInfo();
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
                        .errorCode(payment.getError().getErrorCode())
                        .errorReason(payment.getError().getErrorReason()))
                .eventTime(DateTimeUtils.toDate(payment.getEventTime()))
                .id(payment.getId())
                .merchantInfo(new MerchantInfo()
                        .partyId(merchantInfo.getPartyId())
                        .shopId(merchantInfo.getShopId())
                )
                .paymentCountry(bankCard.isSetIssuerCountry() ? bankCard.getIssuerCountry().name() : null)
                .paymentSystem(bankCard.getPaymentSystem().getId())
                .paymentTool(paymentTool.getFieldValue().toString())
                .provider(new ProviderInfo()
                        .providerId(payment.getProviderInfo().getProviderId())
                        .country(payment.getProviderInfo().getCountry())
                        .terminalId(payment.getProviderInfo().getTerminalId()))
                .status(Payment.StatusEnum.fromValue(payment.getStatus().name()));
    }
}
