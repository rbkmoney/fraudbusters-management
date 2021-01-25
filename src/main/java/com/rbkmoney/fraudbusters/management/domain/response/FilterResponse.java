package com.rbkmoney.fraudbusters.management.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilterResponse<T> {

    private List<T> result;
    private Integer count;

}
