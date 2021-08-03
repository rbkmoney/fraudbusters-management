package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.fraudbusters.management.utils.DateTimeUtils;
import com.rbkmoney.swag.fraudbusters.management.model.Chargeback;
import com.rbkmoney.swag.fraudbusters.management.model.ClientInfo;
import com.rbkmoney.swag.fraudbusters.management.model.MerchantInfo;
import com.rbkmoney.swag.fraudbusters.management.model.ProviderInfo;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;


@Component
public class ChargebackToApiChargebackConverter
        implements Converter<com.rbkmoney.damsel.fraudbusters.Chargeback, Chargeback> {

    @NonNull
    @Override
    public Chargeback convert(com.rbkmoney.damsel.fraudbusters.Chargeback chargeback) {
        var paymentTool = chargeback.getPaymentTool();
        var cost = chargeback.getCost();
        var referenceInfo = chargeback.getReferenceInfo();
        return new Chargeback()
                .amount(cost.getAmount())
                .clientInfo(new ClientInfo()
                        .email(chargeback.getClientInfo().getEmail())
                        .fingerprint(chargeback.getClientInfo().getFingerprint())
                        .ip(chargeback.getClientInfo().getIp())
                )
                .currency(cost.getCurrency().getSymbolicCode())
                .eventTime(DateTimeUtils.toDate(chargeback.getEventTime()))
                .id(chargeback.getId())
                .merchantInfo(new MerchantInfo()
                        .partyId(chargeback.getReferenceInfo().isSetMerchantInfo()
                                ? referenceInfo.getMerchantInfo().getPartyId()
                                : null)
                        .shopId(chargeback.getReferenceInfo().isSetMerchantInfo()
                                ? referenceInfo.getMerchantInfo().getShopId()
                                : null)
                )
                .paymentTool(paymentTool.getFieldValue().toString())
                .provider(new ProviderInfo()
                        .providerId(chargeback.getProviderInfo().getProviderId())
                        .country(chargeback.getProviderInfo().getCountry())
                        .terminalId(chargeback.getProviderInfo().getTerminalId()))
                .status(chargeback.getStatus().name())
                .category(chargeback.getCategory().name())
                .chargebackCode(chargeback.getChargebackCode())
                .paymentId(chargeback.getPaymentId());
    }
}
