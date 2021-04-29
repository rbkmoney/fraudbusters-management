package com.rbkmoney.fraudbusters.management.utils.parser;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public interface CsvParser<T> {

    String TYPE = "text/csv";

    T mapFraudPayment(CSVRecord csvRecord);

    default boolean hasCsvFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    default List<T> parse(InputStream is) {
        try (BufferedReader fileReader = createBufferReader(is); CSVParser csvParser = createCsvParser(fileReader)) {
            return csvParser.getRecords().stream()
                    .map(this::mapFraudPayment)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }

    private BufferedReader createBufferReader(InputStream is) {
        return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
    }

    private CSVParser createCsvParser(BufferedReader fileReader) throws IOException {
        return new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader()
                .withIgnoreHeaderCase().withTrim());
    }

}
