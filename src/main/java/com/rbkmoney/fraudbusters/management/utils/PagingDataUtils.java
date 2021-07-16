package com.rbkmoney.fraudbusters.management.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jooq.SortOrder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PagingDataUtils {

    public static SortOrder getSortOrder(String sortOrder) {
        return sortOrder != null ? SortOrder.valueOf(sortOrder) : null;
    }

}
