package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.damsel.fraudbusters.HistoricalDataResponse;
import com.rbkmoney.damsel.fraudbusters.HistoricalDataServiceSrv;
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

import static com.rbkmoney.fraudbusters.management.resource.utils.ExternalModelBeanFactory.*;
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
        LinkedMultiValueMap<String, String> params = createParams();
        this.mockMvc.perform(get("/payments-historical-data/payments-info")
                .queryParams(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("{\"continuationId\":null,\"result\":[{\"id\":\"test\"," +
                                "\"eventTime\":\"2021-07-29T13:16:18.348795\",\"merchantInfo\":" +
                                "{\"partyId\":\"party\",\"shopId\":\"shop\"},\"amount\":123,\"currency\":\"RUB\"," +
                                "\"cardToken\":null,\"clientInfo\":{\"ip\":\"123.123.123.123\"," +
                                "\"fingerprint\":\"finger\",\"email\":\"email\"},\"status\":\"captured\"," +
                                "\"payerType\":null,\"mobile\":null,\"recurrent\":null,\"error\":{\"errorCode\":null," +
                                "\"errorReason\":null},\"paymentSystem\":\"visa\",\"paymentCountry\":null," +
                                "\"paymentTool\":\"BankCard(token:null, payment_system:PaymentSystemRef(id:visa)," +
                                " bin:1234, last_digits:null, bank_name:test)\",\"provider\":{\"providerId\":\"test\"," +
                                "\"terminalId\":\"1234\",\"country\":\"RUS\"}}]}"));
    }

    @Test
    public void filterRefundsInfo() throws Exception {
        when(iface.getRefunds(any(), any(), any())).thenReturn(new HistoricalDataResponse()
                .setData(createHistoricalDataRefunds()));
        LinkedMultiValueMap<String, String> params = createParams();
        this.mockMvc.perform(get("/payments-historical-data/refunds")
                .queryParams(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("{\"continuationId\":null,\"result\":[{\"id\":\"test\",\"paymentId\":null," +
                                "\"eventTime\":\"2021-07-29T13:16:18.348795\",\"merchantInfo\":{\"partyId\":\"party\"," +
                                "\"shopId\":\"shop\"},\"paymentTool\":" +
                                "\"BankCard(token:null, payment_system:PaymentSystemRef(id:visa), bin:1234, " +
                                "last_digits:null, bank_name:test)\",\"amount\":123,\"currency\":\"RUB\"," +
                                "\"provider\":{\"providerId\":\"test\",\"terminalId\":\"1234\",\"country\":\"RUS\"}," +
                                "\"status\":\"succeeded\",\"error\":{\"errorCode\":null,\"errorReason\":null}," +
                                "\"clientInfo\":{\"ip\":\"123.123.123.123\",\"fingerprint\":\"finger\"," +
                                "\"email\":\"email\"},\"payerType\":null}]}"));
    }

    @Test
    public void filterInspectResults() throws Exception {
        when(iface.getFraudResults(any(), any(), any())).thenReturn(new HistoricalDataResponse()
                .setData(createHistoricalDataInspectResults()));
        LinkedMultiValueMap<String, String> params = createParams();
        this.mockMvc.perform(get("/payments-historical-data/inspect-results")
                .queryParams(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("{\"continuationId\":null,\"result\":[{\"payment\":{\"id\":\"test\"," +
                                "\"eventTime\":\"2021-07-29T13:16:18.348795\",\"merchantInfo\":{\"partyId\":\"party\"," +
                                "\"shopId\":\"shop\"},\"amount\":123,\"currency\":\"RUB\",\"cardToken\":null," +
                                "\"clientInfo\":{\"ip\":\"123.123.123.123\",\"fingerprint\":\"finger\"," +
                                "\"email\":\"email\"},\"status\":\"captured\",\"payerType\":null,\"mobile\":null," +
                                "\"recurrent\":null,\"error\":{\"errorCode\":null,\"errorReason\":null}," +
                                "\"paymentSystem\":\"visa\",\"paymentCountry\":null,\"paymentTool\":" +
                                "\"BankCard(token:null, payment_system:PaymentSystemRef(id:visa), bin:1234, " +
                                "last_digits:null, bank_name:test)\",\"provider\":{\"providerId\":\"test\"," +
                                "\"terminalId\":\"1234\",\"country\":\"RUS\"}},\"checkedTemplate\":null," +
                                "\"resultStatus\":\"<ResultStatus accept:Accept()>\",\"ruleChecked\":null," +
                                "\"notificationsRule\":null}]}"));
    }

    @Test
    public void filterFraudPayments() throws Exception {
        when(iface.getFraudPayments(any(), any(), any())).thenReturn(new HistoricalDataResponse()
                .setData(createHistoricalDataFraudPaymentInfos()));
        LinkedMultiValueMap<String, String> params = createParams();
        this.mockMvc.perform(get("/payments-historical-data/fraud-payments")
                .queryParams(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("{\"continuationId\":null,\"result\":[{\"payment\":{\"id\":\"test\"," +
                                "\"eventTime\":\"2021-07-29T13:16:18.348795\",\"merchantInfo\":{\"partyId\":\"party\"," +
                                "\"shopId\":\"shop\"},\"amount\":123,\"currency\":\"RUB\",\"cardToken\":null," +
                                "\"clientInfo\":{\"ip\":\"123.123.123.123\",\"fingerprint\":\"finger\"," +
                                "\"email\":\"email\"},\"status\":\"captured\",\"payerType\":null,\"mobile\":null," +
                                "\"recurrent\":null,\"error\":{\"errorCode\":null,\"errorReason\":null}," +
                                "\"paymentSystem\":\"visa\",\"paymentCountry\":null,\"paymentTool\":" +
                                "\"BankCard(token:null, payment_system:PaymentSystemRef(id:visa), bin:1234, " +
                                "last_digits:null, bank_name:test)\",\"provider\":{\"providerId\":\"test\"," +
                                "\"terminalId\":\"1234\",\"country\":\"RUS\"}},\"type\":\"type_test\"," +
                                "\"comment\":\"test_comment\"}]}"));
    }

    @Test
    public void filterChargebacks() throws Exception {
        when(iface.getChargebacks(any(), any(), any())).thenReturn(new HistoricalDataResponse()
                .setData(createHistoricalDataChargebacks()));
        LinkedMultiValueMap<String, String> params = createParams();
        this.mockMvc.perform(get("/payments-historical-data/chargebacks")
                .queryParams(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("{\"continuationId\":null,\"result\":[{\"id\":\"test\",\"paymentId\":null," +
                                "\"eventTime\":\"2021-07-29T13:16:18.348795\",\"merchantInfo\":{\"partyId\":\"party\"," +
                                "\"shopId\":\"shop\"},\"paymentTool\":\"BankCard(token:null, " +
                                "payment_system:PaymentSystemRef(id:visa), bin:1234, last_digits:null, bank_name:test)\"" +
                                ",\"amount\":123,\"currency\":\"RUB\",\"provider\":{\"providerId\":\"test\"," +
                                "\"terminalId\":\"1234\",\"country\":\"RUS\"},\"status\":\"accepted\"," +
                                "\"category\":\"authorisation\",\"chargebackCode\":\"123\",\"clientInfo\":" +
                                "{\"ip\":\"123.123.123.123\",\"fingerprint\":\"finger\",\"email\":\"email\"}," +
                                "\"payerType\":null}]}"));
    }

    @NotNull
    private LinkedMultiValueMap<String, String> createParams() {
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("paymentId", "test");
        params.add("size", "100");
        params.add("from", "2021-07-27 00:00:00");
        params.add("to", "2021-07-27 13:28:54");
        return params;
    }

}
