package com.rbkmoney.fraudbusters.management.resource.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.fraudbusters.management.domain.payment.TestDataSetModel;
import com.rbkmoney.fraudbusters.management.service.payment.PaymentsDataSetService;
import com.rbkmoney.swag.fraudbusters.management.model.DataSet;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = {FlywayAutoConfiguration.class, JooqAutoConfiguration.class})
class PaymentDataSetsResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    PaymentsDataSetService paymentsDataSetService;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void filterDataSets() throws Exception {
        when(paymentsDataSetService.filterDataSets(any())).thenReturn(List.of(new TestDataSetModel()));
        LinkedMultiValueMap<String, String> params = createParams();
        this.mockMvc.perform(get("/payments-data-set/data-sets/filter")
                .queryParams(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("{\"continuationId\":null,\"result\":" +
                                "[{\"id\":null,\"name\":null,\"rows\":[]," +
                                "\"lastModificationAt\":null,\"lastModificationInitiator\":null}]}"));
    }

    @Test
    void getDataSet() throws Exception {
        when(paymentsDataSetService.getDataSet(any())).thenReturn(new TestDataSetModel());
        LinkedMultiValueMap<String, String> params = createParams();
        this.mockMvc.perform(get(String.format("/payments-data-set/data-sets/%s", "id")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("{\"id\":null,\"name\":null,\"rows\":[]," +
                                "\"lastModificationAt\":null,\"lastModificationInitiator\":null}"));
    }

    @Test
    void insertDataSet() throws Exception {
        when(paymentsDataSetService.insertDataSet(any(), any())).thenReturn(1L);
        this.mockMvc.perform(post("/payments-data-set/data-sets")
                .content(objectMapper.writeValueAsString(new DataSet()))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .string("1"));
    }

    @Test
    void removeDataSet() throws Exception {
        doNothing().when(paymentsDataSetService).removeDataSet(any(), any());
        LinkedMultiValueMap<String, String> params = createParams();
        this.mockMvc.perform(delete(String.format("/payments-data-set/data-sets/%s", "id")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .string("id"));
    }

    private LinkedMultiValueMap<String, String> createParams() {
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("paymentId", "test");
        params.add("size", "100");
        params.add("from", "2021-07-27 00:00:00");
        params.add("to", "2021-07-27 13:28:54");
        return params;
    }
}
