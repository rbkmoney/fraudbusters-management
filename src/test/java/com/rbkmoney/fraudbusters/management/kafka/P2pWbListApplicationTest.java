package com.rbkmoney.fraudbusters.management.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.damsel.wb_list.*;
import com.rbkmoney.fraudbusters.management.config.KafkaITest;
import com.rbkmoney.fraudbusters.management.dao.p2p.wblist.P2PWbListDao;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.P2pWbListRecords;
import com.rbkmoney.fraudbusters.management.service.iface.AuditService;
import com.rbkmoney.testcontainers.annotations.kafka.config.KafkaConsumer;
import com.rbkmoney.testcontainers.annotations.kafka.config.KafkaProducer;
import org.apache.thrift.TBase;
import org.junit.jupiter.api.Test;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testcontainers.shaded.com.trilead.ssh2.ChannelCondition.TIMEOUT;

@KafkaITest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class P2pWbListApplicationTest {

    public static final String IDENTITY_ID = "identityId";
    private static final String VALUE = "value";
    private static final String LIST_NAME = "listName";
    @MockBean
    public AuditService auditService;
    @Value("${kafka.topic.wblist.event.sink}")
    public String topicEventSink;
    @Value("${kafka.topic.wblist.command}")
    public String topicCommand;
    @MockBean
    public P2PWbListDao wbListDao;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    TestRestTemplate restTemplate;
    @LocalServerPort
    private int port;

    @Autowired
    private KafkaProducer<TBase<?, ?>> testThriftKafkaProducer;

    @Autowired
    private KafkaConsumer<ChangeCommand> testCommandKafkaConsumer;

    @Test
    void listenCreated() {
        clearInvocations(wbListDao);

        Event event = new Event();
        Row row = createRow(ListType.black);
        event.setRow(row);
        event.setEventType(EventType.CREATED);
        testThriftKafkaProducer.send(topicEventSink, event);
        testThriftKafkaProducer.send(topicEventSink, event);
        testThriftKafkaProducer.send(topicEventSink, event);

        await().untilAsserted(() -> {
            verify(wbListDao, times(3)).saveListRecord(any());
        });
    }

    @Test
    void listenCreatedGrey() {
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

        testThriftKafkaProducer.send(topicEventSink, event);
        testThriftKafkaProducer.send(topicEventSink, event);
        testThriftKafkaProducer.send(topicEventSink, event);

        await().untilAsserted(() -> {
            verify(wbListDao, times(3)).saveListRecord(any());
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
            verify(wbListDao, times(1)).removeRecord(any(P2pWbListRecords.class));
        });
    }

    @Test
    void getAvailableListNames() {
        final String[] names = restTemplate.getForObject(
                "http://localhost:" + port + "/fb-management/v1/p2p/lists/availableListNames", String[].class);
        assertEquals(6, names.length);
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
    void deleteFromWhiteList() {
        String test = "test";
        when(wbListDao.getById(test)).thenReturn(new P2pWbListRecords("id",
                "identity",
                com.rbkmoney.fraudbusters.management.domain.enums.ListType.white,
                "test",
                "test",
                "test",
                LocalDateTime.now()));

        deleteFromWhiteList(test);

        List<ChangeCommand> eventList2 = new ArrayList<>();
        testCommandKafkaConsumer.read(topicCommand, data -> eventList2.add(data.value()));
        Unreliables.retryUntilTrue(TIMEOUT, TimeUnit.SECONDS, () -> eventList2.size() == 1);

        assertEquals(1, eventList2.size());
        assertEquals(Command.DELETE, eventList2.get(0).command);
        assertEquals(ListType.white, eventList2.get(0).getRow().getListType());
    }

    private void deleteFromWhiteList(String id) {
        restTemplate.delete("http://localhost:" + port + "/fb-management/v1/p2p/lists/" + id);
    }
}
