package com.rbkmoney.fraudbusters.management.resource.notificator.converter;

public interface BiConverter<S, T> {

    T toTarget(S s);

    S toSource(T t);
}
