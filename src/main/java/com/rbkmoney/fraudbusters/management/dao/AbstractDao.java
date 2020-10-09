package com.rbkmoney.fraudbusters.management.dao;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.fraudbusters.management.dao.condition.ConditionField;
import com.rbkmoney.fraudbusters.management.dao.condition.ConditionParameterSource;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

public abstract class AbstractDao extends AbstractGenericDao {

    public AbstractDao(DataSource dataSource) {
        super(dataSource);
    }

    protected Condition appendConditions(Condition condition, Operator operator, ConditionParameterSource conditionParameterSource) {
        for (ConditionField field : conditionParameterSource.getConditionFields()) {
            if (field.getValue() != null) {
                condition = DSL.condition(operator, condition, buildCondition(field));
            }
        }
        return condition;
    }

    private Condition buildCondition(ConditionField field) {
        return field.getField().compare(
                field.getComparator(),
                field.getValue()
        );
    }

    protected <T extends Record> SelectForUpdateStep<T> addSeekIfNeed(String lastId,
                                                                      Integer size,
                                                                      SelectSeekStep1<T, String> orderQuery) {
        SelectForUpdateStep<T> seekQuery;
        if (!StringUtils.isEmpty(lastId)) {
            seekQuery = orderQuery
                    .seek(lastId)
                    .limit(size);
        } else {
            seekQuery = orderQuery.limit(size);
        }
        return seekQuery;
    }

    protected <T extends Record> SelectSeekStep1<T, String> addSortCondition(TableField<T, String> sortField,
                                                                             SortOrder sortOrder,
                                                                             SelectConditionStep<T> whereQuery) {
        SelectSeekStep1<T, String> orderQuery;
        if (sortOrder.equals(SortOrder.DESC)) {
            orderQuery = whereQuery.orderBy(sortField.desc());
        } else {
            orderQuery = whereQuery.orderBy(sortField.asc());
        }
        return orderQuery;
    }
}
