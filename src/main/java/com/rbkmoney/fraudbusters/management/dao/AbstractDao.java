package com.rbkmoney.fraudbusters.management.dao;

import com.rbkmoney.fraudbusters.management.dao.condition.ConditionField;
import com.rbkmoney.fraudbusters.management.dao.condition.ConditionParameterSource;
import com.rbkmoney.fraudbusters.management.exception.DaoException;
import org.jooq.*;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.core.NestedRuntimeException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Created by tolkonepiu on 29/05/2017.
 */
public abstract class AbstractDao extends NamedParameterJdbcDaoSupport {

    private final DSLContext dslContext;

    public AbstractDao(DataSource dataSource) {
        setDataSource(dataSource);
        Configuration configuration = new DefaultConfiguration();
        configuration.set(SQLDialect.POSTGRES_9_5);
        this.dslContext = DSL.using(configuration);
    }

    protected DSLContext getDslContext() {
        return dslContext;
    }

    public <T> T fetchOne(Query query, Class<T> type) throws DaoException {
        return fetchOne(query, type, getNamedParameterJdbcTemplate());
    }

    public <T> T fetchOne(Query query, Class<T> type, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException {
        return fetchOne(query, new SingleColumnRowMapper<>(type), namedParameterJdbcTemplate);
    }

    public <T> T fetchOne(Query query, RowMapper<T> rowMapper) throws DaoException {
        return fetchOne(query, rowMapper, getNamedParameterJdbcTemplate());
    }

    public <T> T fetchOne(String namedSql, SqlParameterSource parameterSource, RowMapper<T> rowMapper) throws DaoException {
        return fetchOne(namedSql, parameterSource, rowMapper, getNamedParameterJdbcTemplate());
    }

    public <T> T fetchOne(Query query, RowMapper<T> rowMapper, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException {
        return fetchOne(query.getSQL(ParamType.NAMED), toSqlParameterSource(query.getParams()), rowMapper, namedParameterJdbcTemplate);
    }

    public <T> T fetchOne(String namedSql, SqlParameterSource parameterSource, RowMapper<T> rowMapper, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException {
        try {
            return namedParameterJdbcTemplate.queryForObject(
                    namedSql,
                    parameterSource,
                    rowMapper
            );
        } catch (EmptyResultDataAccessException ex) {
            return null;
        } catch (NestedRuntimeException ex) {
            throw new DaoException(ex);
        }
    }

    public <T> List<T> fetch(Query query, RowMapper<T> rowMapper) throws DaoException {
        return fetch(query, rowMapper, getNamedParameterJdbcTemplate());
    }

    public <T> List<T> fetch(String namedSql, SqlParameterSource parameterSource, RowMapper<T> rowMapper) throws DaoException {
        return fetch(namedSql, parameterSource, rowMapper, getNamedParameterJdbcTemplate());
    }

    public <T> List<T> fetch(Query query, RowMapper<T> rowMapper, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException {
        return fetch(query.getSQL(ParamType.NAMED), toSqlParameterSource(query.getParams()), rowMapper, namedParameterJdbcTemplate);
    }

    public <T> List<T> fetch(String namedSql, SqlParameterSource parameterSource, RowMapper<T> rowMapper, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException {
        try {
            return namedParameterJdbcTemplate.query(
                    namedSql,
                    parameterSource,
                    rowMapper
            );
        } catch (NestedRuntimeException e) {
            throw new DaoException(e);
        }
    }

    public void executeOne(Query query) throws DaoException {
        execute(query, 1);
    }

    public void execute(Query query) throws DaoException {
        execute(query, -1);
    }

    public void execute(Query query, int expectedRowsAffected) throws DaoException {
        execute(query, expectedRowsAffected, getNamedParameterJdbcTemplate());
    }

    public void execute(Query query, int expectedRowsAffected, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException {
        execute(query.getSQL(ParamType.NAMED), toSqlParameterSource(query.getParams()), expectedRowsAffected, namedParameterJdbcTemplate);
    }

    public void executeOne(String namedSql, SqlParameterSource parameterSource) throws DaoException {
        execute(namedSql, parameterSource, 1);
    }

    public void execute(String namedSql, SqlParameterSource parameterSource) throws DaoException {
        execute(namedSql, parameterSource, -1);
    }

    public void execute(String namedSql, SqlParameterSource parameterSource, int expectedRowsAffected) throws DaoException {
        execute(namedSql, parameterSource, expectedRowsAffected, getNamedParameterJdbcTemplate());
    }

    public void execute(String namedSql, SqlParameterSource parameterSource, int expectedRowsAffected, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException {
        try {
            int rowsAffected = namedParameterJdbcTemplate.update(
                    namedSql,
                    parameterSource);

            if (expectedRowsAffected != -1 && rowsAffected != expectedRowsAffected) {
                throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(namedSql, expectedRowsAffected, rowsAffected);
            }
        } catch (NestedRuntimeException ex) {
            throw new DaoException(ex);
        }
    }

    protected SqlParameterSource toSqlParameterSource(Map<String, Param<?>> params) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        for (Map.Entry<String, Param<?>> entry : params.entrySet()) {
            Param<?> param = entry.getValue();
            if (param.getValue() instanceof String) {
                sqlParameterSource.addValue(entry.getKey(), ((String) param.getValue()).replace("\u0000", "\\u0000"));
            } else if (param.getValue() instanceof LocalDateTime || param.getValue() instanceof EnumType) {
                sqlParameterSource.addValue(entry.getKey(), param.getValue(), Types.OTHER);
            } else {
                sqlParameterSource.addValue(entry.getKey(), param.getValue());
            }
        }
        return sqlParameterSource;
    }

    protected Condition appendConditions(Condition condition, Operator operator, ConditionParameterSource conditionParameterSource) {
        for (ConditionField field : conditionParameterSource.getConditionFields()) {
            if (field.getValue() != null) {
                condition = DSL.condition(operator, condition, buildCondition(field));
            }
        }
        return condition;
    }

    private Condition buildCondition(ConditionField field) {
        return field.getField().compare(
                field.getComparator(),
                field.getValue()
        );
    }
}
