package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.fraudbusters.management.dao.payment.group.PaymentGroupDao;
import com.rbkmoney.fraudbusters.management.dao.payment.group.PaymentGroupReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.GroupModel;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentGroupReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.domain.response.FilterResponse;
import com.rbkmoney.fraudbusters.management.utils.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentGroupQueryResource {

    private final PaymentGroupDao groupDao;
    private final PaymentGroupReferenceDao referenceDao;
    private final UserInfoService userInfoService;

    @GetMapping(value = "/group/{groupId}/reference")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<List<PaymentGroupReferenceModel>> getReferences(Principal principal,
                                                                          @PathVariable(value = "groupId") String groupId,
                                                                          @Validated @RequestParam(required = false) Integer limit) {
        log.info("getGroupReferences initiator: {} id: {} limit: {}", userInfoService.getUserName(principal), groupId, limit);
        List<PaymentGroupReferenceModel> listByTemplateId = referenceDao.getByGroupId(groupId);
        return ResponseEntity.ok().body(listByTemplateId);
    }

    @GetMapping(value = "/group/reference/filter")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<FilterResponse<PaymentGroupReferenceModel>> filterReference(FilterRequest filterRequest) {
        log.info("filterReference idRegexp: {}", filterRequest.getSearchValue());
        List<PaymentGroupReferenceModel> listByTemplateId = referenceDao.filterReference(filterRequest);
        Integer count = referenceDao.countFilterReference(filterRequest.getSearchValue());
        return ResponseEntity.ok().body(
                FilterResponse.<PaymentGroupReferenceModel>builder()
                        .count(count)
                        .result(listByTemplateId)
                        .build());
    }

    @GetMapping(value = "/group/{groupId}")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<GroupModel> getGroupById(Principal principal,
                                                   @PathVariable String groupId) {
        log.info("getGroupById initiator: {} groupId: {}", userInfoService.getUserName(principal), groupId);
        GroupModel groupModel = groupDao.getById(groupId);
        if (groupModel == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(groupModel);
    }

    @GetMapping(value = "/group/filter")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<List<GroupModel>> filterGroup(Principal principal,
                                                        @RequestParam(required = false, value = "id") String idRegexp) {
        log.info("filterGroup initiator: {} groupId: {}", userInfoService.getUserName(principal), idRegexp);
        List<GroupModel> groupModels = groupDao.filterGroup(idRegexp);
        return ResponseEntity.ok().body(groupModels);
    }

}
