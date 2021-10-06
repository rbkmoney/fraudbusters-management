package com.rbkmoney.fraudbusters.management.utils.parser;

import com.rbkmoney.damsel.fraudbusters.FraudPayment;
import com.rbkmoney.fraudbusters.management.constant.CsvFraudPaymentFields;
import com.rbkmoney.fraudbusters.management.exception.DateFormatException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class CsvFraudPaymentParser implements CsvParser<FraudPayment> {

    private static void isValidFormat(String value) {
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd[ HH:mm:ss]");
            LocalDateTime.parse(value, dateTimeFormatter);
        } catch (Exception ex) {
            log.error("validation error when parse date: {}", value);
            throw new DateFormatException(String.format("validation error when parse date: %s", value), ex);
        }
    }

    @Override
    public FraudPayment mapFraudPayment(CSVRecord csvRecord) {
        String eventTime = Instant.now().toString();
        if (StringUtils.hasLength(csvRecord.get(CsvFraudPaymentFields.EVENT_TIME))) {
            isValidFormat(csvRecord.get(CsvFraudPaymentFields.EVENT_TIME));
            eventTime = csvRecord.get(CsvFraudPaymentFields.EVENT_TIME);
        }
        return new FraudPayment()
                .setEventTime(eventTime)
                .setId(csvRecord.get(CsvFraudPaymentFields.ID))
                .setType(csvRecord.get(CsvFraudPaymentFields.FRAUD_TYPE))
                .setComment(csvRecord.get(CsvFraudPaymentFields.COMMENT));
    }

}
