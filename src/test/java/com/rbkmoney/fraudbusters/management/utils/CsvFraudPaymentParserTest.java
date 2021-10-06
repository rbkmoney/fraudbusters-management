package com.rbkmoney.fraudbusters.management.utils;

import com.rbkmoney.damsel.fraudbusters.FraudPayment;
import com.rbkmoney.fraudbusters.management.utils.parser.CsvFraudPaymentParser;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.thrift.TException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CsvFraudPaymentParserTest {

    CsvFraudPaymentParser csvFraudPaymentParser = new CsvFraudPaymentParser();

    @Test
    void hasCsvFormat() throws IOException {
        File file = new File("src/test/resources/csv/test.csv");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile =
                new MockMultipartFile("file", file.getName(), "text/csv", IOUtils.toByteArray(input));

        assertTrue(csvFraudPaymentParser.hasCsvFormat(multipartFile));
    }

    @Test
    void parse() throws FileNotFoundException {
        File file = new File("src/test/resources/csv/test.csv");
        FileInputStream input = new FileInputStream(file);
        List<FraudPayment> parse = csvFraudPaymentParser.parse(input);

        parse.forEach(fraudPayment -> {
            try {
                fraudPayment.validate();
            } catch (TException e) {
                e.printStackTrace();
            }
        });
    }
}
