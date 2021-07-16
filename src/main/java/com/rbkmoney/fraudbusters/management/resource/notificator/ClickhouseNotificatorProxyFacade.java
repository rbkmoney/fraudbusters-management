package com.rbkmoney.fraudbusters.management.resource.notificator;

import com.rbkmoney.swag.fraudbusters.management.api.NotificationsApi;
import com.rbkmoney.swag.fraudbusters.management.model.Channel;
import com.rbkmoney.swag.fraudbusters.management.model.Notification;
import com.rbkmoney.swag.fraudbusters.management.model.ValidationResponse;
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
public class ClickhouseNotificatorProxyFacade implements NotificationsApi {

    private final RestTemplate restTemplate;

    @Value("${ch.notificator.url}")
    private String baseUrl;

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<com.rbkmoney.swag.fraudbusters.management.model.Channel> createChannel(
            com.rbkmoney.swag.fraudbusters.management.model.@Valid Channel channel) {
        return restTemplate.postForEntity(baseUrl + "/channel", channel, Channel.class);
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<Notification> createOrUpdateNotification(@Valid Notification notification) {
        ResponseEntity<Notification> notificationResponseEntity =
                restTemplate.postForEntity(baseUrl + "/notification", notification, Notification.class);
        log.info("ClickhouseNotificatorFacade created notification: {}", notification);
        return notificationResponseEntity;
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<Void> removeChannel(String name) {
        restTemplate.delete(baseUrl + "/channel/{name}", name);
        log.info("ClickhouseNotificatorFacade deleted name: {}", name);
        return ResponseEntity.ok().build();
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<Void> removeNotification(String name) {
        restTemplate.delete(baseUrl + "/notification/{name}", name);
        log.info("ClickhouseNotificatorFacade deleted name: {}", name);
        return ResponseEntity.ok().build();
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<com.rbkmoney.swag.fraudbusters.management.model.ValidationResponse> validateNotification(
            com.rbkmoney.swag.fraudbusters.management.model.@Valid Notification notification) {
        return restTemplate.postForEntity(baseUrl + "/notification/validate",
                notification, ValidationResponse.class);
    }

}
