package com.wa.cluemrg;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.wa.cluemrg.dao")
@ServletComponentScan
@EnableScheduling
public class ClueMrgApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClueMrgApplication.class, args);
    }

}
