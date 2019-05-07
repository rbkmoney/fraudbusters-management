package com.rbkmoney.fraudbusters.management.utils;

import com.rbkmoney.damsel.fraudbusters.TemplateReference;
import io.micrometer.shaded.io.netty.util.internal.StringUtil;

public class ReferenceKeyGenerator {

    public static final String SEPARATOR = "_";
    public static final String GLOBAL = "GLOBAL";

    public static String generateTemplateKey(TemplateReference reference) {
        if (reference.is_global) {
            return GLOBAL;
        }
        return generateTemplateKey(reference.getPartyId(), reference.getShopId());
    }

    public static String generateTemplateKey(String partyId, String shopId) {
        if (StringUtil.isNullOrEmpty(shopId)
                && !StringUtil.isNullOrEmpty(partyId)) {
            return partyId;
        } else if (!StringUtil.isNullOrEmpty(shopId)
                && !StringUtil.isNullOrEmpty(partyId)) {
            return partyId + SEPARATOR + shopId;
        }
        return null;
    }

}
