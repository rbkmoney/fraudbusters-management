package com.rbkmoney.fraudbusters.management.filter;

import com.rbkmoney.fraudbusters.management.dao.p2p.template.P2pTemplateDao;
import com.rbkmoney.fraudbusters.management.domain.ReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
public class UnknownP2pTemplateInReferenceFilter implements Predicate<ReferenceModel> {

    private final P2pTemplateDao templateDao;

    @Override
    public boolean test(ReferenceModel referenceModel) {
        TemplateModel templateModel = templateDao.getById(referenceModel.getTemplateId());
        return templateModel != null;
    }

}
