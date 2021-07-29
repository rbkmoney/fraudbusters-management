package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.CommandBody;
import com.rbkmoney.swag.fraudbusters.management.model.GroupReference;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class PaymentGroupReferenceModelToCommandConverter implements Converter<GroupReference, Command> {

    @Override
    public Command convert(GroupReference groupReferenceModel) {
        return new Command()
                .setCommandBody(CommandBody.group_reference(
                        new com.rbkmoney.damsel.fraudbusters.GroupReference()
                                .setShopId(groupReferenceModel.getShopId())
                                .setPartyId(groupReferenceModel.getPartyId())
                                .setGroupId(groupReferenceModel.getGroupId()))
                );
    }
}
