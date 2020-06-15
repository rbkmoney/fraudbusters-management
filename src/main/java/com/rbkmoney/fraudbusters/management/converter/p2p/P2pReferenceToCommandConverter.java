package com.rbkmoney.fraudbusters.management.converter.p2p;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.CommandBody;
import com.rbkmoney.damsel.fraudbusters.P2PReference;
import com.rbkmoney.damsel.fraudbusters.TemplateReference;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentReferenceModel;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class P2pReferenceToCommandConverter implements Converter<P2pReferenceModel, Command> {

    @NonNull
    @Override
    public Command convert(P2pReferenceModel referenceModel) {
        Command command = new Command();
        P2PReference reference = new P2PReference();
        reference.setIsGlobal(referenceModel.getIsGlobal());
        if (!referenceModel.getIsGlobal()) {
            reference.setIdentityId(referenceModel.getIdentityId());
        }
        reference.setTemplateId(referenceModel.getTemplateId());
        command.setCommandBody(CommandBody.p2p_reference(reference));
        return command;
    }
}
