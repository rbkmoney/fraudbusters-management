package com.rbkmoney.fraudbusters.management.resource;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.CommandType;
import com.rbkmoney.fraudbusters.management.converter.GroupModelToCommandConverter;
import com.rbkmoney.fraudbusters.management.converter.GroupReferenceToCommandConverter;
import com.rbkmoney.fraudbusters.management.domain.GroupModel;
import com.rbkmoney.fraudbusters.management.domain.GroupReferenceModel;
import com.rbkmoney.fraudbusters.management.service.GroupCommandService;
import com.rbkmoney.fraudbusters.management.service.GroupReferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GroupCommandResource {

    private final GroupReferenceService groupReferenceService;
    private final GroupCommandService groupCommandService;
    private final GroupModelToCommandConverter groupModelToCommandConverter;
    private final GroupReferenceToCommandConverter groupReferenceToCommandConverter;

    @PostMapping(value = "/group")
    public ResponseEntity<String> insertGroup(@Validated @RequestBody GroupModel groupModel) {
        log.info("GroupCommandResource insertTemplate groupModel: {}", groupModel);
        Command command = groupModelToCommandConverter.convert(groupModel);
        command.setCommandType(CommandType.CREATE);
        String idMessage = groupCommandService.sendCommandSync(command);
        return ResponseEntity.ok().body(idMessage);
    }

    @PostMapping(value = "/group/{id}/reference")
    public ResponseEntity<List<String>> insertGroupReference(@PathVariable(value = "id") String id,
                                                             @Validated @RequestBody List<GroupReferenceModel> groupReferenceModels) {
        log.info("GroupCommandResource insertReference referenceModels: {}", groupReferenceModels);
        List<String> ids = groupReferenceModels.stream()
                .map(reference -> convertReferenceModel(reference, id))
                .map(command -> command.setCommandType(CommandType.CREATE))
                .map(groupReferenceService::sendCommandSync)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(ids);
    }

    private Command convertReferenceModel(GroupReferenceModel groupReferenceModel, String groupId) {
        Command command = groupReferenceToCommandConverter.convert(groupReferenceModel);
        command.getCommandBody().getGroupReference().setGroupId(groupId);
        return command;
    }

    @DeleteMapping(value = "/group")
    public ResponseEntity<String> removeGroup(@Validated @RequestBody GroupModel groupModel) {
        log.info("GroupCommandResource removeTemplate groupModel: {}", groupModel);
        Command command = groupModelToCommandConverter.convert(groupModel);
        command.setCommandType(CommandType.DELETE);
        String idMessage = groupCommandService.sendCommandSync(command);
        return ResponseEntity.ok().body(idMessage);
    }

    @DeleteMapping(value = "/group/{id}/reference")
    public ResponseEntity<List<String>> deleteGroupReference(@PathVariable(value = "id") String id,
                                                             @Validated @RequestBody List<GroupReferenceModel> groupModels) {
        log.info("GroupCommandResource insertReference groupModels: {}", groupModels);
        List<String> ids = groupModels.stream()
                .map(reference -> convertReferenceModel(reference, id))
                .map(command -> command.setCommandType(CommandType.DELETE))
                .map(groupReferenceService::sendCommandSync)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(ids);
    }

}
