package com.rbkmoney.fraudbusters.management.dao;

public interface CdDao<T> {

    void saveListRecord(T listRecord);

    void removeRecord(T listRecord);

}
