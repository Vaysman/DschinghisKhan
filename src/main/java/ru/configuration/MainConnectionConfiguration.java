package ru.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.datatables.repository.DataTablesRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Data
@Configuration
@EnableTransactionManagement
@EnableConfigurationProperties
@EnableJpaRepositories(
        basePackages = {"ru.dao"},
        repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class)
@ConfigurationProperties(prefix = "spring.main-datasource")
@EnableSwagger2
public class MainConnectionConfiguration {
//    private String url;
//    private String username;
//    private String password;
//    private String driverClassName;
//
//    @Bean(name = "mainDataSource")
//    @Primary
//    public DataSource dataSource() {
//        return DataSourceBuilder
//                .create()
//                .url(url)
//                .username(username)
//                .password(password)
//                .driverClassName(driverClassName)
//                .build();
//    }
//
//    @Primary
//    @Bean(name = "mainEntityManagerFactory")
//    public LocalContainerEntityManagerFactoryBean barEntityManagerFactory(
//            EntityManagerFactoryBuilder builder,
//            @Qualifier("mainDataSource") DataSource mainDataSource) {
//        return builder
//                .dataSource(mainDataSource)
//                .packages("ru/dao")
//                .persistenceUnit("ru/dao")
//                .build();
//    }
//
//    @Primary
//    @Bean(name = "mainTransactionManager")
//    public PlatformTransactionManager barTransactionManager(
//            @Qualifier("mainEntityManagerFactory") EntityManagerFactory barEntityManagerFactory
//    ) {
//        return new JpaTransactionManager(barEntityManagerFactory);
//    }
}
