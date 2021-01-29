package com.rbkmoney.fraudbusters.management.resource.p2p;

import com.rbkmoney.fraudbusters.management.dao.p2p.group.P2PGroupDao;
import com.rbkmoney.fraudbusters.management.dao.p2p.group.P2pGroupReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.GroupModel;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pGroupReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.domain.response.FilterResponse;
import com.rbkmoney.fraudbusters.management.utils.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/p2p")
@RequiredArgsConstructor
public class P2pGroupQueryResource {

    private final P2PGroupDao groupDao;
    private final P2pGroupReferenceDao referenceDao;
    private final UserInfoService userInfoService;

    @GetMapping(value = "/group/{groupId}/reference")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<List<P2pGroupReferenceModel>> getReferences(Principal principal,
                                                                      @PathVariable(value = "groupId") String groupId,
                                                                      @Validated @RequestParam(required = false) Integer limit) {
        log.info("getGroupReferences initiator: {} id: {} limit: {}", userInfoService.getUserName(principal), groupId, limit);
        List<P2pGroupReferenceModel> listByTemplateId = referenceDao.getByGroupId(groupId);
        return ResponseEntity.ok().body(listByTemplateId);
    }

    @GetMapping(value = "/group/{groupId}")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<GroupModel> getGroupById(Principal principal, @PathVariable String groupId) {
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

    @GetMapping(value = "/group/reference/filter")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<FilterResponse<P2pGroupReferenceModel>> filterReference(Principal principal, FilterRequest filterRequest) {
        log.info("filterReference initiator: {} idRegexp: {}", userInfoService.getUserName(principal), filterRequest.getSearchValue());
        List<P2pGroupReferenceModel> listByTemplateId = referenceDao.filterReference(filterRequest);
        Integer count = referenceDao.countFilterReference(filterRequest.getSearchValue());
        return ResponseEntity.ok().body(FilterResponse.<P2pGroupReferenceModel>builder()
                .count(count)
                .result(listByTemplateId)
                .build());
    }
}
