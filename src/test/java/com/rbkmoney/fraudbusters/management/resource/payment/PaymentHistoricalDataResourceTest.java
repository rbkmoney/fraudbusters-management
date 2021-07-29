package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.fraudbusters.ClientInfo;
import com.rbkmoney.damsel.fraudbusters.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
        when(iface.getPayments(any(), any(), any())).thenReturn(new HistoricalDataResponse()
                .setData(createHistoricalData()));
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("paymentId", "test");
        params.add("size", "100");
        params.add("from", "2021-07-27 00:00:00");
        params.add("to", "2021-07-27 13:28:54");
        this.mockMvc.perform(get("/payments-historical-data/payments-info")
                .queryParams(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("{\n" +
                                "\t\"continuationId\": null,\n" +
                                "\t\"result\": [{\n" +
                                "\t\t\"id\": \"test\",\n" +
                                "\t\t\"eventTime\": \"2021-07-29T13:16:18.348795\",\n" +
                                "\t\t\"merchantInfo\": {\n" +
                                "\t\t\t\"partyId\": \"party\",\n" +
                                "\t\t\t\"shopId\": \"shop\"\n" +
                                "\t\t},\n" +
                                "\t\t\"amount\": 123,\n" +
                                "\t\t\"currency\": \"RUB\",\n" +
                                "\t\t\"cardToken\": null,\n" +
                                "\t\t\"clientInfo\": {\n" +
                                "\t\t\t\"ip\": \"123.123.123.123\",\n" +
                                "\t\t\t\"fingerprint\": \"finger\",\n" +
                                "\t\t\t\"email\": \"email\"\n" +
                                "\t\t},\n" +
                                "\t\t\"status\": \"captured\",\n" +
                                "\t\t\"payerType\": null,\n" +
                                "\t\t\"mobile\": null,\n" +
                                "\t\t\"recurrent\": null,\n" +
                                "\t\t\"error\": {\n" +
                                "\t\t\t\"errorCode\": null,\n" +
                                "\t\t\t\"errorReason\": null\n" +
                                "\t\t},\n" +
                                "\t\t\"paymentSystem\": \"visa\",\n" +
                                "\t\t\"paymentCountry\": null,\n" +
                                "\t\t\"paymentTool\": \"BankCard(token:null, payment_system:PaymentSystemRef(id:visa), bin:1234, last_digits:null, bank_name:test)\",\n" +
                                "\t\t\"provider\": {\n" +
                                "\t\t\t\"providerId\": \"test\",\n" +
                                "\t\t\t\"terminalId\": \"1234\",\n" +
                                "\t\t\t\"country\": \"RUS\"\n" +
                                "\t\t}\n" +
                                "\t}]\n" +
                                "}"));
    }

    @NotNull
    private HistoricalData createHistoricalData() {
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
                        .setEventTime("2021-07-29T13:16:18.348795")
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
        return historicalData;
    }
}
