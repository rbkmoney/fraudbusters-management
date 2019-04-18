package com.rbkmoney.fraudbusters.management.dao.wblist;

import com.rbkmoney.fraudbusters.management.dao.AbstractDao;
import com.rbkmoney.fraudbusters.management.dao.mapper.RecordRowMapper;
import com.rbkmoney.fraudbusters.management.domain.enums.ListType;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
import com.rbkmoney.fraudbusters.management.domain.tables.records.WbListRecordsRecord;
import com.rbkmoney.fraudbusters.management.exception.DaoException;
import org.jooq.DeleteConditionStep;
import org.jooq.Query;
import org.jooq.Record7;
import org.jooq.SelectConditionStep;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

import static com.rbkmoney.fraudbusters.management.domain.tables.WbListRecords.WB_LIST_RECORDS;

@Component
public class WbListDaoImpl extends AbstractDao implements WbListDao {

    private final RowMapper<WbListRecords> listRecordRowMapper;

    public WbListDaoImpl(DataSource dataSource) {
        super(dataSource);
        listRecordRowMapper = new RecordRowMapper<>(WB_LIST_RECORDS, WbListRecords.class);
    }

    @Override
    public void saveListRecord(WbListRecords listRecord) throws DaoException {
        Query query = getDslContext().insertInto(WB_LIST_RECORDS)
                .set(getDslContext().newRecord(WB_LIST_RECORDS, listRecord))
                .onConflict(WB_LIST_RECORDS.PARTY_ID, WB_LIST_RECORDS.SHOP_ID, WB_LIST_RECORDS.LIST_TYPE,
                        WB_LIST_RECORDS.LIST_NAME, WB_LIST_RECORDS.VALUE)
                .doNothing();
        execute(query);
    }

    @Override
    public void removeRecord(String id) throws DaoException {
        DeleteConditionStep<WbListRecordsRecord> where = getDslContext().delete(WB_LIST_RECORDS)
                .where(WB_LIST_RECORDS.ID.eq(id));
        execute(where);
    }

    @Override
    public void removeRecord(WbListRecords listRecord) throws DaoException {
        DeleteConditionStep<WbListRecordsRecord> where = getDslContext().delete(WB_LIST_RECORDS)
                .where(WB_LIST_RECORDS.PARTY_ID.eq(listRecord.getPartyId())
                        .and(WB_LIST_RECORDS.SHOP_ID.eq(listRecord.getShopId()))
                        .and(WB_LIST_RECORDS.LIST_TYPE.eq(listRecord.getListType()))
                        .and(WB_LIST_RECORDS.LIST_NAME.eq(listRecord.getListName()))
                        .and(WB_LIST_RECORDS.VALUE.eq(listRecord.getValue())));
        execute(where);
    }

    @Override
    public WbListRecords getById(String id) throws DaoException {
        SelectConditionStep<Record7<String, String, String, ListType, String, String, LocalDateTime>> query =
                getDslContext()
                        .select(WB_LIST_RECORDS.ID,
                                WB_LIST_RECORDS.PARTY_ID,
                                WB_LIST_RECORDS.SHOP_ID,
                                WB_LIST_RECORDS.LIST_TYPE,
                                WB_LIST_RECORDS.LIST_NAME,
                                WB_LIST_RECORDS.VALUE,
                                WB_LIST_RECORDS.INSERT_TIME)
                        .from(WB_LIST_RECORDS)
                        .where(WB_LIST_RECORDS.ID.eq(id));
        return fetchOne(query, listRecordRowMapper);
    }

    @Override
    public List<WbListRecords> getFilteredListRecords(String partyId, String shopId, ListType listType, String listName) throws DaoException {
        SelectConditionStep<Record7<String, String, String, ListType, String, String, LocalDateTime>> query =
                getDslContext()
                        .select(WB_LIST_RECORDS.ID,
                                WB_LIST_RECORDS.PARTY_ID,
                                WB_LIST_RECORDS.SHOP_ID,
                                WB_LIST_RECORDS.LIST_TYPE,
                                WB_LIST_RECORDS.LIST_NAME,
                                WB_LIST_RECORDS.VALUE,
                                WB_LIST_RECORDS.INSERT_TIME)
                        .from(WB_LIST_RECORDS)
                        .where(WB_LIST_RECORDS.PARTY_ID.eq(partyId)
                                .and(WB_LIST_RECORDS.SHOP_ID.eq(shopId))
                                .and(WB_LIST_RECORDS.LIST_TYPE.eq(listType))
                                .and(WB_LIST_RECORDS.LIST_NAME.eq(listName)));
        return fetch(query, listRecordRowMapper);
    }
}
