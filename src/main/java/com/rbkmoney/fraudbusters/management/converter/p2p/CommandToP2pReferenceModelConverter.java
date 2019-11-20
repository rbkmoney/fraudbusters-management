package com.rbkmoney.fraudbusters.management.converter.p2p;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.P2PReference;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pReferenceModel;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
@RequiredArgsConstructor
public class CommandToP2pReferenceModelConverter implements Converter<Command, P2pReferenceModel> {

    @Override
    public P2pReferenceModel convert(Command command) {
        P2pReferenceModel model = new P2pReferenceModel();
        P2PReference p2pReference = command.getCommandBody().getP2pReference();
        String uid = UUID.randomUUID().toString();
        model.setId(uid);
        model.setIsGlobal(p2pReference.is_global);
        model.setIdentityId(p2pReference.identity_id);
        model.setTemplateId(p2pReference.template_id);
        return model;
    }
}
