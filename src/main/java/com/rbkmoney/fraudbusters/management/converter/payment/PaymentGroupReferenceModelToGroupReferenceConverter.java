package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.fraudbusters.management.domain.payment.PaymentGroupReferenceModel;
import com.rbkmoney.fraudbusters.management.utils.DateTimeUtils;
import com.rbkmoney.swag.fraudbusters.management.model.GroupReference;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class PaymentGroupReferenceModelToGroupReferenceConverter implements
        Converter<PaymentGroupReferenceModel, com.rbkmoney.swag.fraudbusters.management.model.GroupReference> {

    @Override
    public com.rbkmoney.swag.fraudbusters.management.model.GroupReference convert(
            PaymentGroupReferenceModel groupReferenceModel) {
        return new GroupReference()
                .id(groupReferenceModel.getId())
                .shopId(groupReferenceModel.getShopId())
                .partyId(groupReferenceModel.getPartyId())
                .groupId(groupReferenceModel.getGroupId())
                .lastUpdateDate(DateTimeUtils.toDate(groupReferenceModel.getLastUpdateDate()))
                .modifiedByUser(groupReferenceModel.getModifiedByUser());
    }
}
