package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.CommandBody;
import com.rbkmoney.damsel.fraudbusters.GroupReference;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class PaymentGroupReferenceModelToCommandConverter
        implements Converter<com.rbkmoney.swag.fraudbusters.management.model.GroupReference, Command> {

    @Override
    public Command convert(com.rbkmoney.swag.fraudbusters.management.model.GroupReference groupReferenceModel) {
        GroupReference reference = new GroupReference();
        reference.setShopId(groupReferenceModel.getShopId());
        reference.setPartyId(groupReferenceModel.getPartyId());
        reference.setGroupId(groupReferenceModel.getGroupId());
        Command command = new Command();
        command.setCommandBody(CommandBody.group_reference(reference));
        return command;
    }
}
