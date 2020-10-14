package com.rbkmoney.fraudbusters.management.dao.payment.group.model;

import com.rbkmoney.fraudbusters.management.domain.PriorityIdModel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupPriorityRow {

    private String groupId;
    private PriorityIdModel priorityIdModel;

}
