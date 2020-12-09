package com.rbkmoney.fraudbusters.management.dao.payment.wblist;

import com.rbkmoney.fraudbusters.management.converter.ListRecordToRowConverterImpl;
import com.rbkmoney.fraudbusters.management.converter.p2p.P2pCountInfoListRequestToRowConverter;
import com.rbkmoney.fraudbusters.management.converter.p2p.P2pListRecordToRowConverter;
import com.rbkmoney.fraudbusters.management.converter.p2p.P2pWbListRecordsToListRecordWithRowConverter;
import com.rbkmoney.fraudbusters.management.dao.AbstractPostgresIntegrationTest;
import com.rbkmoney.fraudbusters.management.dao.p2p.wblist.P2PWbListDao;
import com.rbkmoney.fraudbusters.management.dao.p2p.wblist.P2PWbListDaoImpl;
import com.rbkmoney.fraudbusters.management.domain.enums.ListType;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pCountInfo;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.P2pWbListRecords;
import com.rbkmoney.fraudbusters.management.utils.CountInfoUtils;
import com.rbkmoney.fraudbusters.management.utils.P2pCountInfoGenerator;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;

@ContextConfiguration(classes = {P2PWbListDaoImpl.class, P2pWbListRecordsToListRecordWithRowConverter.class,
        P2pListRecordToRowConverter.class, P2pCountInfoListRequestToRowConverter.class, ListRecordToRowConverterImpl.class,
        P2pCountInfoGenerator.class, JacksonAutoConfiguration.class, CountInfoUtils.class})
public class P2pWbListDaoImplTest extends AbstractPostgresIntegrationTest {
    public static final String LIST_NAME = "ip";
    public static final String IDENTITY_ID = "identityId";
    @Autowired
    P2PWbListDao p2PWbListDao;

    @Autowired
    P2pWbListRecordsToListRecordWithRowConverter rowConverter;

    @Test
    public void saveListRecord() {
        String id = "id";
        P2pWbListRecords listRecord = createListRecord(id);

        p2PWbListDao.saveListRecord(listRecord);
        P2pWbListRecords byId = p2PWbListDao.getById(id);
        Assert.assertEquals(listRecord, byId);

        p2PWbListDao.removeRecord(listRecord);
        byId = p2PWbListDao.getById(id);
        Assert.assertNull(byId);
    }

    @Test
    public void saveEmptyIdentityListRecord() {
        String id = "id";
        P2pWbListRecords listRecord = createListRecord(id);
        listRecord.setIdentityId(null);

        p2PWbListDao.saveListRecord(listRecord);
        P2pWbListRecords byId = p2PWbListDao.getById(id);
        Assert.assertEquals(listRecord, byId);

        p2PWbListDao.removeRecord(listRecord);
        byId = p2PWbListDao.getById(id);
        Assert.assertNull(byId);
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
    public void getFilteredListRecords() {
        String firstId = "1";
        P2pWbListRecords listRecord = createListRecord(firstId);
        String secondId = "2";
        P2pWbListRecords listRecord_2 = createListRecord(secondId);
        listRecord_2.setIdentityId("identity_2");
        P2pWbListRecords listRecord_3 = createListRecord("3");

        p2PWbListDao.saveListRecord(listRecord);
        p2PWbListDao.saveListRecord(listRecord_2);
        p2PWbListDao.saveListRecord(listRecord_3);

        List<P2pWbListRecords> filteredListRecords = p2PWbListDao.getFilteredListRecords(IDENTITY_ID, ListType.black, LIST_NAME);

        Assert.assertEquals(1, filteredListRecords.size());

        filteredListRecords = p2PWbListDao.getFilteredListRecords(null, ListType.black, null);

        Assert.assertEquals(2, filteredListRecords.size());

        P2pWbListRecords listRecord_4 = createListRecord("4");
        listRecord_4.setRowInfo("{ \n" +
                "  \"count\":5, \n" +
                "  \"time_to_live\":\"2019-08-22T13:14:17.443332Z\",\n" +
                "  \"start_count_time\": \"2019-08-22T11:14:17.443332Z\"\n" +
                "}");
        listRecord_4.setListType(ListType.grey);
        p2PWbListDao.saveListRecord(listRecord_4);

        filteredListRecords = p2PWbListDao.getFilteredListRecords(null, ListType.grey, null);
        Assert.assertEquals(1, filteredListRecords.size());
        Assert.assertFalse(filteredListRecords.get(0).getRowInfo().isEmpty());

        P2pCountInfo countInfoListRecord = rowConverter.convert(filteredListRecords.get(0));

        Assert.assertEquals(5L, countInfoListRecord.getCountInfo().getCount().longValue());
    }
}
