package com.rbkmoney.fraudbusters.management.dao.payment.dataset;

import com.rbkmoney.fraudbusters.management.dao.AbstractDao;
import com.rbkmoney.fraudbusters.management.domain.payment.TestCheckedDataSetModel;
import com.rbkmoney.fraudbusters.management.domain.payment.TestCheckedPaymentModel;
import com.rbkmoney.fraudbusters.management.domain.payment.TestPaymentModel;
import com.rbkmoney.mapper.RecordRowMapper;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.rbkmoney.fraudbusters.management.domain.Tables.*;
import static org.jooq.impl.DSL.select;

@Component
public class TestDataSetCheckingResultDaoImpl extends AbstractDao implements TestDataSetCheckingResultDao {

    public static final String TEST_DATA_SET_ID_JOIN = "test_data_set_id_join";
    private final RowMapper<TestCheckedDataSetModel> listRecordRowMapper;

    public TestDataSetCheckingResultDaoImpl(DataSource dataSource) {
        super(dataSource);
        listRecordRowMapper = new RecordRowMapper<>(TEST_DATA_SET_CHECKING_RESULT, TestCheckedDataSetModel.class);
    }

    @Override
    @Transactional
    public Optional<Long> insert(TestCheckedDataSetModel dataSetModel) {
        dataSetModel.setCreatedAt(null);
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
                .select(TEST_DATA_SET_CHECKING_RESULT.ID.as(TEST_DATA_SET_ID_JOIN))
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

        TestCheckedDataSetModel testCheckedPaymentModel = fetchOne(query, createDataSetRowMapper());
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

        List<TestCheckedPaymentModel> testCheckedPaymentModels = fetch(where, createCheckedPaymentRowMapper());
        testCheckedPaymentModel.setTestCheckedPaymentModels(testCheckedPaymentModels);
        return testCheckedPaymentModel;
    }

    private RowMapper<TestCheckedPaymentModel> createCheckedPaymentRowMapper() {
        return (r, i) -> TestCheckedPaymentModel.builder()
                .resultStatus(r.getString(TEST_PAYMENT_CHECKING_RESULT.RESULT_STATUS.getName()))
                .checkedTemplate(r.getString(TEST_PAYMENT_CHECKING_RESULT.CHECKED_TEMPLATE.getName()))
                .ruleChecked(r.getString(TEST_PAYMENT_CHECKING_RESULT.RULE_CHECKED.getName()))
                .testPaymentId(r.getLong(TEST_PAYMENT_CHECKING_RESULT.TEST_PAYMENT_ID.getName()))
                .testPaymentModel(TestPaymentModel.builder()
                        .paymentCountry(r.getString(TEST_PAYMENT.PAYMENT_COUNTRY.getName()))
                        .paymentId(r.getString(TEST_PAYMENT.PAYMENT_ID.getName()))
                        .testDataSetId(r.getLong(TEST_PAYMENT.TEST_DATA_SET_ID.getName()))
                        .paymentSystem(r.getString(TEST_PAYMENT.PAYMENT_SYSTEM.getName()))
                        .amount(r.getLong(TEST_PAYMENT.AMOUNT.getName()))
                        .country(r.getString(TEST_PAYMENT.COUNTRY.getName()))
                        .currency(r.getString(TEST_PAYMENT.CURRENCY.getName()))
                        .cardToken(r.getString(TEST_PAYMENT.CARD_TOKEN.getName()))
                        .email(r.getString(TEST_PAYMENT.EMAIL.getName()))
                        .errorCode(r.getString(TEST_PAYMENT.ERROR_CODE.getName()))
                        .errorReason(r.getString(TEST_PAYMENT.ERROR_REASON.getName()))
                        .eventTime(r.getString(TEST_PAYMENT.EVENT_TIME.getName()))
                        .fingerprint(r.getString(TEST_PAYMENT.FINGERPRINT.getName()))
                        .id(r.getLong(TEST_PAYMENT.ID.getName()))
                        .paymentTool(r.getString(TEST_PAYMENT.PAYMENT_TOOL.getName()))
                        .ip(r.getString(TEST_PAYMENT.IP.getName()))
                        .lastModificationDate(r.getString(TEST_PAYMENT.LAST_MODIFICATION_TIME.getName()))
                        .lastModificationInitiator(
                                r.getString(TEST_PAYMENT.LAST_MODIFICATION_INITIATOR.getName()))
                        .mobile(r.getBoolean(TEST_PAYMENT.MOBILE.getName()))
                        .recurrent(r.getBoolean(TEST_PAYMENT.RECURRENT.getName()))
                        .payerType(r.getString(TEST_PAYMENT.PAYER_TYPE.getName()))
                        .partyId(r.getString(TEST_PAYMENT.PARTY_ID.getName()))
                        .shopId(r.getString(TEST_PAYMENT.SHOP_ID.getName()))
                        .providerId(r.getString(TEST_PAYMENT.PROVIDER_ID.getName()))
                        .status(r.getString(TEST_PAYMENT.STATUS.getName()))
                        .terminalId(r.getString(TEST_PAYMENT.TERMINAL_ID.getName()))
                        .build()
                )
                .notificationRule(getNotificationRule(r))
                .build();
    }

    private RowMapper<TestCheckedDataSetModel> createDataSetRowMapper() {
        return (r, i) -> TestCheckedDataSetModel.builder()
                .template(r.getString(TEST_DATA_SET_CHECKING_RESULT.TEMPLATE.getName()))
                .checkingTimestamp(r.getString(TEST_DATA_SET_CHECKING_RESULT.CHECKING_TIMESTAMP.getName()))
                .createdAt(r.getString(TEST_DATA_SET_CHECKING_RESULT.CREATED_AT.getName()))
                .partyId(r.getString(TEST_DATA_SET_CHECKING_RESULT.PARTY_ID.getName()))
                .shopId(r.getString(TEST_DATA_SET_CHECKING_RESULT.SHOP_ID.getName()))
                .testDataSetId(r.getLong(TEST_DATA_SET.ID.getName()))
                .id(r.getLong(TEST_DATA_SET_ID_JOIN))
                .initiator(r.getString(TEST_DATA_SET_CHECKING_RESULT.INITIATOR.getName()))
                .build();
    }

    private List<String> getNotificationRule(java.sql.ResultSet r) throws SQLException {
        if (r.getArray(TEST_PAYMENT_CHECKING_RESULT.NOTIFICATIONS_RULE.getName()) != null) {
            return Arrays.asList(
                    (String[]) r.getArray(TEST_PAYMENT_CHECKING_RESULT.NOTIFICATIONS_RULE.getName())
                            .getArray());
        }
        return null;
    }

}
