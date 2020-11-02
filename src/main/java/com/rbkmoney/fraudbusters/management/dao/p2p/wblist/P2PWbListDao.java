package com.rbkmoney.fraudbusters.management.dao.p2p.wblist;

import com.rbkmoney.fraudbusters.management.dao.CdDao;
import com.rbkmoney.fraudbusters.management.domain.enums.ListType;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.P2pWbListRecords;
import org.jooq.SortOrder;
import org.springframework.lang.NonNull;

import java.util.List;

public interface P2PWbListDao extends CdDao<P2pWbListRecords> {

    void saveListRecord(P2pWbListRecords listRecord);

    void removeRecord(String id);

    void removeRecord(P2pWbListRecords listRecord);

    P2pWbListRecords getById(String id);

    List<P2pWbListRecords> getFilteredListRecords(String identityId, ListType listType, String listName);

    <T> List<P2pWbListRecords> filterListRecords(ListType listType, List<String> listNames, String filterValue,
                                              String lastId, T sortFieldValue, Integer size, String sortingBy, SortOrder sortOrder);

    Integer countFilterRecords(@NonNull ListType listType, @NonNull List<String> listNames, String filterValue);

    List<String> getCurrentListNames(ListType listType);
}
