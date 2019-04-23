package com.rbkmoney.fraudbusters.management;

import com.rbkmoney.fraudbusters.management.controller.ErrorController;
import com.rbkmoney.fraudbusters.management.converter.ListRecordToRowConverter;
import com.rbkmoney.fraudbusters.management.converter.WbListRecordsToListRecordConverter;
import com.rbkmoney.fraudbusters.management.dao.wblist.WbListDao;
import com.rbkmoney.fraudbusters.management.domain.ListRecord;
import com.rbkmoney.fraudbusters.management.domain.response.ErrorResponse;
import com.rbkmoney.fraudbusters.management.exception.DaoException;
import com.rbkmoney.fraudbusters.management.exception.KafkaSerializationException;
import com.rbkmoney.fraudbusters.management.listener.WbListEventListener;
import com.rbkmoney.fraudbusters.management.resource.WbListResource;
import com.rbkmoney.fraudbusters.management.service.WbListCommandService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = {WbListResource.class, ErrorController.class})
@EnableAutoConfiguration(exclude = {FlywayAutoConfiguration.class, JooqAutoConfiguration.class})
public class ExceptionApplicationTest {

    private static final String VALUE = "value";
    private static final String KEY = "key";
    private static final String SHOP_ID = "shopId";
    private static final String PARTY_ID = "partyId";
    private static final String LIST_NAME = "listName";
    public static final String ID_TEST = "42";
    public static final String TEST_MESSAGE = "test_message";

    @Value("${kafka.topic.wblist.event.sink}")
    public String topicEventSink;

    @MockBean
    public WbListCommandService wbListCommandService;
    @MockBean
    public ListRecordToRowConverter listRecordToRowConverter;
    @MockBean
    public WbListEventListener wbListEventListener;
    @MockBean
    public WbListDao wbListDao;
    @MockBean
    public WbListRecordsToListRecordConverter wbListRecordsToListRecordConverter;

    @Autowired
    RestTemplateBuilder restTemplateBuilder;

    @LocalServerPort
    int serverPort;

    private static String SERVICE_URL = "http://localhost:%s";

    @NotNull
    private ListRecord createRow() {
        ListRecord listRecord = new ListRecord();
        listRecord.setShopId(SHOP_ID);
        listRecord.setPartyId(PARTY_ID);
        listRecord.setListName(LIST_NAME);
        listRecord.setValue(VALUE);
        return listRecord;
    }

    @Test(expected = HttpClientErrorException.BadRequest.class)
    public void executionRestTestBadRequest() throws ExecutionException, InterruptedException {
        RestTemplate restTemplate = restTemplateBuilder.build();
        Mockito.when(wbListCommandService.sendCommandSync(any(), any(), any())).thenReturn(ID_TEST);

        ListRecord listRecord = new ListRecord();
        String format = String.format(SERVICE_URL, serverPort);
        ResponseEntity<String> response = restTemplate.postForEntity(format + "/whiteList", listRecord, String.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(response.getBody(), ID_TEST);
    }

    @Test
    public void executionRestTest() throws ExecutionException, InterruptedException {
        RestTemplate restTemplate = restTemplateBuilder.build();
        Mockito.when(wbListCommandService.sendCommandSync(any(), any(), any())).thenReturn(ID_TEST);

        ListRecord listRecord = createRow();
        String format = String.format(SERVICE_URL, serverPort);
        ResponseEntity<String> response = restTemplate.postForEntity(format + "/whiteList", listRecord, String.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(response.getBody(), ID_TEST);
    }

    @Test(expected = HttpServerErrorException.InternalServerError.class)
    public void executionRestDaoExceptionTest() throws ExecutionException, InterruptedException {
        RestTemplate restTemplate = restTemplateBuilder.build();
        Mockito.when(wbListCommandService.sendCommandSync(any(), any(), any())).thenThrow(new DaoException(TEST_MESSAGE));

        ListRecord listRecord = createRow();
        String format = String.format(SERVICE_URL, serverPort);
        restTemplate.postForEntity(format + "/whiteList", listRecord, ErrorResponse.class);
    }

    @Test(expected = HttpServerErrorException.InternalServerError.class)
    public void executionRestKafkaSerializationTest() throws ExecutionException, InterruptedException {
        RestTemplate restTemplate = restTemplateBuilder.build();
        Mockito.when(wbListCommandService.sendCommandSync(any(), any(), any())).thenThrow(new KafkaSerializationException(TEST_MESSAGE));

        ListRecord listRecord = createRow();
        String format = String.format(SERVICE_URL, serverPort);
        restTemplate.postForEntity(format + "/whiteList", listRecord, ErrorResponse.class);
    }

    @Test(expected = HttpClientErrorException.BadRequest.class)
    public void getRestTestBadRequest() throws ExecutionException, InterruptedException {
        RestTemplate restTemplate = restTemplateBuilder.build();
        Mockito.when(wbListCommandService.sendCommandSync(any(), any(), any())).thenThrow(new KafkaSerializationException(TEST_MESSAGE));
        HashMap<String, Object> uriVariables = new HashMap<>();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(String.format(SERVICE_URL, serverPort) + "/whiteList")
                .queryParam("partyId", PARTY_ID)
                .queryParam("shopId", SHOP_ID)
        ;
        uriVariables.put("partyId", PARTY_ID);
        uriVariables.put("shopId", SHOP_ID);
        restTemplate.getForEntity(builder.buildAndExpand(uriVariables).toUri(), ErrorResponse.class);
    }
}