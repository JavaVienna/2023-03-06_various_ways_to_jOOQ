package at.softwarecraftsmen.persistenceshowcase.persistence;

import org.jooq.ConnectionProvider;
import org.jooq.DSLContext;
import org.jooq.conf.Settings;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;

@Configuration
public class JooqConfiguration {

    private final DataSource dataSource;

    public JooqConfiguration(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public DataSourceConnectionProvider connectionProvider() {
        return new DataSourceConnectionProvider(
            new TransactionAwareDataSourceProxy(dataSource)
        );
    }

    @Bean
    public DSLContext dsl(DefaultConfiguration configuration) {
        return new DefaultDSLContext(configuration);
    }

    @Bean
    public DefaultConfiguration configuration(ConnectionProvider connectionProvider) {
        var settings = new Settings();
        settings.setRenderSchema(false);

        var configuration = new DefaultConfiguration();
        configuration.set(settings);
        configuration.set(connectionProvider);
        return configuration;
    }
}
