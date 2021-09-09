package com.rbkmoney.fraudbusters.management.converter;

public interface BiConverter<S, T> {

    T toTarget(S s);

    S toSource(T t);
}
