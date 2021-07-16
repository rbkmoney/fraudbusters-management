package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.CommandBody;
import com.rbkmoney.damsel.fraudbusters.TemplateReference;
import com.rbkmoney.swag.fraudbusters.management.model.PaymentReference;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ReferenceToCommandConverter implements Converter<PaymentReference, Command> {

    @NonNull
    @Override
    public Command convert(PaymentReference referenceModel) {
        var reference = new TemplateReference()
                .setShopId(referenceModel.getShopId())
                .setPartyId(referenceModel.getPartyId())
                .setTemplateId(referenceModel.getTemplateId());
        return new Command()
                .setCommandBody(CommandBody.reference(reference));
    }

}
