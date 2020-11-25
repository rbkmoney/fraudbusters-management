package com.rbkmoney.fraudbusters.management.resource.p2p;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.CommandType;
import com.rbkmoney.damsel.fraudbusters.Template;
import com.rbkmoney.damsel.fraudbusters.TemplateValidateError;
import com.rbkmoney.fraudbusters.management.converter.TemplateModelToCommandConverter;
import com.rbkmoney.fraudbusters.management.converter.p2p.P2pReferenceToCommandConverter;
import com.rbkmoney.fraudbusters.management.domain.ErrorTemplateModel;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.response.CreateTemplateResponse;
import com.rbkmoney.fraudbusters.management.domain.response.ValidateTemplatesResponse;
import com.rbkmoney.fraudbusters.management.service.TemplateCommandService;
import com.rbkmoney.fraudbusters.management.service.ValidationTemplateService;
import com.rbkmoney.fraudbusters.management.service.p2p.P2PTemplateReferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
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
    @PreAuthorize("hasAnyAuthority('fraud-officer')")
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

    @PostMapping(value = "/template/validate")
    @PreAuthorize("hasAnyAuthority('fraud-officer')")
    public ResponseEntity<ValidateTemplatesResponse> validateTemplate(@Validated @RequestBody List<TemplateModel> templateModels) {
        log.info("P2PTemplateCommandResource validateTemplate templateModels: {}", templateModels);
        List<TemplateValidateError> templateValidateErrors = p2PValidationService.validateTemplate(templateModels.stream()
                .map(templateModel -> new Template()
                        .setId(templateModel.getId())
                        .setTemplate(templateModel.getTemplate().getBytes()))
                .collect(Collectors.toList())
        );
        log.info("P2PTemplateCommandResource validateTemplate result: {}", templateValidateErrors);
        return ResponseEntity.ok().body(
                ValidateTemplatesResponse.builder()
                        .validateResults(templateValidateErrors.stream()
                                .map(templateValidateError -> ErrorTemplateModel.builder()
                                        .errors(templateValidateError.getReason())
                                        .id(templateValidateError.id).build())
                                .collect(Collectors.toList()))
                        .build()
        );
    }

    @PostMapping(value = "/template/{id}/references")
    @PreAuthorize("hasAnyAuthority('fraud-officer')")
    public ResponseEntity<List<String>> insertReferences(@PathVariable(value = "id") String id,
                                                        @Validated @RequestBody List<P2pReferenceModel> referenceModels) {
        log.info("P2pReferenceCommandResource insertReference referenceModels: {}", referenceModels);
        List<String> ids = referenceModels.stream()
                .map(reference -> convertReferenceModel(reference, id))
                .map(command -> command.setCommandType(CommandType.CREATE))
                .map(p2PTemplateReferenceService::sendCommandSync)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(ids);
    }


    @PostMapping(value = "/template/{id}/reference")
    @PreAuthorize("hasAnyAuthority('fraud-officer')")
    public ResponseEntity<String> insertReference(@PathVariable(value = "id") String id,
                                                  @Validated @RequestBody P2pReferenceModel referenceModel) {
        log.info("TemplateManagementResource insertReference referenceModel: {}", referenceModel);
        String referenceId = Optional.of(referenceModel)
                .map(reference -> convertReferenceModel(reference, id))
                .map(command -> command.setCommandType(CommandType.CREATE))
                .map(p2PTemplateReferenceService::sendCommandSync)
                .orElseThrow();
        return ResponseEntity.ok().body(referenceId);
    }

    @DeleteMapping(value = "/template/{id}")
    @PreAuthorize("hasAnyAuthority('fraud-officer')")
    public ResponseEntity<String> removeTemplate(@PathVariable(value = "id") String id) {
        log.info("TemplateManagementResource removeTemplate id: {}", id);
        Command command = p2pTemplateCommandService.createTemplateCommandById(id);
        command.setCommandType(CommandType.DELETE);
        String idMessage = p2pTemplateCommandService.sendCommandSync(command);
        return ResponseEntity.ok().body(idMessage);
    }

    private Command convertReferenceModel(P2pReferenceModel referenceModel, String templateId) {
        Command command = referenceToCommandConverter.convert(referenceModel);
        command.getCommandBody().getP2pReference().setTemplateId(templateId);
        return command;
    }

    @DeleteMapping(value = "/template/{id}/references")
    @PreAuthorize("hasAnyAuthority('fraud-officer')")
    public ResponseEntity<List<String>> deleteReferences(@PathVariable(value = "id") String id,
                                                        @Validated @RequestBody List<P2pReferenceModel> referenceModels) {
        log.info("P2pReferenceCommandResource deleteReferences referenceModels: {}", referenceModels);
        List<String> ids = referenceModels.stream()
                .map(reference -> convertReferenceModel(reference, id))
                .map(command -> command.setCommandType(CommandType.DELETE))
                .map(p2PTemplateReferenceService::sendCommandSync)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(ids);
    }


    @DeleteMapping(value = "/template/{templateId}/reference")
    @PreAuthorize("hasAnyAuthority('fraud-officer')")
    public ResponseEntity<String> deleteReference(@PathVariable String templateId,
                                                  @RequestParam String identityId) {
        log.info("TemplateManagementResource deleteReference templateId: {}, identityId: {}", templateId, identityId);
        Command command = p2PTemplateReferenceService.createReferenceCommandByIds(templateId, identityId);
        command.setCommandType(CommandType.DELETE);
        String id = p2PTemplateReferenceService.sendCommandSync(command);
        return ResponseEntity.ok().body(id);
    }

}
