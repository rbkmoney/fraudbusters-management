package com.rbkmoney.fraudbusters.management;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.damsel.wb_list.*;
import com.rbkmoney.fraudbusters.management.dao.p2p.wblist.P2PWbListDao;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pCountInfo;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pListRecord;
import com.rbkmoney.fraudbusters.management.domain.p2p.request.P2pListRowsInsertRequest;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.P2pWbListRecords;
import com.rbkmoney.fraudbusters.management.serializer.CommandChangeDeserializer;
import com.rbkmoney.fraudbusters.management.service.iface.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@RunWith(SpringRunner.class)
@EnableAutoConfiguration(exclude = {FlywayAutoConfiguration.class, JooqAutoConfiguration.class})
@SpringBootTest(classes = FraudbustersManagementApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class P2pWbListApplicationTest extends AbstractKafkaIntegrationTest {

    private static final String VALUE = "value";
    private static final String LIST_NAME = "listName";
    public static final String IDENTITY_ID = "identityId";

    @MockBean
    public AuditService auditService;

    @LocalServerPort
    private int port;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${kafka.topic.wblist.event.sink}")
    public String topicEventSink;
    @Value("${kafka.topic.wblist.command}")
    public String topicCommand;

    @MockBean
    public P2PWbListDao wbListDao;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    public void listenCreated() throws ExecutionException, InterruptedException {
        clearInvocations(wbListDao);

        Event event = new Event();
        Row row = createRow(ListType.black);
        event.setRow(row);
        event.setEventType(EventType.CREATED);
        ProducerRecord producerRecord = new ProducerRecord<>(topicEventSink, "test", event);
        try (Producer<String, Event> producer = createProducer()) {
            producer.send(producerRecord).get();
            producer.send(new ProducerRecord<>(topicEventSink, "test_1", event)).get();
            producer.send(new ProducerRecord<>(topicEventSink, "test_2", event)).get();
        }

        await().untilAsserted(() -> {
            verify(wbListDao, times(3)).saveListRecord(any());
        });
    }

    @Test
    public void listenCreatedGrey() throws ExecutionException, InterruptedException {
        clearInvocations(wbListDao);

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
            verify(wbListDao, times(3)).saveListRecord(any());
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
            await().untilAsserted(() -> {
                verify(wbListDao, times(1)).removeRecord((P2pWbListRecords) any());
            });
        }
    }

    @Test
    public void getAvailableListNames() {
        final String[] names = restTemplate.getForObject(
                "http://localhost:" + port + "/fb-management/v1/p2p/lists/availableListNames", String[].class);
        Assert.assertEquals(6, names.length);
    }

    private Row createRow(ListType listType) {
        Row row = new Row();
        row.setId(IdInfo.p2p_id(new P2pId()
                .setIdentityId(IDENTITY_ID))
        );
        row.setListName(LIST_NAME);
        row.setListType(listType);
        row.setValue(VALUE);
        return row;
    }

    @Test
    @WithMockUser("customUsername")
    public void executeTest() throws Exception {
        doNothing().when(wbListDao).saveListRecord(any());

        P2pListRecord record = new P2pListRecord();
        record.setListName(LIST_NAME);
        record.setIdentityId(IDENTITY_ID);
        record.setValue(VALUE);

        insertToBlackList(record);

        try (Consumer<String, ChangeCommand> consumer = createConsumer(CommandChangeDeserializer.class)) {
            consumer.subscribe(Collections.singletonList(topicCommand));
            List<ChangeCommand> eventList = consumeCommand(consumer);

            Assert.assertEquals(1, eventList.size());
            Assert.assertEquals(eventList.get(0).command, Command.CREATE);
            Assert.assertEquals(eventList.get(0).getRow().getListType(), ListType.black);

        }

        String test = "test";
        when(wbListDao.getById(test)).thenReturn(new P2pWbListRecords("id",
                "identity",
                com.rbkmoney.fraudbusters.management.domain.enums.ListType.white,
                "test",
                "test",
                "test",
                LocalDateTime.now()));
        deleteFromWhiteList(test);
        try (Consumer<String, ChangeCommand> consumer = createConsumer(CommandChangeDeserializer.class)) {
            consumer.subscribe(Collections.singletonList(topicCommand));
            List<ChangeCommand> eventList = consumeCommand(consumer);

            Assert.assertEquals(1, eventList.size());
            Assert.assertEquals(eventList.get(0).command, Command.DELETE);
            Assert.assertEquals(eventList.get(0).getRow().getListType(), ListType.white);
        }
    }

    private void insertToBlackList(P2pListRecord... values) throws Exception {
        List<P2pCountInfo> collect = List.of(values).stream()
                .map(p2pListRecord -> {
                    P2pCountInfo p2pCountInfo = new P2pCountInfo();
                    p2pCountInfo.setListRecord(p2pListRecord);
                    return p2pCountInfo;
                })
                .collect(Collectors.toList());
        P2pListRowsInsertRequest p2pListRowsInsertRequest = new P2pListRowsInsertRequest();
        p2pListRowsInsertRequest.setListType(ListType.black);
        p2pListRowsInsertRequest.setRecords(collect);
        HttpEntity<P2pListRowsInsertRequest> entity = new HttpEntity<>(p2pListRowsInsertRequest,
                new org.springframework.http.HttpHeaders());
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/fb-management/v1/p2p/lists",
                HttpMethod.POST, entity, String.class);
        System.out.println(response);
    }

    private void deleteFromWhiteList(String id) throws Exception {
        restTemplate.delete("http://localhost:" + port + "/fb-management/v1/p2p/lists/" + id);
    }

    private List<ChangeCommand> consumeCommand(Consumer<String, ChangeCommand> consumer) {
        List<ChangeCommand> eventList = new ArrayList<>();
        ConsumerRecords<String, ChangeCommand> consumerRecords =
                consumer.poll(Duration.ofSeconds(10));
        consumerRecords.forEach(command -> {
            log.info("poll command: {}", command.value());
            eventList.add(command.value());
        });
        consumer.close();
        return eventList;
    }
}
