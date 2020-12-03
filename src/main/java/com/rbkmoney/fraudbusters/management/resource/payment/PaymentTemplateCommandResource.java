package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.CommandType;
import com.rbkmoney.damsel.fraudbusters.Template;
import com.rbkmoney.damsel.fraudbusters.TemplateValidateError;
import com.rbkmoney.fraudbusters.management.converter.TemplateModelToCommandConverter;
import com.rbkmoney.fraudbusters.management.converter.payment.ReferenceToCommandConverter;
import com.rbkmoney.fraudbusters.management.dao.payment.reference.PaymentReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.ErrorTemplateModel;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.response.CreateTemplateResponse;
import com.rbkmoney.fraudbusters.management.domain.response.ValidateTemplatesResponse;
import com.rbkmoney.fraudbusters.management.exception.NotFoundException;
import com.rbkmoney.fraudbusters.management.service.TemplateCommandService;
import com.rbkmoney.fraudbusters.management.service.ValidationTemplateService;
import com.rbkmoney.fraudbusters.management.service.payment.PaymentTemplateReferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentTemplateCommandResource {

    private final TemplateCommandService paymentTemplateCommandService;
    private final PaymentTemplateReferenceService paymentTemplateReferenceService;
    private final TemplateModelToCommandConverter templateModelToCommandConverter;
    private final ReferenceToCommandConverter referenceToCommandConverter;
    private final PaymentReferenceDao referenceDao;
    private final ValidationTemplateService paymentValidationService;

    @PostMapping(value = "/template")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<CreateTemplateResponse> insertTemplate(@Validated @RequestBody TemplateModel templateModel) {
        log.info("TemplateManagementResource insertTemplate templateModel: {}", templateModel);
        Command command = templateModelToCommandConverter.convert(templateModel);
        List<TemplateValidateError> templateValidateErrors = paymentValidationService.validateTemplate(
                command.getCommandBody().getTemplate()
        );
        if (!CollectionUtils.isEmpty(templateValidateErrors)) {
            return ResponseEntity.badRequest().body(CreateTemplateResponse.builder()
                    .template(templateModel.getTemplate())
                    .errors(templateValidateErrors.get(0).getReason())
                    .build());
        }
        command.setCommandType(CommandType.CREATE);
        String idMessage = paymentTemplateCommandService.sendCommandSync(command);
        return ResponseEntity.ok().body(CreateTemplateResponse.builder()
                .id(idMessage)
                .template(templateModel.getTemplate())
                .build()
        );
    }

    @PostMapping(value = "/template/validate")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<ValidateTemplatesResponse> validateTemplate(@Validated @RequestBody TemplateModel templateModel) {
        log.info("TemplateManagementResource validateTemplate templateModel: {}", templateModel);
        List<TemplateValidateError> templateValidateErrors = paymentValidationService.validateTemplate(new Template()
                .setId(templateModel.getId())
                .setTemplate(templateModel.getTemplate().getBytes()));
        log.info("TemplateManagementResource validateTemplate result: {}", templateValidateErrors);
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

    //todo посмотреть используем ли
    @PostMapping(value = "/template/references")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<List<String>> insertReferences(@Validated @RequestBody List<PaymentReferenceModel> referenceModels) {
        log.info("TemplateManagementResource insertReference referenceModels: {}", referenceModels);
        List<String> ids = referenceModels.stream()
                .map(referenceToCommandConverter::convert)
                .map(command -> command.setCommandType(CommandType.CREATE))
                .map(paymentTemplateReferenceService::sendCommandSync)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(ids);
    }

    @PostMapping(value = "/template/reference")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> insertReference(@Validated @RequestBody PaymentReferenceModel referenceModel) {
        log.info("TemplateManagementResource insertReference referenceModel: {}", referenceModel);
        Command command = referenceToCommandConverter.convert(referenceModel);
        command.setCommandType(CommandType.CREATE);
        String referenceId = paymentTemplateReferenceService.sendCommandSync(command);
        return ResponseEntity.ok().body(referenceId);
    }

    //todo подумать над рефакторингом
    @PostMapping(value = "/template/{id}/default")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> markReferenceAsDefault(@PathVariable(value = "id") String id) {
        log.info("TemplateManagementResource markReferenceAsDefault id: {}", id);
        referenceDao.markReferenceAsDefault(id);
        return ResponseEntity.ok().body(id);
    }

    @Deprecated(forRemoval = true)
    @PostMapping(value = "/template/default")
    @PreAuthorize("hasAnyRole('fraud-officer')")
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
                .map(paymentTemplateReferenceService::sendCommandSync)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(ids);
    }

    private Command convertReferenceModel(PaymentReferenceModel referenceModel, String templateId) {
        Command command = referenceToCommandConverter.convert(referenceModel);
        command.getCommandBody().getReference().setTemplateId(templateId);
        return command;
    }

    @DeleteMapping(value = "/template/{id}")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> removeTemplate(@PathVariable(value = "id") String id) {
        log.info("TemplateManagementResource removeTemplate id: {}", id);
        Command command = paymentTemplateCommandService.createTemplateCommandById(id);
        command.setCommandType(CommandType.DELETE);
        String idMessage = paymentTemplateCommandService.sendCommandSync(command);
        return ResponseEntity.ok().body(idMessage);
    }

    //todo проверить использование
    @Deprecated(forRemoval = true)
    @PostMapping(value = "/template/{id}/reference/delete")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<List<String>> deleteReferences(@PathVariable(value = "id") String id,
                                                         @Validated @RequestBody List<PaymentReferenceModel> referenceModels) {
        log.info("TemplateManagementResource deleteReferences referenceModels: {}", referenceModels);
        List<String> ids = referenceModels.stream()
                .map(reference -> convertReferenceModel(reference, id))
                .map(command -> command.setCommandType(CommandType.DELETE))
                .map(paymentTemplateReferenceService::sendCommandSync)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(ids);
    }

    @DeleteMapping(value = "/template/{templateId}/reference")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> deleteReference(@PathVariable String templateId,
                                                  @RequestParam String partyId,
                                                  @RequestParam String shopId) {
        log.info("TemplateManagementResource deleteReference templateId: {}, partyId: {}, shopId: {}", templateId, partyId, shopId);
        Command command = paymentTemplateReferenceService.createReferenceCommandByIds(templateId, partyId, shopId);
        command.setCommandType(CommandType.DELETE);
        String id = paymentTemplateReferenceService.sendCommandSync(command);
        return ResponseEntity.ok().body(id);
    }

}
