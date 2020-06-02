package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.CommandType;
import com.rbkmoney.fraudbusters.management.converter.payment.ReferenceToCommandConverter;
import com.rbkmoney.fraudbusters.management.converter.TemplateModelToCommandConverter;
import com.rbkmoney.fraudbusters.management.dao.payment.reference.PaymentReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentReferenceModel;
import com.rbkmoney.fraudbusters.management.exception.NotFoundException;
import com.rbkmoney.fraudbusters.management.service.TemplateCommandService;
import com.rbkmoney.fraudbusters.management.service.TemplateReferenceService;
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
public class TemplateCommandResource {

    private final TemplateCommandService templateCommandService;
    private final TemplateReferenceService templateReferenceService;
    private final TemplateModelToCommandConverter templateModelToCommandConverter;
    private final ReferenceToCommandConverter referenceToCommandConverter;
    private final PaymentReferenceDao referenceDao;

    @PostMapping(value = "/template")
    public ResponseEntity<String> insertTemplate(@Validated @RequestBody TemplateModel templateModel) {
        log.info("TemplateManagementResource insertTemplate templateModel: {}", templateModel);
        Command command = templateModelToCommandConverter.convert(templateModel);
        command.setCommandType(CommandType.CREATE);
        String idMessage = templateCommandService.sendCommandSync(command);
        return ResponseEntity.ok().body(idMessage);
    }

    @PostMapping(value = "/template/{id}/reference")
    public ResponseEntity<List<String>> insertReference(@PathVariable(value = "id") String id,
                                                        @Validated @RequestBody List<PaymentReferenceModel> referenceModels) {
        log.info("TemplateManagementResource insertReference referenceModels: {}", referenceModels);
        List<String> ids = referenceModels.stream()
                .map(reference -> convertReferenceModel(reference, id))
                .map(command -> command.setCommandType(CommandType.CREATE))
                .map(templateReferenceService::sendCommandSync)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(ids);
    }

    @PostMapping(value = "/template/{id}/default")
    public ResponseEntity<String> markReferenceAsDefault(@PathVariable(value = "id") String id) {
        log.info("TemplateManagementResource markReferenceAsDefault id: {}", id);
        referenceDao.markReferenceAsDefault(id);
        return ResponseEntity.ok().body(id);
    }

    @PostMapping(value = "/template/default")
    public ResponseEntity<List<String>> insertDefaultReference(@Validated @RequestBody List<PaymentReferenceModel> referenceModels) {
        log.info("TemplateManagementResource insertDefaultReference referenceModels: {}", referenceModels);
        PaymentReferenceModel defaultReference = referenceDao.getDefaultReference();
        if (defaultReference == null) {
            throw new NotFoundException("Couldn't insert default reference: Default template not found");
        }
        String id = defaultReference.getTemplateId();
        List<String> ids = referenceModels.stream()
                .map(reference -> convertReferenceModel(reference, id))
                .map(command -> command.setCommandType(CommandType.CREATE))
                .map(templateReferenceService::sendCommandSync)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(ids);
    }

    private Command convertReferenceModel(PaymentReferenceModel referenceModel, String templateId) {
        Command command = referenceToCommandConverter.convert(referenceModel);
        command.getCommandBody().getReference().setTemplateId(templateId);
        return command;
    }

    @DeleteMapping(value = "/template")
    public ResponseEntity<String> removeTemplate(@Validated @RequestBody TemplateModel templateModel) {
        log.info("TemplateManagementResource removeTemplate templateModel: {}", templateModel);
        Command command = templateModelToCommandConverter.convert(templateModel);
        command.setCommandType(CommandType.DELETE);
        String idMessage = templateCommandService.sendCommandSync(command);
        return ResponseEntity.ok().body(idMessage);
    }

    @DeleteMapping(value = "/template/{id}/reference")
    public ResponseEntity<List<String>> deleteReference(@PathVariable(value = "id") String id,
                                                        @Validated @RequestBody List<PaymentReferenceModel> referenceModels) {
        log.info("TemplateManagementResource insertReference referenceModels: {}", referenceModels);
        List<String> ids = referenceModels.stream()
                .map(reference -> convertReferenceModel(reference, id))
                .map(command -> command.setCommandType(CommandType.DELETE))
                .map(templateReferenceService::sendCommandSync)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(ids);
    }

}
