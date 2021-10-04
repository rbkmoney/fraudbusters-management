package com.rbkmoney.fraudbusters.management.kafka;

import com.rbkmoney.damsel.wb_list.ChangeCommand;
import com.rbkmoney.damsel.wb_list.Command;
import com.rbkmoney.damsel.wb_list.ListType;
import com.rbkmoney.fraudbusters.management.config.KafkaITest;
import com.rbkmoney.fraudbusters.management.dao.payment.wblist.WbListDao;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
import com.rbkmoney.fraudbusters.management.utils.MethodPaths;
import com.rbkmoney.testcontainers.annotations.kafka.config.KafkaConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.testcontainers.shaded.com.trilead.ssh2.ChannelCondition.TIMEOUT;

@KafkaITest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DeleteFromListTest {

    @Value("${kafka.topic.wblist.command}")
    public String topicCommand;
    @MockBean
    public WbListDao wbListDao;
    @Autowired
    TestRestTemplate restTemplate;
    String paymentListPath;
    @Autowired
    private KafkaConsumer<ChangeCommand> testCommandKafkaConsumer;
    @LocalServerPort
    private int port;

    @BeforeEach
    void init() {
        paymentListPath = String.format(MethodPaths.SERVICE_BASE_URL + MethodPaths.INSERT_PAYMENTS_LIST_ROW_PATH,
                port);
    }

    @Test
    void deleteFromList() {
        String test = "test";
        when(wbListDao.getById(test)).thenReturn(new WbListRecords("id",
                "partyId",
                "shopId",
                com.rbkmoney.fraudbusters.management.domain.enums.ListType.white,
                "test",
                "test",
                LocalDateTime.now(), null, null, LocalDateTime.now()));

        deleteFromWhiteList(test);

        List<ChangeCommand> eventList = new ArrayList<>();
        testCommandKafkaConsumer.read(topicCommand, data -> eventList.add(data.value()));
        Unreliables.retryUntilTrue(TIMEOUT, TimeUnit.SECONDS, () -> eventList.size() == 1);

        assertEquals(1, eventList.size());
        assertEquals(eventList.get(0).command, Command.DELETE);
        assertEquals(eventList.get(0).getRow().getListType(), ListType.white);
    }

    private void deleteFromWhiteList(String id) {
        restTemplate.delete(String.format(MethodPaths.SERVICE_BASE_URL + MethodPaths.DELETE_LIST_ROWS_PATH, port, id));
    }
}
