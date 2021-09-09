package com.rbkmoney.fraudbusters.management.converter;

import com.rbkmoney.damsel.fraudbusters_notificator.NotificationStatus;
import com.rbkmoney.swag.fraudbusters.management.model.Notification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Component
public class NotificationConverter
        implements BiConverter<com.rbkmoney.damsel.fraudbusters_notificator.Notification, Notification> {

    @Override
    public Notification toTarget(com.rbkmoney.damsel.fraudbusters_notificator.Notification notification) {
        if (Objects.isNull(notification)) {
            return null;
        }
        Notification result = new Notification();
        result.setId(notification.getId());
        result.setName(notification.getName());
        result.setSubject(notification.getSubject());
        result.setChannel(notification.getChannel());
        result.setFrequency(notification.getFrequency());
        result.setPeriod(notification.getPeriod());
        result.setStatus(Notification.StatusEnum.fromValue(notification.getStatus().name()));
        result.setTemplateId(notification.getTemplateId());
        result.setUpdatedAt(LocalDateTime.parse(notification.getUpdatedAt(), DateTimeFormatter.ISO_DATE_TIME));
        result.setCreatedAt(LocalDateTime.parse(notification.getCreatedAt(), DateTimeFormatter.ISO_DATE_TIME));
        return result;
    }

    @Override
    public com.rbkmoney.damsel.fraudbusters_notificator.Notification toSource(Notification notification) {
        if (Objects.isNull(notification)) {
            return null;
        }
        com.rbkmoney.damsel.fraudbusters_notificator.Notification result =
                new com.rbkmoney.damsel.fraudbusters_notificator.Notification();
        result.setId(notification.getId());
        result.setName(notification.getName());
        result.setSubject(notification.getSubject());
        result.setChannel(notification.getChannel());
        result.setFrequency(notification.getFrequency());
        result.setPeriod(notification.getPeriod());
        result.setStatus(NotificationStatus.valueOf(notification.getStatus().getValue()));
        result.setTemplateId(notification.getTemplateId());
        result.setUpdatedAt(notification.getUpdatedAt().format(DateTimeFormatter.ISO_DATE_TIME));
        result.setCreatedAt(notification.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME));
        return result;
    }
}
