package com.rbkmoney.fraudbusters.management.resource.notificator;

import com.rbkmoney.damsel.fraudbusters_notificator.Filter;
import com.rbkmoney.damsel.fraudbusters_notificator.Page;
import com.rbkmoney.fraudbusters.management.resource.notificator.converter.ChannelConverter;
import com.rbkmoney.fraudbusters.management.resource.notificator.converter.NotificationConverter;
import com.rbkmoney.fraudbusters.management.resource.notificator.converter.NotificationTemplateConverter;
import com.rbkmoney.fraudbusters.management.resource.notificator.converter.ValidationConverter;
import com.rbkmoney.fraudbusters.management.service.iface.ChannelService;
import com.rbkmoney.fraudbusters.management.service.iface.NotificationService;
import com.rbkmoney.fraudbusters.management.service.iface.NotificationTemplateService;
import com.rbkmoney.swag.fraudbusters.management.api.NotificationsApi;
import com.rbkmoney.swag.fraudbusters.management.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NotificationResource implements NotificationsApi {

    private final NotificationService notificationService;
    private final NotificationConverter notificationConverter;
    private final NotificationTemplateService notificationTemplateService;
    private final NotificationTemplateConverter notificationTemplateConverter;
    private final ChannelService channelService;
    private final ChannelConverter channelConverter;
    private final ValidationConverter validationConverter;

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<Channel> createChannel(@Valid Channel channel) {
        var createdChannel = channelService.create(channelConverter.toSource(channel));
        log.info("NotificationResource create channel: {}", createdChannel);
        return ResponseEntity.ok(channelConverter.toTarget(createdChannel));
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<Notification> createOrUpdateNotification(@Valid Notification notification) {
        var createdNotification = notificationService.create(notificationConverter.toSource(notification));
        log.info("NotificationResource create notification: {}", createdNotification);
        return ResponseEntity.ok(notificationConverter.toTarget(createdNotification));
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<Void> removeChannel(String name) {
        channelService.delete(name);
        log.info("NotificationResource delete channel with name: {}", name);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<Void> removeNotification(Long id) {
        notificationService.delete(id);
        log.info("NotificationResource delete notification with id: {}", id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<ValidationResponse> validateNotification(
            com.rbkmoney.swag.fraudbusters.management.model.@Valid Notification notification) {
        var validationResponse = notificationService.validate(notificationConverter.toSource(notification));
        ValidationResponse response = validationConverter.convert(validationResponse);
        return ResponseEntity.ok(response);
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<NotificationListResponse> getNotifications(
            @Valid @RequestParam(value = "lastId", required = false) Long lastId,
            @Valid @RequestParam(value = "size", required = false) Integer size,
            @Valid @RequestParam(value = "searchValue", required = false) String searchValue) {
        Page page = new Page()
                .setContinuationId(lastId)
                .setSize(size);
        Filter filter = new Filter()
                .setSearchField(searchValue);
        var notificationListResponse = notificationService.getAll(page, filter);
        List<Notification> filteredNotifications = notificationListResponse.getNotifications().stream()
                .map(notificationConverter::toTarget)
                .collect(Collectors.toList());
        NotificationListResponse response = new NotificationListResponse();
        response.setResult(filteredNotifications);
        return ResponseEntity.ok(response);
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<ChannelListResponse> getChannels(
            @Valid @RequestParam(value = "lastId", required = false) Long lastId,
            @Valid @RequestParam(value = "size", required = false) Integer size,
            @Valid @RequestParam(value = "searchValue", required = false) String searchValue) {
        Page page = new Page()
                .setContinuationId(lastId)
                .setSize(size);
        Filter filter = new Filter()
                .setSearchField(searchValue);
        var channelListResponse = channelService.getAll(page, filter);
        List<Channel> channels = channelListResponse.getChannels().stream()
                .map(channelConverter::toTarget)
                .collect(Collectors.toList());
        ChannelListResponse response = new ChannelListResponse();
        response.setResult(channels);
        return ResponseEntity.ok(response);
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<Void> updateNotificationStatus(Long id,
                                                         @Valid NotificationStatus notificationStatus) {
        var status = com.rbkmoney.damsel.fraudbusters_notificator.NotificationStatus
                .valueOf(notificationStatus.getStatus().getValue());
        notificationService.updateStatus(id, status);
        log.info("NotificationResource update notification status: {}", notificationStatus);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<ChannelTypeListResponse> getChannelTypes() {
        var channelTypeListResponse = channelService.getAllTypes();
        List<ChannelType> channelTypes = channelTypeListResponse.getChannelTypes().stream()
                .map(value -> new ChannelType().type(ChannelType.TypeEnum.fromValue(value)))
                .collect(Collectors.toList());
        ChannelTypeListResponse response = new ChannelTypeListResponse();
        response.setResult(channelTypes);
        return ResponseEntity.ok(response);
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<NotificationTemplateListResponse> getTemplates() {
        var notificationTemplateListResponse = notificationTemplateService.getAll();
        List<NotificationTemplate> notificationTemplates =
                notificationTemplateListResponse.getNotificationTemplates().stream()
                        .map(notificationTemplateConverter::convert)
                        .collect(Collectors.toList());
        NotificationTemplateListResponse response = new NotificationTemplateListResponse();
        response.setResult(notificationTemplates);
        return ResponseEntity.ok(response);
    }
}
