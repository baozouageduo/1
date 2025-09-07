package com.tests.campuslostandfoundsystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.tests.campuslostandfoundsystem.dao")
public class CampusLostAndFoundSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusLostAndFoundSystemApplication.class, args);
    }

}
