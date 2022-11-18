import org.jooq.meta.jaxb.Property
import nu.studer.gradle.jooq.JooqGenerate

plugins {
    id("java")
    id("nu.studer.jooq") version "8.0"
}

dependencies {
    jooqGenerator(libs.jooqMetaExtensions)
    jooqGenerator(project(":schema"))
}

val flywayScriptsDir = "../schema/src/main/resources/db/migration"

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
                        name = "org.jooq.meta.extensions.ddl.DDLDatabase"
                        properties.add(Property().apply {
                            key = "scripts"
                            value = flywayScriptsDir
                        })
                        properties.add(Property().apply {
                            key = "defaultNameCase"
                            value = "lower"
                        })
                        inputSchema = "PUBLIC"
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
    inputs.dir(flywayScriptsDir)
}
