package com.rbkmoney.fraudbusters.management.resource.p2p;

import com.rbkmoney.fraudbusters.management.dao.p2p.group.P2PGroupDao;
import com.rbkmoney.fraudbusters.management.dao.p2p.group.P2pGroupReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.GroupModel;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pGroupReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.response.FilterP2pGroupsReferenceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.SortOrder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/p2p")
@RequiredArgsConstructor
public class P2pGroupQueryResource {

    private final P2PGroupDao groupDao;
    private final P2pGroupReferenceDao referenceDao;

    @GetMapping(value = "/group/{id}/reference")
    public ResponseEntity<List<P2pGroupReferenceModel>> getReferences(@PathVariable(value = "id") String id,
                                                                      @Validated @RequestParam(required = false) Integer limit) {
        log.info("getGroupReferences id: {} limit: {}", id, limit);
        List<P2pGroupReferenceModel> listByTemplateId = referenceDao.getByGroupId(id);
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

    @GetMapping(value = "/group/reference/filter")
    public ResponseEntity<FilterP2pGroupsReferenceResponse> filterReference(@Validated @RequestParam(required = false) String idRegexp,
                                                                            @Validated @RequestParam(required = false) String lastId,
                                                                            @Validated @RequestParam(required = false) String sortFieldValue,
                                                                            @Validated @RequestParam(required = false) Integer size,
                                                                            @Validated @RequestParam(required = false) String sortBy,
                                                                            @Validated @RequestParam(required = false) SortOrder sortOrder) {
        log.info("filterReference idRegexp: {}", idRegexp);
        List<P2pGroupReferenceModel> listByTemplateId = referenceDao.filterReference(idRegexp, lastId, sortFieldValue,
                size, sortBy, sortOrder);
        Integer count = referenceDao.countFilterReference(idRegexp);
        return ResponseEntity.ok().body(FilterP2pGroupsReferenceResponse.builder()
                .count(count)
                .groupsReferenceModels(listByTemplateId)
                .build());
    }
}
