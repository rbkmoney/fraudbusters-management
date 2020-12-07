package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.damsel.fraudbusters.*;
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
import com.rbkmoney.fraudbusters.management.utils.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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
    private final UserInfoService userInfoService;

    @PostMapping(value = "/template")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<CreateTemplateResponse> insertTemplate(Principal principal,
                                                                 @Validated @RequestBody TemplateModel templateModel) {
        log.info("TemplateManagementResource insertTemplate initiator: {} templateModel: {}", userInfoService.getUserName(principal), templateModel);
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
        command.setUserInfo(new UserInfo()
                .setUserId(userInfoService.getUserName(principal)));
        String idMessage = paymentTemplateCommandService.sendCommandSync(command);
        return ResponseEntity.ok().body(CreateTemplateResponse.builder()
                .id(idMessage)
                .template(templateModel.getTemplate())
                .build()
        );
    }

    @PostMapping(value = "/template/validate")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<ValidateTemplatesResponse> validateTemplate(Principal principal,
                                                                      @Validated @RequestBody TemplateModel templateModel) {
        log.info("TemplateManagementResource validateTemplate initiator: {} templateModel: {}", userInfoService.getUserName(principal), templateModel);
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

    @PostMapping(value = "/template/references")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<List<String>> insertReferences(Principal principal,
                                                         @Validated @RequestBody List<PaymentReferenceModel> referenceModels) {
        log.info("TemplateManagementResource insertReference initiator: {} referenceModels: {}",
                userInfoService.getUserName(principal),
                referenceModels);
        List<String> ids = referenceModels.stream()
                .map(referenceToCommandConverter::convert)
                .map(command -> {
                    command.setCommandType(CommandType.CREATE);
                    command.setUserInfo(new UserInfo()
                            .setUserId(userInfoService.getUserName(principal)));
                    return command;
                })
                .map(paymentTemplateReferenceService::sendCommandSync)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(ids);
    }

    //todo подумать над рефакторингом
    @PostMapping(value = "/template/{id}/default")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> markReferenceAsDefault(Principal principal,
                                                         @PathVariable(value = "id") String id) {
        log.info("TemplateManagementResource markReferenceAsDefault initiator: {} id: {}", userInfoService.getUserName(principal), id);
        referenceDao.markReferenceAsDefault(id);
        return ResponseEntity.ok().body(id);
    }

    @Deprecated(forRemoval = true)
    @PostMapping(value = "/template/default")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<List<String>> insertDefaultReference(Principal principal,
                                                               @Validated @RequestBody List<PaymentReferenceModel> referenceModels) {
        log.info("TemplateManagementResource insertDefaultReference initiator: {} referenceModels: {}",
                userInfoService.getUserName(principal),
                referenceModels);
        PaymentReferenceModel defaultReference = referenceDao.getDefaultReference();
        if (defaultReference == null) {
            throw new NotFoundException("Couldn't insert default reference: Default template not found");
        }
        String id = defaultReference.getTemplateId();
        List<String> ids = referenceModels.stream()
                .map(reference -> convertReferenceModel(reference, id))
                .map(command -> {
                    command.setCommandType(CommandType.CREATE);
                    command.setUserInfo(new UserInfo()
                            .setUserId(userInfoService.getUserName(principal)));
                    return command;
                })
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
    public ResponseEntity<String> removeTemplate(Principal principal, @PathVariable(value = "id") String id) {
        log.info("TemplateManagementResource removeTemplate initiator: {} id: {}", userInfoService.getUserName(principal), id);
        Command command = paymentTemplateCommandService.createTemplateCommandById(id);
        command.setCommandType(CommandType.DELETE);
        command.setUserInfo(new UserInfo()
                .setUserId(userInfoService.getUserName(principal)));
        String idMessage = paymentTemplateCommandService.sendCommandSync(command);
        return ResponseEntity.ok().body(idMessage);
    }

    @DeleteMapping(value = "/template/{templateId}/reference")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> deleteReference(Principal principal,
                                                  @PathVariable String templateId,
                                                  @RequestParam String partyId,
                                                  @RequestParam String shopId) {
        log.info("TemplateManagementResource deleteReference initiator: {} templateId: {}, partyId: {}, shopId: {}",
                userInfoService.getUserName(principal), templateId, partyId, shopId);
        Command command = paymentTemplateReferenceService.createReferenceCommandByIds(templateId, partyId, shopId);
        command.setCommandType(CommandType.DELETE);
        command.setUserInfo(new UserInfo()
                .setUserId(userInfoService.getUserName(principal)));
        String id = paymentTemplateReferenceService.sendCommandSync(command);
        return ResponseEntity.ok().body(id);
    }

}
