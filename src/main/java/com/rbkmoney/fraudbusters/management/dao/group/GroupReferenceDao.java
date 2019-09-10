package com.rbkmoney.fraudbusters.management.dao.group;

import com.rbkmoney.fraudbusters.management.domain.GroupReferenceModel;

public interface GroupReferenceDao {

    void insert(GroupReferenceModel referenceModel);

    void remove(String partyId, String shopId);

    void remove(GroupReferenceModel referenceModel);

    GroupReferenceModel getByGroupId(String id);

}
