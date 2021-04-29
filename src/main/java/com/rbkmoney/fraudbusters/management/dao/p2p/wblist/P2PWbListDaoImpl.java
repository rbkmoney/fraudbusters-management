package com.rbkmoney.fraudbusters.management.dao.p2p.wblist;

import com.rbkmoney.fraudbusters.management.dao.AbstractDao;
import com.rbkmoney.fraudbusters.management.dao.condition.ConditionParameterSource;
import com.rbkmoney.fraudbusters.management.domain.enums.ListType;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.P2pWbListRecords;
import com.rbkmoney.fraudbusters.management.domain.tables.records.P2pWbListRecordsRecord;
import com.rbkmoney.mapper.RecordRowMapper;
import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

import java.time.LocalDateTime;
import java.util.List;

import static com.rbkmoney.fraudbusters.management.domain.tables.P2pWbListRecords.P2P_WB_LIST_RECORDS;
import static org.jooq.Comparator.EQUALS;

@Slf4j
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
        log.info("P2PWbListDaoImpl saveListRecord listRecord: {}", listRecord);
        Query query = getDslContext()
                .insertInto(P2P_WB_LIST_RECORDS)
                .set(getDslContext().newRecord(P2P_WB_LIST_RECORDS, listRecord))
                .onConflict(P2P_WB_LIST_RECORDS.IDENTITY_ID, P2P_WB_LIST_RECORDS.LIST_TYPE,
                        P2P_WB_LIST_RECORDS.LIST_NAME, P2P_WB_LIST_RECORDS.VALUE)
                .doNothing();
        execute(query);
    }

    @Override
    public void removeRecord(P2pWbListRecords listRecord) {
        log.info("P2PWbListDaoImpl removeRecord listRecord: {}", listRecord);
        DeleteConditionStep<P2pWbListRecordsRecord> where = getDslContext()
                .delete(P2P_WB_LIST_RECORDS)
                .where(isNullOrValueCondition(P2P_WB_LIST_RECORDS.IDENTITY_ID, listRecord.getIdentityId())
                        .and(P2P_WB_LIST_RECORDS.LIST_TYPE.eq(listRecord.getListType()))
                        .and(P2P_WB_LIST_RECORDS.LIST_NAME.eq(listRecord.getListName()))
                        .and(P2P_WB_LIST_RECORDS.VALUE.eq(listRecord.getValue())));
        execute(where);
    }

    private Condition isNullOrValueCondition(TableField<P2pWbListRecordsRecord, String> key, String value) {
        return value == null ? key.isNull() : key.eq(value);
    }

    @Override
    public P2pWbListRecords getById(String id) {
        log.info("P2PWbListDaoImpl getById id: {}", id);
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
        log.info("WbListDaoImpl getFilteredListRecords identityId: {} listType: {} listName: {}", identityId, listType,
                listName);
        Condition condition = DSL.trueCondition();
        SelectLimitPercentStep<Record7<String, String, ListType, String, String, LocalDateTime, String>> query =
                getDslContext()
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

    @Override
    public List<P2pWbListRecords> filterListRecords(@NonNull ListType listType, @NonNull List<String> listNames,
                                                    FilterRequest filterRequest) {
        SelectWhereStep<P2pWbListRecordsRecord> from = getDslContext()
                .selectFrom(P2P_WB_LIST_RECORDS);
        Condition condition =
                P2P_WB_LIST_RECORDS.LIST_NAME.in(listNames).and(P2P_WB_LIST_RECORDS.LIST_TYPE.eq(listType));
        SelectConditionStep<P2pWbListRecordsRecord> whereQuery = StringUtils.isEmpty(filterRequest.getSearchValue())
                ? from.where(condition)
                : from.where(condition.and(
                        P2P_WB_LIST_RECORDS.VALUE.like(filterRequest.getSearchValue())
                                .or(P2P_WB_LIST_RECORDS.IDENTITY_ID.like(filterRequest.getSearchValue()))));
        Field field = StringUtils.isEmpty(filterRequest.getSortBy())
                ? P2P_WB_LIST_RECORDS.INSERT_TIME
                : P2P_WB_LIST_RECORDS.field(filterRequest.getSortBy());
        SelectSeekStep2<P2pWbListRecordsRecord, Object, String> wbListRecordsRecords =
                addSortCondition(P2P_WB_LIST_RECORDS.ID,
                        field, filterRequest.getSortOrder(), whereQuery);
        return fetch(
                addSeekIfNeed(
                        filterRequest.getLastId(),
                        filterRequest.getSortFieldValue(),
                        filterRequest.getSize(),
                        wbListRecordsRecords
                ),
                listRecordRowMapper
        );
    }

    @Override
    public Integer countFilterRecords(@NonNull ListType listType, @NonNull List<String> listNames, String filterValue) {
        SelectJoinStep<Record1<Integer>> from = getDslContext()
                .selectCount()
                .from(P2P_WB_LIST_RECORDS);
        Condition condition =
                P2P_WB_LIST_RECORDS.LIST_NAME.in(listNames).and(P2P_WB_LIST_RECORDS.LIST_TYPE.eq(listType));
        SelectConditionStep<Record1<Integer>> where = StringUtils.isEmpty(filterValue)
                ? from.where(condition)
                : from.where(condition.and(
                        P2P_WB_LIST_RECORDS.VALUE.like(filterValue)
                                .or(P2P_WB_LIST_RECORDS.IDENTITY_ID.like(filterValue))));
        return fetchOne(where, Integer.class);
    }

    @Override
    public List<String> getCurrentListNames(ListType listType) {
        SelectConditionStep<Record1<String>> where = getDslContext()
                .selectDistinct(P2P_WB_LIST_RECORDS.LIST_NAME)
                .from(P2P_WB_LIST_RECORDS)
                .where(P2P_WB_LIST_RECORDS.LIST_TYPE.eq(listType));
        return fetch(where, (rs, rowNum) ->
                rs.getString(P2P_WB_LIST_RECORDS.LIST_NAME.getName())
        );
    }
}
