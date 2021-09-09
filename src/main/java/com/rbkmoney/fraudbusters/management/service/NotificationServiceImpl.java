package com.rbkmoney.fraudbusters.management.service;

import com.rbkmoney.damsel.fraudbusters_notificator.*;
import com.rbkmoney.fraudbusters.management.service.iface.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationServiceSrv.Iface notificationClient;

    @Override
    public Notification create(Notification notification) {
        try {
            return notificationClient.create(notification);
        } catch (TException e) {
            log.error("Error call NotificationService create ", e);
            return null;
        }
    }

    @Override
    public void delete(Long id) {
        try {
            notificationClient.remove(id);
        } catch (TException e) {
            log.error("Error call NotificationService remove ", e);
        }

    }

    @Override
    public void updateStatus(Long id, NotificationStatus status) {
        try {
            notificationClient.updateStatus(id, status);
        } catch (TException e) {
            log.error("Error call NotificationService updateStatus ", e);
        }
    }

    @Override
    public ValidationResponse validate(Notification notification) {
        try {
            return notificationClient.validate(notification);
        } catch (TException e) {
            log.error("Error call NotificationService validate ", e);
            return null;
        }
    }

    @Override
    public NotificationListResponse getAll(Page page, Filter filter) {
        try {
            return notificationClient.getAll(page, filter);
        } catch (TException e) {
            log.error("Error call NotificationService getAll ", e);
            return new NotificationListResponse()
                    .setNotifications(Collections.emptyList());
        }
    }
}
