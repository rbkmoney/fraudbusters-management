package com.rbkmoney.fraudbusters.management.dao.payment.wblist;

import com.rbkmoney.fraudbusters.management.converter.ListRecordToRowConverterImpl;
import com.rbkmoney.fraudbusters.management.converter.payment.PaymentCountInfoRequestToRowConverter;
import com.rbkmoney.fraudbusters.management.converter.payment.PaymentListRecordToRowConverter;
import com.rbkmoney.fraudbusters.management.converter.payment.WbListRecordsToCountInfoListRequestConverter;
import com.rbkmoney.fraudbusters.management.dao.AbstractPostgresIntegrationTest;
import com.rbkmoney.fraudbusters.management.domain.enums.ListType;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentCountInfo;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
import com.rbkmoney.fraudbusters.management.utils.CountInfoUtils;
import com.rbkmoney.fraudbusters.management.utils.PaymentCountInfoGenerator;
import org.jetbrains.annotations.NotNull;
import org.jooq.SortOrder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

@ContextConfiguration(classes = {WbListDaoImpl.class, WbListRecordsToCountInfoListRequestConverter.class,
        PaymentListRecordToRowConverter.class, PaymentCountInfoRequestToRowConverter.class, ListRecordToRowConverterImpl.class,
        PaymentCountInfoGenerator.class, JacksonAutoConfiguration.class, CountInfoUtils.class})
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
        assertEquals(listRecord, byId);

        wbListDao.removeRecord(listRecord);
        byId = wbListDao.getById(id);
        assertNull(byId);
    }

    @Test
    public void saveEmptyPartyListRecord() {
        String id = "id";
        WbListRecords listRecord = createListRecord(id);
        listRecord.setPartyId(null);
        listRecord.setShopId(null);

        wbListDao.saveListRecord(listRecord);
        WbListRecords byId = wbListDao.getById(id);
        assertEquals(listRecord, byId);

        wbListDao.removeRecord(listRecord);
        byId = wbListDao.getById(id);
        assertNull(byId);
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

        assertEquals(1, filteredListRecords.size());

        filteredListRecords = wbListDao.getFilteredListRecords(null, SHOP, ListType.black, null);

        assertEquals(2, filteredListRecords.size());

        WbListRecords listRecord_4 = createListRecord("4");
        listRecord_4.setRowInfo("{ \n" +
                "  \"count\":5, \n" +
                "  \"time_to_live\":\"2019-08-22T13:14:17.443332Z\",\n" +
                "  \"start_count_time\": \"2019-08-22T11:14:17.443332Z\"\n" +
                "}");
        listRecord_4.setListType(ListType.grey);
        wbListDao.saveListRecord(listRecord_4);

        filteredListRecords = wbListDao.getFilteredListRecords(null, SHOP, ListType.grey, null);
        assertEquals(1, filteredListRecords.size());
        assertFalse(filteredListRecords.get(0).getRowInfo().isEmpty());

        PaymentCountInfo countInfoListRecord = wbListRecordsToListRecordWithRowConverter.convert(filteredListRecords.get(0));

        assertEquals(5L, countInfoListRecord.getCountInfo().getCount().longValue());

        //check sorting
        List<WbListRecords> wbListRecordsFirst = wbListDao.filterListRecords(ListType.black, List.of(LIST_NAME),
                new FilterRequest(
                        null,
                        null,
                        null,
                        3,
                        null,
                        SortOrder.ASC));
        List<WbListRecords> wbListRecordsSecond = wbListDao.filterListRecords(ListType.black, List.of(LIST_NAME), new FilterRequest(
                null,
                null,
                null,
                3,
                null,
                SortOrder.ASC));
        assertEquals(wbListRecordsFirst.get(0).getPartyId(), wbListRecordsSecond.get(1).getPartyId());

        //check paging
        wbListRecordsFirst = wbListDao.filterListRecords(ListType.black, List.of(LIST_NAME), new FilterRequest(
                null,
                null,
                null,
                1,
                null,
                SortOrder.ASC));
        wbListRecordsSecond = wbListDao.filterListRecords(ListType.black, List.of(LIST_NAME), new FilterRequest(
                null,
                wbListRecordsFirst.get(0).getId(),
                wbListRecordsFirst.get(0).getInsertTime().toString(),
                3,
                null,
                SortOrder.ASC));
        Integer count = wbListDao.countFilterRecords(ListType.black, List.of(LIST_NAME), null);
        assertEquals(Integer.valueOf(2), count);
        assertNotEquals(wbListRecordsFirst.get(0).getPartyId(), wbListRecordsSecond.get(0).getPartyId());

        List<String> currentListNames = wbListDao.getCurrentListNames(ListType.black);
        assertEquals(LIST_NAME, currentListNames.get(0));
    }
}
