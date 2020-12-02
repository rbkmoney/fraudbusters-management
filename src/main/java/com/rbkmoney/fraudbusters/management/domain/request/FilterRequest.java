package com.rbkmoney.fraudbusters.management.domain.request;

import lombok.Data;
import org.jooq.SortOrder;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class FilterRequest {
    String searchValue;
    String lastId;
    String sortFieldValue;
    Integer size;
    String sortBy;
    SortOrder sortOrder;
}
