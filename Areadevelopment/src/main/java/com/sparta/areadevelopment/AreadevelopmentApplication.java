package com.sparta.areadevelopment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class AreadevelopmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(AreadevelopmentApplication.class, args);
    }

}
