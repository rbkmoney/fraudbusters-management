package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.CommandBody;
import com.rbkmoney.damsel.fraudbusters.Group;
import com.rbkmoney.damsel.fraudbusters.PriorityId;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class GroupToCommandConverter
        implements Converter<com.rbkmoney.swag.fraudbusters.management.model.Group, Command> {

    @NonNull
    @Override
    public Command convert(com.rbkmoney.swag.fraudbusters.management.model.Group groupModel) {
        return new Command()
                .setCommandBody(CommandBody.group(new Group()
                        .setGroupId(groupModel.getGroupId())
                        .setTemplateIds(groupModel.getPriorityTemplates().stream()
                                .map(pair -> new PriorityId()
                                        .setPriority(pair.getPriority())
                                        .setId(pair.getId()))
                                .collect(Collectors.toList()))));
    }
}
