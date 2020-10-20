package com.rbkmoney.fraudbusters.management.service.payment;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.management.converter.TemplateModelToCommandConverter;
import com.rbkmoney.fraudbusters.management.converter.payment.ReferenceToCommandConverter;
import com.rbkmoney.fraudbusters.management.domain.ReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentReferenceModel;
import com.rbkmoney.fraudbusters.management.service.CommandSender;
import com.rbkmoney.fraudbusters.management.utils.ReferenceKeyGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentTemplateReferenceService {

    private final CommandSender commandSender;
    private final ReferenceToCommandConverter referenceToCommandConverter;

    @Value("${kafka.topic.fraudbusters.payment.reference}")
    public String topic;

    public String sendCommandSync(Command command) {
        String key = ReferenceKeyGenerator.generateTemplateKey(command.getCommandBody().getReference());
        return commandSender.send(topic, command, key);
    }

    public Command createReferenceCommandByIds(String templateId, String partyId, String shopId) {
        PaymentReferenceModel referenceModel = new PaymentReferenceModel();
        referenceModel.setShopId(shopId);
        referenceModel.setPartyId(partyId);
        referenceModel.setTemplateId(templateId);
        referenceModel.setIsGlobal(false);
        return referenceToCommandConverter.convert(referenceModel);
    }
}
