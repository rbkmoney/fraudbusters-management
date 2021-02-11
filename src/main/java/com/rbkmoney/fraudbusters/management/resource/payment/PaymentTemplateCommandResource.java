package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.fraudbusters.management.converter.TemplateModelToCommandConverter;
import com.rbkmoney.fraudbusters.management.converter.payment.ReferenceToCommandConverter;
import com.rbkmoney.fraudbusters.management.dao.payment.DefaultPaymentReferenceDaoImpl;
import com.rbkmoney.fraudbusters.management.dao.payment.reference.PaymentReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.ErrorTemplateModel;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import com.rbkmoney.fraudbusters.management.domain.payment.DefaultPaymentReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.response.CreateTemplateResponse;
import com.rbkmoney.fraudbusters.management.domain.response.ValidateTemplatesResponse;
import com.rbkmoney.fraudbusters.management.service.TemplateCommandService;
import com.rbkmoney.fraudbusters.management.service.iface.ValidationTemplateService;
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
import java.util.UUID;
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
    private final DefaultPaymentReferenceDaoImpl defaultReferenceDao;
    private final ValidationTemplateService paymentValidationService;
    private final UserInfoService userInfoService;

    @PostMapping(value = "/template")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<CreateTemplateResponse> insertTemplate(Principal principal,
                                                                 @Validated @RequestBody TemplateModel templateModel) {
        log.info("insertTemplate initiator: {} templateModel: {}", userInfoService.getUserName(principal), templateModel);
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
        log.info("validateTemplate initiator: {} templateModel: {}", userInfoService.getUserName(principal), templateModel);
        List<TemplateValidateError> templateValidateErrors = paymentValidationService.validateTemplate(new Template()
                .setId(templateModel.getId())
                .setTemplate(templateModel.getTemplate().getBytes()));
        log.info("validateTemplate result: {}", templateValidateErrors);
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
        log.info("insertReference initiator: {} referenceModels: {}", userInfoService.getUserName(principal), referenceModels);
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

    @PostMapping(value = "/template/{id}/default")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> insertDefaultReference(Principal principal,
                                                         @Validated @RequestBody DefaultPaymentReferenceModel referenceModel) {
        log.info("insertDefaultReference initiator: {} referenceModels: {}", userInfoService.getUserName(principal), referenceModel);
        String uid = UUID.randomUUID().toString();
        referenceModel.setId(uid);
        referenceModel.setModifiedByUser(userInfoService.getUserName(principal));
        defaultReferenceDao.insert(referenceModel);
        return ResponseEntity.ok().body(uid);
    }

    @DeleteMapping(value = "/template/{id}/default")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> removeDefaultReference(Principal principal, @PathVariable(value = "id") String id) {
        log.info("removeDefaultReference initiator: {} id: {}", userInfoService.getUserName(principal), id);
        defaultReferenceDao.remove(id);
        return ResponseEntity.ok().body(id);
    }

    @DeleteMapping(value = "/template/{id}")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> removeTemplate(Principal principal, @PathVariable(value = "id") String id) {
        log.info("removeTemplate initiator: {} id: {}", userInfoService.getUserName(principal), id);
        Command command = paymentTemplateCommandService.createTemplateCommandById(id);
        command.setCommandType(CommandType.DELETE);
        command.setUserInfo(new UserInfo()
                .setUserId(userInfoService.getUserName(principal)));
        String idMessage = paymentTemplateCommandService.sendCommandSync(command);
        return ResponseEntity.ok().body(idMessage);
    }

    @DeleteMapping(value = "/template/{templateId}/reference/{id}")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> removeReference(Principal principal, @PathVariable(value = "id") String id) {
        log.info("removeReference initiator: {} id: {}", userInfoService.getUserName(principal), id);
        PaymentReferenceModel reference = referenceDao.getById(id);
        final Command command = referenceToCommandConverter.convert(reference);
        command.setCommandType(CommandType.DELETE);
        command.setUserInfo(new UserInfo()
                .setUserId(userInfoService.getUserName(principal)));
        String commandSendDeletedId = paymentTemplateReferenceService.sendCommandSync(command);
        return ResponseEntity.ok().body(commandSendDeletedId);
    }

}
