package com.rbkmoney.fraudbusters.management.utils.parser;

import com.rbkmoney.fraudbusters.management.constant.CsvPaymentListLoadFields;
import com.rbkmoney.fraudbusters.management.exception.DateFormatException;
import com.rbkmoney.fraudbusters.management.utils.DateTimeUtils;
import com.rbkmoney.swag.fraudbusters.management.model.CountInfo;
import com.rbkmoney.swag.fraudbusters.management.model.PaymentCountInfo;
import com.rbkmoney.swag.fraudbusters.management.model.PaymentListRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class CsvPaymentCountInfoParser implements CsvParser<PaymentCountInfo> {

    private static void isValidFormat(String value) {
        try {
            LocalDateTime.parse(value, DateTimeFormatter.ISO_INSTANT);
        } catch (Exception ex) {
            log.error("validation error when parse date: {}", value);
            throw new DateFormatException(String.format("validation error when parse date: %s", value), ex);
        }
    }

    @Override
    public PaymentCountInfo mapFraudPayment(CSVRecord csvRecord) {
        var listRecord = new PaymentListRecord();
        listRecord.setPartyId(csvRecord.isSet(CsvPaymentListLoadFields.PARTY_ID)
                ? csvRecord.get(CsvPaymentListLoadFields.PARTY_ID) : null);
        listRecord.setShopId(csvRecord.isSet(CsvPaymentListLoadFields.PARTY_ID)
                ? csvRecord.get(CsvPaymentListLoadFields.SHOP_ID) : null);
        listRecord.setListName(csvRecord.get(CsvPaymentListLoadFields.LIST_NAME));
        listRecord.setValue(csvRecord.get(CsvPaymentListLoadFields.VALUE));
        var paymentCountInfo = new PaymentCountInfo();
        paymentCountInfo.setListRecord(listRecord);
        if (csvRecord.isSet(CsvPaymentListLoadFields.COUNT_INFO_COUNT)) {
            var countInfo = new CountInfo();
            countInfo.setCount(Long.valueOf(csvRecord.get(CsvPaymentListLoadFields.COUNT_INFO_COUNT)));

            isValidFormat(csvRecord.get(CsvPaymentListLoadFields.COUNT_INFO_END_TIME));
            isValidFormat(csvRecord.get(CsvPaymentListLoadFields.COUNT_INFO_START_TIME));

            countInfo.setEndCountTime(
                    DateTimeUtils.toDate(csvRecord.get(CsvPaymentListLoadFields.COUNT_INFO_END_TIME)));
            countInfo.setStartCountTime(
                    DateTimeUtils.toDate(csvRecord.get(CsvPaymentListLoadFields.COUNT_INFO_START_TIME)));
            paymentCountInfo.setCountInfo(countInfo);
        }
        return paymentCountInfo;
    }
}
