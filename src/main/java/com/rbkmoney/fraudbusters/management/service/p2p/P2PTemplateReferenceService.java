package com.rbkmoney.fraudbusters.management.service.p2p;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.management.converter.p2p.P2pReferenceToCommandConverter;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pReferenceModel;
import com.rbkmoney.fraudbusters.management.service.CommandSender;
import com.rbkmoney.fraudbusters.management.utils.P2PReferenceKeyGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class P2PTemplateReferenceService {

    private final CommandSender commandSender;
    private final P2pReferenceToCommandConverter referenceToCommandConverter;

    @Value("${kafka.topic.fraudbusters.p2p.reference}")
    public String topic;

    public String sendCommandSync(Command command) {
        String key = P2PReferenceKeyGenerator.generateTemplateKey(command.getCommandBody().getP2pReference());
        return commandSender.send(topic, command, key);
    }

    public Command createReferenceCommandByIds(String templateId, String identityId) {
        P2pReferenceModel referenceModel = new P2pReferenceModel();
        referenceModel.setIdentityId(identityId);
        referenceModel.setTemplateId(templateId);
        referenceModel.setIsGlobal(false);
        return referenceToCommandConverter.convert(referenceModel);
    }
}
