package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.fraudbusters.management.domain.payment.TestCheckedDataSetModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestCheckedPaymentModelToCheckedDataSetRowConverter.class,
        TestCheckedDataSetModelToCheckedDataSetApiConverter.class})
public class TestCheckedDataSetModelToCheckedDataSetApiConverterTest {

    @Autowired
    TestCheckedDataSetModelToCheckedDataSetApiConverter testCheckedDataSetModelToCheckedDataSetApiConverter;

    @Test
    public void testConvert() {
        TestCheckedDataSetModel testDataSetModel = new TestCheckedDataSetModel();
        var checkedDataSet = testCheckedDataSetModelToCheckedDataSetApiConverter.convert(testDataSetModel);
    }

}
