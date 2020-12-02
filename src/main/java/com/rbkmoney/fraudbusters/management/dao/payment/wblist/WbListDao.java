package com.rbkmoney.fraudbusters.management.dao.payment.wblist;

import com.rbkmoney.fraudbusters.management.dao.CdDao;
import com.rbkmoney.fraudbusters.management.domain.enums.ListType;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
import org.jooq.SortOrder;
import org.springframework.lang.NonNull;

import java.util.List;

public interface WbListDao extends CdDao<WbListRecords> {

    void saveListRecord(WbListRecords listRecord);

    //todo не используется, выпилить?
    void removeRecord(String id);

    void removeRecord(WbListRecords listRecord);

    WbListRecords getById(String id);

    List<WbListRecords> getFilteredListRecords(String partyId, String shopId, ListType listType, String listName);

    List<WbListRecords> filterListRecords(ListType listType, List<String> listNames, FilterRequest filterRequest);

    Integer countFilterRecords(@NonNull ListType listType, @NonNull List<String> listNames, String filterValue);

    List<String> getCurrentListNames(ListType listType);
}
