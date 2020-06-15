package com.rbkmoney.fraudbusters.management.resource.p2p;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.CommandType;
import com.rbkmoney.damsel.fraudbusters.Template;
import com.rbkmoney.damsel.fraudbusters.TemplateValidateError;
import com.rbkmoney.fraudbusters.management.converter.TemplateModelToCommandConverter;
import com.rbkmoney.fraudbusters.management.converter.p2p.P2pReferenceToCommandConverter;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.response.CreateTemplateResponse;
import com.rbkmoney.fraudbusters.management.service.TemplateCommandService;
import com.rbkmoney.fraudbusters.management.service.ValidationTemplateService;
import com.rbkmoney.fraudbusters.management.service.p2p.P2PTemplateReferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/p2p")
@RequiredArgsConstructor
public class P2PTemplateCommandResource {

    private final TemplateCommandService p2pTemplateCommandService;
    private final P2PTemplateReferenceService p2PTemplateReferenceService;
    private final TemplateModelToCommandConverter templateModelToCommandConverter;
    private final P2pReferenceToCommandConverter referenceToCommandConverter;
    private final ValidationTemplateService p2PValidationService;

    @PostMapping(value = "/template")
    public ResponseEntity<CreateTemplateResponse> insertTemplate(@Validated @RequestBody TemplateModel templateModel) {
        log.info("P2pReferenceCommandResource insertTemplate templateModel: {}", templateModel);
        Command command = templateModelToCommandConverter.convert(templateModel);
        List<TemplateValidateError> templateValidateErrors = p2PValidationService.validateTemplate(
                List.of(command.getCommandBody().getTemplate()));
        if (!CollectionUtils.isEmpty(templateValidateErrors)) {
            return ResponseEntity.ok().body(CreateTemplateResponse.builder()
                    .template(templateModel.getTemplate())
                    .errors(templateValidateErrors.get(0).getReason())
                    .build());
        }
        command.setCommandType(CommandType.CREATE);
        String idMessage = p2pTemplateCommandService.sendCommandSync(command);
        return ResponseEntity.ok().body(CreateTemplateResponse.builder()
                .id(idMessage)
                .template(templateModel.getTemplate())
                .build()
        );
    }

    @PostMapping(value = "/validateTemplate")
    public ResponseEntity<List<String>> validateTemplate(@Validated @RequestBody List<TemplateModel> templateModels) {
        log.info("P2PTemplateCommandResource validateTemplate templateModels: {}", templateModels);
        List<TemplateValidateError> templateValidateErrors = p2PValidationService.validateTemplate(templateModels.stream()
                .map(templateModel -> new Template()
                        .setId(templateModel.getId())
                        .setTemplate(templateModel.getTemplate().getBytes()))
                .collect(Collectors.toList()));
        log.info("P2PTemplateCommandResource validateTemplate result: {}", templateValidateErrors);
        return ResponseEntity.ok().body(templateValidateErrors.get(0).getReason());
    }

    @PostMapping(value = "/template/{id}/reference")
    public ResponseEntity<List<String>> insertReference(@PathVariable(value = "id") String id,
                                                        @Validated @RequestBody List<P2pReferenceModel> referenceModels) {
        log.info("P2pReferenceCommandResource insertReference referenceModels: {}", referenceModels);
        List<String> ids = referenceModels.stream()
                .map(reference -> convertReferenceModel(reference, id))
                .map(command -> command.setCommandType(CommandType.CREATE))
                .map(p2PTemplateReferenceService::sendCommandSync)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(ids);
    }

    @DeleteMapping(value = "/template")
    public ResponseEntity<String> removeTemplate(@Validated @RequestBody TemplateModel templateModel) {
        log.info("TemplateManagementResource removeTemplate templateModel: {}", templateModel);
        Command command = templateModelToCommandConverter.convert(templateModel);
        command.setCommandType(CommandType.DELETE);
        String idMessage = p2pTemplateCommandService.sendCommandSync(command);
        return ResponseEntity.ok().body(idMessage);
    }

    private Command convertReferenceModel(P2pReferenceModel referenceModel, String templateId) {
        Command command = referenceToCommandConverter.convert(referenceModel);
        command.getCommandBody().getP2pReference().setTemplateId(templateId);
        return command;
    }

    @DeleteMapping(value = "/template/{id}/reference")
    public ResponseEntity<List<String>> deleteReference(@PathVariable(value = "id") String id,
                                                        @Validated @RequestBody List<P2pReferenceModel> referenceModels) {
        log.info("P2pReferenceCommandResource insertReference referenceModels: {}", referenceModels);
        List<String> ids = referenceModels.stream()
                .map(reference -> convertReferenceModel(reference, id))
                .map(command -> command.setCommandType(CommandType.DELETE))
                .map(p2PTemplateReferenceService::sendCommandSync)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(ids);
    }

}
