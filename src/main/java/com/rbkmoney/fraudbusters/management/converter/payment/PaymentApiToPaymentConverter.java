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
                .setCost(createCash(payment))
                .setStatus(PaymentStatus.valueOf(payment.getStatus().getValue()))
                .setError(createError(payment))
                .setEventTime(payment.getEventTime() != null
                        ? payment.getEventTime().toString()
                        : null)
                .setMobile(Optional.ofNullable(payment.getMobile()).orElse(false))
                .setRecurrent(Optional.ofNullable(payment.getRecurrent()).orElse(false))
                .setPayerType(payment.getPayerType() != null
                        ? PayerType.valueOf(payment.getPayerType())
                        : PayerType.payment_resource)
                .setPaymentTool(createPaymentTool(payment))
                .setProviderInfo(createProviderInfo(payment))
                .setReferenceInfo(ReferenceInfo.merchant_info(createMerchantInfo(payment)));
    }

    private Cash createCash(Payment payment) {
        return new Cash()
                .setAmount(payment.getAmount())
                .setCurrency(new CurrencyRef()
                        .setSymbolicCode(payment.getCurrency()));
    }

    private PaymentTool createPaymentTool(Payment payment) {
        return PaymentTool.bank_card(new BankCard()
                .setBin(payment.getBin())
                .setLastDigits(payment.getLastDigits())
                .setToken(payment.getCardToken()));
    }

    private ProviderInfo createProviderInfo(Payment payment) {
        return payment.getProvider() != null
                ? new ProviderInfo()
                .setTerminalId(payment.getProvider().getProviderId())
                .setCountry(payment.getProvider().getCountry())
                .setProviderId(payment.getProvider().getProviderId())
                : new ProviderInfo();
    }

    private MerchantInfo createMerchantInfo(Payment payment) {
        return new MerchantInfo()
                .setShopId(payment.getMerchantInfo().getShopId())
                .setPartyId(payment.getMerchantInfo().getPartyId());
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
