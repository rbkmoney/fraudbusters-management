package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.CommandType;
import com.rbkmoney.fraudbusters.management.converter.GroupModelToCommandConverter;
import com.rbkmoney.fraudbusters.management.converter.payment.PaymentGroupReferenceModelToCommandConverter;
import com.rbkmoney.fraudbusters.management.domain.GroupModel;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentGroupReferenceModel;
import com.rbkmoney.fraudbusters.management.service.GroupCommandService;
import com.rbkmoney.fraudbusters.management.service.payment.PaymentGroupReferenceService;
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

    private final PaymentGroupReferenceService paymentGroupReferenceService;
    private final GroupCommandService paymentGroupCommandService;
    private final GroupModelToCommandConverter groupModelToCommandConverter;
    private final PaymentGroupReferenceModelToCommandConverter groupReferenceToCommandConverter;

    @PostMapping(value = "/group")
    public ResponseEntity<String> insertGroup(@RequestBody GroupModel groupModel) {
        log.info("insertTemplate groupModel: {}", groupModel);
        Command command = groupModelToCommandConverter.convert(groupModel);
        command.setCommandType(CommandType.CREATE);
        String idMessage = paymentGroupCommandService.sendCommandSync(command);
        return ResponseEntity.ok().body(idMessage);
    }

    @PostMapping(value = "/group/{id}/reference")
    public ResponseEntity<List<String>> insertGroupReference(@PathVariable(value = "id") String id,
                                                             @Validated @RequestBody List<PaymentGroupReferenceModel> groupReferenceModels) {
        log.info("insertReference referenceModels: {}", groupReferenceModels);
        List<String> ids = groupReferenceModels.stream()
                .map(reference -> convertReferenceModel(reference, id))
                .map(command -> command.setCommandType(CommandType.CREATE))
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
    public ResponseEntity<String> removeGroup(@PathVariable(value = "id") String id) {
        log.info("removeGroup id: {}", id);
        Command command = paymentGroupCommandService.createTemplateCommandById(id);
        command.setCommandType(CommandType.DELETE);
        String idMessage = paymentGroupCommandService.sendCommandSync(command);
        return ResponseEntity.ok().body(idMessage);
    }

    @DeleteMapping(value = "/group/{id}/reference")
    public ResponseEntity<String> removeGroupReference(@PathVariable(value = "id") String groupId,
                                                       @PathVariable(value = "partyId") String partyId,
                                                       @PathVariable(value = "shopId") String shopId) {
        log.info("insertReference groupId: {} partyId: {} shopId: {}", groupId, partyId, shopId);
        PaymentGroupReferenceModel groupReferenceModel = new PaymentGroupReferenceModel();
        Command command = convertReferenceModel(groupReferenceModel, groupId);
        command.setCommandType(CommandType.DELETE);
        String id = paymentGroupReferenceService.sendCommandSync(command);
        log.info("insertReference sendCommand id: {}", id);
        return ResponseEntity.ok().body(id);
    }

}
