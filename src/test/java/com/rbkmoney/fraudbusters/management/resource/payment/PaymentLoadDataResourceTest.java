package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.damsel.fraudbusters.PaymentServiceSrv;
import com.rbkmoney.fraudbusters.management.utils.CSVFraudPaymentParser;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.thrift.TException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {PaymentLoadDataResource.class, CSVFraudPaymentParser.class})
public class PaymentLoadDataResourceTest {

    @Autowired
    PaymentLoadDataResource paymentLoadDataResource;
    @MockBean
    PaymentServiceSrv.Iface paymentServiceSrv;

    @Test
    public void loadFraudOperation() throws IOException, TException {
        File file = new File("src/test/resources/csv/test.csv");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "text/csv", IOUtils.toByteArray(input));

        paymentLoadDataResource.loadFraudOperation(multipartFile);

        Mockito.verify(paymentServiceSrv, Mockito.times(1)).insertFraudPayments(any());
    }
}