package com.bubua12.cloud.storage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("com.bubua12.cloud.storage.mapper")
public class SeataStorageMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeataStorageMainApplication.class, args);
    }
}
