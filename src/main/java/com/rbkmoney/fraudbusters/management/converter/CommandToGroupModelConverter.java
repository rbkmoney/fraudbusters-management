package com.rbkmoney.fraudbusters.management.converter;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.Group;
import com.rbkmoney.damsel.fraudbusters.PriorityId;
import com.rbkmoney.fraudbusters.management.domain.GroupModel;
import com.rbkmoney.fraudbusters.management.domain.PriorityIdModel;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class CommandToGroupModelConverter implements Converter<Command, GroupModel> {

    @Override
    public GroupModel convert(Command command) {
        GroupModel model = new GroupModel();
        Group group = command.getCommandBody().getGroup();
        model.setGroupId(group.getGroupId());
        List<PriorityId> templateIds = group.getTemplateIds();
        if (!CollectionUtils.isEmpty(templateIds)) {
            model.setPriorityTemplates(convertPriorityIds(templateIds));
        }
        return model;
    }

    private List<PriorityIdModel> convertPriorityIds(List<PriorityId> templateIds) {
        return templateIds.stream()
                .map(priorityId -> new PriorityIdModel(priorityId.getPriority(), priorityId.getId()))
                .collect(Collectors.toList());
    }
}
