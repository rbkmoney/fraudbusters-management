package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.GroupReference;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentGroupReferenceModel;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class CommandToGroupReferenceModelConverter implements Converter<Command, PaymentGroupReferenceModel> {

    @Override
    public PaymentGroupReferenceModel convert(Command command) {
        PaymentGroupReferenceModel model = new PaymentGroupReferenceModel();
        GroupReference groupReference = command.getCommandBody().getGroupReference();
        model.setPartyId(groupReference.getPartyId());
        model.setShopId(groupReference.getShopId());
        model.setGroupId(groupReference.getGroupId());
        return model;
    }
}
