package com.rbkmoney.fraudbusters.management.dao.p2p.wblist;

import com.rbkmoney.fraudbusters.management.dao.CdDao;
import com.rbkmoney.fraudbusters.management.domain.enums.ListType;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.P2pWbListRecords;
import org.jooq.SortOrder;
import org.springframework.lang.NonNull;

import java.util.List;

public interface P2PWbListDao extends CdDao<P2pWbListRecords> {

    void saveListRecord(P2pWbListRecords listRecord);

    //todo не используется, удалить?
    void removeRecord(String id);

    void removeRecord(P2pWbListRecords listRecord);

    P2pWbListRecords getById(String id);

    List<P2pWbListRecords> getFilteredListRecords(String identityId, ListType listType, String listName);

    List<P2pWbListRecords> filterListRecords(ListType listType, List<String> listNames, FilterRequest filterRequest);

    Integer countFilterRecords(@NonNull ListType listType, @NonNull List<String> listNames, String filterValue);

    List<String> getCurrentListNames(ListType listType);
}
