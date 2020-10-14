package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.fraudbusters.management.dao.payment.group.PaymentGroupDao;
import com.rbkmoney.fraudbusters.management.dao.payment.group.PaymentGroupReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.GroupModel;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentGroupReferenceModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentGroupQueryResource {

    private final PaymentGroupDao groupDao;
    private final PaymentGroupReferenceDao referenceDao;

    @GetMapping(value = "/group/{id}/reference")
    public ResponseEntity<List<PaymentGroupReferenceModel>> getReferences(@PathVariable(value = "id") String id,
                                                                          @Validated @RequestParam(required = false) Integer limit) {
        log.info("GroupQueryResource getGroupReferences id: {} limit: {}", id, limit);
        List<PaymentGroupReferenceModel> listByTemplateId = referenceDao.getByGroupId(id);
        return ResponseEntity.ok().body(listByTemplateId);
    }

    @GetMapping(value = "/group")
    public ResponseEntity<GroupModel> findGroup(@RequestParam(value = "id") String id) {
        log.info("GroupQueryResource findGroup groupId: {}", id);
        GroupModel groupModel = groupDao.getById(id);
        return ResponseEntity.ok().body(groupModel);
    }

    @GetMapping(value = "/group/filter")
    public ResponseEntity<List<GroupModel>> filterGroup(@RequestParam(value = "id") String id) {
        log.info("GroupQueryResource findGroup groupId: {}", id);
        List<GroupModel> groupModels = groupDao.filterGroup(id);
        return ResponseEntity.ok().body(groupModels);
    }

}
