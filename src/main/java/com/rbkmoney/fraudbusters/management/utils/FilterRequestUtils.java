package com.rbkmoney.fraudbusters.management.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilterRequestUtils {

    public static String prepareSearchValue(String searchValue) {
        if (StringUtils.hasText(searchValue)) {
            return "%" + searchValue + "%";
        }
        return searchValue;
    }

}
