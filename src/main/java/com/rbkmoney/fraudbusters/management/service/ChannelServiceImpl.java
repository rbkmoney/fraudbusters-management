package com.rbkmoney.fraudbusters.management.service;

import com.rbkmoney.damsel.fraudbusters_notificator.*;
import com.rbkmoney.fraudbusters.management.service.iface.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService {

    private final ChannelServiceSrv.Iface channelClient;

    @Override
    public Channel create(Channel channel) {
        try {
            return channelClient.create(channel);
        } catch (TException e) {
            log.error("Error call ChannelService create ", e);
            return null;
        }
    }

    @Override
    public void delete(String name) {
        try {
            channelClient.remove(name);
        } catch (TException e) {
            log.error("Error call ChannelService remove ", e);
        }

    }

    @Override
    public ChannelListResponse getAll(Page page, Filter filter) {
        try {
            return channelClient.getAll(page, filter);
        } catch (TException e) {
            log.error("Error call ChannelService getAll ", e);
            return new ChannelListResponse()
                    .setChannels(Collections.emptyList());
        }
    }

    @Override
    public ChannelTypeListResponse getAllTypes() {
        try {
            return channelClient.getAllTypes();
        } catch (TException e) {
            log.error("Error call ChannelService getAllTypes ", e);
            return new ChannelTypeListResponse()
                    .setChannelTypes(Collections.emptyList());
        }
    }
}
