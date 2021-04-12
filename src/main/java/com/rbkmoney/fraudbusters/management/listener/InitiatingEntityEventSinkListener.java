package com.rbkmoney.fraudbusters.management.listener;

import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.fraudbusters.management.dao.payment.DefaultPaymentReferenceDaoImpl;
import com.rbkmoney.fraudbusters.management.dao.payment.reference.PaymentReferenceDaoImpl;
import com.rbkmoney.fraudbusters.management.domain.payment.DefaultPaymentReferenceModel;
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
    private final DefaultPaymentReferenceDaoImpl defaultPaymentReferenceDao;
    private final PaymentReferenceDaoImpl referenceDao;

    @KafkaListener(topics = "${kafka.topic.fraudbusters.unknown-initiating-entity}",
            containerFactory = "kafkaReferenceInfoListenerContainerFactory")
    public void listen(ReferenceInfo event) {
        log.info("InitiatingEntityEventSinkListener event: {}", event);
        DefaultPaymentReferenceModel defaultReference = cascadFindDefaultTemplate(event);
        if (defaultReference == null) {
            log.warn("default reference for this type event: {} not found", event);
            return;
        }
        if (!referenceDao.isPartyShopReferenceExist(
                event.getMerchantInfo().getPartyId(), event.getMerchantInfo().getShopId())
                && event.isSetMerchantInfo()) {
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
            log.warn("Handler for this type event: {} not found or reference exist", event);
        }
    }

    private DefaultPaymentReferenceModel cascadFindDefaultTemplate(ReferenceInfo event) {
        return defaultPaymentReferenceDao
                .getByPartyAndShop(event.getMerchantInfo().getPartyId(), event.getMerchantInfo().getShopId())
                .orElse(defaultPaymentReferenceDao.getByPartyAndShop(event.getMerchantInfo().getPartyId(), null)
                        .orElse(defaultPaymentReferenceDao.getByPartyAndShop(null, null)
                                .orElse(null)));
    }

}
