package com.rbkmoney.fraudbusters.management.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.Principal;

@Service
public class UserInfoService {

    public static final String UNKNOWN = "UNKNOWN";

    public String getUserName() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return UNKNOWN;
        }
        return ((Principal) authentication.getPrincipal()).getName();
    }

    public String getUserName(Principal principal) {
        if (principal == null || !StringUtils.hasText(principal.getName())) {
            return UNKNOWN;
        }
        return principal.getName();
    }
}
