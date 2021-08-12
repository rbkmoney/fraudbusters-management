package com.rbkmoney.fraudbusters.management.dao.payment.dataset;

import com.rbkmoney.fraudbusters.management.dao.AbstractDao;
import com.rbkmoney.fraudbusters.management.domain.payment.TestPaymentModel;
import com.rbkmoney.fraudbusters.management.domain.tables.records.TestPaymentRecord;
import com.rbkmoney.mapper.RecordRowMapper;
import org.jooq.DeleteConditionStep;
import org.jooq.InsertOnDuplicateSetMoreStep;
import org.jooq.Query;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import java.util.List;
import java.util.stream.Collectors;

import static com.rbkmoney.fraudbusters.management.domain.Tables.TEST_PAYMENT;

@Component
public class TestPaymentDaoImpl extends AbstractDao implements TestPaymentDao {

    private final RowMapper<TestPaymentModel> listRecordRowMapper;

    public TestPaymentDaoImpl(DataSource dataSource) {
        super(dataSource);
        listRecordRowMapper = new RecordRowMapper<>(TEST_PAYMENT, TestPaymentModel.class);
    }

    @Override
    public void insert(TestPaymentModel paymentModel) {
        paymentModel.setLastModificationDate(null);
        Query query = getDslContext().insertInto(TEST_PAYMENT)
                .set(getDslContext().newRecord(TEST_PAYMENT, paymentModel))
                .onConflict(TEST_PAYMENT.ID)
                .doUpdate()
                .set(getDslContext().newRecord(TEST_PAYMENT, paymentModel));
        execute(query);
    }

    @Override
    public void insertBatch(List<TestPaymentModel> dataSetModels) {
        var dslContext = getDslContext();
        List<InsertOnDuplicateSetMoreStep<TestPaymentRecord>> collect = dataSetModels.stream()
                .map(paymentModel ->
                        dslContext.insertInto(TEST_PAYMENT)
                                .set(getDslContext().newRecord(TEST_PAYMENT, paymentModel))
                                .onConflict(TEST_PAYMENT.ID)
                                .doUpdate()
                                .set(getDslContext().newRecord(TEST_PAYMENT, paymentModel)))
                .collect(Collectors.toList());
        dslContext.batch(collect)
                .execute();
    }

    @Override
    public void remove(Long id) {
        DeleteConditionStep<TestPaymentRecord> where = getDslContext()
                .delete(TEST_PAYMENT)
                .where(TEST_PAYMENT.ID.eq(id));
        execute(where);
    }

    @Override
    public void removeByDataSetId(Long id) {
        DeleteConditionStep<TestPaymentRecord> where = getDslContext()
                .delete(TEST_PAYMENT)
                .where(TEST_PAYMENT.TEST_DATA_SET_ID.eq(id));
        execute(where);
    }

    @Override
    public List<TestPaymentModel> getByDataSetId(Long id) {
        Query query = getDslContext()
                .selectFrom(TEST_PAYMENT)
                .where(TEST_PAYMENT.ID.eq(id));
        return fetch(query, listRecordRowMapper);
    }
}
