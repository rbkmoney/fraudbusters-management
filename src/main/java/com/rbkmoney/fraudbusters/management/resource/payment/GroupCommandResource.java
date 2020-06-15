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
        log.info("GroupCommandResource insertTemplate groupModel: {}", groupModel);
        Command command = groupModelToCommandConverter.convert(groupModel);
        command.setCommandType(CommandType.CREATE);
        String idMessage = paymentGroupCommandService.sendCommandSync(command);
        return ResponseEntity.ok().body(idMessage);
    }

    @PostMapping(value = "/group/{id}/reference")
    public ResponseEntity<List<String>> insertGroupReference(@PathVariable(value = "id") String id,
                                                             @Validated @RequestBody List<PaymentGroupReferenceModel> groupReferenceModels) {
        log.info("GroupCommandResource insertReference referenceModels: {}", groupReferenceModels);
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

    @DeleteMapping(value = "/group")
    public ResponseEntity<String> removeGroup(@Validated @RequestBody GroupModel groupModel) {
        log.info("GroupCommandResource removeTemplate groupModel: {}", groupModel);
        Command command = groupModelToCommandConverter.convert(groupModel);
        command.setCommandType(CommandType.DELETE);
        String idMessage = paymentGroupCommandService.sendCommandSync(command);
        return ResponseEntity.ok().body(idMessage);
    }

    @DeleteMapping(value = "/group/{id}/reference")
    public ResponseEntity<List<String>> deleteGroupReference(@PathVariable(value = "id") String id,
                                                             @Validated @RequestBody List<PaymentGroupReferenceModel> groupModels) {
        log.info("GroupCommandResource insertReference groupModels: {}", groupModels);
        List<String> ids = groupModels.stream()
                .map(reference -> convertReferenceModel(reference, id))
                .map(command -> command.setCommandType(CommandType.DELETE))
                .map(paymentGroupReferenceService::sendCommandSync)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(ids);
    }

}
