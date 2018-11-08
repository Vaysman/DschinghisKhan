package ru.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import ru.dao.entity.*;

@Configuration
public class RepositoryConfiguration extends RepositoryRestConfigurerAdapter {
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(Route.class);
        config.exposeIdsFor(Company.class);
        config.exposeIdsFor(Driver.class);
        config.exposeIdsFor(Transport.class);
        config.exposeIdsFor(Order.class);
        config.exposeIdsFor(Client.class);
    }
}
