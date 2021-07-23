package com.rbkmoney.fraudbusters.management.service.payment;

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
import com.rbkmoney.fraudbusters.management.utils.FilterRequestUtils;
import com.rbkmoney.swag.fraudbusters.management.model.CreateTemplateResponse;
import com.rbkmoney.swag.fraudbusters.management.model.Template;
import com.rbkmoney.swag.fraudbusters.management.model.TemplatesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentsTemplatesService {

    private final TemplateDao paymentTemplateDao;
    private final TemplateModelToTemplateConverter templateModelToTemplateConverter;
    private final TemplateToCommandConverter templateModelToCommandConverter;
    private final TemplateCommandService paymentTemplateCommandService;
    private final ValidationTemplateService paymentValidationService;

    public TemplatesResponse filterTemplates(FilterRequest filterRequest) {
        filterRequest.setSearchValue(FilterRequestUtils.prepareSearchValue(filterRequest.getSearchValue()));
        List<TemplateModel> templateModels = paymentTemplateDao.filterModel(filterRequest);
        Integer count = paymentTemplateDao.countFilterModel(filterRequest.getSearchValue());
        return new TemplatesResponse()
                .count(count)
                .result(templateModels.stream()
                        .map(templateModelToTemplateConverter::destinationToSource)
                        .collect(Collectors.toList())
                );
    }


    public CreateTemplateResponse createTemplate(Template template, String initiator) {
        var command = templateModelToCommandConverter.convert(template);
        List<TemplateValidateError> templateValidateErrors = paymentValidationService.validateTemplate(
                command.getCommandBody().getTemplate()
        );
        if (!CollectionUtils.isEmpty(templateValidateErrors)) {
            return new CreateTemplateResponse()
                    .template(template.getTemplate())
                    .errors(templateValidateErrors.get(0).getReason());
        }
        command.setCommandType(CommandType.CREATE);
        command.setUserInfo(new UserInfo()
                .setUserId(initiator));
        String idMessage = paymentTemplateCommandService.sendCommandSync(command);
        return new CreateTemplateResponse()
                .id(idMessage)
                .template(template.getTemplate());
    }
}
