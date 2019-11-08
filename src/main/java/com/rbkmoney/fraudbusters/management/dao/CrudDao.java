package com.rbkmoney.fraudbusters.management.dao;

public interface CrudDao<T> {

    void saveListRecord(T listRecord);

    void removeRecord(T listRecord);

}
