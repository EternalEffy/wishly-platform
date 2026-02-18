package com.pastebin.pasteservice.generator;

import com.pastebin.common.generator.HashGenerator;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class LocalHashGenerator implements HashGenerator {
    private static final String CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String generate(int length) {
        StringBuilder hash = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(CHARACTERS.length());
            hash.append(CHARACTERS.charAt(index));
        }
        return hash.toString();
    }
}
