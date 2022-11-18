package at.softwarecraftsmen.persistenceshowcase.deriveddb;

import org.flywaydb.core.Flyway;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.List;

public class StartDerivedProductionDatabase {

    public static void main(String[] args) throws IOException {
        try (var postgres = new StartDerivedProductionDatabase().start()) {
            System.in.read();
        }
    }

    public PostgreSQLContainer<?> start() {
        var dockerImageName = DockerImageName.parse("postgres:15.1-alpine");
        var postgres = new PostgreSQLContainer<>(dockerImageName)
            .withUsername("test")
            .withPassword("test");
        postgres.setPortBindings(List.of("5433:5432"));
        postgres.start();
        Flyway.configure()
            .dataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())
            .schemas("photography")
            .defaultSchema("photography")
            .locations("db/migration")
            .failOnMissingLocations(true)
            .load()
            .migrate();
        return postgres;
    }
}
