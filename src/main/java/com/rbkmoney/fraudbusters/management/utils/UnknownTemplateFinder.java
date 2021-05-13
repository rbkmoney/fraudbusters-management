package com.rbkmoney.fraudbusters.management.utils;

import com.rbkmoney.fraudbusters.management.domain.ReferenceModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UnknownTemplateFinder {

    public List<String> findUnknownTemplate(List<? extends ReferenceModel> referenceModels,
                                            Predicate<ReferenceModel> filter) {
        return referenceModels.stream()
                .filter(t -> !filter.test(t))
                .map(ReferenceModel::getTemplateId)
                .collect(Collectors.toList());
    }

}
