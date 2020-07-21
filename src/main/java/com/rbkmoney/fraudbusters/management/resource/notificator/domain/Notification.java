package com.rbkmoney.fraudbusters.management.resource.notificator.domain;

import com.rbkmoney.fraudbusters.management.resource.notificator.constant.NotificationStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Notification {

    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String queryText;
    private String period;
    private String frequency;
    private String alertchanel;
    private NotificationStatus status;
    private String templateType;
    private String templateValue;
    private String groupbyparams;
    private String subject;

}
