package com.rbkmoney.fraudbusters.management.converter;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.CommandBody;
import com.rbkmoney.damsel.fraudbusters.GroupReference;
import com.rbkmoney.fraudbusters.management.domain.GroupReferenceModel;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class GroupReferenceToCommandConverter implements Converter<GroupReferenceModel, Command> {

    @Override
    public Command convert(GroupReferenceModel groupReferenceModel) {
        Command command = new Command();
        GroupReference reference = new GroupReference();
        reference.setShopId(groupReferenceModel.getShopId());
        reference.setPartyId(groupReferenceModel.getPartyId());
        reference.setGroupId(groupReferenceModel.getGroupId());
        command.setCommandBody(CommandBody.group_reference(reference));
        return command;
    }
}
