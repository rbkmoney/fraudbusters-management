package com.rbkmoney.fraudbusters.management.domain.p2p.response;

import com.rbkmoney.fraudbusters.management.domain.tables.pojos.P2pWbListRecords;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class P2pFilterListRecordsResponse {

    private List<P2pWbListRecords> wbListRecords;
    private Integer count;

}
