package com.rbkmoney.fraudbusters.management.filter;

import com.rbkmoney.fraudbusters.management.dao.payment.template.PaymentTemplateDao;
import com.rbkmoney.swag.fraudbusters.management.model.PaymentReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
public class UnknownPaymentTemplateInReferenceFilter implements Predicate<PaymentReference> {

    private final PaymentTemplateDao templateDao;

    @Override
    public boolean test(PaymentReference paymentReference) {
        var templateModel = templateDao.getById(paymentReference.getTemplateId());
        return templateModel != null;
    }

}
