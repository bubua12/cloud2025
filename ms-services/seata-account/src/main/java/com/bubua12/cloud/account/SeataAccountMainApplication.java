package com.bubua12.cloud.account;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("com.bubua12.cloud.account.mapper")
public class SeataAccountMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeataAccountMainApplication.class, args);
    }
}
