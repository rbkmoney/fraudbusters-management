package com.rbkmoney.fraudbusters.management.service.iface;

import com.rbkmoney.damsel.fraudbusters_notificator.*;

public interface NotificationService {

    Notification create(Notification notification);

    void delete(Long id);

    void updateStatus(Long id, NotificationStatus status);

    ValidationResponse validate(Notification notification);

    NotificationListResponse getAll(Page page, Filter filter);

}
