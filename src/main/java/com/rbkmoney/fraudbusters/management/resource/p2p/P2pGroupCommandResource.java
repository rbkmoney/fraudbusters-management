package com.rbkmoney.fraudbusters.management.resource.p2p;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.CommandType;
import com.rbkmoney.fraudbusters.management.converter.GroupModelToCommandConverter;
import com.rbkmoney.fraudbusters.management.converter.p2p.P2pGroupReferenceToCommandConverter;
import com.rbkmoney.fraudbusters.management.domain.GroupModel;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pGroupReferenceModel;
import com.rbkmoney.fraudbusters.management.service.GroupCommandService;
import com.rbkmoney.fraudbusters.management.service.p2p.P2PGroupReferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/p2p")
@RequiredArgsConstructor
public class P2pGroupCommandResource {

    private final P2PGroupReferenceService p2pGroupReferenceService;
    private final GroupCommandService p2pGroupCommandService;
    private final GroupModelToCommandConverter groupModelToCommandConverter;
    private final P2pGroupReferenceToCommandConverter groupReferenceToCommandConverter;

    @PostMapping(value = "/group")
    public ResponseEntity<String> insertGroup(@RequestBody GroupModel groupModel) {
        log.info("GroupCommandResource insertTemplate groupModel: {}", groupModel);
        Command command = groupModelToCommandConverter.convert(groupModel);
        command.setCommandType(CommandType.CREATE);
        String idMessage = p2pGroupCommandService.sendCommandSync(command);
        return ResponseEntity.ok().body(idMessage);
    }

    @PostMapping(value = "/group/{id}/reference")
    public ResponseEntity<List<String>> insertGroupReference(@PathVariable(value = "id") String id,
                                                             @Validated @RequestBody List<P2pGroupReferenceModel> groupReferenceModels) {
        log.info("P2pGroupReferenceCommandResource insertReference referenceModels: {}", groupReferenceModels);
        List<String> ids = groupReferenceModels.stream()
                .map(reference -> convertReferenceModel(reference, id))
                .map(command -> command.setCommandType(CommandType.CREATE))
                .map(p2pGroupReferenceService::sendCommandSync)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(ids);
    }

    @DeleteMapping(value = "/group")
    public ResponseEntity<String> removeGroup(@Validated @RequestBody GroupModel groupModel) {
        log.info("GroupCommandResource removeTemplate groupModel: {}", groupModel);
        Command command = groupModelToCommandConverter.convert(groupModel);
        command.setCommandType(CommandType.DELETE);
        String idMessage = p2pGroupCommandService.sendCommandSync(command);
        return ResponseEntity.ok().body(idMessage);
    }

    private Command convertReferenceModel(P2pGroupReferenceModel groupReferenceModel, String groupId) {
        Command command = groupReferenceToCommandConverter.convert(groupReferenceModel);
        command.getCommandBody().getP2pGroupReference().setGroupId(groupId);
        return command;
    }

    @DeleteMapping(value = "/group/{id}/reference")
    public ResponseEntity<List<String>> deleteGroupReference(@PathVariable(value = "id") String id,
                                                             @Validated @RequestBody List<P2pGroupReferenceModel> groupModels) {
        log.info("P2pGroupReferenceCommandResource insertReference groupModels: {}", groupModels);
        List<String> ids = groupModels.stream()
                .map(reference -> convertReferenceModel(reference, id))
                .map(command -> command.setCommandType(CommandType.DELETE))
                .map(p2pGroupReferenceService::sendCommandSync)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(ids);
    }

}
