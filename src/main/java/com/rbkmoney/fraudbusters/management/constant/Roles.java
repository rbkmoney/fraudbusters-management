package com.rbkmoney.fraudbusters.management.constant;


import lombok.Getter;

public enum Roles {

    FRAUD_SUPPORT("fraud-support"),
    FRAUD_MONITORING("fraud-monitoring"),
    FRAUD_OFFICER("fraud-officer");

    @Getter
    private final String value;

    Roles(String value) {
        this.value = value;
    }

}
