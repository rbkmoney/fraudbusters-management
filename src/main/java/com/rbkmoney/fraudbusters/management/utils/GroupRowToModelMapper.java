package com.rbkmoney.fraudbusters.management.utils;

import com.rbkmoney.fraudbusters.management.dao.payment.group.model.GroupPriorityRow;
import com.rbkmoney.fraudbusters.management.domain.GroupModel;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupRowToModelMapper {

    public List<GroupModel> groupByGroupId(List<GroupPriorityRow> groupPriorityRows) {
        if (!CollectionUtils.isEmpty(groupPriorityRows)) {
            return groupPriorityRows.stream()
                    .collect(Collectors.groupingBy(GroupPriorityRow::getGroupId,
                            Collectors.mapping(GroupPriorityRow::getPriorityIdModel, Collectors.toList()))
                    ).entrySet().stream()
                    .map(entry -> GroupModel.builder()
                            .groupId(entry.getKey())
                            .priorityTemplates(entry.getValue())
                            .build())
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

}
