package com.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan(basePackages = {"com.example.demos.mapper"})
@EnableScheduling
public class MiYuanBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiYuanBackendApplication.class, args);
    }

}
