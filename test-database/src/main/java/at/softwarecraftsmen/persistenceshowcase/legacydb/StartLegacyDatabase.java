package at.softwarecraftsmen.persistenceshowcase.legacydb;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static at.softwarecraftsmen.jooqshowcase.Photography.PHOTOGRAPHY;

public class StartLegacyDatabase {

    private static final DockerImageName POSTGRES_IMAGE_NAME = DockerImageName.parse("postgres");

    public static void main(String[] args) throws IOException, SQLException {
        var postgres = initPostgresContainer();
        postgres.start();

        var migrateResult = migrateSchema(postgres);

        if (!migrateResult.migrations.isEmpty()) {
            generateLegacyData(postgres);
        }

        block();
        postgres.stop();
    }

    private static PostgreSQLContainer<?> initPostgresContainer() {
        var postgres = new PostgreSQLContainer<>(POSTGRES_IMAGE_NAME)
            .withUsername("test")
            .withPassword("test");

        postgres.setPortBindings(List.of("5432:5432"));

        return postgres;
    }

    private static MigrateResult migrateSchema(PostgreSQLContainer<?> postgres) {
        return Flyway.configure()
            .dataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())
            .defaultSchema(PHOTOGRAPHY.getName())
            .schemas(PHOTOGRAPHY.getName())
            .locations("filesystem:./schema/src/main/resources/db/migration")
            .load()
            .migrate();
    }

    private static void generateLegacyData(PostgreSQLContainer<?> postgres) throws SQLException {
        var sql = DSL.using(postgres.createConnection(""), SQLDialect.POSTGRES);
        new LegacyDataGenerator(sql).fillWithTestData();
        System.out.println("Successfully loaded data");
    }

    private static void block() throws IOException {
        System.in.read();
    }
}
