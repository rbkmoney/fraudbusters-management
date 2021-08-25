package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.fraudbusters.management.domain.payment.TestCheckedDataSetModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestCheckedPaymentModelToCheckedDataSetRowConverter.class,
        TestCheckedDataSetModelToCheckedDataSetApiConverter.class,
        TestPaymentModelToPaymentApiConverter.class})
public class TestCheckedDataSetModelToCheckedDataSetApiConverterTest {

    public static final String TEST = "TEST";
    public static final String TEST_TEMPLATE = "test_template";
    public static final String TEST_DATA_SET_ID = "1";
    @Autowired
    TestCheckedDataSetModelToCheckedDataSetApiConverter testCheckedDataSetModelToCheckedDataSetApiConverter;

    @Test
    public void testConvert() {
        TestCheckedDataSetModel testDataSetModel = TestCheckedDataSetModel.builder()
                .initiator(TEST)
                .testDataSetId(1L)
                .shopId(TEST)
                .partyId(TEST)
                .id(2L)
                .createdAt(LocalDateTime.now())
                .template(TEST_TEMPLATE)
                .testCheckedPaymentModels(List.of())
                .build();
        var checkedDataSet = testCheckedDataSetModelToCheckedDataSetApiConverter.convert(testDataSetModel);

        assertNotNull(checkedDataSet);
        assertEquals(TEST_DATA_SET_ID, checkedDataSet.getTestDataSetId());
        assertEquals(TEST, checkedDataSet.getInitiator());
        assertEquals(TEST_TEMPLATE, checkedDataSet.getTemplate());
        assertTrue(checkedDataSet.getRows().isEmpty());
    }

}
