package com.atacankullabci.immovin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ImMovinApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImMovinApplication.class, args);
    }
}
