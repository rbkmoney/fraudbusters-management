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
        log.info("getGroupReferences id: {} limit: {}", id, limit);
        List<PaymentGroupReferenceModel> listByTemplateId = referenceDao.getByGroupId(id);
        return ResponseEntity.ok().body(listByTemplateId);
    }

    @GetMapping(value = "/group/reference/filter")
    public ResponseEntity<List<PaymentGroupReferenceModel>> filterReference(@RequestParam(required = false, value = "id") String idRegexp) {
        log.info("getGroupReferences idRegexp: {}", idRegexp);
        List<PaymentGroupReferenceModel> listByTemplateId = referenceDao.getByGroupId(idRegexp);
        return ResponseEntity.ok().body(listByTemplateId);
    }

    @GetMapping(value = "/group/{id}")
    public ResponseEntity<GroupModel> findGroup(@PathVariable String id) {
        log.info("findGroup groupId: {}", id);
        GroupModel groupModel = groupDao.getById(id);
        return ResponseEntity.ok().body(groupModel);
    }

    @GetMapping(value = "/group/filter")
    public ResponseEntity<List<GroupModel>> filterGroup(@RequestParam(required = false, value = "id") String idRegexp) {
        log.info("filterGroup groupId: {}", idRegexp);
        List<GroupModel> groupModels = groupDao.filterGroup(idRegexp);
        return ResponseEntity.ok().body(groupModels);
    }

}
