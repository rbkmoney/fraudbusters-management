package com.rbkmoney.fraudbusters.management.utils;

import com.rbkmoney.damsel.fraudbusters.FraudPayment;
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
import java.util.List;

public class CSVFraudPaymentParserTest {

    CSVFraudPaymentParser csvFraudPaymentParser = new CSVFraudPaymentParser();

    @Test
    public void hasCSVFormat() throws IOException {
        File file = new File("src/test/resources/csv/test.csv");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "text/csv", IOUtils.toByteArray(input));

        Assert.assertTrue(csvFraudPaymentParser.hasCSVFormat(multipartFile));
    }

    @Test
    public void parse() throws FileNotFoundException {
        File file = new File("src/test/resources/csv/test.csv");
        FileInputStream input = new FileInputStream(file);
        List<FraudPayment> parse = csvFraudPaymentParser.parse(input);

        parse.forEach(fraudPayment -> {
            try {
                fraudPayment.validate();
                if (fraudPayment.getPayer().isSetPaymentResource()) {
                    fraudPayment.getPayer().getPaymentResource().validate();
                } else {
                    fraudPayment.getPayer().getRecurrent().validate();
                }
            } catch (TException e) {
                e.printStackTrace();
            }
        });
    }
}