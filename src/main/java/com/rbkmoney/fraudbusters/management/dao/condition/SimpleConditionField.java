package com.rbkmoney.fraudbusters.management.dao.condition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jooq.Comparator;
import org.jooq.Field;

@Getter
@AllArgsConstructor
public class SimpleConditionField<T> implements ConditionField<T, T> {

    private final Field<T> field;
    private final T value;
    private final Comparator comparator;

}
