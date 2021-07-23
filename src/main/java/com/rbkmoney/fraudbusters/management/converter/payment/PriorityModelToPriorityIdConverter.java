package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.fraudbusters.management.domain.PriorityIdModel;
import com.rbkmoney.swag.fraudbusters.management.model.PriorityId;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface PriorityModelToPriorityIdConverter {

    PriorityId destinationToSource(PriorityIdModel destination);

}
