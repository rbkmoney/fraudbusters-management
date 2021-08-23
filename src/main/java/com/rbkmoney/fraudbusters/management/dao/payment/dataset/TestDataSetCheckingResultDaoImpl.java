package com.rbkmoney.fraudbusters.management.dao.payment.dataset;

import com.rbkmoney.fraudbusters.management.dao.AbstractDao;
import com.rbkmoney.fraudbusters.management.domain.payment.TestCheckedDataSetModel;
import com.rbkmoney.fraudbusters.management.domain.payment.TestCheckedPaymentModel;
import com.rbkmoney.fraudbusters.management.domain.payment.TestPaymentModel;
import com.rbkmoney.mapper.RecordRowMapper;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
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

@Component
public class TestDataSetCheckingResultDaoImpl extends AbstractDao implements TestDataSetCheckingResultDao {

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
        Query query = getDslContext()
                .selectFrom(TEST_DATA_SET_CHECKING_RESULT)
                .where(TEST_DATA_SET_CHECKING_RESULT.ID.eq(id));

        TestCheckedDataSetModel testCheckedPaymentModel = fetchOne(query, listRecordRowMapper);
        SelectConditionStep<Record> where = getDslContext().select(TEST_PAYMENT.fields())
                .select(TEST_PAYMENT_CHECKING_RESULT.fields())
                .from(TEST_PAYMENT)
                .leftJoin(TEST_PAYMENT_CHECKING_RESULT)
                .on(TEST_PAYMENT.ID.eq(TEST_PAYMENT_CHECKING_RESULT.TEST_PAYMENT_ID))
                .where(TEST_PAYMENT_CHECKING_RESULT.TEST_DATA_SET_CHECKING_RESULT_ID.equal(id));

        List<TestCheckedPaymentModel> testCheckedPaymentModels =
                fetch(where, (r, i) -> TestCheckedPaymentModel.builder()
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
                        .build());
        testCheckedPaymentModel.setTestCheckedPaymentModels(testCheckedPaymentModels);

        return testCheckedPaymentModel;
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
