package com.rbkmoney.fraudbusters.management.resource.notificator.converter;


import com.rbkmoney.swag.fraudbusters.management.model.ValidationError;
import com.rbkmoney.swag.fraudbusters.management.model.ValidationResponse;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ValidationConverter
        implements Converter<com.rbkmoney.damsel.fraudbusters_notificator.ValidationResponse, ValidationResponse> {

    @Override
    public ValidationResponse convert(
            com.rbkmoney.damsel.fraudbusters_notificator.ValidationResponse validationResponse) {
        ValidationResponse response = new ValidationResponse();
        if (validationResponse.isSetErrors()) {
            List<String> errors = validationResponse.getErrors();
            List<ValidationError> validationErrors = errors.stream()
                    .map(error -> new ValidationError().errorReason(error))
                    .collect(Collectors.toList());
            response.setErrors(validationErrors);
        }
        if (validationResponse.isSetResult()) {
            response.setResult(validationResponse.getResult());
        }
        return response;
    }
}
