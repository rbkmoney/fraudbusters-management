package com.rbkmoney.fraudbusters.management.dao.condition;

import org.jooq.Comparator;
import org.jooq.Field;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by tolkonepiu on 31/05/2017.
 */
public class ConditionParameterSource {

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

    public List<ConditionField> getConditionFields() {
        return conditionFields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConditionParameterSource that = (ConditionParameterSource) o;

        return conditionFields.equals(that.conditionFields);
    }

    @Override
    public int hashCode() {
        return conditionFields.hashCode();
    }

    @Override
    public String toString() {
        return "ConditionParameterSource{" +
                "conditionFields=" + conditionFields +
                '}';
    }
}
