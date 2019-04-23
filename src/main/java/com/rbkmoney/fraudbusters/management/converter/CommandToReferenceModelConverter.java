package com.rbkmoney.fraudbusters.management.converter;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.TemplateReference;
import com.rbkmoney.fraudbusters.management.domain.ReferenceModel;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
@RequiredArgsConstructor
public class CommandToReferenceModelConverter implements Converter<Command, ReferenceModel> {

    @Override
    public ReferenceModel convert(Command command) {
        ReferenceModel model = new ReferenceModel();
        TemplateReference templateReference = command.getCommandBody().getReference();
        String uid = UUID.randomUUID().toString();
        model.setId(uid);
        model.setIsGlobal(templateReference.is_global);
        model.setPartyId(templateReference.party_id);
        model.setShopId(templateReference.shop_id);
        model.setTemplateId(templateReference.template_id);
        return model;
    }
}
