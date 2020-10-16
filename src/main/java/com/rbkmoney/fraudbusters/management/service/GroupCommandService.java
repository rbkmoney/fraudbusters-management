package com.rbkmoney.fraudbusters.management.service;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.management.converter.GroupModelToCommandConverter;
import com.rbkmoney.fraudbusters.management.domain.GroupModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
@RequiredArgsConstructor
public class GroupCommandService {

    private final CommandSender commandSender;
    public final String topic;
    public final GroupModelToCommandConverter groupModelToCommandConverter;

    public String sendCommandSync(Command command) {
        String key = command.getCommandBody().getGroup().getGroupId();
        return commandSender.send(topic, command, key);
    }

    public Command createTemplateCommandById(String id) {
        return groupModelToCommandConverter.convert(GroupModel.builder()
                .groupId(id)
                .priorityTemplates(new ArrayList<>())
                .build());
    }
}
