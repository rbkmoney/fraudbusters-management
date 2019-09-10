package com.rbkmoney.fraudbusters.management.domain;

import lombok.Data;
import org.antlr.v4.runtime.misc.Pair;

import java.util.List;

@Data
public class GroupModel {

    private String groupId;
    private List<Pair<Long, String>> priorityTemplates;

}
