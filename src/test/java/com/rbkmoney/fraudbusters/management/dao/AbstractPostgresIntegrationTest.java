package com.rbkmoney.fraudbusters.management.dao;

import com.rbkmoney.testcontainers.annotations.postgresql.PostgresqlTestcontainerSingleton;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;

@PostgresqlTestcontainerSingleton
@JooqTest
public abstract class AbstractPostgresIntegrationTest {

}
