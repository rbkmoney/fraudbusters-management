package com.rbkmoney.fraudbusters.management.dao.group;

import com.rbkmoney.fraudbusters.management.domain.GroupModel;

public interface GroupDao {

    void insert(GroupModel groupModel);

    void remove(String id);

    void remove(GroupModel groupModel);

    GroupModel getById(String id);

}
