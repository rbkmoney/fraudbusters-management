package com.rbkmoney.fraudbusters.management.domain;

import com.rbkmoney.fraudbusters.management.domain.tables.pojos.CommandAudit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandAuditFilterResponse {

    private List<CommandAudit> logs;
    private Integer count;

}
