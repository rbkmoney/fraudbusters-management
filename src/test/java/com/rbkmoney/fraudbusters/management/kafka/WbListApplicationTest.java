package com.rbkmoney.fraudbusters.management.kafka;

import com.rbkmoney.damsel.wb_list.*;
import com.rbkmoney.fraudbusters.management.config.KafkaITest;
import com.rbkmoney.fraudbusters.management.dao.payment.wblist.WbListDao;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentCountInfo;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentListRecord;
import com.rbkmoney.fraudbusters.management.domain.payment.request.ListRowsInsertRequest;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
import com.rbkmoney.fraudbusters.management.service.iface.AuditService;
import com.rbkmoney.fraudbusters.management.utils.MethodPaths;
import com.rbkmoney.swag.fraudbusters.management.model.ListResponse;
import com.rbkmoney.testcontainers.annotations.kafka.config.KafkaConsumer;
import com.rbkmoney.testcontainers.annotations.kafka.config.KafkaProducer;
import org.apache.thrift.TBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.testcontainers.shaded.com.trilead.ssh2.ChannelCondition.TIMEOUT;

@KafkaITest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WbListApplicationTest {

    public static final String BASE_URL = "http://localhost:";
    private static final String VALUE = "value";
    private static final String SHOP_ID = "shopId";
    private static final String PARTY_ID = "partyId";
    private static final String LIST_NAME = "listName";
    @Value("${kafka.topic.wblist.event.sink}")
    public String topicEventSink;
    @Value("${kafka.topic.wblist.command}")
    public String topicCommand;
    @MockBean
    public AuditService auditService;
    @MockBean
    public WbListDao wbListDao;
    TestRestTemplate restTemplate = new TestRestTemplate();
    String paymentListPath;
    @LocalServerPort
    private int port;

    @Autowired
    private KafkaProducer<TBase<?, ?>> testThriftKafkaProducer;

    @Autowired
    private KafkaConsumer<ChangeCommand> testCommandKafkaConsumer;

    @BeforeEach
    void init() {
        paymentListPath = String.format(MethodPaths.SERVICE_BASE_URL + MethodPaths.INSERT_PAYMENTS_LIST_ROW_PATH,
                port);
    }

    @Test
    void listenCreated() {
        Mockito.clearInvocations(wbListDao);

        Event event = new Event();
        Row row = createRow(ListType.black);
        event.setRow(row);
        event.setEventType(EventType.CREATED);
        testThriftKafkaProducer.send(topicEventSink, event);
        testThriftKafkaProducer.send(topicEventSink, event);
        testThriftKafkaProducer.send(topicEventSink, event);

        await().untilAsserted(() -> {
            Mockito.verify(wbListDao, Mockito.times(3)).saveListRecord(any());
        });
    }

    @Test
    void listenCreatedGrey() {
        Mockito.clearInvocations(wbListDao);

        Event event = new Event();
        Row row = createRow(ListType.grey);
        event.setEventType(EventType.CREATED);
        RowInfo rowInfo = RowInfo.count_info(new CountInfo()
                .setCount(5)
                .setTimeToLive("2019-08-22T13:14:17.443332Z")
                .setStartCountTime("2019-08-22T11:14:17.443332Z")
        );
        row.setRowInfo(rowInfo);

        event.setRow(row);

        testThriftKafkaProducer.send(topicEventSink, event);
        testThriftKafkaProducer.send(topicEventSink, event);
        testThriftKafkaProducer.send(topicEventSink, event);

        await().untilAsserted(() -> {
            Mockito.verify(wbListDao, Mockito.times(3)).saveListRecord(any(WbListRecords.class));
        });
    }

    @Test
    void listenDeleted() {
        Event event = new Event();
        Row row = createRow(ListType.black);
        event.setRow(row);
        event.setEventType(EventType.DELETED);
        testThriftKafkaProducer.send(topicEventSink, event);

        await().untilAsserted(() -> {
            Mockito.verify(wbListDao, Mockito.times(1)).removeRecord(any(WbListRecords.class));
        });

    }

    private Row createRow(ListType listType) {
        Row row = new Row();
        row.setPartyId(PARTY_ID);
        row.setShopId(SHOP_ID);
        row.setListName(LIST_NAME);
        row.setListType(listType);
        row.setValue(VALUE);
        return row;
    }

    @Test
    void createListRow() {
        String value = VALUE + 66;
        HttpEntity<ListRowsInsertRequest> entity =
                new HttpEntity<>(createListRowsInsertRequest(value), new org.springframework.http.HttpHeaders());
        restTemplate.exchange(paymentListPath, HttpMethod.POST, entity, String.class);

        List<ChangeCommand> eventList = new ArrayList<>();
        testCommandKafkaConsumer.read(topicCommand, data -> eventList.add(data.value()));
        Unreliables.retryUntilTrue(TIMEOUT, TimeUnit.SECONDS, () -> eventList.size() == 1);

        assertEquals(1, eventList.size());
        assertEquals(eventList.get(0).command, Command.CREATE);
        assertEquals(eventList.get(0).getRow().getListType(), ListType.black);
        assertEquals(value, eventList.get(0).getRow().getValue());

        String paymentListNames = String.format(MethodPaths.SERVICE_BASE_URL + MethodPaths.LISTS_NAMES_LIST_TYPE_PATH,
                port);
        ResponseEntity<ListResponse> result =
                restTemplate.getForEntity(paymentListNames, ListResponse.class, "black");
        assertTrue(result.getBody().getResult().isEmpty());
    }

    private ListRowsInsertRequest createListRowsInsertRequest(String value) {
        ListRowsInsertRequest listRowsInsertRequest = new ListRowsInsertRequest();
        listRowsInsertRequest.setListType(ListType.black);
        PaymentListRecord listRecord = new PaymentListRecord();
        listRecord.setListName(LIST_NAME);
        listRecord.setPartyId(PARTY_ID);
        listRecord.setShopId(SHOP_ID);
        listRecord.setValue(value);
        PaymentCountInfo paymentCountInfo = new PaymentCountInfo();
        paymentCountInfo.setListRecord(listRecord);
        listRowsInsertRequest.setRecords(List.of(paymentCountInfo));
        return listRowsInsertRequest;
    }
}
