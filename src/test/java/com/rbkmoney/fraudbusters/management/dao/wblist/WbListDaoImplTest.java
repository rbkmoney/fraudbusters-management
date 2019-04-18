package com.rbkmoney.fraudbusters.management.dao.wblist;

import com.rbkmoney.fraudbusters.management.dao.AbstractPostgresIntegrationTest;
import com.rbkmoney.fraudbusters.management.domain.enums.ListType;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;

@ContextConfiguration(classes = {WbListDaoImpl.class})
public class WbListDaoImplTest extends AbstractPostgresIntegrationTest {

    public static final String PARTY = "party";
    public static final String SHOP = "shop";
    public static final String LIST_NAME = "ip";
    @Autowired
    WbListDao wbListDao;

    @Test
    public void saveListRecord() {
        String id = "id";
        WbListRecords listRecord = createListRecord(id);
        wbListDao.saveListRecord(listRecord);
        WbListRecords byId = wbListDao.getById(id);
        Assert.assertEquals(listRecord, byId);

        wbListDao.removeRecord(id);
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
    }
}