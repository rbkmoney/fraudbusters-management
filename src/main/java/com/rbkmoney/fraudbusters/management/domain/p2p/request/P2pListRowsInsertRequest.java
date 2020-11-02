package com.rbkmoney.fraudbusters.management.domain.p2p.request;

import com.rbkmoney.damsel.wb_list.ListType;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pCountInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class P2pListRowsInsertRequest {

    private ListType listType;
    private List<P2pCountInfo> records;
}
