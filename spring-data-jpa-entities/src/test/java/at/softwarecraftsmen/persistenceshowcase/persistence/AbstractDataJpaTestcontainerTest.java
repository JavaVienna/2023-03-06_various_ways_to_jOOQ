package at.softwarecraftsmen.persistenceshowcase.persistence;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
//@Testcontainers
//@DirtiesContext
// see https://www.appsloveworld.com/springboot/100/6/testcontainers-hikari-and-failed-to-validate-connection-org-postgresql-jdbc-pgco
// for argumentation of the need for DirtiesContext
public abstract class AbstractDataJpaTestcontainerTest {
//
//    @Container
//    public static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:14.6")
//            .withDatabaseName("showcase-testdb")
//            .withUsername("sa")
//            .withPassword("sa")
//            .withReuse(true);
//
//    @DynamicPropertySource
//    static void setupProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
//        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
//        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
//    }
}
