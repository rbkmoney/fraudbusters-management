package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.fraudbusters.management.domain.GroupModel;
import com.rbkmoney.fraudbusters.management.domain.PriorityIdModel;
import com.rbkmoney.swag.fraudbusters.management.model.Group;
import com.rbkmoney.swag.fraudbusters.management.model.PriorityId;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class GroupModelToGroupConverter
        implements Converter<GroupModel, com.rbkmoney.swag.fraudbusters.management.model.Group> {

    private final PriorityModelToPriorityIdConverter priorityModelToPriorityIdConverter;

    @NonNull
    @Override
    public com.rbkmoney.swag.fraudbusters.management.model.Group convert(GroupModel groupModel) {
        return new Group()
                .groupId(groupModel.getGroupId())
                .priorityTemplates(convertPriorityTemplates(groupModel.getPriorityTemplates()))
                .modifiedByUser(groupModel.getModifiedByUser());
    }

    private List<PriorityId> convertPriorityTemplates(List<PriorityIdModel> priorityTemplates) {
        return priorityTemplates.stream()
                .map(priorityModelToPriorityIdConverter::destinationToSource)
                .collect(Collectors.toList());
    }
}
