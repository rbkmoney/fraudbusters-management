package com.rbkmoney.fraudbusters.management.utils;

import com.rbkmoney.damsel.fraudbusters.FraudPayment;
import com.rbkmoney.fraudbusters.management.utils.parser.CsvFraudPaymentParser;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class CsvFraudPaymentParserTest {

    CsvFraudPaymentParser csvFraudPaymentParser = new CsvFraudPaymentParser();

    @Test
    public void hasCsvFormat() throws IOException {
        File file = new File("src/test/resources/csv/test.csv");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile =
                new MockMultipartFile("file", file.getName(), "text/csv", IOUtils.toByteArray(input));

        Assert.assertTrue(csvFraudPaymentParser.hasCsvFormat(multipartFile));
    }

    @Test
    public void testttt(){
        LocalDateTime parse = LocalDateTime.parse("2021-04-27 12:33:40.340621");
    }

    @Test
    public void parse() throws FileNotFoundException {
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
