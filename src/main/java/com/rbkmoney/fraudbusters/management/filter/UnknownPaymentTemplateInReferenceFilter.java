package com.rbkmoney.fraudbusters.management.filter;

import com.rbkmoney.fraudbusters.management.dao.payment.template.PaymentTemplateDao;
import com.rbkmoney.fraudbusters.management.domain.ReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
public class UnknownPaymentTemplateInReferenceFilter implements Predicate<ReferenceModel> {

    private final PaymentTemplateDao templateDao;

    @Override
    public boolean test(ReferenceModel referenceModel) {
        TemplateModel templateModel = templateDao.getById(referenceModel.getTemplateId());
        return templateModel != null;
    }

}
