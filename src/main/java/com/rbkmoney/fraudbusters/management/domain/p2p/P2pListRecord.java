package com.rbkmoney.fraudbusters.management.domain.p2p;

import com.rbkmoney.fraudbusters.management.domain.ListRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class P2pListRecord extends ListRecord {

    private String identityId;
}
