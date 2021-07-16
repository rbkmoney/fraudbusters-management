package com.rbkmoney.fraudbusters.management.service;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.management.converter.payment.GroupToCommandConverter;
import com.rbkmoney.swag.fraudbusters.management.model.Group;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
@RequiredArgsConstructor
public class GroupCommandService {

    private final CommandSender commandSender;
    private final String topic;
    private final GroupToCommandConverter groupToCommandConverter;

    public String sendCommandSync(Command command) {
        String key = command.getCommandBody().getGroup().getGroupId();
        return commandSender.send(topic, command, key);
    }

    public Command createTemplateCommandById(String id) {
        return groupToCommandConverter.convert(new Group()
                .groupId(id)
                .priorityTemplates(new ArrayList<>()));
    }
}
