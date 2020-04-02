package com.rbkmoney.fraudbusters.management.dao.payment.wblist;

import com.rbkmoney.fraudbusters.management.dao.AbstractDao;
import com.rbkmoney.fraudbusters.management.dao.condition.ConditionParameterSource;
import com.rbkmoney.fraudbusters.management.domain.enums.ListType;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
import com.rbkmoney.fraudbusters.management.domain.tables.records.WbListRecordsRecord;
import com.rbkmoney.mapper.RecordRowMapper;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

import static com.rbkmoney.fraudbusters.management.domain.tables.WbListRecords.WB_LIST_RECORDS;
import static org.jooq.Comparator.EQUALS;

@Component
public class WbListDaoImpl extends AbstractDao implements WbListDao {

    private static final int LIMIT_TOTAL = 100;
    private final RowMapper<WbListRecords> listRecordRowMapper;

    public WbListDaoImpl(DataSource dataSource) {
        super(dataSource);
        listRecordRowMapper = new RecordRowMapper<>(WB_LIST_RECORDS, WbListRecords.class);
    }

    @Override
    public void saveListRecord(WbListRecords listRecord) {
        Query query = getDslContext()
                .insertInto(WB_LIST_RECORDS)
                .set(getDslContext().newRecord(WB_LIST_RECORDS, listRecord))
                .onConflict(WB_LIST_RECORDS.PARTY_ID, WB_LIST_RECORDS.SHOP_ID, WB_LIST_RECORDS.LIST_TYPE,
                        WB_LIST_RECORDS.LIST_NAME, WB_LIST_RECORDS.VALUE)
                .doNothing();
        execute(query);
    }

    @Override
    public void removeRecord(String id) {
        DeleteConditionStep<WbListRecordsRecord> where = getDslContext()
                .delete(WB_LIST_RECORDS)
                .where(WB_LIST_RECORDS.ID.eq(id));
        execute(where);
    }

    @Override
    public void removeRecord(WbListRecords listRecord) {
        DeleteConditionStep<WbListRecordsRecord> where = getDslContext()
                .delete(WB_LIST_RECORDS)
                .where(isNullOrValueCondition(WB_LIST_RECORDS.PARTY_ID, listRecord.getPartyId())
                        .and(isNullOrValueCondition(WB_LIST_RECORDS.SHOP_ID, listRecord.getShopId())
                                .and(WB_LIST_RECORDS.LIST_TYPE.eq(listRecord.getListType()))
                                .and(WB_LIST_RECORDS.LIST_NAME.eq(listRecord.getListName()))
                                .and(WB_LIST_RECORDS.VALUE.eq(listRecord.getValue()))));
        execute(where);
    }

    private Condition isNullOrValueCondition(TableField<WbListRecordsRecord, String> key, String value) {
        return value == null ? key.isNull() : key.eq(value);
    }

    @Override
    public WbListRecords getById(String id) {
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
    public List<WbListRecords> getFilteredListRecords(String partyId, String shopId, ListType listType, String listName) {
        Condition condition = DSL.trueCondition();
        SelectLimitPercentStep<Record8<String, String, String, ListType, String, String, LocalDateTime, String>> query = getDslContext()
                .select(WB_LIST_RECORDS.ID,
                        WB_LIST_RECORDS.PARTY_ID,
                        WB_LIST_RECORDS.SHOP_ID,
                        WB_LIST_RECORDS.LIST_TYPE,
                        WB_LIST_RECORDS.LIST_NAME,
                        WB_LIST_RECORDS.VALUE,
                        WB_LIST_RECORDS.INSERT_TIME,
                        WB_LIST_RECORDS.ROW_INFO)
                .from(WB_LIST_RECORDS)
                .where(appendConditions(condition, Operator.AND,
                        new ConditionParameterSource()
                                .addValue(WB_LIST_RECORDS.PARTY_ID, partyId, EQUALS)
                                .addValue(WB_LIST_RECORDS.SHOP_ID, shopId, EQUALS)
                                .addValue(WB_LIST_RECORDS.LIST_TYPE, listType, EQUALS)
                                .addValue(WB_LIST_RECORDS.LIST_NAME, listName, EQUALS)))
                .limit(LIMIT_TOTAL);
        return fetch(query, listRecordRowMapper);
    }
}
