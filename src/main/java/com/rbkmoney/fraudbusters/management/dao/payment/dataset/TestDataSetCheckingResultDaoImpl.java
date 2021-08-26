package com.rbkmoney.fraudbusters.management.dao.payment.dataset;

import com.rbkmoney.fraudbusters.management.dao.AbstractDao;
import com.rbkmoney.fraudbusters.management.dao.payment.dataset.mapper.CheckedPaymentModelRowMapper;
import com.rbkmoney.fraudbusters.management.dao.payment.dataset.mapper.DataSetRowMapper;
import com.rbkmoney.fraudbusters.management.domain.payment.TestCheckedDataSetModel;
import com.rbkmoney.fraudbusters.management.domain.payment.TestCheckedPaymentModel;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.rbkmoney.fraudbusters.management.domain.Tables.*;
import static org.jooq.impl.DSL.select;

@Component
public class TestDataSetCheckingResultDaoImpl extends AbstractDao implements TestDataSetCheckingResultDao {

    public TestDataSetCheckingResultDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    private final CheckedPaymentModelRowMapper checkedPaymentModelRowMapper = new CheckedPaymentModelRowMapper();
    private final DataSetRowMapper dataSetRowMapper = new DataSetRowMapper();

    @Override
    @Transactional
    public Optional<Long> insert(TestCheckedDataSetModel dataSetModel) {
        dataSetModel.setCreatedAt(LocalDateTime.now());
        Query query = getDslContext().insertInto(TEST_DATA_SET_CHECKING_RESULT)
                .set(getDslContext().newRecord(TEST_DATA_SET_CHECKING_RESULT, dataSetModel))
                .onConflict(TEST_DATA_SET_CHECKING_RESULT.ID)
                .doUpdate()
                .set(getDslContext().newRecord(TEST_DATA_SET_CHECKING_RESULT, dataSetModel))
                .returning(TEST_DATA_SET_CHECKING_RESULT.ID);
        var keyHolder = new GeneratedKeyHolder();
        execute(query, keyHolder);

        Optional<Long> checkedDataSetModelId = Optional.ofNullable(keyHolder.getKey()).map(Number::longValue);
        List<Query> queries = dataSetModel.getTestCheckedPaymentModels().stream()
                .peek(testCheckedPaymentModel -> testCheckedPaymentModel
                        .setTestDataSetCheckingResultId(checkedDataSetModelId.get()))
                .map(payment -> getDslContext().newRecord(TEST_PAYMENT_CHECKING_RESULT, payment))
                .map(testCheckedPaymentModel -> getDslContext().insertInto(TEST_PAYMENT_CHECKING_RESULT)
                        .set(getDslContext().newRecord(TEST_PAYMENT_CHECKING_RESULT, testCheckedPaymentModel))
                        .onConflict(TEST_PAYMENT_CHECKING_RESULT.ID)
                        .doUpdate()
                        .set(getDslContext().newRecord(TEST_PAYMENT_CHECKING_RESULT, testCheckedPaymentModel)))
                .collect(Collectors.toList());
        batchExecute(queries);
        return checkedDataSetModelId;
    }

    @Override
    public TestCheckedDataSetModel getById(Long id) {
        var dslContext = getDslContext();
        Query query = dslContext.select(TEST_DATA_SET.fields())
                .select(TEST_DATA_SET_CHECKING_RESULT.fields())
                .select(TEST_DATA_SET_CHECKING_RESULT.ID.as(DataSetRowMapper.TEST_DATA_SET_ID_JOIN))
                .from(TEST_DATA_SET)
                .leftJoin(TEST_DATA_SET_CHECKING_RESULT)
                .on(TEST_DATA_SET.ID.eq(TEST_DATA_SET_CHECKING_RESULT.TEST_DATA_SET_ID))
                .where(TEST_DATA_SET.ID.eq(id)
                        .and(TEST_DATA_SET_CHECKING_RESULT.ID.eq(
                                select(DSL.max(TEST_DATA_SET_CHECKING_RESULT.ID))
                                        .from(TEST_DATA_SET_CHECKING_RESULT)
                                        .where(TEST_DATA_SET_CHECKING_RESULT.TEST_DATA_SET_ID.eq(id)))
                                .or(TEST_DATA_SET_CHECKING_RESULT.ID.isNull()))
                );

        TestCheckedDataSetModel testCheckedPaymentModel = fetchOne(query, dataSetRowMapper);
        SelectConditionStep<Record> where = null;
        if (testCheckedPaymentModel.getId() != null && testCheckedPaymentModel.getId() != 0) {
            where = dslContext.select(TEST_PAYMENT.fields())
                    .select(TEST_PAYMENT_CHECKING_RESULT.fields())
                    .from(TEST_PAYMENT)
                    .leftJoin(TEST_PAYMENT_CHECKING_RESULT)
                    .on(TEST_PAYMENT.ID.eq(TEST_PAYMENT_CHECKING_RESULT.TEST_PAYMENT_ID))
                    .where(TEST_PAYMENT_CHECKING_RESULT.TEST_DATA_SET_CHECKING_RESULT_ID
                            .equal(testCheckedPaymentModel.getId()));
        } else {
            where = dslContext.select(TEST_PAYMENT.fields())
                    .select(TEST_PAYMENT_CHECKING_RESULT.fields())
                    .from(TEST_PAYMENT)
                    .leftJoin(TEST_PAYMENT_CHECKING_RESULT)
                    .on(TEST_PAYMENT.ID.eq(TEST_PAYMENT_CHECKING_RESULT.TEST_PAYMENT_ID))
                    .where(TEST_PAYMENT.TEST_DATA_SET_ID.equal(testCheckedPaymentModel.getTestDataSetId()));
        }

        List<TestCheckedPaymentModel> testCheckedPaymentModels = fetch(where, checkedPaymentModelRowMapper);
        testCheckedPaymentModel.setTestCheckedPaymentModels(testCheckedPaymentModels);
        return testCheckedPaymentModel;
    }


}
