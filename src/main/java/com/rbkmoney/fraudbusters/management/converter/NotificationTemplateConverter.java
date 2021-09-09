package com.rbkmoney.fraudbusters.management.converter;

import com.rbkmoney.swag.fraudbusters.management.model.NotificationTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Component
public class NotificationTemplateConverter
        implements
        BiConverter<com.rbkmoney.damsel.fraudbusters_notificator.NotificationTemplate, NotificationTemplate> {

    @Override
    public NotificationTemplate toTarget(
            com.rbkmoney.damsel.fraudbusters_notificator.NotificationTemplate notificationTemplate) {
        if (Objects.isNull(notificationTemplate)) {
            return null;
        }
        NotificationTemplate result = new NotificationTemplate();
        result.setId(notificationTemplate.getId());
        result.setName(notificationTemplate.getName());
        result.setBasicParams(notificationTemplate.getBasicParams());
        result.setQueryText(notificationTemplate.getQueryText());
        result.setSkeleton(notificationTemplate.getSkeleton());
        result.setType(notificationTemplate.getType());
        result.setUpdatedAt(LocalDateTime.parse(notificationTemplate.getUpdatedAt(), DateTimeFormatter.ISO_DATE_TIME));
        result.setCreatedAt(LocalDateTime.parse(notificationTemplate.getCreatedAt(), DateTimeFormatter.ISO_DATE_TIME));
        return result;
    }

    @Override
    public com.rbkmoney.damsel.fraudbusters_notificator.NotificationTemplate toSource(
            NotificationTemplate notificationTemplate) {
        if (Objects.isNull(notificationTemplate)) {
            return null;
        }
        com.rbkmoney.damsel.fraudbusters_notificator.NotificationTemplate result =
                new com.rbkmoney.damsel.fraudbusters_notificator.NotificationTemplate();
        result.setId(notificationTemplate.getId());
        result.setName(notificationTemplate.getName());
        result.setBasicParams(notificationTemplate.getBasicParams());
        result.setQueryText(notificationTemplate.getQueryText());
        result.setSkeleton(notificationTemplate.getSkeleton());
        result.setType(notificationTemplate.getType());
        result.setUpdatedAt(notificationTemplate.getUpdatedAt().format(DateTimeFormatter.ISO_DATE_TIME));
        result.setCreatedAt(notificationTemplate.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME));
        return result;
    }
}
