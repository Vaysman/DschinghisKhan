package ru;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.datatables.repository.DataTablesRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

//Worker bees can leave
    //Even drones can fly away
        //The queen is their slave
@SpringBootApplication
@EnableJpaRepositories(
        basePackages = {"ru.dao"},repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class
)
@EnableScheduling
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
