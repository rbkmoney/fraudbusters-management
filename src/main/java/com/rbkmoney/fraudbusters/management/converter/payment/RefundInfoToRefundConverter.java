package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.fraudbusters.management.utils.DateTimeUtils;
import com.rbkmoney.swag.fraudbusters.management.model.Error;
import com.rbkmoney.swag.fraudbusters.management.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;


@Component
public class RefundInfoToRefundConverter
        implements Converter<com.rbkmoney.damsel.fraudbusters.Refund, Refund> {

    @NonNull
    @Override
    public Refund convert(com.rbkmoney.damsel.fraudbusters.Refund refund) {
        var paymentTool = refund.getPaymentTool();
        var cost = refund.getCost();
        var referenceInfo = refund.getReferenceInfo();
        return new Refund()
                .amount(cost.getAmount())
                .clientInfo(new ClientInfo()
                        .email(refund.getClientInfo().getEmail())
                        .fingerprint(refund.getClientInfo().getFingerprint())
                        .ip(refund.getClientInfo().getIp())
                )
                .currency(cost.getCurrency().getSymbolicCode())
                .error(new Error()
                        .errorCode(refund.isSetError() ? refund.getError().getErrorCode() : null)
                        .errorReason(refund.isSetError() ? refund.getError().getErrorReason() : null))
                .eventTime(DateTimeUtils.toDate(refund.getEventTime()))
                .id(refund.getId())
                .merchantInfo(new MerchantInfo()
                        .partyId(refund.getReferenceInfo().isSetMerchantInfo()
                                ? referenceInfo.getMerchantInfo().getPartyId()
                                : null)
                        .shopId(refund.getReferenceInfo().isSetMerchantInfo()
                                ? referenceInfo.getMerchantInfo().getShopId()
                                : null)
                )
                .paymentTool(paymentTool.getFieldValue().toString())
                .provider(new ProviderInfo()
                        .providerId(refund.getProviderInfo().getProviderId())
                        .country(refund.getProviderInfo().getCountry())
                        .terminalId(refund.getProviderInfo().getTerminalId()))
                .status(refund.getStatus().name())
                .paymentId(refund.getPaymentId());
    }
}
