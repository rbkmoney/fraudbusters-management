package com.rbkmoney.fraudbusters.management.dao.group;

import com.rbkmoney.fraudbusters.management.domain.GroupReferenceModel;

import java.util.List;

public interface GroupReferenceDao {

    void insert(GroupReferenceModel referenceModel);

    void remove(String partyId, String shopId);

    void remove(GroupReferenceModel referenceModel);

    List<GroupReferenceModel> getByGroupId(String id);

    List<GroupReferenceModel> getByPartyIdAndShopId(String partyId, String shopId);

}
