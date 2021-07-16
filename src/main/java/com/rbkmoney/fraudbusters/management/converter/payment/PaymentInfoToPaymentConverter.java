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
        implements Converter<com.rbkmoney.damsel.fraudbusters.PaymentInfo, Payment> {

    @NonNull
    @Override
    public Payment convert(com.rbkmoney.damsel.fraudbusters.PaymentInfo paymentInfo) {
        return new Payment()
                .cardToken(paymentInfo.getCardToken())
                .amount(paymentInfo.getAmount())
                .clientInfo(new ClientInfo()
                        .email(paymentInfo.getClientInfo().getEmail())
                        .fingerprint(paymentInfo.getClientInfo().getFingerprint())
                        .ip(paymentInfo.getClientInfo().getIp())
                )
                .currency(paymentInfo.getCurrency())
                .error(new Error()
                        .errorCode(paymentInfo.getError().getErrorCode())
                        .errorReason(paymentInfo.getError().getErrorReason()))
                .eventTime(DateTimeUtils.toDate(paymentInfo.getEventTime()))
                .id(paymentInfo.getId())
                .merchantInfo(new MerchantInfo()
                        .partyId(paymentInfo.getMerchantInfo().getPartyId())
                        .shopId(paymentInfo.getMerchantInfo().getShopId())
                )
                .paymentCountry(paymentInfo.getPaymentCountry())
                .paymentSystem(paymentInfo.getPaymentSystem())
                .paymentTool(paymentInfo.getPaymentTool())
                .provider(new ProviderInfo()
                        .providerId(paymentInfo.getProvider().getProviderId())
                        .country(paymentInfo.getProvider().getCountry())
                        .terminalId(paymentInfo.getProvider().getTerminalId()))
                .status(Payment.StatusEnum.valueOf(paymentInfo.getStatus().name()));
    }
}
