package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.fraudbusters.management.domain.PriorityIdModel;
import com.rbkmoney.fraudbusters.management.utils.DateTimeUtils;
import com.rbkmoney.swag.fraudbusters.management.model.PriorityId;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class PriorityModelToPriorityIdConverter implements Converter<PriorityIdModel, PriorityId> {

    @NonNull
    @Override
    public PriorityId convert(PriorityIdModel priorityIdModel) {
        return new PriorityId()
                .id(priorityIdModel.getId())
                .priority(priorityIdModel.getPriority())
                .lastUpdateTime(DateTimeUtils.toDate(priorityIdModel.getLastUpdateTime()));
    }

}
