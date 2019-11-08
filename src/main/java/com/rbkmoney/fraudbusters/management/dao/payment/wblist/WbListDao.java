package com.rbkmoney.fraudbusters.management.dao.payment.wblist;

import com.rbkmoney.fraudbusters.management.dao.CrudDao;
import com.rbkmoney.fraudbusters.management.domain.enums.ListType;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;

import java.util.List;

public interface WbListDao extends CrudDao<WbListRecords> {

    void saveListRecord(WbListRecords listRecord);

    void removeRecord(String id);

    void removeRecord(WbListRecords listRecord);

    WbListRecords getById(String id);

    List<WbListRecords> getFilteredListRecords(String partyId, String shopId, ListType listType, String listName);

}
