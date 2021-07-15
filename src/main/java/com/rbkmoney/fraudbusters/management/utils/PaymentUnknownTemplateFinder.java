package com.rbkmoney.fraudbusters.management.utils;

import com.rbkmoney.swag.fraudbusters.management.model.PaymentReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PaymentUnknownTemplateFinder {

    public List<String> findUnknownTemplate(List<PaymentReference> referenceModels,
                                            Predicate<PaymentReference> filter) {
        return referenceModels.stream()
                .filter(t -> !filter.test(t))
                .map(PaymentReference::getTemplateId)
                .collect(Collectors.toList());
    }

}
