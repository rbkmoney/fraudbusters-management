package com.rbkmoney.fraudbusters.management;

import com.rbkmoney.damsel.wb_list.*;
import com.rbkmoney.fraudbusters.management.domain.tables.records.WbListRecordsRecord;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public abstract class TestObjectFactory {

    public static Row buildRow() {
        PaymentId paymentId = new PaymentId();
        paymentId.setPartyId(randomString());
        paymentId.setShopId(randomString());
        IdInfo idInfo = new IdInfo();
        idInfo.setPaymentId(paymentId);
        Row row = new Row();
        row.setId(idInfo);
        row.setListName(randomString());
        row.setValue(randomString());
        row.setListType(ListType.grey);
        CountInfo countInfo = new CountInfo();
        countInfo.setTimeToLive(Instant.now().toString());
        RowInfo rowInfo = new RowInfo();
        rowInfo.setCountInfo(countInfo);
        row.setRowInfo(rowInfo);
        return row;
    }

    public static String randomString() {
        return UUID.randomUUID().toString();
    }

    public static WbListRecordsRecord createWbListRecordsRecord(String id) {
        WbListRecordsRecord listRecord = new WbListRecordsRecord();
        listRecord.setId(id);
        listRecord.setListName(randomString());
        listRecord.setListType(com.rbkmoney.fraudbusters.management.domain.enums.ListType.black);
        listRecord.setInsertTime(LocalDateTime.now());
        listRecord.setPartyId(randomString());
        listRecord.setShopId(randomString());
        listRecord.setValue("192.168.1.1");
        return listRecord;
    }
}
