package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.damsel.fraudbusters.TemplateValidateError;
import com.rbkmoney.swag.fraudbusters.management.model.ErrorTemplate;
import com.rbkmoney.swag.fraudbusters.management.model.ValidateTemplatesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TemplateValidateErrorsToValidateTemplateResponseConverter
        implements Converter<List<TemplateValidateError>, ValidateTemplatesResponse> {

    @Override
    public ValidateTemplatesResponse convert(List<TemplateValidateError> templateValidateErrors) {
        return new ValidateTemplatesResponse()
                .validateResults(templateValidateErrors.stream()
                        .map(templateValidateError -> new ErrorTemplate()
                                .errors(templateValidateError.getReason())
                                .id(templateValidateError.id))
                        .collect(Collectors.toList()));
    }
}
