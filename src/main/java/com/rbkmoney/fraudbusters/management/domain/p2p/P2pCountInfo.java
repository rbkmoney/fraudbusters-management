package com.rbkmoney.fraudbusters.management.domain.p2p;

import com.rbkmoney.fraudbusters.management.domain.CountInfo;
import lombok.Data;

@Data
public class P2pCountInfo {

    private CountInfo countInfo;
    private P2pListRecord listRecord;

}
