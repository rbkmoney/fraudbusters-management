package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.CommandBody;
import com.rbkmoney.damsel.fraudbusters.TemplateReference;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentReferenceModel;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ReferenceToCommandConverter implements Converter<PaymentReferenceModel, Command> {

    @Override
    public Command convert(PaymentReferenceModel referenceModel) {
        Command command = new Command();
        TemplateReference reference = new TemplateReference();
        reference.setIsGlobal(referenceModel.getIsGlobal());
        if (!referenceModel.getIsGlobal()) {
            reference.setShopId(referenceModel.getShopId());
            reference.setPartyId(referenceModel.getPartyId());
        }
        reference.setTemplateId(referenceModel.getTemplateId());
        command.setCommandBody(CommandBody.reference(reference));
        return command;
    }
}
