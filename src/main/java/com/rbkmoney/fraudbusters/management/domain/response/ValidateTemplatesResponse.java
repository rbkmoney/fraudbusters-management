package com.rbkmoney.fraudbusters.management.domain.response;

import com.rbkmoney.fraudbusters.management.domain.ErrorTemplateModel;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ValidateTemplatesResponse {

    private List<ErrorTemplateModel> validateResults;

}
