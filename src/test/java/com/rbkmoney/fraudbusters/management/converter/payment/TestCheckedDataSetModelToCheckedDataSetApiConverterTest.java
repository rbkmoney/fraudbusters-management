package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.fraudbusters.management.domain.payment.CheckedDataSetModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CheckedDataSetModelToCheckedDataSetApiConverter.class,
        CheckedPaymentModelToCheckedDataSetRowConverter.class,
        PaymentModelToPaymentApiConverter.class})
public class TestCheckedDataSetModelToCheckedDataSetApiConverterTest {

    public static final String TEST = "TEST";
    public static final String TEST_TEMPLATE = "test_template";
    public static final String TEST_DATA_SET_ID = "1";

    @Autowired
    CheckedDataSetModelToCheckedDataSetApiConverter checkedDataSetModelToCheckedDataSetApiConverter;

    @Test
    void testConvert() {
        CheckedDataSetModel testDataSetModel = CheckedDataSetModel.builder()
                .initiator(TEST)
                .testDataSetId(1L)
                .shopId(TEST)
                .partyId(TEST)
                .id(2L)
                .createdAt(LocalDateTime.now())
                .template(TEST_TEMPLATE)
                .checkedPaymentModels(List.of())
                .build();
        var checkedDataSet = checkedDataSetModelToCheckedDataSetApiConverter.convert(testDataSetModel);

        assertNotNull(checkedDataSet);
        assertEquals(TEST_DATA_SET_ID, checkedDataSet.getTestDataSetId());
        assertEquals(TEST, checkedDataSet.getInitiator());
        assertEquals(TEST_TEMPLATE, checkedDataSet.getTemplate());
        assertTrue(checkedDataSet.getRows().isEmpty());
    }

}
