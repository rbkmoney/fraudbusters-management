package com.rbkmoney.fraudbusters.management.converter.p2p;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.P2PGroupReference;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pGroupReferenceModel;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class CommandToP2pGroupReferenceModelConverter implements Converter<Command, P2pGroupReferenceModel> {

    @Override
    public P2pGroupReferenceModel convert(Command command) {
        P2pGroupReferenceModel model = new P2pGroupReferenceModel();
        P2PGroupReference groupReference = command.getCommandBody().getP2pGroupReference();
        model.setIdentityId(groupReference.getIdentityId());
        model.setGroupId(groupReference.getGroupId());
        return model;
    }
}
