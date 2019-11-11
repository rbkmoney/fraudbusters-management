package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.TemplateReference;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentReferenceModel;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
@RequiredArgsConstructor
public class CommandToReferenceModelConverter implements Converter<Command, PaymentReferenceModel> {

    @Override
    public PaymentReferenceModel convert(Command command) {
        PaymentReferenceModel model = new PaymentReferenceModel();
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
