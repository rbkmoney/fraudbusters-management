package com.rbkmoney.fraudbusters.management.converter.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.fraudbusters.management.TestObjectFactory;
import com.rbkmoney.fraudbusters.management.domain.enums.ListType;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
import com.rbkmoney.fraudbusters.management.utils.RowUtilsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RowToWbListRecordsConverterTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private RowToWbListRecordsConverter converter;
    private RowUtilsService rowUtilsService;

    @BeforeEach
    void setUp() {
        rowUtilsService = new RowUtilsService(objectMapper);
        converter = new RowToWbListRecordsConverter(rowUtilsService);
    }

    @Test
    void shouldConvertSuccess() {
        Row row = TestObjectFactory.buildRow();

        WbListRecords wbListRecords = converter.convert(row);

        assertEquals(row.getValue(), wbListRecords.getValue());
        assertEquals(row.getListName(), wbListRecords.getListName());
        assertEquals(row.getId().getPaymentId().getPartyId(), wbListRecords.getPartyId());
        assertEquals(row.getId().getPaymentId().getShopId(), wbListRecords.getShopId());
        assertEquals(ListType.valueOf(row.getListType().name()), wbListRecords.getListType());
        String actualTimeToLive = wbListRecords.getTimeToLive().toInstant(
                ZoneOffset.UTC).toString();
        assertEquals(row.getRowInfo().getCountInfo().getTimeToLive(), actualTimeToLive);
        assertThat(wbListRecords.getRowInfo(), containsString(actualTimeToLive));
    }
}
