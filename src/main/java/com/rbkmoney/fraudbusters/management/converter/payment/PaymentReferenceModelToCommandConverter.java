package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.CommandBody;
import com.rbkmoney.damsel.fraudbusters.TemplateReference;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentReferenceModel;
import lombok.RequiredArgsConstructor;
import net.logstash.logback.encoder.org.apache.commons.lang.BooleanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class PaymentReferenceModelToCommandConverter implements Converter<PaymentReferenceModel, Command> {

    @NonNull
    @Override
    public Command convert(PaymentReferenceModel referenceModel) {
        TemplateReference reference = new TemplateReference();
        reference.setIsGlobal(referenceModel.getIsGlobal());
        if (BooleanUtils.isFalse(referenceModel.getIsGlobal())) {
            reference.setShopId(referenceModel.getShopId());
            reference.setPartyId(referenceModel.getPartyId());
        }
        reference.setTemplateId(referenceModel.getTemplateId());
        Command command = new Command();
        command.setCommandBody(CommandBody.reference(reference));
        return command;
    }

}
