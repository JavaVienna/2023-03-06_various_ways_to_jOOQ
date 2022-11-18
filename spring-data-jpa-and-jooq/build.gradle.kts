import org.jooq.meta.jaxb.Property

plugins {
    id("java")
    id("org.springframework.boot") version "3.0.2"
    id("io.spring.dependency-management") version "1.1.0"
    id("nu.studer.jooq") version "8.0"
}

java.sourceCompatibility = JavaVersion.VERSION_17

dependencies {
    jooqGenerator(libs.bundles.jooqMetaExtensionsHibernate)
    jooqGenerator(project(":spring-data-jpa-entities"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jooq {
    version.set(libs.versions.jooq)

    configurations {
        create("main") {
            jooqConfiguration.apply {
                generator.apply {
                    database.apply {
                        name = "org.jooq.meta.extensions.jpa.JPADatabase"
                        properties.add(Property().apply {
                            key = "packages"
                            value = "at.softwarecraftsmen.persistenceshowcase.entity"
                        })
                    }
                }
            }
        }
    }
}
