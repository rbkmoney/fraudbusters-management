package com.rbkmoney.fraudbusters.management;

import com.rbkmoney.damsel.wb_list.ListType;
import com.rbkmoney.dao.DaoException;
import com.rbkmoney.fraudbusters.management.converter.payment.*;
import com.rbkmoney.fraudbusters.management.dao.payment.wblist.WbListDao;
import com.rbkmoney.fraudbusters.management.domain.ListRecord;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentCountInfo;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentListRecord;
import com.rbkmoney.fraudbusters.management.domain.payment.request.ListRowsInsertRequest;
import com.rbkmoney.fraudbusters.management.domain.response.ErrorResponse;
import com.rbkmoney.fraudbusters.management.exception.KafkaSerializationException;
import com.rbkmoney.fraudbusters.management.listener.WbListEventListener;
import com.rbkmoney.fraudbusters.management.resource.payment.PaymentsListsResource;
import com.rbkmoney.fraudbusters.management.service.WbListCommandService;
import com.rbkmoney.fraudbusters.management.service.iface.AuditService;
import com.rbkmoney.fraudbusters.management.service.payment.PaymentsListsService;
import com.rbkmoney.fraudbusters.management.utils.*;
import com.rbkmoney.fraudbusters.management.utils.parser.CsvPaymentCountInfoParser;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = {ParametersService.class, PaymentsListsResource.class,
        UserInfoService.class, WbListRecordToRowConverter.class, PaymentCountInfoGenerator.class,
        CountInfoUtils.class, CountInfoSwagUtils.class, CsvPaymentCountInfoParser.class,
        WbListRecordsModelToWbListRecordConverter.class, PaymentsListsService.class})
@EnableAutoConfiguration(exclude = {FlywayAutoConfiguration.class, JooqAutoConfiguration.class})
public class ExceptionApplicationTest {

    public static final String ID_TEST = "42";
    public static final String TEST_MESSAGE = "test_message";
    private static final String VALUE = "value";
    private static final String SHOP_ID = "shopId";
    private static final String PARTY_ID = "partyId";
    private static final String LIST_NAME = "listName";

    @Value("${kafka.topic.wblist.event.sink}")
    public String topicEventSink;
    @MockBean
    public AuditService auditService;
    @MockBean
    public WbListCommandService wbListCommandService;
    @MockBean
    public PaymentListRecordToRowConverter paymentListRecordToRowConverter;
    @MockBean
    public WbListEventListener wbListEventListener;
    @MockBean
    public WbListDao wbListDao;
    @MockBean
    public WbListRecordsToListRecordConverter wbListRecordsToListRecordConverter;
    @MockBean
    public PaymentCountInfoRequestToRowConverter countInfoListRecordToRowConverter;
    @MockBean
    public WbListRecordsToCountInfoListRequestConverter wbListRecordsToListRecordWithRowConverter;
    @Autowired
    RestTemplateBuilder restTemplateBuilder;
    @LocalServerPort
    int serverPort;

    String paymentListPath;
    String paymentListFilterPath;

    @Before
    public void init() {
        paymentListPath = String.format(MethodPaths.SERVICE_BASE_URL + MethodPaths.INSERT_PAYMENTS_LIST_ROW_PATH,
                serverPort);
        paymentListFilterPath = String.format(MethodPaths.SERVICE_BASE_URL + MethodPaths.INSERT_PAYMENTS_FILTER_PATH,
                serverPort);
    }

    private ListRecord createRow() {
        PaymentListRecord listRecord = new PaymentListRecord();
        listRecord.setShopId(SHOP_ID);
        listRecord.setPartyId(PARTY_ID);
        listRecord.setListName(LIST_NAME);
        listRecord.setValue(VALUE);
        return listRecord;
    }

//    @Test(expected = HttpClientErrorException.BadRequest.class)
//    public void executionRestTestBadRequest() {
//        RestTemplate restTemplate = restTemplateBuilder.build();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<Object> requestEntity = new HttpEntity<>(null, headers);
//        restTemplate.exchange(paymentListPath, HttpMethod.POST,
//                requestEntity, new ParameterizedTypeReference<>() {
//                });
//    }

    @Test
    public void executionRestTest() {
        RestTemplate restTemplate = restTemplateBuilder.build();
        Mockito.when(wbListCommandService.sendListRecords(any(), any(), any(), any()))
                .thenReturn(ResponseEntity.ok(List.of(ID_TEST)));

        ResponseEntity<List<String>> response = restTemplate.exchange(paymentListPath, HttpMethod.POST,
                new HttpEntity<>(createRequest()), new ParameterizedTypeReference<>() {
                });
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(response.getBody().get(0), ID_TEST);
    }

    @Test(expected = HttpServerErrorException.InternalServerError.class)
    public void executionRestDaoExceptionTest() {
        RestTemplate restTemplate = restTemplateBuilder.build();
        Mockito.when(wbListCommandService.sendListRecords(any(), any(), any(), any()))
                .thenThrow(new DaoException(TEST_MESSAGE));
        restTemplate.postForEntity(paymentListPath, createRequest(), ErrorResponse.class);
    }

    private ListRowsInsertRequest createRequest() {
        ListRowsInsertRequest listRowsInsertRequest = new ListRowsInsertRequest();
        listRowsInsertRequest.setListType(ListType.white);
        PaymentCountInfo countInfo = new PaymentCountInfo();
        countInfo.setListRecord((PaymentListRecord) createRow());
        listRowsInsertRequest.setRecords(List.of(countInfo));
        return listRowsInsertRequest;
    }

    @Test(expected = HttpServerErrorException.InternalServerError.class)
    public void executionRestKafkaSerializationTest() {
        RestTemplate restTemplate = restTemplateBuilder.build();
        Mockito.when(wbListCommandService.sendListRecords(any(), any(), any(), any()))
                .thenThrow(new KafkaSerializationException(TEST_MESSAGE));

        restTemplate.postForEntity(paymentListPath, createRequest(), ErrorResponse.class);
    }

    @Test(expected = HttpClientErrorException.BadRequest.class)
    public void getRestTestBadRequest() {
        Mockito.when(wbListCommandService.sendListRecords(any(), any(), any(), any()))
                .thenThrow(new KafkaSerializationException(TEST_MESSAGE));
        HashMap<String, Object> uriVariables = new HashMap<>();
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromUriString(paymentListFilterPath)
                        .queryParam("partyId", PARTY_ID)
                        .queryParam("shopId", SHOP_ID);
        uriVariables.put("partyId", PARTY_ID);
        uriVariables.put("shopId", SHOP_ID);
        RestTemplate restTemplate = restTemplateBuilder.build();
        restTemplate.getForEntity(builder.buildAndExpand(uriVariables).toUri(), ErrorResponse.class);
    }
}
