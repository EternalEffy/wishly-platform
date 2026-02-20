package com.pastebin.pasteservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableJpaAuditing
@EnableWebMvc
@EnableScheduling
public class PasteServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PasteServiceApplication.class, args);
    }
}
