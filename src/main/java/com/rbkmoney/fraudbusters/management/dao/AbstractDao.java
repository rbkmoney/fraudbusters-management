package com.rbkmoney.fraudbusters.management.dao;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.fraudbusters.management.dao.condition.ConditionField;
import com.rbkmoney.fraudbusters.management.dao.condition.ConditionParameterSource;
import org.jooq.Condition;
import org.jooq.EnumType;
import org.jooq.Operator;
import org.jooq.Param;
import org.jooq.impl.DSL;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.Map;

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
}
