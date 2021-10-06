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
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pListRecord;
import com.rbkmoney.fraudbusters.management.domain.p2p.request.P2pListRowsInsertRequest;
import com.rbkmoney.fraudbusters.management.resource.p2p.P2pListsResource;
import com.rbkmoney.testcontainers.annotations.kafka.config.KafkaConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.com.trilead.ssh2.ChannelCondition.TIMEOUT;

@KafkaITest
public class P2pInsertInListTest {

    public static final String IDENTITY_ID = "identityId";
    private static final String VALUE = "value";
    private static final String LIST_NAME = "listName";

    @Value("${kafka.topic.wblist.command}")
    public String topicCommand;
    @MockBean
    public P2PWbListDao wbListDao;
    @Autowired
    private KafkaConsumer<ChangeCommand> testCommandKafkaConsumer;

    @Autowired
    private P2pListsResource p2pListsResource;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(p2pListsResource, new ErrorController()).build();
    }

    @Test
    @WithMockUser("customUsername")
    void insertInBlackList() throws Exception {
        doNothing().when(wbListDao).saveListRecord(any());
        P2pListRecord record = new P2pListRecord();
        record.setListName(LIST_NAME);
        record.setIdentityId(IDENTITY_ID);
        record.setValue(VALUE);
        P2pListRowsInsertRequest p2pListRowsInsertRequest = TestObjectFactory.testListRowsInsertRequest(record);
        mockMvc.perform(post("/p2p/lists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(p2pListRowsInsertRequest)))
                .andExpect(status().isOk());

        List<ChangeCommand> eventList = new ArrayList<>();
        testCommandKafkaConsumer.read(topicCommand, data -> eventList.add(data.value()));
        Unreliables.retryUntilTrue(TIMEOUT, TimeUnit.SECONDS, () -> eventList.size() == 1);

        assertEquals(1, eventList.size());
        assertEquals(Command.CREATE, eventList.get(0).command);
        assertEquals(ListType.black, eventList.get(0).getRow().getListType());
    }
}
