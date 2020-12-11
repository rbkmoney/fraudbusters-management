package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.CommandType;
import com.rbkmoney.damsel.fraudbusters.UserInfo;
import com.rbkmoney.fraudbusters.management.converter.GroupModelToCommandConverter;
import com.rbkmoney.fraudbusters.management.converter.payment.PaymentGroupReferenceModelToCommandConverter;
import com.rbkmoney.fraudbusters.management.domain.GroupModel;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentGroupReferenceModel;
import com.rbkmoney.fraudbusters.management.service.GroupCommandService;
import com.rbkmoney.fraudbusters.management.service.payment.PaymentGroupReferenceService;
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
@RequiredArgsConstructor
public class GroupCommandResource {

    private final PaymentGroupReferenceService paymentGroupReferenceService;
    private final GroupCommandService paymentGroupCommandService;
    private final GroupModelToCommandConverter groupModelToCommandConverter;
    private final PaymentGroupReferenceModelToCommandConverter groupReferenceToCommandConverter;
    private final UserInfoService userInfoService;

    @PostMapping(value = "/group")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> insertGroup(Principal principal,
                                              @RequestBody GroupModel groupModel) {
        log.info("insertTemplate initiator: {} groupModel: {}", userInfoService.getUserName(principal), groupModel);
        Command command = groupModelToCommandConverter.convert(groupModel);
        command.setCommandType(CommandType.CREATE);
        command.setUserInfo(new UserInfo()
                .setUserId(userInfoService.getUserName(principal)));
        String idMessage = paymentGroupCommandService.sendCommandSync(command);
        return ResponseEntity.ok().body(idMessage);
    }

    @PostMapping(value = "/group/{id}/reference")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<List<String>> insertGroupReference(Principal principal,
                                                             @PathVariable(value = "id") String id,
                                                             @Validated @RequestBody List<PaymentGroupReferenceModel> groupReferenceModels) {
        log.info("insertReference initiator: {} referenceModels: {}", userInfoService.getUserName(principal), groupReferenceModels);
        List<String> ids = groupReferenceModels.stream()
                .map(reference -> convertReferenceModel(reference, id))
                .map(command -> {
                    command.setCommandType(CommandType.CREATE);
                    command.setUserInfo(new UserInfo()
                            .setUserId(userInfoService.getUserName(principal)));
                    return command;
                })
                .map(paymentGroupReferenceService::sendCommandSync)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(ids);
    }

    private Command convertReferenceModel(PaymentGroupReferenceModel groupReferenceModel, String groupId) {
        Command command = groupReferenceToCommandConverter.convert(groupReferenceModel);
        command.getCommandBody().getGroupReference().setGroupId(groupId);
        return command;
    }

    @DeleteMapping(value = "/group/{id}")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> removeGroup(Principal principal,
                                              @PathVariable(value = "id") String id) {
        log.info("removeGroup initiator: {} id: {}", userInfoService.getUserName(principal), id);
        Command command = paymentGroupCommandService.createTemplateCommandById(id);
        command.setCommandType(CommandType.DELETE);
        command.setUserInfo(new UserInfo()
                .setUserId(userInfoService.getUserName(principal)));
        String idMessage = paymentGroupCommandService.sendCommandSync(command);
        return ResponseEntity.ok().body(idMessage);
    }

    @DeleteMapping(value = "/group/{id}/reference")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> removeGroupReference(Principal principal,
                                                       @PathVariable(value = "id") String groupId,
                                                       @RequestParam(value = "partyId") String partyId,
                                                       @RequestParam(value = "shopId") String shopId) {
        log.info("removeGroupReference initiator: {} groupId: {} partyId: {} shopId: {}", userInfoService.getUserName(principal), groupId, partyId, shopId);
        PaymentGroupReferenceModel groupReferenceModel = new PaymentGroupReferenceModel();
        groupReferenceModel.setPartyId(partyId);
        groupReferenceModel.setShopId(shopId);
        groupReferenceModel.setGroupId(groupId);
        Command command = convertReferenceModel(groupReferenceModel, groupId);
        command.setCommandType(CommandType.DELETE);
        command.setUserInfo(new UserInfo()
                .setUserId(userInfoService.getUserName(principal)));
        String id = paymentGroupReferenceService.sendCommandSync(command);
        log.info("removeGroupReference sendCommand id: {}", id);
        return ResponseEntity.ok().body(id);
    }

}
