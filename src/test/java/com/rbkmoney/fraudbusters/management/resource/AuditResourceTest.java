package com.rbkmoney.fraudbusters.management.resource;

import com.rbkmoney.fraudbusters.management.dao.audit.CommandAuditDao;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = {FlywayAutoConfiguration.class, JooqAutoConfiguration.class})
class AuditResourceTest {

    @MockBean
    private CommandAuditDao commandAuditDao;

    @Autowired
    private MockMvc mockMvc;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(AuditResource.YYYY_MM_DD_HH_MM_SS);

    @Test
    void filter() throws Exception {
        Mockito.when(commandAuditDao.filterLog(any(), any(), any(), any(), any())).thenReturn(List.of());
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("from", LocalDateTime.now().format(formatter));
        params.add("to", LocalDateTime.now().format(formatter));
        params.add("commandTypes", "[]");
        params.add("objectTypes", "[]");
        this.mockMvc.perform(get("/audit/filter").queryParams(params))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getCommandTypes() throws Exception {
        this.mockMvc.perform(get("/audit/commandTypes"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getObjectTypes() throws Exception {
        this.mockMvc.perform(get("/audit/objectTypes"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
