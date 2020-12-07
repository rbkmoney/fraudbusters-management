package com.rbkmoney.fraudbusters.management.utils;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.Principal;

@Service
public class UserInfoService {

    public static final String UNKNOWN = "UNKNOWN";

    public String getUserName(Principal principal) {
        if (principal == null || StringUtils.isEmpty(principal.getName())) {
            return UNKNOWN;
        }
        return principal.getName();
    }

}
