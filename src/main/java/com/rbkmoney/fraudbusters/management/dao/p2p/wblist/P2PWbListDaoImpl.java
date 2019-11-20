package com.rbkmoney.fraudbusters.management.dao.p2p.wblist;

import com.rbkmoney.fraudbusters.management.dao.AbstractDao;
import com.rbkmoney.fraudbusters.management.dao.condition.ConditionParameterSource;
import com.rbkmoney.fraudbusters.management.domain.enums.ListType;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.P2pWbListRecords;
import com.rbkmoney.fraudbusters.management.domain.tables.records.P2pWbListRecordsRecord;
import com.rbkmoney.mapper.RecordRowMapper;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

import static com.rbkmoney.fraudbusters.management.domain.tables.P2pWbListRecords.P2P_WB_LIST_RECORDS;
import static org.jooq.Comparator.EQUALS;

@Component
public class P2PWbListDaoImpl extends AbstractDao implements P2PWbListDao {

    private static final int LIMIT_TOTAL = 100;
    private final RowMapper<P2pWbListRecords> listRecordRowMapper;

    public P2PWbListDaoImpl(DataSource dataSource) {
        super(dataSource);
        listRecordRowMapper = new RecordRowMapper<>(P2P_WB_LIST_RECORDS, P2pWbListRecords.class);
    }

    @Override
    public void saveListRecord(P2pWbListRecords listRecord) {
        Query query = getDslContext()
                .insertInto(P2P_WB_LIST_RECORDS)
                .set(getDslContext().newRecord(P2P_WB_LIST_RECORDS, listRecord))
                .onConflict(P2P_WB_LIST_RECORDS.IDENTITY_ID, P2P_WB_LIST_RECORDS.LIST_TYPE,
                        P2P_WB_LIST_RECORDS.LIST_NAME, P2P_WB_LIST_RECORDS.VALUE)
                .doNothing();
        execute(query);
    }

    @Override
    public void removeRecord(String id) {
        DeleteConditionStep<P2pWbListRecordsRecord> where = getDslContext()
                .delete(P2P_WB_LIST_RECORDS)
                .where(P2P_WB_LIST_RECORDS.ID.eq(id));
        execute(where);
    }

    @Override
    public void removeRecord(P2pWbListRecords listRecord) {
        DeleteConditionStep<P2pWbListRecordsRecord> where = getDslContext()
                .delete(P2P_WB_LIST_RECORDS)
                .where(P2P_WB_LIST_RECORDS.IDENTITY_ID.eq(listRecord.getIdentityId())
                        .and(P2P_WB_LIST_RECORDS.LIST_TYPE.eq(listRecord.getListType()))
                        .and(P2P_WB_LIST_RECORDS.LIST_NAME.eq(listRecord.getListName()))
                        .and(P2P_WB_LIST_RECORDS.VALUE.eq(listRecord.getValue())));
        execute(where);
    }

    @Override
    public P2pWbListRecords getById(String id) {
        SelectConditionStep<Record6<String, String, ListType, String, String, LocalDateTime>> query =
                getDslContext()
                        .select(P2P_WB_LIST_RECORDS.ID,
                                P2P_WB_LIST_RECORDS.IDENTITY_ID,
                                P2P_WB_LIST_RECORDS.LIST_TYPE,
                                P2P_WB_LIST_RECORDS.LIST_NAME,
                                P2P_WB_LIST_RECORDS.VALUE,
                                P2P_WB_LIST_RECORDS.INSERT_TIME)
                        .from(P2P_WB_LIST_RECORDS)
                        .where(P2P_WB_LIST_RECORDS.ID.eq(id));
        return fetchOne(query, listRecordRowMapper);
    }

    @Override
    public List<P2pWbListRecords> getFilteredListRecords(String identityId, ListType listType, String listName) {
        Condition condition = DSL.trueCondition();
        SelectLimitPercentStep<Record7<String, String, ListType, String, String, LocalDateTime, String>> query = getDslContext()
                .select(P2P_WB_LIST_RECORDS.ID,
                        P2P_WB_LIST_RECORDS.IDENTITY_ID,
                        P2P_WB_LIST_RECORDS.LIST_TYPE,
                        P2P_WB_LIST_RECORDS.LIST_NAME,
                        P2P_WB_LIST_RECORDS.VALUE,
                        P2P_WB_LIST_RECORDS.INSERT_TIME,
                        P2P_WB_LIST_RECORDS.ROW_INFO)
                .from(P2P_WB_LIST_RECORDS)
                .where(appendConditions(condition, Operator.AND,
                        new ConditionParameterSource()
                                .addValue(P2P_WB_LIST_RECORDS.IDENTITY_ID, identityId, EQUALS)
                                .addValue(P2P_WB_LIST_RECORDS.LIST_TYPE, listType, EQUALS)
                                .addValue(P2P_WB_LIST_RECORDS.LIST_NAME, listName, EQUALS)))
                .limit(LIMIT_TOTAL);
        return fetch(query, listRecordRowMapper);
    }
}
