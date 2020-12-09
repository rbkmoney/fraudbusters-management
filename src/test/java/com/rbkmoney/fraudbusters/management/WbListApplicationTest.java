package com.rbkmoney.fraudbusters.management;

import com.rbkmoney.damsel.wb_list.*;
import com.rbkmoney.fraudbusters.management.dao.payment.wblist.WbListDao;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentCountInfo;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentListRecord;
import com.rbkmoney.fraudbusters.management.domain.payment.request.ListRowsInsertRequest;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
import com.rbkmoney.fraudbusters.management.serializer.CommandChangeDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
@RunWith(SpringRunner.class)
@EnableAutoConfiguration(exclude = {FlywayAutoConfiguration.class, JooqAutoConfiguration.class})
@SpringBootTest(classes = FraudbustersManagementApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WbListApplicationTest extends AbstractKafkaIntegrationTest {

    private static final String VALUE = "value";
    private static final String SHOP_ID = "shopId";
    private static final String PARTY_ID = "partyId";
    private static final String LIST_NAME = "listName";

    @LocalServerPort
    private int port;

    TestRestTemplate restTemplate = new TestRestTemplate();

    @Value("${kafka.topic.wblist.event.sink}")
    public String topicEventSink;
    @Value("${kafka.topic.wblist.command}")
    public String topicCommand;

    @MockBean
    public WbListDao wbListDao;

    @Test
    public void listenCreated() throws ExecutionException, InterruptedException {
        Mockito.clearInvocations(wbListDao);

        Event event = new Event();
        Row row = createRow(ListType.black);
        event.setRow(row);
        event.setEventType(EventType.CREATED);
        try (Producer<String, Event> producer = createProducer()) {
            ProducerRecord<String, Event> producerRecord = new ProducerRecord<>(topicEventSink, "test", event);
            producer.send(producerRecord).get();
            producer.send(new ProducerRecord<>(topicEventSink, "test_1", event)).get();
            producer.send(new ProducerRecord<>(topicEventSink, "test_2", event)).get();
        }
        await().untilAsserted(() -> {
            Mockito.verify(wbListDao, Mockito.times(3)).saveListRecord(any());
        });
    }

    @Test
    public void listenCreatedGrey() throws ExecutionException, InterruptedException {
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

        try (Producer<String, Event> producer = createProducer()) {
            ProducerRecord<String, Event> producerRecord = new ProducerRecord<>(topicEventSink, "test", event);
            producer.send(producerRecord).get();
            producer.send(new ProducerRecord<>(topicEventSink, "test_1", event)).get();
            producer.send(new ProducerRecord<>(topicEventSink, "test_2", event)).get();
        }

        await().untilAsserted(() -> {
            Mockito.verify(wbListDao, Mockito.times(3)).saveListRecord(any());
        });
    }

    @Test
    public void listenDeleted() throws ExecutionException, InterruptedException {
        Event event = new Event();
        Row row = createRow(ListType.black);
        event.setRow(row);
        event.setEventType(EventType.DELETED);
        try (Producer<String, Event> producer = createProducer()) {
            ProducerRecord<String, Event> producerRecord = new ProducerRecord<>(topicEventSink, "test", event);
            producer.send(producerRecord).get();
            producer.close();

            await().untilAsserted(() -> {
                Mockito.verify(wbListDao, Mockito.times(1)).removeRecord((WbListRecords) any());
            });
        }
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
    public void executeTest() throws IOException {
        Mockito.doNothing().when(wbListDao).saveListRecord(any());

        PaymentListRecord record = new PaymentListRecord();
        record.setListName(LIST_NAME);
        record.setPartyId(PARTY_ID);
        record.setShopId(SHOP_ID);
        record.setValue(VALUE);

        PaymentListRecord recordSecond = new PaymentListRecord();
        recordSecond.setListName(LIST_NAME);
        recordSecond.setPartyId(PARTY_ID);
        recordSecond.setShopId(SHOP_ID);
        recordSecond.setValue(VALUE + 2);

        insertToBlackList(record, recordSecond);

        try (Consumer<String, ChangeCommand> consumer = createConsumer(CommandChangeDeserializer.class)) {
            consumer.subscribe(Collections.singletonList(topicCommand));
            List<ChangeCommand> eventList = consumeCommand(consumer);
            assertEquals(2, eventList.size());
            assertEquals(eventList.get(0).command, Command.CREATE);
            assertEquals(eventList.get(0).getRow().getListType(), ListType.black);
        }

        String test = "test";
        when(wbListDao.getById(test)).thenReturn(new WbListRecords("id",
                "partyId",
                "shopId",
                com.rbkmoney.fraudbusters.management.domain.enums.ListType.white,
                "test",
                "test",
                LocalDateTime.now(), null, null));

        deleteFromWhiteList(test);
        try (Consumer<String, ChangeCommand> consumer = createConsumer(CommandChangeDeserializer.class)) {
            consumer.subscribe(Collections.singletonList(topicCommand));
            List<ChangeCommand> eventList = consumeCommand(consumer);

            assertEquals(1, eventList.size());
            assertEquals(eventList.get(0).command, Command.DELETE);
            assertEquals(eventList.get(0).getRow().getListType(), ListType.white);
        }

        String value = VALUE + 66;
        HttpEntity<ListRowsInsertRequest> entity = new HttpEntity<>(createListRowsInsertRequest(value), new org.springframework.http.HttpHeaders());
        restTemplate.exchange("http://localhost:" + port + "/fb-management/v1/lists", HttpMethod.POST, entity, String.class);

        try (Consumer<String, ChangeCommand> consumer = createConsumer(CommandChangeDeserializer.class)) {
            consumer.subscribe(Collections.singletonList(topicCommand));
            List<ChangeCommand> eventList = consumeCommand(consumer);

            assertEquals(1, eventList.size());
            assertEquals(eventList.get(0).command, Command.CREATE);
            assertEquals(eventList.get(0).getRow().getListType(), ListType.black);
            assertEquals(value, eventList.get(0).getRow().getValue());
            log.info("{}", eventList.get(0).getRow());
        }

        ResponseEntity<ArrayList> result = restTemplate.getForEntity("http://localhost:" + port + "/fb-management/v1/lists/names?listType={name}",
                ArrayList.class, "black");
        assertTrue(result.getBody().isEmpty());
    }

    private ListRowsInsertRequest createListRowsInsertRequest(String value) {
        ListRowsInsertRequest listRowsInsertRequest = new ListRowsInsertRequest();
        listRowsInsertRequest.setListType(ListType.black);
        PaymentCountInfo paymentCountInfo = new PaymentCountInfo();
        PaymentListRecord listRecord = new PaymentListRecord();
        listRecord.setListName(LIST_NAME);
        listRecord.setPartyId(PARTY_ID);
        listRecord.setShopId(SHOP_ID);
        listRecord.setValue(value);
        paymentCountInfo.setListRecord(listRecord);
        listRowsInsertRequest.setRecords(List.of(paymentCountInfo));
        return listRowsInsertRequest;
    }

    private void insertToBlackList(PaymentListRecord... values) {
        List<PaymentCountInfo> collect = List.of(values).stream()
                .map(paymentListRecord -> {
                    PaymentCountInfo paymentCountInfo = new PaymentCountInfo();
                    paymentCountInfo.setListRecord(paymentListRecord);
                    return paymentCountInfo;
                })
                .collect(Collectors.toList());
        ListRowsInsertRequest insertRequest = new ListRowsInsertRequest();
        insertRequest.setListType(ListType.black);
        insertRequest.setRecords(collect);
        HttpEntity<ListRowsInsertRequest> entity = new HttpEntity<>(insertRequest,
                new org.springframework.http.HttpHeaders());
        restTemplate.exchange("http://localhost:" + port + "/fb-management/v1/lists", HttpMethod.POST, entity, String.class);
    }

    private void deleteFromWhiteList(String id) {
        restTemplate.delete("http://localhost:" + port + "/fb-management/v1/lists/" + id);
    }

    private List<ChangeCommand> consumeCommand(Consumer<String, ChangeCommand> consumer) {
        List<ChangeCommand> eventList = new ArrayList<>();
        ConsumerRecords<String, ChangeCommand> consumerRecords =
                consumer.poll(Duration.ofSeconds(10));
        consumerRecords.forEach(command -> {
            log.info("poll command: {}", command.value());
            eventList.add(command.value());
        });
        return eventList;
    }
}
