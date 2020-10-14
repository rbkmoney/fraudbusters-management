package com.rbkmoney.fraudbusters.management.resource.p2p;

import com.rbkmoney.fraudbusters.management.dao.p2p.group.P2PGroupDao;
import com.rbkmoney.fraudbusters.management.dao.p2p.group.P2pGroupReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.GroupModel;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pGroupReferenceModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @GetMapping(value = "/group")
    public ResponseEntity<GroupModel> findGroup(@RequestParam(value = "id") String id) {
        log.info("findGroup groupId: {}", id);
        GroupModel groupModel = groupDao.getById(id);
        return ResponseEntity.ok().body(groupModel);
    }

    @GetMapping(value = "/group/filter")
    public ResponseEntity<List<GroupModel>> filterGroup(@RequestParam(required = false) String id) {
        log.info("filterGroup groupId: {}", id);
        List<GroupModel> groupModels = groupDao.filterGroup(id);
        return ResponseEntity.ok().body(groupModels);
    }
}
