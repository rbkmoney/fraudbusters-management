package com.rbkmoney.fraudbusters.management.dao.payment.wblist;

import com.rbkmoney.fraudbusters.management.config.PostgresqlJooqITest;
import com.rbkmoney.fraudbusters.management.converter.ListRecordToRowConverterImpl;
import com.rbkmoney.fraudbusters.management.converter.p2p.P2pCountInfoListRequestToRowConverter;
import com.rbkmoney.fraudbusters.management.converter.p2p.P2pListRecordToRowConverter;
import com.rbkmoney.fraudbusters.management.converter.p2p.P2pWbListRecordsToListRecordWithRowConverter;
import com.rbkmoney.fraudbusters.management.dao.p2p.wblist.P2PWbListDao;
import com.rbkmoney.fraudbusters.management.dao.p2p.wblist.P2PWbListDaoImpl;
import com.rbkmoney.fraudbusters.management.domain.enums.ListType;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pCountInfo;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.P2pWbListRecords;
import com.rbkmoney.fraudbusters.management.utils.CountInfoUtils;
import com.rbkmoney.fraudbusters.management.utils.P2pCountInfoGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@PostgresqlJooqITest
@ContextConfiguration(classes = {P2PWbListDaoImpl.class, P2pWbListRecordsToListRecordWithRowConverter.class,
        P2pListRecordToRowConverter.class, P2pCountInfoListRequestToRowConverter.class,
        ListRecordToRowConverterImpl.class,
        P2pCountInfoGenerator.class, JacksonAutoConfiguration.class, CountInfoUtils.class})
public class P2pWbListDaoImplTest {
    public static final String LIST_NAME = "ip";
    public static final String IDENTITY_ID = "identityId";
    @Autowired
    P2PWbListDao p2PWbListDao;

    @Autowired
    P2pWbListRecordsToListRecordWithRowConverter rowConverter;

    @Test
    void saveListRecord() {
        String id = "id";
        P2pWbListRecords listRecord = createListRecord(id);

        p2PWbListDao.saveListRecord(listRecord);
        P2pWbListRecords byId = p2PWbListDao.getById(id);
        assertEquals(listRecord.getListType(), byId.getListType());
        assertEquals(listRecord.getListName(), byId.getListName());
        assertEquals(listRecord.getValue(), byId.getValue());
        assertEquals(listRecord.getIdentityId(), byId.getIdentityId());

        p2PWbListDao.removeRecord(listRecord);
        byId = p2PWbListDao.getById(id);
        assertNull(byId);
    }

    @Test
    void saveEmptyIdentityListRecord() {
        String id = "id";
        P2pWbListRecords listRecord = createListRecord(id);
        listRecord.setIdentityId(null);

        p2PWbListDao.saveListRecord(listRecord);
        P2pWbListRecords byId = p2PWbListDao.getById(id);
        assertEquals(listRecord.getListType(), byId.getListType());
        assertEquals(listRecord.getListName(), byId.getListName());
        assertEquals(listRecord.getValue(), byId.getValue());
        assertEquals(listRecord.getIdentityId(), byId.getIdentityId());

        p2PWbListDao.removeRecord(listRecord);
        byId = p2PWbListDao.getById(id);
        assertNull(byId);
    }

    private P2pWbListRecords createListRecord(String id) {
        P2pWbListRecords listRecord = new P2pWbListRecords();
        listRecord.setId(id);
        listRecord.setListName(LIST_NAME);
        listRecord.setListType(ListType.black);
        listRecord.setInsertTime(LocalDateTime.now());
        listRecord.setIdentityId(IDENTITY_ID);
        listRecord.setValue("192.168.1.1");
        return listRecord;
    }

    @Test
    void getFilteredListRecords() {
        String firstId = "1";
        P2pWbListRecords listRecord = createListRecord(firstId);
        String secondId = "2";
        P2pWbListRecords listRecord2 = createListRecord(secondId);
        listRecord2.setIdentityId("identity_2");
        P2pWbListRecords listRecord3 = createListRecord("3");

        p2PWbListDao.saveListRecord(listRecord);
        p2PWbListDao.saveListRecord(listRecord2);
        p2PWbListDao.saveListRecord(listRecord3);

        List<P2pWbListRecords> filteredListRecords =
                p2PWbListDao.getFilteredListRecords(IDENTITY_ID, ListType.black, LIST_NAME);

        assertEquals(1, filteredListRecords.size());

        filteredListRecords = p2PWbListDao.getFilteredListRecords(null, ListType.black, null);

        assertEquals(2, filteredListRecords.size());

        P2pWbListRecords listRecord4 = createListRecord("4");
        listRecord4.setRowInfo("{ \n" +
                "  \"count\":5, \n" +
                "  \"time_to_live\":\"2019-08-22T13:14:17.443332Z\",\n" +
                "  \"start_count_time\": \"2019-08-22T11:14:17.443332Z\"\n" +
                "}");
        listRecord4.setListType(ListType.grey);
        p2PWbListDao.saveListRecord(listRecord4);

        filteredListRecords = p2PWbListDao.getFilteredListRecords(null, ListType.grey, null);
        assertEquals(1, filteredListRecords.size());
        assertFalse(filteredListRecords.get(0).getRowInfo().isEmpty());

        P2pCountInfo countInfoListRecord = rowConverter.convert(filteredListRecords.get(0));

        assertEquals(5L, countInfoListRecord.getCountInfo().getCount().longValue());
    }
}
