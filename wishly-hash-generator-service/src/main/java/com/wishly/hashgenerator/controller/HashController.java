package com.wishly.hashgenerator.controller;

import com.wishly.common.generator.HashGenerator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hash")
public class HashController {
    private final HashGenerator hashGenerator;

    public HashController(HashGenerator hashGenerator) {
        this.hashGenerator = hashGenerator;
    }

    @GetMapping
    public String generateHash(@RequestParam int length) {
        return hashGenerator.generate(length);
    }

}
