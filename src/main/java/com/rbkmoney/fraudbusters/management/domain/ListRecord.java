package com.rbkmoney.fraudbusters.management.domain;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ListRecord {

    private String partyId;

    private String shopId;
    @NotNull
    private String listName;
    @NotNull
    private String value;

}
