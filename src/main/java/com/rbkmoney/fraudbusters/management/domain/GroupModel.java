package com.rbkmoney.fraudbusters.management.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupModel {

    private String groupId;
    private List<PriorityIdModel> priorityTemplates;

}
