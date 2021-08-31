package com.rbkmoney.fraudbusters.management.dao.payment.dataset;

import com.rbkmoney.fraudbusters.management.dao.AbstractDao;
import com.rbkmoney.fraudbusters.management.domain.payment.DataSetModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.domain.tables.records.TestDataSetRecord;
import com.rbkmoney.fraudbusters.management.utils.DateTimeUtils;
import com.rbkmoney.mapper.RecordRowMapper;
import org.jooq.*;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.rbkmoney.fraudbusters.management.domain.Tables.TEST_DATA_SET;

@Component
public class DataSetDaoImpl extends AbstractDao implements DataSetDao {

    private final RowMapper<DataSetModel> listRecordRowMapper;

    public DataSetDaoImpl(DataSource dataSource) {
        super(dataSource);
        listRecordRowMapper = new RecordRowMapper<>(TEST_DATA_SET, DataSetModel.class);
    }

    @Override
    public Optional<Long> insert(DataSetModel dataSetModel) {
        dataSetModel.setLastModificationTime(null);
        Query query = getDslContext().insertInto(TEST_DATA_SET)
                .set(getDslContext().newRecord(TEST_DATA_SET, dataSetModel))
                .onConflict(TEST_DATA_SET.NAME)
                .doUpdate()
                .set(getDslContext().newRecord(TEST_DATA_SET, dataSetModel))
                .returning(TEST_DATA_SET.ID);
        var keyHolder = new GeneratedKeyHolder();
        execute(query, keyHolder);
        return Optional.ofNullable(keyHolder.getKey()).map(Number::longValue);
    }

    @Override
    public void remove(Long id) {
        DeleteConditionStep<TestDataSetRecord> where = getDslContext()
                .delete(TEST_DATA_SET)
                .where(TEST_DATA_SET.ID.eq(id));
        execute(where);
    }

    @Override
    public DataSetModel getById(Long id) {
        Query query = getDslContext()
                .selectFrom(TEST_DATA_SET)
                .where(TEST_DATA_SET.ID.eq(id));
        return fetchOne(query, listRecordRowMapper);
    }

    @Override
    public List<DataSetModel> filter(LocalDateTime from, LocalDateTime to, FilterRequest filterRequest) {
        SelectWhereStep<TestDataSetRecord> where = getDslContext()
                .selectFrom(TEST_DATA_SET);

        SelectConditionStep<TestDataSetRecord> whereQuery =
                !StringUtils.hasLength(filterRequest.getSearchValue())
                        ? where.where(TEST_DATA_SET.LAST_MODIFICATION_TIME.between(from, to))
                        : where.where(TEST_DATA_SET.NAME.like(filterRequest.getSearchValue())
                        .and(TEST_DATA_SET.LAST_MODIFICATION_TIME.between(from, to)));

        SelectSeekStep2<TestDataSetRecord, LocalDateTime, Long> queryOrdered =
                addSortCondition(TEST_DATA_SET.ID, TEST_DATA_SET.LAST_MODIFICATION_TIME, filterRequest.getSortOrder(),
                        whereQuery);

        return fetch(
                addSeekIfNeed(
                        filterRequest.getLastId() != null ? Long.valueOf(filterRequest.getLastId()) : null,
                        filterRequest.getSortBy() != null ? DateTimeUtils.toDate(filterRequest.getSortBy()) : null,
                        filterRequest.getSize(),
                        queryOrdered
                ),
                listRecordRowMapper
        );
    }
}
