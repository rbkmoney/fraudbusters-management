package com.rbkmoney.fraudbusters.management.kafka;

import com.rbkmoney.damsel.wb_list.ChangeCommand;
import com.rbkmoney.damsel.wb_list.Command;
import com.rbkmoney.damsel.wb_list.ListType;
import com.rbkmoney.fraudbusters.management.config.KafkaITest;
import com.rbkmoney.fraudbusters.management.dao.p2p.wblist.P2PWbListDao;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentCountInfo;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentListRecord;
import com.rbkmoney.fraudbusters.management.domain.payment.request.ListRowsInsertRequest;
import com.rbkmoney.fraudbusters.management.utils.MethodPaths;
import com.rbkmoney.testcontainers.annotations.kafka.config.KafkaConsumer;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.testcontainers.shaded.com.trilead.ssh2.ChannelCondition.TIMEOUT;

@KafkaITest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InsertInListTest {

    public static final String BASE_URL = "http://localhost:";
    private static final String VALUE = "value";
    private static final String SHOP_ID = "shopId";
    private static final String PARTY_ID = "partyId";
    private static final String LIST_NAME = "listName";

    @Value("${kafka.topic.wblist.command}")
    public String topicCommand;
    @MockBean
    public P2PWbListDao wbListDao;
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
    void insertToList() {
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

        List<ChangeCommand> eventList = new ArrayList<>();
        testCommandKafkaConsumer.read(topicCommand, data -> eventList.add(data.value()));
        Unreliables.retryUntilTrue(TIMEOUT, TimeUnit.SECONDS, () -> eventList.size() == 2);
        assertEquals(2, eventList.size());
        assertEquals(eventList.get(0).command, Command.CREATE);
        assertEquals(eventList.get(0).getRow().getListType(), ListType.black);
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
        restTemplate.exchange(paymentListPath, HttpMethod.POST, entity,
                String.class);
    }
}
