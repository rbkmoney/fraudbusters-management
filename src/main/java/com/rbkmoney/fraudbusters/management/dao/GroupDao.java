package com.rbkmoney.fraudbusters.management.dao;

import com.rbkmoney.fraudbusters.management.domain.GroupModel;

import java.util.List;

public interface GroupDao {

    void insert(GroupModel groupModel);

    void remove(String id);

    void remove(GroupModel groupModel);

    GroupModel getById(String id);

    List<GroupModel> filterGroup(String filterValue);
}
