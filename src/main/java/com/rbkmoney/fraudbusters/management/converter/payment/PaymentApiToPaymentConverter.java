package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.damsel.domain.BankCard;
import com.rbkmoney.damsel.domain.Cash;
import com.rbkmoney.damsel.domain.CurrencyRef;
import com.rbkmoney.damsel.domain.PaymentTool;
import com.rbkmoney.damsel.fraudbusters.Error;
import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.swag.fraudbusters.management.model.Payment;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class PaymentApiToPaymentConverter
        implements Converter<Payment, com.rbkmoney.damsel.fraudbusters.Payment> {

    @NonNull
    @Override
    public com.rbkmoney.damsel.fraudbusters.Payment convert(Payment payment) {
        return new com.rbkmoney.damsel.fraudbusters.Payment()
                .setId(payment.getId())
                .setClientInfo(createClientInfo(payment))
                .setCost(new Cash()
                        .setAmount(payment.getAmount())
                        .setCurrency(new CurrencyRef()
                                .setSymbolicCode(payment.getCurrency())))
                .setStatus(PaymentStatus.valueOf(payment.getStatus().getValue()))
                .setError(createError(payment))
                .setEventTime(payment.getEventTime().toString())
                .setMobile(Optional.ofNullable(payment.getMobile()).orElse(false))
                .setRecurrent(Optional.ofNullable(payment.getRecurrent()).orElse(false))
                .setPayerType(payment.getPayerType() != null
                        ? PayerType.valueOf(payment.getPayerType())
                        : PayerType.payment_resource)
                .setPaymentTool(PaymentTool.bank_card(new BankCard()
                        .setBin(payment.getBin())
                        .setLastDigits(payment.getLastDigits())
                        .setToken(payment.getCardToken())))
                .setProviderInfo(payment.getProvider() != null
                        ? new ProviderInfo()
                        .setTerminalId(payment.getProvider().getProviderId())
                        .setCountry(payment.getProvider().getCountry())
                        .setProviderId(payment.getProvider().getProviderId())
                        : new ProviderInfo());
    }

    private Error createError(com.rbkmoney.swag.fraudbusters.management.model.Payment payment) {
        return payment.getError() != null
                ? new Error()
                .setErrorCode(payment.getError().getErrorCode())
                .setErrorReason(payment.getError().getErrorReason())
                : null;
    }

    private ClientInfo createClientInfo(com.rbkmoney.swag.fraudbusters.management.model.Payment payment) {
        return payment.getClientInfo() != null
                ? new ClientInfo()
                .setIp(payment.getClientInfo().getIp())
                .setEmail(payment.getClientInfo().getEmail())
                .setFingerprint(payment.getClientInfo().getFingerprint())
                : null;
    }
}
