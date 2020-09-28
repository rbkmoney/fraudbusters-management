package com.rbkmoney.fraudbusters.management.utils;

import com.rbkmoney.damsel.fraudbusters.FraudPayment;
import com.rbkmoney.fraudbusters.management.constant.CsvFraudPaymentFields;
import com.rbkmoney.fraudbusters.management.exception.DateFormatException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class CSVFraudPaymentParser implements CsvParser<FraudPayment> {

    public static final String TYPE = "text/csv";

    @Override
    public boolean hasCSVFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    @Override
    public List<FraudPayment> parse(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {
            List<FraudPayment> fraudPayments = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                fraudPayments.add(mapFraudPayment(csvRecord));
            }

            return fraudPayments;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }

    private static FraudPayment mapFraudPayment(CSVRecord csvRecord) {
        String eventTime = Instant.now().toString();
        if (!StringUtils.isEmpty(csvRecord.get(CsvFraudPaymentFields.EVENT_TIME))) {
            isValidFormat(csvRecord.get(CsvFraudPaymentFields.EVENT_TIME));
            eventTime = csvRecord.get(CsvFraudPaymentFields.EVENT_TIME);
        }
        return new FraudPayment()
                .setEventTime(eventTime)
                .setId(csvRecord.get(CsvFraudPaymentFields.ID))
                .setType(csvRecord.get(CsvFraudPaymentFields.FRAUD_TYPE))
                .setComment(csvRecord.get(CsvFraudPaymentFields.COMMENT));
    }

    private static void isValidFormat(String value) {
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd[ HH:mm:ss]");
            LocalDateTime.parse(value, dateTimeFormatter);
        } catch (Exception ex) {
            log.error("validation error when parse date: {}", value);
            throw new DateFormatException(String.format("validation error when parse date: %s", value), ex);
        }
    }


}
