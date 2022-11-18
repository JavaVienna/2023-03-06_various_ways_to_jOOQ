import org.jooq.meta.jaxb.Property
import nu.studer.gradle.jooq.JooqGenerate

plugins {
    id("java")
    id("nu.studer.jooq") version "8.0"
}

dependencies {
    jooqGenerator(project(":jooq-testcontainers-database"))
    jooqGenerator(project(":schema"))
}

jooq {
    version.set(libs.versions.jooq)

    configurations {
        create("main") {
            jooqConfiguration.apply {
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    target.apply {
                        packageName = "at.softwarecraftsmen.jooqshowcase"
                    }
                    database.apply {
                        name = "com.softwarecraftsmen.persistenceshowcase.jooqtestcontainers.PostgresTestcontainersDatabase"
                        properties.add(Property().apply {
                            key = "scripts"
                            value = "db/migration"
                        })
                        inputSchema = "public"
                        outputSchema = "photography"
                    }
                    generate.apply {
                        isRecords = true
                        isDaos = true
                        isPojos = true
                        isJavaTimeTypes = true
                        isFluentSetters = true
                    }
                }
            }
        }
    }
}

tasks.named<JooqGenerate>("generateJooq") {
    allInputsDeclared.set(true)
}
