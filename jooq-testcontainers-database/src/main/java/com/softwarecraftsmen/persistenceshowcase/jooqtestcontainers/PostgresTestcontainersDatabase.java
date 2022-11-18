package com.softwarecraftsmen.persistenceshowcase.jooqtestcontainers;

import org.flywaydb.core.Flyway;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.meta.IndexDefinition;
import org.jooq.meta.TableDefinition;
import org.jooq.meta.postgres.PostgresDatabase;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.SQLException;

public class PostgresTestcontainersDatabase extends PostgresDatabase {

    private final PostgreSQLContainer<?> postgres;

    public PostgresTestcontainersDatabase() {
        var dockerImageName = DockerImageName.parse("postgres:15.1-alpine");
        postgres = new PostgreSQLContainer<>(dockerImageName);
        addFilter(definition ->
            definition instanceof TableDefinition && definition.getName().equals("flyway_schema_history") ||
            definition instanceof IndexDefinition && definition.getName().equals("flyway_schema_history_s_idx")
        );
    }

    @Override
    protected DSLContext create0() {
        return DSL.using(connection());
    }

    protected Connection connection() {
        if (getConnection() == null) {
            postgres.start();
            createSchema();
            setConnection(createConnection(postgres));
        }

        return getConnection();
    }

    private void createSchema() {
        Flyway.configure()
            .dataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())
            .locations(getProperties().getProperty("scripts"))
            .failOnMissingLocations(true)
            .load()
            .migrate();
    }

    private Connection createConnection(PostgreSQLContainer<?> postgres) {
        try {
            return postgres.createConnection("");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        super.close();
        postgres.close();
    }
}
