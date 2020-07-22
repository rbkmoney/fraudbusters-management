package com.rbkmoney.fraudbusters.management.resource.notificator.domain;

import com.rbkmoney.fraudbusters.management.resource.notificator.constant.ChannelType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Channel {

    private String name;
    private LocalDateTime createdAt;
    private ChannelType type;
    private String destination;
    private String subject;

}