package com.rbkmoney.fraudbusters.management.dao.payment.wblist;

import com.rbkmoney.fraudbusters.management.converter.payment.WbListRecordsToCountInfoListRequestConverter;
import com.rbkmoney.fraudbusters.management.dao.AbstractPostgresIntegrationTest;
import com.rbkmoney.fraudbusters.management.domain.enums.ListType;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentCountInfo;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
import com.rbkmoney.fraudbusters.management.utils.CountInfoGenerator;
import com.rbkmoney.fraudbusters.management.utils.PaymentCountInfoListRequestGenerator;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;

@ContextConfiguration(classes = {WbListDaoImpl.class, WbListRecordsToCountInfoListRequestConverter.class,
        PaymentCountInfoListRequestGenerator.class, JacksonAutoConfiguration.class, CountInfoGenerator.class})
public class WbListDaoImplTest extends AbstractPostgresIntegrationTest {

    public static final String PARTY = "party";
    public static final String SHOP = "shop";
    public static final String LIST_NAME = "ip";
    @Autowired
    WbListDao wbListDao;

    @Autowired
    WbListRecordsToCountInfoListRequestConverter wbListRecordsToListRecordWithRowConverter;

    @Test
    public void saveListRecord() {
        String id = "id";
        WbListRecords listRecord = createListRecord(id);

        wbListDao.saveListRecord(listRecord);
        WbListRecords byId = wbListDao.getById(id);
        Assert.assertEquals(listRecord, byId);

        wbListDao.removeRecord(listRecord);
        byId = wbListDao.getById(id);
        Assert.assertNull(byId);
    }

    @Test
    public void saveEmptyPartyListRecord() {
        String id = "id";
        WbListRecords listRecord = createListRecord(id);
        listRecord.setPartyId(null);
        listRecord.setShopId(null);

        wbListDao.saveListRecord(listRecord);
        WbListRecords byId = wbListDao.getById(id);
        Assert.assertEquals(listRecord, byId);

        wbListDao.removeRecord(listRecord);
        byId = wbListDao.getById(id);
        Assert.assertNull(byId);
    }

    @NotNull
    private WbListRecords createListRecord(String id) {
        WbListRecords listRecord = new WbListRecords();
        listRecord.setId(id);
        listRecord.setListName(LIST_NAME);
        listRecord.setListType(ListType.black);
        listRecord.setInsertTime(LocalDateTime.now());
        listRecord.setPartyId(PARTY);
        listRecord.setShopId(SHOP);
        listRecord.setValue("192.168.1.1");
        return listRecord;
    }

    @Test
    public void getFilteredListRecords() {
        String firstId = "1";
        WbListRecords listRecord = createListRecord(firstId);
        String secondId = "2";
        WbListRecords listRecord_2 = createListRecord(secondId);
        listRecord_2.setPartyId("party_2");
        WbListRecords listRecord_3 = createListRecord("3");

        wbListDao.saveListRecord(listRecord);
        wbListDao.saveListRecord(listRecord_2);
        wbListDao.saveListRecord(listRecord_3);

        List<WbListRecords> filteredListRecords = wbListDao.getFilteredListRecords(PARTY, SHOP, ListType.black, LIST_NAME);

        Assert.assertEquals(1, filteredListRecords.size());

        filteredListRecords = wbListDao.getFilteredListRecords(null, SHOP, ListType.black, null);

        Assert.assertEquals(2, filteredListRecords.size());

        WbListRecords listRecord_4 = createListRecord("4");
        listRecord_4.setRowInfo("{ \n" +
                "  \"count\":5, \n" +
                "  \"time_to_live\":\"2019-08-22T13:14:17.443332Z\",\n" +
                "  \"start_count_time\": \"2019-08-22T11:14:17.443332Z\"\n" +
                "}");
        listRecord_4.setListType(ListType.grey);
        wbListDao.saveListRecord(listRecord_4);

        filteredListRecords = wbListDao.getFilteredListRecords(null, SHOP, ListType.grey, null);
        Assert.assertEquals(1, filteredListRecords.size());
        Assert.assertFalse(filteredListRecords.get(0).getRowInfo().isEmpty());

        PaymentCountInfo countInfoListRecord = wbListRecordsToListRecordWithRowConverter.convert(filteredListRecords.get(0));

        Assert.assertEquals(5L, countInfoListRecord.getCountInfo().getCount().longValue());
    }
}