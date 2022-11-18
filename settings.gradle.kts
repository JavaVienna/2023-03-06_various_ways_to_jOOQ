rootProject.name = "persistence-showcase"

include(
    "jooq-flyway-generation",
    "jooq-showcase",
    "jooq-testcontainers-database",
    "jooq-testcontainers-generation",
    "test-database",
    "schema",
    "spring-data-jpa-entities",
    "spring-data-jpa-and-jooq"
)

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("flyway", "org.flywaydb", "flyway-core").version("9.10.1")

            val jooqVersion = version("jooq", "3.17.5")
            library("jooq", "org.jooq", "jooq").versionRef(jooqVersion)
            library("jooqMetaExtensions", "org.jooq", "jooq-meta-extensions").versionRef(jooqVersion)
            library("jooqMetaExtensionsHibernate", "org.jooq", "jooq-meta-extensions-hibernate").versionRef(jooqVersion)

            library("lombok", "org.projectlombok", "lombok").version("1.18.24")

            val springBootVersion = version("spring-boot", "3.0.2")
            library("springDataJpa", "org.springframework.boot", "spring-boot-starter-data-jpa").versionRef(springBootVersion)
            library("springValidation", "org.springframework.boot", "spring-boot-starter-validation").versionRef(springBootVersion)

            library("jakartaXmlBind", "jakarta.xml.bind", "jakarta.xml.bind-api").version("4.0.0")
            library("jakartaPersistence", "jakarta.persistence", "jakarta.persistence-api").version("3.1.0")
            library("jakartaTransaction", "jakarta.transaction", "jakarta.transaction-api").version("2.0.1")
            bundle("jooqMetaExtensionsHibernate", listOf(
                "jooqMetaExtensionsHibernate",
                "jakartaXmlBind",
                "jakartaPersistence",
                "jakartaTransaction"
            ))

            library("postgres", "org.postgresql:postgresql:42.5.1")
        }

        create("testLibs") {
            val assertJVersion = version("assertJ", "3.23.1")
            library("assertJ", "org.assertj", "assertj-core").versionRef(assertJVersion)

            val junitVersion = version("junit5", "5.9.2")
            library("junit-api", "org.junit.jupiter", "junit-jupiter-api").versionRef(junitVersion)
            library("junit-engine", "org.junit.jupiter", "junit-jupiter-engine").versionRef(junitVersion)
            library("junit-params", "org.junit.jupiter", "junit-jupiter-params").versionRef(junitVersion)

            val springBootVersion = version("spring-boot", "3.0.2")
            library("springDataTest", "org.springframework.boot", "spring-boot-starter-test").versionRef(springBootVersion)

            val testcontainersVersion = version("testcontainers", "1.17.4")
            library("testcontainers", "org.testcontainers", "testcontainers").versionRef(testcontainersVersion)
            library("testcontainersJunit", "org.testcontainers", "junit-jupiter").versionRef(testcontainersVersion)
            library("testcontainersPostgres", "org.testcontainers", "postgresql").versionRef(testcontainersVersion)
            bundle("testcontainersPostgres", listOf("testcontainers", "testcontainersJunit", "testcontainersPostgres"))

            bundle("testing", listOf("assertJ", "junit-api", "junit-engine", "junit-params"))
        }
    }
}
include("derived-production-databse")
