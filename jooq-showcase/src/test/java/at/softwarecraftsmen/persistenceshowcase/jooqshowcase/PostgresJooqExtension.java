package at.softwarecraftsmen.persistenceshowcase.jooqshowcase;

import org.flywaydb.core.Flyway;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.sql.SQLException;

import static at.softwarecraftsmen.jooqshowcase.Photography.PHOTOGRAPHY;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

public class PostgresJooqExtension
    implements BeforeAllCallback, BeforeEachCallback, ParameterResolver, CloseableResource
{

    private static final DockerImageName POSTGRES_IMAGE_NAME = DockerImageName.parse("postgres:15.1-alpine");
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(POSTGRES_IMAGE_NAME);
    private static DSLContext DSL_CONTEXT = null;

    @Override
    public void beforeAll(ExtensionContext context) throws SQLException {
        if (!POSTGRES.isRunning()) {
            POSTGRES.start();
            context.getRoot().getStore(GLOBAL).put(this.getClass().getName(), this);
            DSL_CONTEXT = DSL.using(POSTGRES.createConnection("?currentSchema=" + PHOTOGRAPHY.getName()), SQLDialect.POSTGRES);
        }
    }

    @Override
    public void close() {
        if (POSTGRES.isRunning()) {
            POSTGRES.stop();
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        DSL_CONTEXT.dropSchemaIfExists(PHOTOGRAPHY).cascade().execute();
        Flyway.configure()
            .dataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())
            .schemas(PHOTOGRAPHY.getName())
            .defaultSchema(PHOTOGRAPHY.getName())
            .locations("classpath:db/migration")
            .failOnMissingLocations(true)
            .load()
            .migrate();
    }

    @Override
    public boolean supportsParameter(
        ParameterContext parameterContext,
        ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(DSLContext.class);
    }

    @Override
    public Object resolveParameter(
        ParameterContext parameterContext,
        ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        return DSL_CONTEXT;
    }
}
