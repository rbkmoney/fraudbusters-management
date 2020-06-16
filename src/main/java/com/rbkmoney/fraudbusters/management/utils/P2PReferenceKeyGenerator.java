package com.rbkmoney.fraudbusters.management.utils;

import com.rbkmoney.damsel.fraudbusters.P2PReference;

public class P2PReferenceKeyGenerator {

    public static final String SEPARATOR = "_";
    public static final String GLOBAL = "GLOBAL";

    public static String generateTemplateKey(P2PReference reference) {
        if (reference.is_global) {
            return GLOBAL;
        }
        return generateTemplateKey(reference.getIdentityId());
    }

    public static String generateTemplateKey(String identityId) {
        return identityId;
    }

}
