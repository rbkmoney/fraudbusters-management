package com.rbkmoney.fraudbusters.management.service;

import com.rbkmoney.damsel.fraudbusters_notificator.*;
import com.rbkmoney.fraudbusters.management.exception.NotificatorCallException;
import com.rbkmoney.fraudbusters.management.service.iface.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationServiceSrv.Iface notificationClient;

    @Override
    public Notification create(Notification notification) {
        try {
            return notificationClient.create(notification);
        } catch (NotificationServiceException e) {
            log.error("Error call notificator create notification ", e);
            throw new NotificatorCallException(e.getCode(), e.getReason());
        } catch (TException e) {
            log.error("Error call notificator create notification ", e);
            throw new NotificatorCallException("Error call notificator create notification");
        }
    }

    @Override
    public void delete(Long id) {
        try {
            notificationClient.remove(id);
        } catch (TException e) {
            log.error("Error call notificator remove notification ", e);
            throw new NotificatorCallException("Error call notificator remove notification");
        }

    }

    @Override
    public void updateStatus(Long id, NotificationStatus status) {
        try {
            notificationClient.updateStatus(id, status);
        } catch (TException e) {
            log.error("Error call notificator update notification status ", e);
            throw new NotificatorCallException("Error call notificator update notification status");
        }
    }

    @Override
    public ValidationResponse validate(Notification notification) {
        try {
            return notificationClient.validate(notification);
        } catch (TException e) {
            log.error("Error call notificator validate notification ", e);
            throw new NotificatorCallException("Error call notificator validate notification");
        }
    }

    @Override
    public NotificationListResponse getAll(Page page, Filter filter) {
        try {
            return notificationClient.getAll(page, filter);
        } catch (TException e) {
            log.error("Error call notificator getAll notifications ", e);
            throw new NotificatorCallException("Error call notificator getAll notifications");
        }
    }
}
