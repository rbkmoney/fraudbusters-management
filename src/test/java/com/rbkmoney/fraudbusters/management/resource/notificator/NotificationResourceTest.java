package com.rbkmoney.fraudbusters.management.resource.notificator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rbkmoney.damsel.fraudbusters_notificator.*;
import com.rbkmoney.fraudbusters.management.TestObjectFactory;
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

import static org.hamcrest.Matchers.hasSize;
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
        this.mockMvc = MockMvcBuilders.standaloneSetup(notificationResource).build();
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
    void removeChannel() throws Exception {
        String channelName = TestObjectFactory.randomString();

        mockMvc.perform(delete("/notifications/channels/{name}", channelName))
                .andExpect(status().isNoContent())
                .andReturn();

        verify(channelClient, times(1)).remove(channelName);
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
    void getNotifications() throws Exception {
        long lastId = 1L;
        int size = 10;
        String searchValue = TestObjectFactory.randomString();
        var notifications = TestObjectFactory.testInternalNotifications(3);
        when(notificationClient
                .getAll(new Page(size).setContinuationId(lastId), new Filter().setSearchField(searchValue))
        )
                .thenReturn(new NotificationListResponse().setNotifications(notifications));

        mockMvc.perform(get("/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .param("lastId", String.valueOf(lastId))
                .param("size", String.valueOf(size))
                .param("searchValue", searchValue))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.[*]", hasSize(notifications.size())));

    }

    @Test
    void getChannels() throws Exception {
        long lastId = 1L;
        int size = 10;
        String searchValue = TestObjectFactory.randomString();
        var channels = TestObjectFactory.testInternalChannels(3);
        when(channelClient
                .getAll(new Page(size).setContinuationId(lastId), new Filter().setSearchField(searchValue))
        )
                .thenReturn(new ChannelListResponse().setChannels(channels));

        mockMvc.perform(get("/notifications/channels")
                .contentType(MediaType.APPLICATION_JSON)
                .param("lastId", String.valueOf(lastId))
                .param("size", String.valueOf(size))
                .param("searchValue", searchValue))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.[*]", hasSize(channels.size())));
    }

    @Test
    void updateNotificationStatus() throws Exception {
        var status = new com.rbkmoney.swag.fraudbusters.management.model.NotificationStatus();
        status.setStatus(com.rbkmoney.swag.fraudbusters.management.model.NotificationStatus.StatusEnum.ACTIVE);
        long id = 1L;

        mockMvc.perform(put("/notifications/{id}/status", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(status)))
                .andExpect(status().isNoContent())
                .andReturn();

        verify(notificationClient, times(1))
                .updateStatus(id, NotificationStatus.valueOf(status.getStatus().getValue()));

    }

    @Test
    void getChannelTypes() throws Exception {
        var channelTypes =
                List.of(com.rbkmoney.swag.fraudbusters.management.model.ChannelType.TypeEnum.MAIL.getValue());
        when(channelClient.getAllTypes()).thenReturn(new ChannelTypeListResponse().setChannelTypes(channelTypes));

        mockMvc.perform(get("/notifications/channels/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.[*]", hasSize(channelTypes.size())));

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