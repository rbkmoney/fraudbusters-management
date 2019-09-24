package com.rbkmoney.fraudbusters.management.domain;

import lombok.Data;

import java.util.List;

@Data
public class GroupModel {

    private String groupId;
    private List<PriorityIdModel> priorityTemplates;

}
