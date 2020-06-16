package com.rbkmoney.fraudbusters.management.converter.p2p;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.CommandBody;
import com.rbkmoney.damsel.fraudbusters.P2PGroupReference;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pGroupReferenceModel;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class P2pGroupReferenceToCommandConverter implements Converter<P2pGroupReferenceModel, Command> {

    @NonNull
    @Override
    public Command convert(P2pGroupReferenceModel groupReferenceModel) {
        Command command = new Command();
        P2PGroupReference reference = new P2PGroupReference();
        reference.setIdentityId(groupReferenceModel.getIdentityId());
        reference.setGroupId(groupReferenceModel.getGroupId());
        command.setCommandBody(CommandBody.p2p_group_reference(reference));
        return command;
    }
}
