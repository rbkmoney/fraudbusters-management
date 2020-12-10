package com.rbkmoney.fraudbusters.management.listener;

import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.dao.DaoException;
import com.rbkmoney.fraudbusters.management.dao.payment.reference.PaymentReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentReferenceModel;
import com.rbkmoney.fraudbusters.management.service.payment.PaymentTemplateReferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitiatingEntityEventSinkListener {

    public static final String FRAUDBUSTERS = "fraudbusters";
    private final PaymentTemplateReferenceService paymentTemplateReferenceService;
    private final PaymentReferenceDao referenceDao;

    @KafkaListener(topics = "${kafka.topic.fraudbusters.unknown-initiating-entity}",
            containerFactory = "kafkaReferenceInfoListenerContainerFactory")
    public void listen(ReferenceInfo event) throws DaoException {
        log.info("InitiatingEntityEventSinkListener event: {}", event);
        PaymentReferenceModel defaultReference = referenceDao.getDefaultReference();
        if (defaultReference == null) {
            log.warn("default reference for this type event: {} not found", event);
            return;
        }
        if (event.isSetMerchantInfo()) {
            Command command = new Command();
            command.setCommandBody(CommandBody.reference(new TemplateReference()
                    .setIsGlobal(false)
                    .setTemplateId(defaultReference.getTemplateId())
                    .setPartyId(event.getMerchantInfo().getPartyId())
                    .setShopId(event.getMerchantInfo().getShopId()))
            );
            command.setCommandType(CommandType.CREATE);
            command.setUserInfo(new UserInfo()
                    .setUserId(FRAUDBUSTERS));
            paymentTemplateReferenceService.sendCommandSync(command);
        } else {
            log.warn("Handler for this type event: {} not found", event);
        }
    }

}
