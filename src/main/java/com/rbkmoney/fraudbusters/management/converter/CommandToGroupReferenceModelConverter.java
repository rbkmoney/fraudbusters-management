package com.rbkmoney.fraudbusters.management.converter;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.GroupReference;
import com.rbkmoney.fraudbusters.management.domain.GroupReferenceModel;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
@RequiredArgsConstructor
public class CommandToGroupReferenceModelConverter implements Converter<Command, GroupReferenceModel> {

    @Override
    public GroupReferenceModel convert(Command command) {
        GroupReferenceModel model = new GroupReferenceModel();
        GroupReference groupReference = command.getCommandBody().getGroupReference();
        model.setPartyId(groupReference.getPartyId());
        model.setShopId(groupReference.getShopId());
        model.setGroupId(groupReference.getGroupId());
        return model;
    }
}
