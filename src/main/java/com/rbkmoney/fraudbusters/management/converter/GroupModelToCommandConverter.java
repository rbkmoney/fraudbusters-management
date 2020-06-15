package com.rbkmoney.fraudbusters.management.converter;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.CommandBody;
import com.rbkmoney.damsel.fraudbusters.Group;
import com.rbkmoney.damsel.fraudbusters.PriorityId;
import com.rbkmoney.fraudbusters.management.domain.GroupModel;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class GroupModelToCommandConverter implements Converter<GroupModel, Command> {

    @NonNull
    @Override
    public Command convert(GroupModel groupModel) {
        Command command = new Command();
        Group group = new Group();
        group.setGroupId(groupModel.getGroupId());
        group.setTemplateIds(groupModel.getPriorityTemplates().stream()
                .map(pair -> new PriorityId()
                        .setPriority(pair.getPriority())
                        .setId(pair.getId()))
                .collect(Collectors.toList()));
        command.setCommandBody(CommandBody.group(group));
        return command;
    }
}
