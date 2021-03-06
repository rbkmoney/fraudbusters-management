package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.damsel.wb_list.ListType;
import com.rbkmoney.fraudbusters.management.converter.payment.WbListRecordToRowConverter;
import com.rbkmoney.fraudbusters.management.dao.payment.wblist.WbListDao;
import com.rbkmoney.fraudbusters.management.service.WbListCommandService;
import com.rbkmoney.fraudbusters.management.utils.ParametersService;
import com.rbkmoney.fraudbusters.management.utils.PaymentCountInfoGenerator;
import com.rbkmoney.fraudbusters.management.utils.UserInfoService;
import com.rbkmoney.fraudbusters.management.utils.parser.CsvPaymentCountInfoParser;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.http.auth.BasicUserPrincipal;
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
@ContextConfiguration(classes = {ListsResource.class, CsvPaymentCountInfoParser.class})
public class PaymentListLoadDataResourceTest {

    @MockBean
    WbListDao wbListDao;
    @MockBean
    WbListCommandService wbListCommandService;
    @MockBean
    WbListRecordToRowConverter wbListRecordToRowConverter;
    @MockBean
    PaymentCountInfoGenerator paymentCountInfoGenerator;
    @MockBean
    UserInfoService userInfoService;
    @MockBean
    ParametersService parametersService;

    @Autowired
    ListsResource listsResource;

    @Test
    public void loadFraudOperation() throws IOException, TException {
        File file = new File("src/test/resources/csv/list-test.csv");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile =
                new MockMultipartFile("file", file.getName(), "text/csv", IOUtils.toByteArray(input));

        listsResource.insertFromCsv(new BasicUserPrincipal("test"), multipartFile, ListType.black);

        Mockito.verify(wbListCommandService, Mockito.times(1)).sendListRecords(any(), any(), any(), any());
    }
}
