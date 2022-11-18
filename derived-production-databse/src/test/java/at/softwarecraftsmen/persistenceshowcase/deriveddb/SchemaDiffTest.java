package at.softwarecraftsmen.persistenceshowcase.deriveddb;

import org.jooq.DSLContext;
import org.jooq.Meta;
import org.jooq.Queries;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static java.util.stream.Collectors.joining;
import static org.jooq.SQLDialect.POSTGRES;

class SchemaDiffTest {

    private DSLContext dsl;
    private Queries originalSchema;
    private Queries derivedSchema;

    @BeforeEach
    void setup() throws Exception {
        var settings = new Settings()
            .withParseIgnoreComments(true);
        dsl = DSL.using(POSTGRES, settings);

        originalSchema = readQueriesFromResource(dsl, "db/migration/V1__schema.sql");
        derivedSchema = originalSchema.concat(readQueriesFromResource(dsl, "db/migration/V2__derived_schema.sql"));
    }

    @Test
    void schema_diff() {
        Meta originalMeta = dsl.meta(originalSchema.queries());
        Meta derivedMeta = dsl.meta(derivedSchema.queries());

        originalMeta.migrateTo(derivedMeta).queryStream().forEach(query -> {
            System.out.println(query + ";");
            System.out.println();
        });
    }

    private Queries readQueriesFromResource(DSLContext dsl, String name) throws Exception {
        return dsl.parser().parse(readResource(name));
    }

    private String readResource(String name) throws Exception {
        var classLoader = ClassLoader.getSystemClassLoader();
        try (
            var stream = classLoader.getResourceAsStream(name);
            var reader = new InputStreamReader(stream);
            var bufferedReader = new BufferedReader(reader)
        ) {
            return bufferedReader.lines().collect(joining("\n"));
        }
    }
}
