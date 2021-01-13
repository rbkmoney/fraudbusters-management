package com.rbkmoney.fraudbusters.management.dao;

import org.flywaydb.core.Flyway;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.Duration;

@RunWith(SpringRunner.class)
@EnableConfigurationProperties({DataSourceProperties.class})
@ContextConfiguration(classes = {DataSourceAutoConfiguration.class, JdbcTemplateAutoConfiguration.class},
        initializers = AbstractPostgresIntegrationTest.Initializer.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public abstract class AbstractPostgresIntegrationTest {

    @ClassRule
    public static PostgreSQLContainer postgres = (PostgreSQLContainer) new PostgreSQLContainer("postgres:9.6")
            .withStartupTimeout(Duration.ofMinutes(5));

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgres.getJdbcUrl(),
                    "spring.datasource.username=" + postgres.getUsername(),
                    "spring.datasource.password=" + postgres.getPassword(),
                    "flyway.url=" + postgres.getJdbcUrl(),
                    "flyway.user=" + postgres.getUsername(),
                    "flyway.password=" + postgres.getPassword()
            ).applyTo(configurableApplicationContext);
            Flyway flyway = Flyway.configure()
                    .dataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())
                    .schemas("af")
                    .load();
            flyway.migrate();
        }
    }

}
