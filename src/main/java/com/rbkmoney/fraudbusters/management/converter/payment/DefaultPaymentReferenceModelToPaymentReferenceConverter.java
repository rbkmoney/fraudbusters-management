package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.fraudbusters.management.domain.payment.DefaultPaymentReferenceModel;
import com.rbkmoney.fraudbusters.management.utils.DateTimeUtils;
import com.rbkmoney.swag.fraudbusters.management.model.PaymentReference;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


@Component
public class DefaultPaymentReferenceModelToPaymentReferenceConverter
        implements Converter<DefaultPaymentReferenceModel, PaymentReference> {

    @NonNull
    @Override
    public PaymentReference convert(DefaultPaymentReferenceModel defaultPaymentReferenceModel) {
        return new PaymentReference()
                .id(defaultPaymentReferenceModel.getId())
                .lastUpdateDate(DateTimeUtils.toDate(defaultPaymentReferenceModel.getLastUpdateDate()))
                .shopId(defaultPaymentReferenceModel.getShopId())
                .partyId(defaultPaymentReferenceModel.getPartyId())
                .modifiedByUser(defaultPaymentReferenceModel.getModifiedByUser())
                .templateId(defaultPaymentReferenceModel.getTemplateId());
    }

}
