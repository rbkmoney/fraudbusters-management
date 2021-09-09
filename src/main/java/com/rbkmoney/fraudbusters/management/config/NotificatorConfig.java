package com.rbkmoney.fraudbusters.management.config;

import com.rbkmoney.damsel.fraudbusters_notificator.ChannelServiceSrv;
import com.rbkmoney.damsel.fraudbusters_notificator.NotificationServiceSrv;
import com.rbkmoney.damsel.fraudbusters_notificator.NotificationTemplateServiceSrv;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class NotificatorConfig {

    @Bean
    public NotificationServiceSrv.Iface notificationClient(@Value("${service.notification.url}") Resource resource,
                                                           @Value("${service.notification.networkTimeout}")
                                                                   int networkTimeout)
            throws IOException {
        return new THSpawnClientBuilder()
                .withNetworkTimeout(networkTimeout)
                .withAddress(resource.getURI())
                .build(NotificationServiceSrv.Iface.class);
    }

    @Bean
    public ChannelServiceSrv.Iface notificationChannelClient(
            @Value("${service.notification-channel.url}") Resource resource,
            @Value("${service.notification-channel.networkTimeout}") int networkTimeout)
            throws IOException {
        return new THSpawnClientBuilder()
                .withNetworkTimeout(networkTimeout)
                .withAddress(resource.getURI())
                .build(ChannelServiceSrv.Iface.class);
    }

    @Bean
    public NotificationTemplateServiceSrv.Iface notificationTemplateClient(
            @Value("${service.notification-template.url}") Resource resource,
            @Value("${service.notification-template.networkTimeout}") int networkTimeout)
            throws IOException {
        return new THSpawnClientBuilder()
                .withNetworkTimeout(networkTimeout)
                .withAddress(resource.getURI())
                .build(NotificationTemplateServiceSrv.Iface.class);
    }

}
