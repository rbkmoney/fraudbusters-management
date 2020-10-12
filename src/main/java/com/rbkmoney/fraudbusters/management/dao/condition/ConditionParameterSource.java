package com.rbkmoney.fraudbusters.management.dao.condition;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jooq.Comparator;
import org.jooq.Field;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode
@ToString
public class ConditionParameterSource {

    @Getter
    private List<ConditionField> conditionFields;

    public ConditionParameterSource() {
        this.conditionFields = new ArrayList<>();
    }

    public <T> ConditionParameterSource addValue(Field<T> field, T value, Comparator comparator) {
        if (value != null) {
            ConditionField conditionField = new SimpleConditionField<>(field, value, comparator);
            conditionFields.add(conditionField);
        }
        return this;
    }

}
