package com.rbkmoney.fraudbusters.management.service.payment;

import com.rbkmoney.fraudbusters.management.dao.payment.group.PaymentGroupDao;
import com.rbkmoney.fraudbusters.management.dao.payment.group.PaymentGroupReferenceDao;
import com.rbkmoney.fraudbusters.management.dao.payment.reference.PaymentReferenceDao;
import com.rbkmoney.fraudbusters.management.dao.payment.template.PaymentTemplateDao;
import com.rbkmoney.fraudbusters.management.domain.PriorityIdModel;
import com.rbkmoney.fraudbusters.management.domain.ReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentGroupReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentReferenceModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentEmulateService {

    private final PaymentGroupDao groupDao;
    private final PaymentTemplateDao templateDao;
    private final PaymentGroupReferenceDao groupReferenceDao;
    private final PaymentReferenceDao referenceDao;

    public List<TemplateModel> getTemplatesFlow(String partyId, String shopId) {
        List<TemplateModel> resultModels = new ArrayList<>();
        ReferenceModel globalReference = referenceDao.getGlobalReference();

        if (globalReference != null) {
            TemplateModel globalTemplate = templateDao.getById(globalReference.getTemplateId());
            resultModels.add(globalTemplate);
        }

        List<PaymentGroupReferenceModel> groupReferenceModels =
                groupReferenceDao.getByPartyIdAndShopId(partyId, shopId);
        if (!CollectionUtils.isEmpty(groupReferenceModels)) {
            for (PaymentGroupReferenceModel groupReferenceModel : groupReferenceModels) {
                var groupModel = groupDao.getById(groupReferenceModel.getGroupId());
                if (groupModel != null) {
                    List<TemplateModel> groupsTemplates = groupModel.getPriorityTemplates().stream()
                            .sorted(Comparator.comparingLong(PriorityIdModel::getPriority))
                            .map(p -> templateDao.getById(p.getId()))
                            .collect(Collectors.toList());
                    resultModels.addAll(groupsTemplates);
                }
            }
        }

        List<PaymentReferenceModel> referenceModels = referenceDao.getByPartyAndShop(partyId, shopId);
        if (!CollectionUtils.isEmpty(referenceModels)) {
            List<TemplateModel> merchantTemplates = referenceModels.stream()
                    .map(model ->
                            new Pair<>(model.getShopId() == null ? 1L : 2L, templateDao.getById(model.getTemplateId())))
                    .sorted(Comparator.comparingLong(value -> value.a))
                    .map(p -> p.b)
                    .collect(Collectors.toList());
            resultModels.addAll(merchantTemplates);
        }
        return resultModels;
    }

}
