package com.pastebin.hashgenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HashGeneratorApplication {
    public static void main(String[] args) {
        SpringApplication.run(HashGeneratorApplication.class, args);
    }
}
