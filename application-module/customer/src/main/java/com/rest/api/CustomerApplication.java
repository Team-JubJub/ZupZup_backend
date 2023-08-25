package com.rest.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.filter.HiddenHttpMethodFilter;

@SpringBootApplication
@EntityScan(basePackages = {"com.zupzup"})
@ComponentScan({"com.zupzup.untact.config"})
@EnableJpaRepositories(basePackages = {"com.zupzup.untact.repository"})
@EnableAsync
@EnableScheduling
@OpenAPIDefinition(servers = {@Server(url = "https://zupzuptest.com:8090", description = "Default Server URL")})
public class CustomerApplication {

    public static void main(String[] args) { SpringApplication.run(CustomerApplication.class, args); }

    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }

}
