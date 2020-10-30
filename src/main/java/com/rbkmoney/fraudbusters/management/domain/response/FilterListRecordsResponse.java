package com.rbkmoney.fraudbusters.management.domain.response;

import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilterListRecordsResponse {

    private List<WbListRecords> wbListRecords;
    private Integer count;

}
