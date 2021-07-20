package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.damsel.fraudbusters.HistoricalDataServiceSrv;
import com.rbkmoney.damsel.fraudbusters.PaymentInfoResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = {FlywayAutoConfiguration.class, JooqAutoConfiguration.class})
public class PaymentHistoricalDataResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    HistoricalDataServiceSrv.Iface iface;

    @Test
    public void filterPaymentsInfo() throws Exception {
        when(iface.getPayments(any(), any())).thenReturn(new PaymentInfoResult()
                .setPayments(new ArrayList<>()));
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("paymentId", "test");
        params.add("size", "100");
        this.mockMvc.perform(get("/payments-historical-data/payments-info")
                .queryParams(params))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
