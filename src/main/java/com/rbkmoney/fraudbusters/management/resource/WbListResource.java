package com.rbkmoney.fraudbusters.management.resource;

import com.rbkmoney.fraudbusters.management.dao.wblist.WbListDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WbListResource {

    private final WbListDao wbListDao;

}
