package com.rbkmoney.fraudbusters.management.service;

import com.rbkmoney.damsel.wb_list.Command;
import com.rbkmoney.damsel.wb_list.ListType;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.fraudbusters.management.converter.payment.WbListRecordToRowConverter;
import com.rbkmoney.fraudbusters.management.dao.AbstractPostgresIntegrationTest;
import com.rbkmoney.fraudbusters.management.dao.payment.wblist.WbListDaoImpl;
import com.rbkmoney.fraudbusters.management.domain.tables.records.WbListRecordsRecord;
import com.rbkmoney.fraudbusters.management.utils.CountInfoUtils;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.rbkmoney.fraudbusters.management.TestObjectFactory.createWbListRecordsRecord;
import static com.rbkmoney.fraudbusters.management.TestObjectFactory.randomString;
import static com.rbkmoney.fraudbusters.management.domain.tables.WbListRecords.WB_LIST_RECORDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {WbListDaoImpl.class, GreyRottenRuleCleanerService.class, WbListRecordToRowConverter.class,
        JacksonAutoConfiguration.class, CountInfoUtils.class})
public class GreyRottenRuleCleanerServiceTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private GreyRottenRuleCleanerService greyRottenRuleCleanerService;

    @MockBean
    private WbListCommandService wbListCommandService;

    @Autowired
    DSLContext dslContext;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(greyRottenRuleCleanerService, "freshPeriod", 2);
    }

    @Test
    public void notExistRottenRecords() {
        greyRottenRuleCleanerService.clean();
        verify(wbListCommandService, times(0)).sendCommandSync(any(Row.class), any(ListType.class), any(Command.class), anyString());
    }

    @Test
    public void cleanRottenRecords() {
        WbListRecordsRecord rotRecord1 = createWbListRecordsRecord(randomString());
        rotRecord1.setTimeToLive(LocalDateTime.now().minusDays(5));
        rotRecord1.setValue(randomString());
        WbListRecordsRecord rotRecord2 = createWbListRecordsRecord(randomString());
        rotRecord2.setTimeToLive(LocalDateTime.now().minusDays(4));
        rotRecord2.setValue(randomString());
        WbListRecordsRecord freshRecord1 = createWbListRecordsRecord(randomString());
        freshRecord1.setTimeToLive(LocalDateTime.now().plusDays(1));
        freshRecord1.setValue(randomString());
        WbListRecordsRecord freshRecord2 = createWbListRecordsRecord(randomString());
        freshRecord2.setTimeToLive(LocalDateTime.now().plusDays(2));
        freshRecord2.setValue(randomString());
        dslContext.insertInto(WB_LIST_RECORDS)
                .set(rotRecord1)
                .newRecord()
                .set(rotRecord2)
                .newRecord()
                .set(freshRecord1)
                .newRecord()
                .set(freshRecord2)
                .execute();

        greyRottenRuleCleanerService.clean();

        Result<WbListRecordsRecord> freshRecords = dslContext.selectFrom(WB_LIST_RECORDS).fetch();
        assertEquals(2, freshRecords.size());
        List<String> ids = freshRecords.stream().map(WbListRecordsRecord::getId).collect(Collectors.toList());
        assertTrue(ids.containsAll(List.of(freshRecord1.getId(), freshRecord2.getId())));
        verify(wbListCommandService, atLeastOnce()).sendCommandSync(any(Row.class), any(ListType.class), any(Command.class), anyString());
    }
}


