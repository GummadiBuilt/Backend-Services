package com.infra.gummadibuilt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GummadiBuiltApplication {
    public static void main(String[] args) {
        SpringApplication.run(GummadiBuiltApplication.class, args);
    }

}
