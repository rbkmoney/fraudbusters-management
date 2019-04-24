package com.rbkmoney.fraudbusters.management.resource;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.CommandType;
import com.rbkmoney.fraudbusters.management.converter.ReferenceToCommandConverter;
import com.rbkmoney.fraudbusters.management.converter.TemplateModelToCommandConverter;
import com.rbkmoney.fraudbusters.management.domain.ReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import com.rbkmoney.fraudbusters.management.service.FraudbustersCommandService;
import com.rbkmoney.fraudbusters.management.service.FraudbustersReferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TemplateCommandResource {

    private final FraudbustersCommandService fraudbustersCommandService;
    private final FraudbustersReferenceService fraudbustersReferenceService;
    private final TemplateModelToCommandConverter templateModelToCommandConverter;
    private final ReferenceToCommandConverter referenceToCommandConverter;

    @PostMapping(value = "/template")
    public ResponseEntity<String> insertTemplate(@Validated @RequestBody TemplateModel templateModel) {
        log.info("TemplateManagementResource insertTemplate templateModel: {}", templateModel);
        Command command = templateModelToCommandConverter.convert(templateModel);
        command.setCommandType(CommandType.CREATE);
        String idMessage = fraudbustersCommandService.sendCommandSync(command);
        return ResponseEntity.ok().body(idMessage);
    }

    @PostMapping(value = "/template/{id}/reference")
    public ResponseEntity<List<String>> insertReference(@PathVariable(value = "id") String id,
                                                        @Validated @RequestBody List<ReferenceModel> referenceModels) {
        log.info("TemplateManagementResource insertReference referenceModels: {}", referenceModels);
        List<String> ids = referenceModels.stream()
                .map(reference -> convertReferenceModel(reference, id))
                .map(command -> command.setCommandType(CommandType.CREATE))
                .map(fraudbustersReferenceService::sendCommandSync)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(ids);
    }

    private Command convertReferenceModel(ReferenceModel referenceModel, String templateId) {
        Command command = referenceToCommandConverter.convert(referenceModel);
        command.getCommandBody().getReference().setTemplateId(templateId);
        return command;
    }

    @DeleteMapping(value = "/template")
    public ResponseEntity<String> removeTemplate(@Validated @RequestBody TemplateModel templateModel) {
        log.info("TemplateManagementResource removeTemplate templateModel: {}", templateModel);
        Command command = templateModelToCommandConverter.convert(templateModel);
        command.setCommandType(CommandType.DELETE);
        String idMessage = fraudbustersCommandService.sendCommandSync(command);
        return ResponseEntity.ok().body(idMessage);
    }

    @DeleteMapping(value = "/template/{id}/reference")
    public ResponseEntity<List<String>> deleteReference(@PathVariable(value = "id") String id,
                                                        @Validated @RequestBody List<ReferenceModel> referenceModels) {
        log.info("TemplateManagementResource insertReference referenceModels: {}", referenceModels);
        List<String> ids = referenceModels.stream()
                .map(reference -> convertReferenceModel(reference, id))
                .map(command -> command.setCommandType(CommandType.DELETE))
                .map(fraudbustersReferenceService::sendCommandSync)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(ids);
    }

}
