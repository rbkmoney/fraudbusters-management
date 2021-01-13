package com.rbkmoney.fraudbusters.management.domain.p2p;

import com.rbkmoney.fraudbusters.management.domain.ReferenceModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class P2pReferenceModel extends ReferenceModel {

    private String identityId;

}
