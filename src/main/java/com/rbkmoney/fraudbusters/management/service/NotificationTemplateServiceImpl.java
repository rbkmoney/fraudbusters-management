package com.rbkmoney.fraudbusters.management.service;

import com.rbkmoney.damsel.fraudbusters_notificator.NotificationTemplateListResponse;
import com.rbkmoney.damsel.fraudbusters_notificator.NotificationTemplateServiceSrv;
import com.rbkmoney.fraudbusters.management.service.iface.NotificationTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationTemplateServiceImpl implements NotificationTemplateService {

    private final NotificationTemplateServiceSrv.Iface notificationTemplateClient;

    @Override
    public NotificationTemplateListResponse getAll() {
        try {
            return notificationTemplateClient.getAll();
        } catch (TException e) {
            log.error("Error call NotificationTemplateService getAll ", e);
            return new NotificationTemplateListResponse()
                    .setNotificationTemplates(Collections.emptyList());
        }
    }
}
