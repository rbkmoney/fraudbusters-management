package com.rbkmoney.fraudbusters.management.kafka;

import com.rbkmoney.damsel.wb_list.ChangeCommand;
import com.rbkmoney.damsel.wb_list.Command;
import com.rbkmoney.damsel.wb_list.ListType;
import com.rbkmoney.fraudbusters.management.config.KafkaITest;
import com.rbkmoney.fraudbusters.management.dao.p2p.wblist.P2PWbListDao;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pCountInfo;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pListRecord;
import com.rbkmoney.fraudbusters.management.domain.p2p.request.P2pListRowsInsertRequest;
import com.rbkmoney.testcontainers.annotations.kafka.config.KafkaConsumer;
import org.junit.jupiter.api.Test;
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
import org.springframework.security.test.context.support.WithMockUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.testcontainers.shaded.com.trilead.ssh2.ChannelCondition.TIMEOUT;

@KafkaITest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class P2pInsertInListTest {

    public static final String IDENTITY_ID = "identityId";
    private static final String VALUE = "value";
    private static final String LIST_NAME = "listName";

    @Value("${kafka.topic.wblist.command}")
    public String topicCommand;
    @MockBean
    public P2PWbListDao wbListDao;
    @Autowired
    TestRestTemplate restTemplate;
    @LocalServerPort
    private int port;

    @Autowired
    private KafkaConsumer<ChangeCommand> testCommandKafkaConsumer;

    @Test
    @WithMockUser("customUsername")
    void insertInBlackList() {
        doNothing().when(wbListDao).saveListRecord(any());

        P2pListRecord record = new P2pListRecord();
        record.setListName(LIST_NAME);
        record.setIdentityId(IDENTITY_ID);
        record.setValue(VALUE);

        insertToBlackList(record);

        List<ChangeCommand> eventList = new ArrayList<>();
        testCommandKafkaConsumer.read(topicCommand, data -> eventList.add(data.value()));
        Unreliables.retryUntilTrue(TIMEOUT, TimeUnit.SECONDS, () -> eventList.size() == 1);

        assertEquals(1, eventList.size());
        assertEquals(Command.CREATE, eventList.get(0).command);
        assertEquals(ListType.black, eventList.get(0).getRow().getListType());
    }

    private void insertToBlackList(P2pListRecord... values) {
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
        ResponseEntity<String> response =
                restTemplate.exchange("http://localhost:" + port + "/fb-management/v1/p2p/lists",
                        HttpMethod.POST, entity, String.class);
        System.out.println(response);
    }
}
