package com.rbkmoney.fraudbusters.management.service.iface;

import com.rbkmoney.damsel.fraudbusters_notificator.*;

public interface ChannelService {

    Channel create(Channel channel);

    void delete(String name);

    ChannelListResponse getAll(Page page, Filter filter);

    ChannelTypeListResponse getAllTypes();

    Channel getById(String name);

}
