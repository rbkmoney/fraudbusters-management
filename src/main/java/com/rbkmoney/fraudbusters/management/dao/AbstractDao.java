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

    protected <T extends Record, E, P> SelectForUpdateStep<T> addSeekIfNeed(E lastId, P sortId,
                                                                            Integer size,
                                                                            SelectSeekStep2<T, P, E> orderQuery) {
        SelectForUpdateStep<T> seekQuery;
        if (lastId != null && sortId != null) {
            seekQuery = orderQuery
                    .seek(sortId, lastId)
                    .limit(size);
        } else {
            seekQuery = orderQuery.limit(size);
        }
        return seekQuery;
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

    protected <T extends Record, E> SelectSeekStep1<T, E> addSortCondition(Field<E> sortField,
                                                                           SortOrder sortOrder,
                                                                           SelectConditionStep<T> whereQuery) {
        SelectSeekStep1<T, E> orderQuery;
        if (sortOrder != null && sortOrder.equals(SortOrder.DESC)) {
            orderQuery = whereQuery.orderBy(sortField.desc());
        } else {
            orderQuery = whereQuery.orderBy(sortField.asc());
        }
        return orderQuery;
    }

    protected <T extends Record, E, P> SelectSeekStep2<T, E, P> addSortCondition(Field<P> sortField, Field<E> sortFieldSecond,
                                                                                 SortOrder sortOrder,
                                                                                 SelectConditionStep<T> whereQuery) {
        if (sortOrder == SortOrder.DESC) {
            return whereQuery.orderBy(sortFieldSecond.desc(), sortField.desc());
        } else {
            return whereQuery.orderBy(sortFieldSecond.asc(), sortField.desc());
        }
    }

}
