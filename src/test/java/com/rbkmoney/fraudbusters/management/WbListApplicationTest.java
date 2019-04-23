package com.rbkmoney.fraudbusters.management;

import com.rbkmoney.damsel.wb_list.*;
import com.rbkmoney.fraudbusters.management.dao.wblist.WbListDao;
import com.rbkmoney.fraudbusters.management.domain.ListRecord;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
import com.rbkmoney.fraudbusters.management.listener.WbListEventListener;
import com.rbkmoney.fraudbusters.management.resource.WbListResource;
import com.rbkmoney.fraudbusters.management.serializer.CommandChangeDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.any;

@Slf4j
@RunWith(SpringRunner.class)
@EnableAutoConfiguration(exclude = {FlywayAutoConfiguration.class, JooqAutoConfiguration.class})
@SpringBootTest(classes = FraudbustersManagementApplication.class)
public class WbListApplicationTest extends AbstractKafkaIntegrationTest {

    private static final String VALUE = "value";
    private static final String KEY = "key";
    private static final String SHOP_ID = "shopId";
    private static final String PARTY_ID = "partyId";
    private static final String LIST_NAME = "listName";

    @Value("${kafka.topic.wblist.event.sink}")
    public String topicEventSink;
    @Value("${kafka.topic.wblist.command}")
    public String topicCommand;

    @MockBean
    public WbListDao wbListDao;

    @Autowired
    WbListEventListener wbListEventListener;

    @Autowired
    WbListResource wbListResource;

    @Test
    public void listenCreated() throws ExecutionException, InterruptedException {
        Event event = new Event();
        Row row = createRow(ListType.black);
        event.setRow(row);
        event.setEventType(EventType.CREATED);
        ProducerRecord producerRecord = new ProducerRecord<>(topicEventSink, "test", event);
        Producer<String, Event> producer = createProducer();

        producer.send(producerRecord).get();
        producer.close();
        Thread.sleep(500L);

        Mockito.verify(wbListDao, Mockito.times(1)).saveListRecord(any());
    }

    @Test
    public void listenDeleted() throws ExecutionException, InterruptedException {
        Event event = new Event();
        Row row = createRow(ListType.black);
        event.setRow(row);
        event.setEventType(EventType.DELETED);
        ProducerRecord producerRecord = new ProducerRecord<>(topicEventSink, "test", event);
        Producer<String, Event> producer = createProducer();

        producer.send(producerRecord).get();
        producer.close();
        Thread.sleep(500L);

        Mockito.verify(wbListDao, Mockito.times(1)).removeRecord((WbListRecords) any());
    }

    @NotNull
    private Row createRow(ListType listType) {
        Row row = new Row();
        row.setShopId(SHOP_ID);
        row.setPartyId(PARTY_ID);
        row.setListName(LIST_NAME);
        row.setListType(listType);
        row.setValue(VALUE);
        return row;
    }

    @Test
    public void executeTest() {
        Mockito.doNothing().when(wbListDao).saveListRecord(any());

        ListRecord record = new ListRecord();
        record.setListName(LIST_NAME);
        record.setPartyId(PARTY_ID);
        record.setShopId(SHOP_ID);
        record.setValue(VALUE);

        ResponseEntity<String> stringResponseEntity = wbListResource.insertRowToBlack(record);

        Consumer<String, ChangeCommand> consumer = createConsumer(CommandChangeDeserializer.class);
        consumer.subscribe(Collections.singletonList(topicCommand));
        List<ChangeCommand> eventList = consumeCommand(consumer);

        Assert.assertEquals(1, eventList.size());
        Assert.assertEquals(eventList.get(0).command, Command.CREATE);
        Assert.assertEquals(eventList.get(0).getRow().getListType(), ListType.black);

        stringResponseEntity = wbListResource.removeRowFromWhiteList(record);
        consumer = createConsumer(CommandChangeDeserializer.class);
        consumer.subscribe(Collections.singletonList(topicCommand));
        eventList = consumeCommand(consumer);

        Assert.assertEquals(1, eventList.size());
        Assert.assertEquals(eventList.get(0).command, Command.DELETE);
        Assert.assertEquals(eventList.get(0).getRow().getListType(), ListType.white);
    }

    @NotNull
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