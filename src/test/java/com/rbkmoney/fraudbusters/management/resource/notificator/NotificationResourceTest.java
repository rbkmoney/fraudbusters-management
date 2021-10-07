package com.rbkmoney.fraudbusters.management.resource.notificator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rbkmoney.damsel.fraudbusters_notificator.*;
import com.rbkmoney.fraudbusters.management.TestObjectFactory;
import com.rbkmoney.fraudbusters.management.controller.ErrorController;
import com.rbkmoney.fraudbusters.management.resource.notificator.converter.ChannelConverter;
import com.rbkmoney.fraudbusters.management.resource.notificator.converter.NotificationConverter;
import com.rbkmoney.fraudbusters.management.resource.notificator.converter.NotificationTemplateConverter;
import com.rbkmoney.fraudbusters.management.resource.notificator.converter.ValidationConverter;
import com.rbkmoney.fraudbusters.management.service.ChannelServiceImpl;
import com.rbkmoney.fraudbusters.management.service.NotificationServiceImpl;
import com.rbkmoney.fraudbusters.management.service.NotificationTemplateServiceImpl;
import com.rbkmoney.swag.fraudbusters.management.model.Channel;
import com.rbkmoney.swag.fraudbusters.management.model.Notification;
import com.rbkmoney.swag.fraudbusters.management.model.ValidationError;
import com.rbkmoney.swag.fraudbusters.management.model.ValidationResponse;
import org.apache.thrift.TException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.stream.Collectors;

import static com.rbkmoney.damsel.fraudbusters_notificator.fraudbusters_notificatorConstants.VALIDATION_ERROR;
import static com.rbkmoney.fraudbusters.management.controller.ErrorController.NOTIFICATOR_CALL_EXCEPTION;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
class NotificationResourceTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    private NotificationServiceSrv.Iface notificationClient;
    @Mock
    private NotificationTemplateServiceSrv.Iface notificationTemplateClient;
    @Mock
    private ChannelServiceSrv.Iface channelClient;

    @BeforeEach
    void setup() {
        var notificationConverter = new NotificationConverter();
        var notificationService = new NotificationServiceImpl(notificationClient);
        var channelConverter = new ChannelConverter();
        var channelService = new ChannelServiceImpl(channelClient);
        var notificationTemplateConverter = new NotificationTemplateConverter();
        var notificationTemplateService = new NotificationTemplateServiceImpl(notificationTemplateClient);
        var validationConverter = new ValidationConverter();
        var notificationResource =
                new NotificationResource(notificationService, notificationConverter, notificationTemplateService,
                        notificationTemplateConverter, channelService, channelConverter, validationConverter);
        this.mockMvc = MockMvcBuilders.standaloneSetup(notificationResource, new ErrorController()).build();
    }

    @Test
    void createChannelWithErrorCall() throws Exception {
        Channel channel = TestObjectFactory.testChannel();
        when(channelClient.create(any(com.rbkmoney.damsel.fraudbusters_notificator.Channel.class)))
                .thenThrow(new TException());

        mockMvc.perform(post("/notifications/channels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(channel)))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.code", is(NOTIFICATOR_CALL_EXCEPTION)))
                .andExpect(jsonPath("$.message", is("Error call notificator create channel")));
    }

    @Test
    void createChannel() throws Exception {
        Channel channel = TestObjectFactory.testChannel();
        var convertChannel = new com.rbkmoney.damsel.fraudbusters_notificator.Channel()
                .setDestination(channel.getDestination())
                .setName(channel.getName());
        when(channelClient.create(any(com.rbkmoney.damsel.fraudbusters_notificator.Channel.class)))
                .thenReturn(convertChannel);

        MvcResult result = mockMvc.perform(post("/notifications/channels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(channel)))
                .andExpect(status().isOk())
                .andReturn();

        Channel resultChannel = objectMapper.readValue(result.getResponse().getContentAsString(), Channel.class);
        assertEquals(convertChannel.getName(), resultChannel.getName());
        assertEquals(convertChannel.getDestination(), resultChannel.getDestination());
        verify(channelClient, times(1)).create(any(com.rbkmoney.damsel.fraudbusters_notificator.Channel.class));
    }

    @Test
    void createOrUpdateNotificationWithErrorCall() throws Exception {
        Notification notification = TestObjectFactory.testNotification();
        when(notificationClient.create(any(com.rbkmoney.damsel.fraudbusters_notificator.Notification.class)))
                .thenThrow(new TException());

        mockMvc.perform(post("/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notification)))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.code", is(NOTIFICATOR_CALL_EXCEPTION)))
                .andExpect(jsonPath("$.message", is("Error call notificator create notification")));
    }

    @Test
    void createOrUpdateNotificationWithValidationErrorCall() throws Exception {
        Notification notification = TestObjectFactory.testNotification();
        String reason = "Error call";
        when(notificationClient.create(any(com.rbkmoney.damsel.fraudbusters_notificator.Notification.class)))
                .thenThrow(new NotificationServiceException()
                        .setCode(VALIDATION_ERROR)
                        .setReason(reason));

        mockMvc.perform(post("/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notification)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(NOTIFICATOR_CALL_EXCEPTION)))
                .andExpect(jsonPath("$.message", is(reason)));
    }

    @Test
    void createOrUpdateNotification() throws Exception {
        Notification notification = TestObjectFactory.testNotification();
        var convertNotification = new com.rbkmoney.damsel.fraudbusters_notificator.Notification()
                .setName(notification.getName())
                .setSubject(notification.getSubject())
                .setTemplateId(notification.getTemplateId());
        when(notificationClient.create(any(com.rbkmoney.damsel.fraudbusters_notificator.Notification.class)))
                .thenReturn(convertNotification);

        MvcResult result = mockMvc.perform(post("/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notification)))
                .andExpect(status().isOk())
                .andReturn();

        Notification resultNotification =
                objectMapper.readValue(result.getResponse().getContentAsString(), Notification.class);
        assertEquals(convertNotification.getName(), resultNotification.getName());
        assertEquals(convertNotification.getSubject(), resultNotification.getSubject());
        assertEquals(convertNotification.getTemplateId(), resultNotification.getTemplateId());
        verify(notificationClient, times(1))
                .create(any(com.rbkmoney.damsel.fraudbusters_notificator.Notification.class));
    }

    @Test
    void removeChannelWithErrorCall() throws Exception {
        String channelName = TestObjectFactory.randomString();
        doThrow(new TException()).when(channelClient).remove(channelName);

        mockMvc.perform(delete("/notifications/channels/{name}", channelName))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.code", is(NOTIFICATOR_CALL_EXCEPTION)))
                .andExpect(jsonPath("$.message", is("Error call notificator remove channel")));
    }

    @Test
    void removeChannel() throws Exception {
        String channelName = TestObjectFactory.randomString();

        mockMvc.perform(delete("/notifications/channels/{name}", channelName))
                .andExpect(status().isNoContent())
                .andReturn();

        verify(channelClient, times(1)).remove(channelName);
    }

    @Test
    void removeNotificationWithErrorCall() throws Exception {
        Long notificationId = TestObjectFactory.randomLong();
        doThrow(new TException()).when(notificationClient).remove(notificationId);

        mockMvc.perform(delete("/notifications/{id}", notificationId))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.code", is(NOTIFICATOR_CALL_EXCEPTION)))
                .andExpect(jsonPath("$.message", is("Error call notificator remove notification")));
    }

    @Test
    void removeNotification() throws Exception {
        Long notificationId = TestObjectFactory.randomLong();

        mockMvc.perform(delete("/notifications/{id}", notificationId))
                .andExpect(status().isNoContent())
                .andReturn();

        verify(notificationClient, times(1)).remove(notificationId);
    }

    @Test
    void validateNotificationWithErrorCall() throws Exception {
        Notification notification = TestObjectFactory.testNotification();
        when(notificationClient.validate(any(com.rbkmoney.damsel.fraudbusters_notificator.Notification.class)))
                .thenThrow(new TException());

        mockMvc.perform(post("/notifications/validation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notification)))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.code", is(NOTIFICATOR_CALL_EXCEPTION)))
                .andExpect(jsonPath("$.message", is("Error call notificator validate notification")));
    }

    @Test
    void validateNotificationWithResult() throws Exception {
        Notification notification = TestObjectFactory.testNotification();
        var convertValidationResponse = new com.rbkmoney.damsel.fraudbusters_notificator.ValidationResponse();
        convertValidationResponse.setResult(TestObjectFactory.randomString());
        when(notificationClient.validate(any(com.rbkmoney.damsel.fraudbusters_notificator.Notification.class)))
                .thenReturn(convertValidationResponse);

        MvcResult result = mockMvc.perform(post("/notifications/validation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notification)))
                .andExpect(status().isOk())
                .andReturn();

        ValidationResponse validationResponse =
                objectMapper.readValue(result.getResponse().getContentAsString(), ValidationResponse.class);

        assertNotNull(validationResponse.getResult());
        assertNull(validationResponse.getErrors());
        assertEquals(convertValidationResponse.getResult(), validationResponse.getResult());
    }

    @Test
    void validateNotificationWithErrors() throws Exception {
        Notification notification = TestObjectFactory.testNotification();
        var convertValidationResponse = new com.rbkmoney.damsel.fraudbusters_notificator.ValidationResponse();
        List<String> errors = List.of(TestObjectFactory.randomString(), TestObjectFactory.randomString());
        convertValidationResponse.setErrors(errors);
        when(notificationClient.validate(any(com.rbkmoney.damsel.fraudbusters_notificator.Notification.class)))
                .thenReturn(convertValidationResponse);

        MvcResult result = mockMvc.perform(post("/notifications/validation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notification)))
                .andExpect(status().isOk())
                .andReturn();

        ValidationResponse validationResponse =
                objectMapper.readValue(result.getResponse().getContentAsString(), ValidationResponse.class);

        assertNull(validationResponse.getResult());
        assertFalse(validationResponse.getErrors().isEmpty());
        assertIterableEquals(convertValidationResponse.getErrors(), validationResponse.getErrors().stream().map(
                ValidationError::getErrorReason).collect(Collectors.toList()));
    }

    @Test
    void getNotificationsWithErrorCall() throws Exception {
        String continuationId = String.valueOf(1L);
        int size = 10;
        String searchValue = TestObjectFactory.randomString();
        when(notificationClient
                .getAll(new Page(size).setContinuationId(continuationId), new Filter().setSearchField(searchValue)))
                .thenThrow(new TException());

        mockMvc.perform(get("/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .param("lastId", continuationId)
                .param("size", String.valueOf(size))
                .param("searchValue", searchValue))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.code", is(NOTIFICATOR_CALL_EXCEPTION)))
                .andExpect(jsonPath("$.message", is("Error call notificator getAll notifications")));
    }

    @Test
    void getNotifications() throws Exception {
        String continuationId = String.valueOf(1L);
        int size = 10;
        String searchValue = TestObjectFactory.randomString();
        var notifications = TestObjectFactory.testInternalNotifications(3);
        when(notificationClient
                .getAll(new Page(size).setContinuationId(continuationId), new Filter().setSearchField(searchValue)))
                .thenReturn(new NotificationListResponse().setNotifications(notifications));

        mockMvc.perform(get("/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .param("lastId", continuationId)
                .param("size", String.valueOf(size))
                .param("searchValue", searchValue))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.[*]", hasSize(notifications.size())));
    }

    @Test
    void getNotificationByID() throws Exception {
        var notification = TestObjectFactory.testInternalNotification();
        long id = 1L;
        when(notificationClient
                .getById(id))
                .thenReturn(notification);

        mockMvc.perform(get("/notifications/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(notification.getName())))
                .andExpect(jsonPath("$.channel", is(notification.getChannel())))
                .andExpect(jsonPath("$.subject", is(notification.getSubject())));
    }

    @Test
    void getNotificationByIDWithErrorCall() throws Exception {
        long id = 1L;
        when(notificationClient
                .getById(id))
                .thenThrow(new TException());

        mockMvc.perform(get("/notifications/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.code", is(NOTIFICATOR_CALL_EXCEPTION)))
                .andExpect(jsonPath("$.message", is("Error call notificator getById notification")));
    }

    @Test
    void getChannelsWithErrorCall() throws Exception {
        String continuationId = String.valueOf(1L);
        int size = 10;
        String searchValue = TestObjectFactory.randomString();
        var channels = TestObjectFactory.testInternalChannels(3);
        when(channelClient
                .getAll(new Page(size).setContinuationId(continuationId), new Filter().setSearchField(searchValue)))
                .thenThrow(new TException());

        mockMvc.perform(get("/notifications/channels")
                .contentType(MediaType.APPLICATION_JSON)
                .param("lastId", continuationId)
                .param("size", String.valueOf(size))
                .param("searchValue", searchValue))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.code", is(NOTIFICATOR_CALL_EXCEPTION)))
                .andExpect(jsonPath("$.message", is("Error call notificator getAll channels")));
    }

    @Test
    void getChannels() throws Exception {
        String continuationId = String.valueOf(1L);
        int size = 10;
        String searchValue = TestObjectFactory.randomString();
        var channels = TestObjectFactory.testInternalChannels(3);
        when(channelClient
                .getAll(new Page(size).setContinuationId(continuationId), new Filter().setSearchField(searchValue)))
                .thenReturn(new ChannelListResponse().setChannels(channels));

        mockMvc.perform(get("/notifications/channels")
                .contentType(MediaType.APPLICATION_JSON)
                .param("lastId", continuationId)
                .param("size", String.valueOf(size))
                .param("searchValue", searchValue))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.[*]", hasSize(channels.size())));
    }

    @Test
    void getChannelByID() throws Exception {
        var channel = TestObjectFactory.testInternalChannel();
        String name = TestObjectFactory.randomString();
        when(channelClient
                .getById(name))
                .thenReturn(channel);

        mockMvc.perform(get("/notifications/channels/{id}", name)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(channel.getName())))
                .andExpect(jsonPath("$.destination", is(channel.getDestination())));
    }

    @Test
    void getChannelByIDWithErrorCall() throws Exception {
        String name = TestObjectFactory.randomString();
        when(channelClient
                .getById(name))
                .thenThrow(new TException());

        mockMvc.perform(get("/notifications/channels/{name}", name)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.code", is(NOTIFICATOR_CALL_EXCEPTION)))
                .andExpect(jsonPath("$.message", is("Error call notificator getById channel")));
    }

    @Test
    void updateNotificationStatusWithErrorCall() throws Exception {
        var status = com.rbkmoney.swag.fraudbusters.management.model.NotificationStatus.ACTIVE.getValue();
        long id = 1L;
        doThrow(new TException()).when(notificationClient).updateStatus(id, NotificationStatus.valueOf(status));

        mockMvc.perform(put("/notifications/{id}/status", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(com.rbkmoney.swag.fraudbusters.management.model.NotificationStatus.ACTIVE.getValue()))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.code", is(NOTIFICATOR_CALL_EXCEPTION)))
                .andExpect(jsonPath("$.message", is("Error call notificator update notification status")));


    }

    @Test
    void updateNotificationStatus() throws Exception {
        var status = com.rbkmoney.swag.fraudbusters.management.model.NotificationStatus.ACTIVE.getValue();
        long id = 1L;

        mockMvc.perform(put("/notifications/{id}/status", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(com.rbkmoney.swag.fraudbusters.management.model.NotificationStatus.ACTIVE.getValue()))
                .andExpect(status().isNoContent())
                .andReturn();

        verify(notificationClient, times(1))
                .updateStatus(id, NotificationStatus.valueOf(status));

    }

    @Test
    void getChannelTypesWithErrorCall() throws Exception {
        when(channelClient.getAllTypes()).thenThrow(new TException());

        mockMvc.perform(get("/notifications/channels/types"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.code", is(NOTIFICATOR_CALL_EXCEPTION)))
                .andExpect(jsonPath("$.message", is("Error call notificator getAll channel types")));

    }

    @Test
    void getChannelTypes() throws Exception {
        var channelTypes =
                List.of(com.rbkmoney.swag.fraudbusters.management.model.ChannelType.MAIL.getValue());
        when(channelClient.getAllTypes()).thenReturn(new ChannelTypeListResponse().setChannelTypes(channelTypes));

        mockMvc.perform(get("/notifications/channels/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.[*]", hasSize(channelTypes.size())));

    }

    @Test
    void getTemplatesWithErrorCall() throws Exception {
        when(notificationTemplateClient.getAll()).thenThrow(new TException());

        mockMvc.perform(get("/notifications/templates"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.code", is(NOTIFICATOR_CALL_EXCEPTION)))
                .andExpect(jsonPath("$.message", is("Error call notificator getAll templates")));
    }

    @Test
    void getTemplates() throws Exception {
        var templates = TestObjectFactory.testNotificationTemplates(3);
        when(notificationTemplateClient.getAll())
                .thenReturn(new NotificationTemplateListResponse().setNotificationTemplates(templates));

        mockMvc.perform(get("/notifications/templates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.[*]", hasSize(templates.size())));
    }
}