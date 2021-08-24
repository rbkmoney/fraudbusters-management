package com.rbkmoney.fraudbusters.management.service.payment;

import com.rbkmoney.fraudbusters.management.domain.payment.TestDataSetModel;
import com.rbkmoney.fraudbusters.management.domain.payment.TestPaymentModel;
import com.rbkmoney.swag.fraudbusters.management.model.Payment;

import java.time.LocalDateTime;
import java.util.List;

public class DataSetModelUtils {
    
    public static final String TEST_INITIATOR = "test_initiator";
    public static final String TEST = "test";
    public static final String PAYMENT_ID = "id";
    public static final String PAYMENT_COUNTRY = "RUS";
    public static final String PARTY_ID = "partyId";
    public static final String SHOP_ID = "shopId";

    public static TestDataSetModel initTestDataSetModel(LocalDateTime lastModificationDate) {
        return TestDataSetModel.builder()
                .lastModificationInitiator(TEST_INITIATOR)
                .lastModificationTime(lastModificationDate)
                .name(TEST)
                .testPaymentModelList(List.of(initTestPaymentModel(lastModificationDate)))
                .build();
    }

    public static TestPaymentModel initTestPaymentModel(LocalDateTime lastModificationDate) {
        return TestPaymentModel.builder()
                .lastModificationDate(lastModificationDate)
                .status(Payment.StatusEnum.CAPTURED.name())
                .amount(123L)
                .currency("RUB")
                .cardToken("cardToken")
                .paymentId(PAYMENT_ID)
                .paymentCountry(PAYMENT_COUNTRY)
                .terminalId("123")
                .eventTime(lastModificationDate)
                .partyId(PARTY_ID)
                .shopId(SHOP_ID)
                .build();
    }
}
