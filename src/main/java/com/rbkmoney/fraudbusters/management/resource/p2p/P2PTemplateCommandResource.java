package com.rbkmoney.fraudbusters.management.resource.p2p;

import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.fraudbusters.management.converter.p2p.P2pReferenceToCommandConverter;
import com.rbkmoney.fraudbusters.management.converter.p2p.TemplateModelToCommandConverter;
import com.rbkmoney.fraudbusters.management.dao.p2p.DefaultP2pReferenceDaoImpl;
import com.rbkmoney.fraudbusters.management.domain.ErrorTemplateModel;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import com.rbkmoney.fraudbusters.management.domain.p2p.DefaultP2pReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.response.CreateTemplateResponse;
import com.rbkmoney.fraudbusters.management.domain.response.ValidateTemplatesResponse;
import com.rbkmoney.fraudbusters.management.filter.UnknownP2pTemplateInReferenceFilter;
import com.rbkmoney.fraudbusters.management.service.TemplateCommandService;
import com.rbkmoney.fraudbusters.management.service.iface.ValidationTemplateService;
import com.rbkmoney.fraudbusters.management.service.p2p.P2PTemplateReferenceService;
import com.rbkmoney.fraudbusters.management.utils.CommandMapper;
import com.rbkmoney.fraudbusters.management.utils.UnknownTemplateFinder;
import com.rbkmoney.fraudbusters.management.utils.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/p2p")
@RequiredArgsConstructor
public class P2PTemplateCommandResource {

    private final TemplateCommandService p2pTemplateCommandService;
    private final P2PTemplateReferenceService p2PTemplateReferenceService;
    private final TemplateModelToCommandConverter templateModelToCommandConverter;
    private final P2pReferenceToCommandConverter referenceToCommandConverter;
    private final ValidationTemplateService p2PValidationService;
    private final UserInfoService userInfoService;
    private final DefaultP2pReferenceDaoImpl defaultReferenceDao;
    private final UnknownP2pTemplateInReferenceFilter templateInReferenceFilter;
    private final UnknownTemplateFinder unknownTemplateFinder;
    private final CommandMapper commandMapper;

    @PostMapping(value = "/template")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<CreateTemplateResponse> insertTemplate(
            Principal principal, @Validated @RequestBody TemplateModel templateModel) {
        log.info("P2pReferenceCommandResource insertTemplate userName: {} templateModel: {}",
                userInfoService.getUserName(principal), templateModel);
        Command command = templateModelToCommandConverter.convert(templateModel);
        List<TemplateValidateError> templateValidateErrors = p2PValidationService.validateTemplate(
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
        String idMessage = p2pTemplateCommandService.sendCommandSync(command);
        return ResponseEntity.ok().body(CreateTemplateResponse.builder()
                .id(idMessage)
                .template(templateModel.getTemplate())
                .build()
        );
    }

    @PostMapping(value = "/template/validate")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<ValidateTemplatesResponse> validateTemplate(
            Principal principal, @Validated @RequestBody TemplateModel templateModel) {
        log.info("P2PTemplateCommandResource validateTemplate userName: {} templateModel: {}",
                userInfoService.getUserName(principal), templateModel);
        List<TemplateValidateError> templateValidateErrors = p2PValidationService.validateTemplate(new Template()
                .setId(templateModel.getId())
                .setTemplate(templateModel.getTemplate().getBytes()));
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
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<List<String>> insertReferences(
            Principal principal,
            @PathVariable(value = "id") String id,
            @Validated @RequestBody List<P2pReferenceModel> referenceModels) {
        String userName = userInfoService.getUserName(principal);
        log.info("P2pReferenceCommandResource insertReference userName: {} referenceModels: {}",
                userName, referenceModels);
        List<String> unknownTemplates =
                unknownTemplateFinder.findUnknownTemplate(referenceModels, templateInReferenceFilter);
        if (!CollectionUtils.isEmpty(unknownTemplates)) {
            return new ResponseEntity<>(unknownTemplates, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        List<String> ids = referenceModels.stream()
                .map(reference -> convertReferenceModel(reference, id))
                .map(command -> commandMapper.mapToConcreteCommand(userName, command, CommandType.CREATE))
                .map(p2PTemplateReferenceService::sendCommandSync)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(ids);
    }

    @PostMapping(value = "/template/default-references")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> insertDefaultReference(
            Principal principal, @Validated @RequestBody DefaultP2pReferenceModel referenceModel) {
        log.info("insertDefaultReference initiator: {} referenceModels: {}", userInfoService.getUserName(principal),
                referenceModel);
        String uid = UUID.randomUUID().toString();
        referenceModel.setId(uid);
        referenceModel.setModifiedByUser(userInfoService.getUserName(principal));
        defaultReferenceDao.insert(referenceModel);
        return ResponseEntity.ok().body(uid);
    }

    @DeleteMapping(value = "/template/default-references/{id}")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> removeDefaultReference(Principal principal, @PathVariable(value = "id") String id) {
        log.info("removeDefaultReference initiator: {} id: {}", userInfoService.getUserName(principal), id);
        defaultReferenceDao.remove(id);
        return ResponseEntity.ok().body(id);
    }

    @DeleteMapping(value = "/template/{id}")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> removeTemplate(Principal principal, @PathVariable(value = "id") String id) {
        String userName = userInfoService.getUserName(principal);
        log.info("TemplateManagementResource removeTemplate initiator: {} id: {}",
                userName, id);
        Command command = p2pTemplateCommandService.createTemplateCommandById(id);
        String idMessage = p2pTemplateCommandService
                .sendCommandSync(commandMapper.mapToConcreteCommand(userName, command, CommandType.DELETE));
        return ResponseEntity.ok().body(idMessage);
    }

    private Command convertReferenceModel(P2pReferenceModel referenceModel, String templateId) {
        Command command = referenceToCommandConverter.convert(referenceModel);
        command.getCommandBody().getP2pReference().setTemplateId(templateId);
        return command;
    }

    @DeleteMapping(value = "/template/{templateId}/reference")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> deleteReference(Principal principal,
                                                  @PathVariable String templateId,
                                                  @RequestParam String identityId) {
        String userName = userInfoService.getUserName(principal);
        log.info("TemplateManagementResource deleteReference initiator: {}  templateId: {}, identityId: {}",
                userName, templateId, identityId);
        Command command = p2PTemplateReferenceService.createReferenceCommandByIds(templateId, identityId);
        String id = p2PTemplateReferenceService
                .sendCommandSync(commandMapper.mapToConcreteCommand(userName, command, CommandType.DELETE));
        return ResponseEntity.ok().body(id);
    }

}
