package com.health;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.health.mapper")
@ComponentScan(basePackages = {"com.health"})
public class HealthManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(HealthManagementApplication.class, args);
    }
}