package com.rbkmoney.fraudbusters.management.service.p2p;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.management.converter.p2p.GroupModelToCommandConverter;
import com.rbkmoney.fraudbusters.management.domain.GroupModel;
import com.rbkmoney.fraudbusters.management.service.CommandSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
@RequiredArgsConstructor
public class GroupModelCommandService {

    private final CommandSender commandSender;
    private final String topic;
    private final GroupModelToCommandConverter groupModelToCommandConverter;

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
