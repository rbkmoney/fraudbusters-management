package com.rbkmoney.fraudbusters.management.domain.p2p.response;

import com.rbkmoney.fraudbusters.management.domain.p2p.DefaultP2pReferenceModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilterDefaultP2pReferenceResponse {

    private List<DefaultP2pReferenceModel> referenceModels;
    private Integer count;

}
