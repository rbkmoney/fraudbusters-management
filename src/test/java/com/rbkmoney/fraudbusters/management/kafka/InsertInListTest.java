package com.rbkmoney.fraudbusters.management.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rbkmoney.damsel.wb_list.ChangeCommand;
import com.rbkmoney.damsel.wb_list.Command;
import com.rbkmoney.damsel.wb_list.ListType;
import com.rbkmoney.fraudbusters.management.TestObjectFactory;
import com.rbkmoney.fraudbusters.management.config.KafkaITest;
import com.rbkmoney.fraudbusters.management.controller.ErrorController;
import com.rbkmoney.fraudbusters.management.dao.p2p.wblist.P2PWbListDao;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentListRecord;
import com.rbkmoney.fraudbusters.management.domain.payment.request.ListRowsInsertRequest;
import com.rbkmoney.fraudbusters.management.resource.payment.PaymentsListsResource;
import com.rbkmoney.testcontainers.annotations.kafka.config.KafkaConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.com.trilead.ssh2.ChannelCondition.TIMEOUT;

@KafkaITest
public class InsertInListTest {

    private static final String VALUE = "value";
    private static final String SHOP_ID = "shopId";
    private static final String PARTY_ID = "partyId";
    private static final String LIST_NAME = "listName";

    @Value("${kafka.topic.wblist.command}")
    public String topicCommand;
    @MockBean
    public P2PWbListDao wbListDao;
    @Autowired
    private KafkaConsumer<ChangeCommand> testCommandKafkaConsumer;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Autowired
    private PaymentsListsResource paymentsListsResource;

    private MockMvc mockMvc;

    @BeforeEach
    void init() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(paymentsListsResource, new ErrorController()).build();
    }

    @Test
    void insertToList() throws Exception {
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
        ListRowsInsertRequest listRowsInsertRequest = TestObjectFactory.testListRowsInsertRequest(record, recordSecond);
        mockMvc.perform(post("/payments-lists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(listRowsInsertRequest)))
                .andExpect(status().isOk());

        List<ChangeCommand> eventList = new ArrayList<>();
        testCommandKafkaConsumer.read(topicCommand, data -> eventList.add(data.value()));
        Unreliables.retryUntilTrue(TIMEOUT, TimeUnit.SECONDS, () -> eventList.size() == 2);

        assertEquals(2, eventList.size());
        assertEquals(eventList.get(0).command, Command.CREATE);
        assertEquals(eventList.get(0).getRow().getListType(), ListType.black);
    }
}
