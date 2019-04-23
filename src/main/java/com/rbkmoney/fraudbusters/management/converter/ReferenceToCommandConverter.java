package com.rbkmoney.fraudbusters.management.converter;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.CommandBody;
import com.rbkmoney.damsel.fraudbusters.TemplateReference;
import com.rbkmoney.fraudbusters.management.domain.ReferenceModel;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ReferenceToCommandConverter implements Converter<ReferenceModel, Command> {

    @Override
    public Command convert(ReferenceModel referenceModel) {
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
