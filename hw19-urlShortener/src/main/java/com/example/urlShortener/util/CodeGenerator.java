package com.example.urlShortener.util;

import com.example.urlShortener.dao.LinkRepository;
import java.security.SecureRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CodeGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 6;
    private static final int MAX_ATTEMPTS = 100;
    private final SecureRandom random = new SecureRandom();
    private final LinkRepository linkRepository;

    public String generateUniqueCode() {
        int attempts = 0;
        String code;

        do {
            if (attempts++ >= MAX_ATTEMPTS) {
                throw new RuntimeException("Cannot generate unique code after " + MAX_ATTEMPTS + " attempts");
            }
            code = generateCode();
        } while (linkRepository.existsByShortCode(code));

        return code;
    }

    private String generateCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }
}
