package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.fraudbusters.ClientInfo;
import com.rbkmoney.damsel.fraudbusters.*;
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

import java.time.Instant;
import java.util.List;

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
        HistoricalData historicalData = new HistoricalData();
        BankCard bankCard = new BankCard()
                .setBankName("test")
                .setBin("1234")
                .setPaymentSystem(new PaymentSystemRef()
                        .setId("visa"));
        PaymentTool paymentTool = new PaymentTool();
        paymentTool.setBankCard(bankCard);
        ReferenceInfo referenceInfo = new ReferenceInfo();
        referenceInfo.setMerchantInfo(new MerchantInfo().setPartyId("party")
                .setShopId("shop"));
        historicalData.setPayments(List.of(
                new Payment()
                        .setId("test")
                        .setMobile(false)
                        .setEventTime(Instant.now().toString())
                        .setPaymentTool(paymentTool)
                        .setClientInfo(new ClientInfo()
                                .setEmail("email")
                                .setFingerprint("finger")
                                .setIp("123.123.123.123"))
                        .setCost(new Cash()
                                .setAmount(123L)
                                .setCurrency(new CurrencyRef()
                                        .setSymbolicCode("RUB")))
                        .setProviderInfo(new ProviderInfo()
                                .setProviderId("test")
                                .setCountry("RUS")
                                .setTerminalId("1234"))
                        .setReferenceInfo(referenceInfo)
                        .setStatus(PaymentStatus.captured))
        );
        when(iface.getPayments(any(), any(), any())).thenReturn(new HistoricalDataResponse()
                .setData(historicalData));
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("paymentId", "test");
        params.add("size", "100");
        params.add("from", "2021-07-27 00:00:00");
        params.add("to", "2021-07-27 13:28:54");
        this.mockMvc.perform(get("/payments-historical-data/payments-info")
                .queryParams(params))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
