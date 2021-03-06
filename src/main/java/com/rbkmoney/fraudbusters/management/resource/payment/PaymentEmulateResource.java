package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.fraudbusters.management.dao.payment.group.PaymentGroupDao;
import com.rbkmoney.fraudbusters.management.dao.payment.group.PaymentGroupReferenceDao;
import com.rbkmoney.fraudbusters.management.dao.payment.reference.PaymentReferenceDao;
import com.rbkmoney.fraudbusters.management.dao.payment.template.PaymentTemplateDao;
import com.rbkmoney.fraudbusters.management.domain.GroupModel;
import com.rbkmoney.fraudbusters.management.domain.PriorityIdModel;
import com.rbkmoney.fraudbusters.management.domain.ReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentGroupReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentReferenceModel;
import com.rbkmoney.fraudbusters.management.utils.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentEmulateResource {

    private final PaymentGroupDao groupDao;
    private final PaymentTemplateDao templateDao;
    private final PaymentGroupReferenceDao groupReferenceDao;
    private final PaymentReferenceDao referenceDao;
    private final UserInfoService userInfoService;

    @GetMapping(value = "/rules")
    @PreAuthorize("hasAnyRole('fraud-support', 'fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<List<TemplateModel>> getRulesByPartyAndShop(Principal principal,
                                                                      @Validated @RequestParam String partyId,
                                                                      @Validated @RequestParam String shopId) {
        log.info("EmulateResource getRulesByPartyAndShop initiator: {} partyId: {} shopId: {}",
                userInfoService.getUserName(principal), partyId, shopId);
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
                GroupModel groupModel = groupDao.getById(groupReferenceModel.getGroupId());
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

        log.info("EmulateResource getRulesByPartyAndShop result: {}", resultModels);
        return ResponseEntity.ok().body(resultModels.stream()
                .filter(model -> model != null)
                .collect(Collectors.toList())
        );
    }
}
