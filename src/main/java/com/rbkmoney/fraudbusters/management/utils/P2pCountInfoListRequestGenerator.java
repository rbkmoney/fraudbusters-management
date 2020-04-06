package com.rbkmoney.fraudbusters.management.utils;

import com.rbkmoney.fraudbusters.management.domain.CountInfo;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pCountInfo;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pListRecord;
import io.micrometer.shaded.io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class P2pCountInfoListRequestGenerator {

    private final CountInfoUtils countInfoGenerator;

    public P2pCountInfo initDestination(String rowInfo, P2pListRecord listRecord) {
        P2pCountInfo p2PCountInfo = new P2pCountInfo();
        p2PCountInfo.setListRecord(listRecord);
        if (!StringUtil.isNullOrEmpty(rowInfo)) {
            CountInfo countInfoValue = countInfoGenerator.initRowCountInfo(rowInfo);
            p2PCountInfo.setCountInfo(countInfoValue);
        }
        return p2PCountInfo;
    }

}
