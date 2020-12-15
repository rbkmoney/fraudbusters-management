package com.rbkmoney.fraudbusters.management.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ParametersService {

    @Value("${parameters.listNames}")
    private List<String> availableListNames;

    public List<String> getAvailableListNames() {
        return availableListNames;
    }
}
