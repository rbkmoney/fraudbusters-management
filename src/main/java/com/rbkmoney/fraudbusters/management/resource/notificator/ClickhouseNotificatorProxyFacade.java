package com.rbkmoney.fraudbusters.management.resource.notificator;

import com.rbkmoney.fraudbusters.management.resource.notificator.domain.Channel;
import com.rbkmoney.fraudbusters.management.resource.notificator.domain.Notification;
import com.rbkmoney.fraudbusters.management.resource.notificator.domain.ValidationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ClickhouseNotificatorProxyFacade {

    private final RestTemplate restTemplate;

    @PostMapping(value = "/notification")
    public Notification createOrUpdate(@Validated @RequestBody Notification notification) {
        ResponseEntity<Notification> notificationResponseEntity = restTemplate.postForEntity("/ch-manager/notification", notification, Notification.class);
        log.info("ClickhouseNotificatorFacade created notification: {}", notification);
        return notificationResponseEntity.getBody();
    }

    @DeleteMapping(value = "/notification/{name}")
    public void delete(@Validated @PathVariable String name) {
        restTemplate.delete("/ch-manager/notification/{name}", name);
        log.info("ClickhouseNotificatorFacade deleted name: {}", name);
    }

    @PostMapping(value = "/notification/validate")
    public ValidationResponse validate(@Validated @RequestBody Notification notification) {
        ResponseEntity<ValidationResponse> responseEntity = restTemplate.postForEntity("/ch-manager/notification",
                notification, ValidationResponse.class);
        return responseEntity.getBody();
    }

    @PostMapping(value = "/channel")
    public Channel createOrUpdate(@Validated @RequestBody Channel channel) {
        ResponseEntity<Channel> responseEntity = restTemplate.postForEntity("/ch-manager/channel", channel, Channel.class);
        return responseEntity.getBody();
    }

    @DeleteMapping(value = "/channel/{name}")
    public void deleteChannel(@Validated @PathVariable String name) {
        restTemplate.delete("/ch-manager/channel/{name}", name);
        log.info("ClickhouseNotificatorFacade deleted name: {}", name);
    }

}
