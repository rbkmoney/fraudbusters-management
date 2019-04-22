package com.rbkmoney.fraudbusters.management.dao.template;

import com.rbkmoney.fraudbusters.management.domain.enums.ListType;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
import com.rbkmoney.fraudbusters.management.exception.DaoException;

import java.util.List;

public interface TemplateDao {

    void saveListRecord(WbListRecords listRecord) throws DaoException;

    void removeRecord(String id) throws DaoException;

    void removeRecord(WbListRecords listRecord) throws DaoException;

    WbListRecords getById(String id) throws DaoException;

    List<WbListRecords> getFilteredListRecords(String partyId, String shopId, ListType listType, String listName) throws DaoException;

}
