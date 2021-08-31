package com.rbkmoney.fraudbusters.management.resource.notificator;

import com.rbkmoney.swag.fraudbusters.management.api.NotificationsApi;
import com.rbkmoney.swag.fraudbusters.management.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NotificationProxyFacade implements NotificationsApi {

    private final RestTemplate restTemplate;

    @Value("${service.fb-notificator.url}")
    private String baseUrl;

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<com.rbkmoney.swag.fraudbusters.management.model.Channel> createChannel(
            com.rbkmoney.swag.fraudbusters.management.model.@Valid Channel channel) {
        return restTemplate.postForEntity(baseUrl + "/channels", channel, Channel.class);
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<Notification> createOrUpdateNotification(@Valid Notification notification) {
        ResponseEntity<Notification> notificationResponseEntity =
                restTemplate.postForEntity(baseUrl + "/notifications", notification, Notification.class);
        log.info("NotificationProxyFacade create notification: {}", notification);
        return notificationResponseEntity;
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<Void> removeChannel(String name) {
        restTemplate.delete(baseUrl + "/channels/{name}", name);
        log.info("NotificationProxyFacade delete channel with name: {}", name);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<Void> removeNotification(Long id) {
        restTemplate.delete(baseUrl + "/notifications/{id}", id);
        log.info("NotificationProxyFacade delete notification with id: {}", id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<com.rbkmoney.swag.fraudbusters.management.model.ValidationResponse> validateNotification(
            com.rbkmoney.swag.fraudbusters.management.model.@Valid Notification notification) {
        return restTemplate.postForEntity(baseUrl + "/notifications/validation",
                notification, ValidationResponse.class);
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<NotificationListResponse> getNotifications() {
        return restTemplate.getForEntity(baseUrl + "/notifications", NotificationListResponse.class);
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<ChannelListResponse> getChannels() {
        return restTemplate.getForEntity(baseUrl + "/channels", ChannelListResponse.class);
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<Void> updateNotificationStatus(Long id,
                                                         @Valid NotificationStatus notificationStatus) {
        restTemplate.put(baseUrl + "/notifications/{id}/status", notificationStatus, id);
        log.info("NotificationProxyFacade update notification status: {}", notificationStatus);
        return ResponseEntity.noContent().build();
    }
}
