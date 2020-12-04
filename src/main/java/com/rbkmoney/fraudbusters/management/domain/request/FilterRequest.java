package com.rbkmoney.fraudbusters.management.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jooq.SortOrder;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class FilterRequest {
    String searchValue;
    String lastId;
    String sortFieldValue;
    Integer size;
    String sortBy;
    SortOrder sortOrder;
}
