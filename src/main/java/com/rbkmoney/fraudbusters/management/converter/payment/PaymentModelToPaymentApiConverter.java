package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.fraudbusters.management.domain.payment.PaymentModel;
import com.rbkmoney.swag.fraudbusters.management.model.Error;
import com.rbkmoney.swag.fraudbusters.management.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class PaymentModelToPaymentApiConverter implements Converter<PaymentModel, Payment> {

    @Override
    public Payment convert(PaymentModel paymentModel) {
        return new Payment()
                .id(String.valueOf(paymentModel.getId()))
                .paymentId(paymentModel.getPaymentId())
                .eventTime(paymentModel.getEventTime())
                .amount(paymentModel.getAmount())
                .currency(paymentModel.getCurrency())
                .cardToken(paymentModel.getCardToken())
                .bin(paymentModel.getBin())
                .lastDigits(paymentModel.getLastDigits())
                .paymentCountry(paymentModel.getPaymentCountry())
                .clientInfo(new ClientInfo()
                        .email(paymentModel.getEmail())
                        .ip(paymentModel.getIp())
                        .fingerprint(paymentModel.getFingerprint()))
                .error(StringUtils.hasText(paymentModel.getErrorCode())
                        ? new Error()
                        .errorReason(paymentModel.getErrorReason())
                        .errorCode(paymentModel.getErrorCode())
                        : null)
                .payerType(paymentModel.getPayerType())
                .paymentSystem(paymentModel.getPaymentSystem())
                .paymentTool(paymentModel.getPaymentTool())
                .mobile(paymentModel.getMobile())
                .recurrent(paymentModel.getRecurrent())
                .merchantInfo(new MerchantInfo()
                        .shopId(paymentModel.getShopId())
                        .partyId(paymentModel.getPartyId()))
                .provider(new ProviderInfo()
                        .terminalId(paymentModel.getTerminalId())
                        .providerId(paymentModel.getProviderId())
                        .country(paymentModel.getCountry()))
                .status(Payment.StatusEnum.valueOf(paymentModel.getStatus()));
    }

}
