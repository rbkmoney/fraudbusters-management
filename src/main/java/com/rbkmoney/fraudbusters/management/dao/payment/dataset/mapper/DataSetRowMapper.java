package com.rbkmoney.fraudbusters.management.dao.payment.dataset.mapper;

import com.rbkmoney.fraudbusters.management.domain.payment.TestCheckedDataSetModel;
import com.rbkmoney.fraudbusters.management.domain.payment.TestCheckedPaymentModel;
import com.rbkmoney.fraudbusters.management.domain.payment.TestPaymentModel;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.rbkmoney.fraudbusters.management.domain.Tables.*;
import static com.rbkmoney.fraudbusters.management.domain.Tables.TEST_DATA_SET_CHECKING_RESULT;

public class DataSetRowMapper implements RowMapper<TestCheckedDataSetModel> {

    public static final String TEST_DATA_SET_ID_JOIN = "test_data_set_id_join";

    @Override
    public TestCheckedDataSetModel mapRow(ResultSet resultSet, int i) throws SQLException {
        return TestCheckedDataSetModel.builder()
                .template(resultSet.getString(TEST_DATA_SET_CHECKING_RESULT.TEMPLATE.getName()))
                .checkingTimestamp(
                        resultSet.getObject(TEST_DATA_SET_CHECKING_RESULT.CHECKING_TIMESTAMP.getName(), LocalDateTime.class))
                .createdAt(resultSet.getObject(TEST_DATA_SET_CHECKING_RESULT.CREATED_AT.getName(), LocalDateTime.class))
                .partyId(resultSet.getString(TEST_DATA_SET_CHECKING_RESULT.PARTY_ID.getName()))
                .shopId(resultSet.getString(TEST_DATA_SET_CHECKING_RESULT.SHOP_ID.getName()))
                .testDataSetId(resultSet.getLong(TEST_DATA_SET.ID.getName()))
                .id(resultSet.getLong(TEST_DATA_SET_ID_JOIN))
                .initiator(resultSet.getString(TEST_DATA_SET_CHECKING_RESULT.INITIATOR.getName()))
                .build();
    }

}
