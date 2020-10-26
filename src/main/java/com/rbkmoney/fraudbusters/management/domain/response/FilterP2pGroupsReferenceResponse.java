package com.rbkmoney.fraudbusters.management.domain.response;

import com.rbkmoney.fraudbusters.management.domain.p2p.P2pGroupReferenceModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilterP2pGroupsReferenceResponse {

    private List<P2pGroupReferenceModel> groupsReferenceModels;
    private Integer count;

}
