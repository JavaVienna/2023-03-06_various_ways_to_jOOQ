# Persistence Showcase

## Prerequisites
In order to run the tests in this repository, you need
* Java 17+
* Docker (for tests with Testcontainers)

## Submodules
The repository is divided into several submodules that showcase different aspects of jOOQ, (Spring Data) JPA and combinations of the technologies.

### Code Generation for jOOQ
These submodules show different ways of generating code for jOOQ:

* **jooq-flyway-generation**: Shows how to generate code from Flyway scripts, using jOOQ's DDLDatabase.
* **jooq-testcontainers-generation**: Also generates code from Flyway scripts, but uses Postgres with Testcontainers. That way, all Postgres-specific things are fully supported. The **jooq-testcontainers-database** submodule enables this way of code generation.
* **jooq-spring-data-jpa-and-jooq**: Minimal setup to generate jOOQ code based on existing JPA entities (which are defined in the **spring-data-jpa-entities**). Note that keeping the entities in a separate module is the recommended way of doing this, which is a downside to this approach.

### Other Modules in this Project
* **schema**: The schema used throughout this repository is defined here (as a Flyway script).
* **jooq-showcase**: Showcases the most important jOOQ features, in the form of tests.
* **test-database**: Allows starting a local Postgres instance, filled with some test data generated on-the-fly.
* **derived-production-database**: Contains some changes to the schema, and a test that showcases how jOOQ can be used to detect schema diffs. 
