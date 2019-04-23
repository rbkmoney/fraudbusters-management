package com.rbkmoney.fraudbusters.management.domain;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ListRecord {

    @NotNull
    private String partyId;
    @NotNull
    private String shopId;
    @NotNull
    private String listName;
    @NotNull
    private String value;

}
