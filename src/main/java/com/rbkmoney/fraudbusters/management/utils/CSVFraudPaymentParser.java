package com.rbkmoney.fraudbusters.management.utils;

import com.rbkmoney.damsel.fraudbusters.FraudPayment;
import com.rbkmoney.fraudbusters.management.constant.CsvFraudPaymentFields;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
        return new FraudPayment()
                .setEventTime(Instant.now().toString())
                .setId(csvRecord.get(CsvFraudPaymentFields.ID))
                .setType(csvRecord.get(CsvFraudPaymentFields.FRAUD_TYPE))
                .setComment(csvRecord.get(CsvFraudPaymentFields.COMMENT));
    }

}