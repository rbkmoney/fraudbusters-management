package com.rbkmoney.fraudbusters.management.domain.p2p.response;

import com.rbkmoney.fraudbusters.management.domain.p2p.P2pReferenceModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilterP2pReferenceResponse {

    private List<P2pReferenceModel> referenceModels;
    private Integer count;

}
