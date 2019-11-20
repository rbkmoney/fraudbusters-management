package com.rbkmoney.fraudbusters.management.dao.p2p.wblist;

import com.rbkmoney.fraudbusters.management.dao.CdDao;
import com.rbkmoney.fraudbusters.management.domain.enums.ListType;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.P2pWbListRecords;

import java.util.List;

public interface P2PWbListDao extends CdDao<P2pWbListRecords> {

    void saveListRecord(P2pWbListRecords listRecord);

    void removeRecord(String id);

    void removeRecord(P2pWbListRecords listRecord);

    P2pWbListRecords getById(String id);

    List<P2pWbListRecords> getFilteredListRecords(String identityId, ListType listType, String listName);

}
