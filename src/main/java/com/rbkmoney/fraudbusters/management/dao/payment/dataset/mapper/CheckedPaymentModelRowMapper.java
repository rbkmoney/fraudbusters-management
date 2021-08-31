package com.rbkmoney.fraudbusters.management.dao.payment.dataset.mapper;

import com.rbkmoney.fraudbusters.management.domain.payment.CheckedPaymentModel;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentModel;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.rbkmoney.fraudbusters.management.domain.Tables.TEST_PAYMENT;
import static com.rbkmoney.fraudbusters.management.domain.Tables.TEST_PAYMENT_CHECKING_RESULT;

public class CheckedPaymentModelRowMapper implements RowMapper<CheckedPaymentModel> {

    @Override
    public CheckedPaymentModel mapRow(ResultSet resultSet, int i) throws SQLException {
        return CheckedPaymentModel.builder()
                .resultStatus(resultSet.getString(TEST_PAYMENT_CHECKING_RESULT.RESULT_STATUS.getName()))
                .checkedTemplate(resultSet.getString(TEST_PAYMENT_CHECKING_RESULT.CHECKED_TEMPLATE.getName()))
                .ruleChecked(resultSet.getString(TEST_PAYMENT_CHECKING_RESULT.RULE_CHECKED.getName()))
                .testPaymentId(resultSet.getLong(TEST_PAYMENT_CHECKING_RESULT.TEST_PAYMENT_ID.getName()))
                .paymentModel(PaymentModel.builder()
                        .paymentCountry(resultSet.getString(TEST_PAYMENT.PAYMENT_COUNTRY.getName()))
                        .paymentId(resultSet.getString(TEST_PAYMENT.PAYMENT_ID.getName()))
                        .testDataSetId(resultSet.getLong(TEST_PAYMENT.TEST_DATA_SET_ID.getName()))
                        .paymentSystem(resultSet.getString(TEST_PAYMENT.PAYMENT_SYSTEM.getName()))
                        .amount(resultSet.getLong(TEST_PAYMENT.AMOUNT.getName()))
                        .country(resultSet.getString(TEST_PAYMENT.COUNTRY.getName()))
                        .currency(resultSet.getString(TEST_PAYMENT.CURRENCY.getName()))
                        .cardToken(resultSet.getString(TEST_PAYMENT.CARD_TOKEN.getName()))
                        .email(resultSet.getString(TEST_PAYMENT.EMAIL.getName()))
                        .errorCode(resultSet.getString(TEST_PAYMENT.ERROR_CODE.getName()))
                        .errorReason(resultSet.getString(TEST_PAYMENT.ERROR_REASON.getName()))
                        .eventTime(resultSet.getObject(TEST_PAYMENT.EVENT_TIME.getName(), LocalDateTime.class))
                        .fingerprint(resultSet.getString(TEST_PAYMENT.FINGERPRINT.getName()))
                        .id(resultSet.getLong(TEST_PAYMENT.ID.getName()))
                        .paymentTool(resultSet.getString(TEST_PAYMENT.PAYMENT_TOOL.getName()))
                        .ip(resultSet.getString(TEST_PAYMENT.IP.getName()))
                        .lastModificationDate(
                                resultSet.getObject(TEST_PAYMENT.LAST_MODIFICATION_TIME.getName(), LocalDateTime.class))
                        .lastModificationInitiator(
                                resultSet.getString(TEST_PAYMENT.LAST_MODIFICATION_INITIATOR.getName()))
                        .mobile(resultSet.getBoolean(TEST_PAYMENT.MOBILE.getName()))
                        .recurrent(resultSet.getBoolean(TEST_PAYMENT.RECURRENT.getName()))
                        .payerType(resultSet.getString(TEST_PAYMENT.PAYER_TYPE.getName()))
                        .partyId(resultSet.getString(TEST_PAYMENT.PARTY_ID.getName()))
                        .shopId(resultSet.getString(TEST_PAYMENT.SHOP_ID.getName()))
                        .providerId(resultSet.getString(TEST_PAYMENT.PROVIDER_ID.getName()))
                        .status(resultSet.getString(TEST_PAYMENT.STATUS.getName()))
                        .terminalId(resultSet.getString(TEST_PAYMENT.TERMINAL_ID.getName()))
                        .bin(resultSet.getString(TEST_PAYMENT.BIN.getName()))
                        .lastDigits(resultSet.getString(TEST_PAYMENT.LAST_DIGITS.getName()))
                        .build()
                )
                .notificationRule(getNotificationRule(resultSet))
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
