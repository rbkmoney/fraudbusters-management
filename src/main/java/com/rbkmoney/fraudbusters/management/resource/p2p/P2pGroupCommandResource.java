package com.rbkmoney.fraudbusters.management.resource.p2p;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.CommandType;
import com.rbkmoney.damsel.fraudbusters.UserInfo;
import com.rbkmoney.fraudbusters.management.converter.p2p.GroupModelToCommandConverter;
import com.rbkmoney.fraudbusters.management.converter.p2p.P2pGroupReferenceToCommandConverter;
import com.rbkmoney.fraudbusters.management.domain.GroupModel;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pGroupReferenceModel;
import com.rbkmoney.fraudbusters.management.service.GroupCommandService;
import com.rbkmoney.fraudbusters.management.service.p2p.P2PGroupReferenceService;
import com.rbkmoney.fraudbusters.management.utils.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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
    private final UserInfoService userInfoService;

    @PostMapping(value = "/group")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> insertGroup(Principal principal, @RequestBody GroupModel groupModel) {
        log.info("GroupCommandResource insertTemplate initiator: {} groupModel: {}",
                userInfoService.getUserName(principal), groupModel);
        Command command = groupModelToCommandConverter.convert(groupModel);
        command.setCommandType(CommandType.CREATE);
        command.setUserInfo(new UserInfo()
                .setUserId(userInfoService.getUserName(principal)));
        String idMessage = p2pGroupCommandService.sendCommandSync(command);
        return ResponseEntity.ok().body(idMessage);
    }

    @PostMapping(value = "/group/{id}/reference")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<List<String>> insertGroupReference(
            Principal principal,
            @PathVariable(value = "id") String id,
            @Validated @RequestBody List<P2pGroupReferenceModel> groupReferenceModels) {
        log.info("P2pGroupReferenceCommandResource insertReference initiator: {} referenceModels: {}",
                userInfoService.getUserName(principal),
                groupReferenceModels);
        List<String> ids = groupReferenceModels.stream()
                .map(reference -> convertReferenceModel(reference, id))
                .map(command -> {
                    command.setCommandType(CommandType.CREATE);
                    command.setUserInfo(new UserInfo()
                            .setUserId(userInfoService.getUserName(principal)));
                    return command;
                })
                .map(p2pGroupReferenceService::sendCommandSync)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(ids);
    }

    @DeleteMapping(value = "/group/{id}")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> removeGroup(Principal principal,
                                              @PathVariable(value = "id") String id) {
        log.info("removeGroup initiator: {} id: {}", userInfoService.getUserName(principal), id);
        Command command = p2pGroupCommandService.createTemplateCommandById(id);
        command.setCommandType(CommandType.DELETE);
        command.setUserInfo(new UserInfo()
                .setUserId(userInfoService.getUserName(principal)));
        String idMessage = p2pGroupCommandService.sendCommandSync(command);
        return ResponseEntity.ok().body(idMessage);
    }

    private Command convertReferenceModel(P2pGroupReferenceModel groupReferenceModel, String groupId) {
        Command command = groupReferenceToCommandConverter.convert(groupReferenceModel);
        command.getCommandBody().getP2pGroupReference().setGroupId(groupId);
        return command;
    }

    @DeleteMapping(value = "/group/{groupId}/reference/{identityId}")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> removeGroupReference(Principal principal,
                                                       @PathVariable(value = "groupId") String groupId,
                                                       @PathVariable(value = "identityId") String identityId) {
        log.info("removeGroupReference initiator: {} groupId: {} identityId: {}",
                userInfoService.getUserName(principal), groupId, identityId);
        P2pGroupReferenceModel groupReferenceModel = new P2pGroupReferenceModel();
        groupReferenceModel.setIdentityId(identityId);
        groupReferenceModel.setGroupId(groupId);
        Command command = convertReferenceModel(groupReferenceModel, groupId);
        command.setCommandType(CommandType.DELETE);
        command.setUserInfo(new UserInfo()
                .setUserId(userInfoService.getUserName(principal)));
        String id = p2pGroupReferenceService.sendCommandSync(command);
        log.info("removeGroupReference sendCommand id: {}", id);
        return ResponseEntity.ok().body(id);
    }
}
