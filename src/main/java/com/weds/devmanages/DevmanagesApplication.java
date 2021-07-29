package com.weds.devmanages;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class DevmanagesApplication {

    public static void main(String[] args) {
        SpringApplication.run(DevmanagesApplication.class, args);
    }

}
