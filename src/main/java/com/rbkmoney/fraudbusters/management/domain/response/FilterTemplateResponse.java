package com.rbkmoney.fraudbusters.management.domain.response;

import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilterTemplateResponse {

    private List<TemplateModel> templateModels;
    private Integer count;

}
