package com.rbkmoney.fraudbusters.management.utils;

import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilterRequestUtils {

    public static void prepareFilterRequest(FilterRequest filterRequest) {
        if (!StringUtils.isEmpty(filterRequest.getSearchValue())) {
            filterRequest.setSearchValue("%" + filterRequest.getSearchValue() + "%");
        }
    }

}
