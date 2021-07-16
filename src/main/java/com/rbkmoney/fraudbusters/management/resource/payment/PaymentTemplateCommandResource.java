package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.damsel.fraudbusters.CommandType;
import com.rbkmoney.damsel.fraudbusters.TemplateValidateError;
import com.rbkmoney.damsel.fraudbusters.UserInfo;
import com.rbkmoney.fraudbusters.management.converter.payment.TemplateModelToTemplateConverter;
import com.rbkmoney.fraudbusters.management.converter.payment.TemplateToCommandConverter;
import com.rbkmoney.fraudbusters.management.dao.TemplateDao;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.service.TemplateCommandService;
import com.rbkmoney.fraudbusters.management.service.iface.ValidationTemplateService;
import com.rbkmoney.fraudbusters.management.utils.CommandMapper;
import com.rbkmoney.fraudbusters.management.utils.FilterRequestUtils;
import com.rbkmoney.fraudbusters.management.utils.PagingDataUtils;
import com.rbkmoney.fraudbusters.management.utils.UserInfoService;
import com.rbkmoney.swag.fraudbusters.management.api.PaymentsTemplatesApi;
import com.rbkmoney.swag.fraudbusters.management.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentTemplateCommandResource implements PaymentsTemplatesApi {

    private final TemplateCommandService paymentTemplateCommandService;
    private final TemplateToCommandConverter templateModelToCommandConverter;
    private final ValidationTemplateService paymentValidationService;
    private final TemplateDao paymentTemplateDao;
    private final UserInfoService userInfoService;
    private final CommandMapper commandMapper;
    private final TemplateModelToTemplateConverter templateModelToTemplateConverter;

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<TemplatesResponse> filterTemplates(@Valid String lastId, @Valid String sortOrder,
                                                             @Valid String searchValue, @Valid String sortBy,
                                                             @Valid String sortFieldValue, @Valid Integer size) {
        var filterRequest = new FilterRequest(searchValue, lastId, sortFieldValue, size, sortBy,
                PagingDataUtils.getSortOrder(sortOrder));
        String userName = userInfoService.getUserName();
        log.info("filterTemplates initiator: {} filterRequest: {}", userName, filterRequest);
        FilterRequestUtils.prepareFilterRequest(filterRequest);
        List<TemplateModel> templateModels = paymentTemplateDao.filterModel(filterRequest);
        Integer count = paymentTemplateDao.countFilterModel(filterRequest.getSearchValue());
        return ResponseEntity.ok().body(new TemplatesResponse()
                .count(count)
                .result(templateModels.stream()
                        .map(templateModelToTemplateConverter::destinationToSource)
                        .collect(Collectors.toList())
                ));
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<ListResponse> getTemplateNames(@Valid String regexpName) {
        log.info("getTemplatesName initiator: {} regexpName: {}", userInfoService.getUserName(), regexpName);
        List<String> list = paymentTemplateDao.getListNames(regexpName);
        return ResponseEntity.ok().body(new ListResponse()
                .result(list));
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<CreateTemplateResponse> insertTemplate(
            com.rbkmoney.swag.fraudbusters.management.model.@Valid Template template) {
        String userName = userInfoService.getUserName();
        log.info("insertTemplate initiator: {} templateModel: {}", userName,
                template);
        var command = templateModelToCommandConverter.convert(template);
        List<TemplateValidateError> templateValidateErrors = paymentValidationService.validateTemplate(
                command.getCommandBody().getTemplate()
        );
        if (!CollectionUtils.isEmpty(templateValidateErrors)) {
            return ResponseEntity.badRequest().body(new CreateTemplateResponse()
                    .template(template.getTemplate())
                    .errors(templateValidateErrors.get(0).getReason()));
        }
        command.setCommandType(CommandType.CREATE);
        command.setUserInfo(new UserInfo()
                .setUserId(userName));
        String idMessage = paymentTemplateCommandService.sendCommandSync(command);
        return ResponseEntity.ok().body(new CreateTemplateResponse()
                .id(idMessage)
                .template(template.getTemplate())
        );
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> removeTemplate(String id) {
        String userName = userInfoService.getUserName();
        log.info("removeTemplate initiator: {} id: {}", userName, id);
        var command = paymentTemplateCommandService.createTemplateCommandById(id);
        String messageId = paymentTemplateCommandService
                .sendCommandSync(commandMapper.mapToConcreteCommand(userName, command, CommandType.DELETE));
        return ResponseEntity.ok().body(messageId);
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<ValidateTemplatesResponse> validateTemplate(@Valid Template template) {
        log.info("validateTemplate initiator: {} templateModel: {}", userInfoService.getUserName(),
                template);
        List<TemplateValidateError> templateValidateErrors = paymentValidationService.validateTemplate(
                new com.rbkmoney.damsel.fraudbusters.Template()
                        .setId(template.getId())
                        .setTemplate(template.getTemplate().getBytes()));
        log.info("validateTemplate result: {}", templateValidateErrors);
        return ResponseEntity.ok().body(new ValidateTemplatesResponse()
                .validateResults(templateValidateErrors.stream()
                        .map(templateValidateError -> new ErrorTemplate()
                                .errors(templateValidateError.getReason())
                                .id(templateValidateError.id))
                        .collect(Collectors.toList()))
        );
    }
}
