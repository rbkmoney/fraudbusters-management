package com.rbkmoney.fraudbusters.management.dao.payment.dataset.mapper;

import com.rbkmoney.fraudbusters.management.domain.payment.CheckedDataSetModel;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static com.rbkmoney.fraudbusters.management.domain.Tables.TEST_DATA_SET;
import static com.rbkmoney.fraudbusters.management.domain.Tables.TEST_DATA_SET_CHECKING_RESULT;

public class DataSetRowMapper implements RowMapper<CheckedDataSetModel> {

    public static final String TEST_DATA_SET_ID_JOIN = "test_data_set_id_join";

    @Override
    public CheckedDataSetModel mapRow(ResultSet resultSet, int i) throws SQLException {
        return CheckedDataSetModel.builder()
                .template(resultSet.getString(TEST_DATA_SET_CHECKING_RESULT.TEMPLATE.getName()))
                .checkingTimestamp(resultSet.getObject(TEST_DATA_SET_CHECKING_RESULT.CHECKING_TIMESTAMP.getName(),
                                LocalDateTime.class))
                .createdAt(resultSet.getObject(TEST_DATA_SET_CHECKING_RESULT.CREATED_AT.getName(), LocalDateTime.class))
                .partyId(resultSet.getString(TEST_DATA_SET_CHECKING_RESULT.PARTY_ID.getName()))
                .shopId(resultSet.getString(TEST_DATA_SET_CHECKING_RESULT.SHOP_ID.getName()))
                .testDataSetId(resultSet.getLong(TEST_DATA_SET.ID.getName()))
                .id(resultSet.getLong(TEST_DATA_SET_ID_JOIN))
                .initiator(resultSet.getString(TEST_DATA_SET_CHECKING_RESULT.INITIATOR.getName()))
                .build();
    }

}
