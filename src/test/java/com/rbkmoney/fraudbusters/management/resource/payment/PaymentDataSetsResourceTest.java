package com.rbkmoney.fraudbusters.management.resource.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.damsel.fraudbusters.HistoricalDataServiceSrv;
import com.rbkmoney.fraudbusters.management.domain.payment.CheckedDataSetModel;
import com.rbkmoney.fraudbusters.management.domain.payment.DataSetModel;
import com.rbkmoney.fraudbusters.management.service.payment.PaymentsDataSetService;
import com.rbkmoney.fraudbusters.management.utils.DataSourceBeanUtils;
import com.rbkmoney.swag.fraudbusters.management.model.DataSet;
import com.rbkmoney.swag.fraudbusters.management.model.Payment;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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
    @MockBean
    HistoricalDataServiceSrv.Iface historicalDataServiceSrv;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getCheckedDataSet() throws Exception {
        when(paymentsDataSetService.getCheckedDataSet(any())).thenReturn(new CheckedDataSetModel());
        String id = "id";
        this.mockMvc.perform(get(String.format("/payments-data-set/checked-data-sets/%s", id)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("{\"id\":\"null\",\"testDataSetId\":\"null\",\"template\":null," +
                                "\"rows\":[],\"createdAt\":null,\"checkingTimestamp\":null," +
                                "\"initiator\":null,\"merchantInfo\":{\"partyId\":null,\"shopId\":null}}"));

        verify(paymentsDataSetService, times(1)).getCheckedDataSet(id);
    }

    @Test
    void filterDataSets() throws Exception {
        when(paymentsDataSetService.filterDataSets(any(), any(), any())).thenReturn(List.of(new DataSetModel()));
        this.mockMvc.perform(get("/payments-data-set/data-sets/filter")
                .queryParams(DataSourceBeanUtils.createParams()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("{\"continuationId\":null,\"result\":" +
                                "[{\"id\":null,\"name\":null,\"rows\":[]," +
                                "\"lastModificationAt\":null,\"lastModificationInitiator\":null}]}"));

        verify(paymentsDataSetService, times(1)).filterDataSets(any(), any(), any());
    }

    @Test
    void getDataSet() throws Exception {
        when(paymentsDataSetService.getDataSet(any())).thenReturn(new DataSetModel());
        String id = "id";
        this.mockMvc.perform(get(String.format("/payments-data-set/data-sets/%s", id)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("{\"id\":null,\"name\":null,\"rows\":[]," +
                                "\"lastModificationAt\":null,\"lastModificationInitiator\":null}"));

        verify(paymentsDataSetService, times(1)).getDataSet(id);
    }

    @Test
    void insertDataSet() throws Exception {
        when(paymentsDataSetService.insertDataSet(any())).thenReturn(1L);
        this.mockMvc.perform(post("/payments-data-set/data-sets")
                .content(objectMapper.writeValueAsString(new DataSet()))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .string("1"));
    }

    @Test
    void applyRuleOnHistoricalDataSet() throws Exception {
        Payment payment = DataSourceBeanUtils.createPayment();
        when(historicalDataServiceSrv.applyRuleOnHistoricalDataSet(any()))
                .thenReturn(DataSourceBeanUtils.createHistoricalResponse());
        this.mockMvc.perform(post("/payments-data-set/data-sets/applyRuleOnHistoricalDataSet")
                .content(objectMapper.writeValueAsString(DataSourceBeanUtils.createApplyRequst(payment)))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }

    @Test
    void removeDataSet() throws Exception {
        doNothing().when(paymentsDataSetService).removeDataSet(any(), any());
        this.mockMvc.perform(delete(String.format("/payments-data-set/data-sets/%s", "id")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .string("id"));
    }

}
