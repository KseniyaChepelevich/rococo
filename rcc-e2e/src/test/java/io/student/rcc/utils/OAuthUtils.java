package io.student.rcc.utils;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Pattern;

@Slf4j
public class OAuthUtils {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final int VERIFIER_BYTE_LENGTH = 32;
    private static final String ALGORITHM = "SHA-256";
    private static final Pattern SAFE_PATTERN = Pattern.compile("^[A-Za-z0-9._~-]+$");


    private OAuthUtils() {
    }

    public static String generateCodeVerifier() {
        byte[] values = new byte[VERIFIER_BYTE_LENGTH];
        secureRandom.nextBytes(values);
        String codeVerifier = Base64.getUrlEncoder().withoutPadding().encodeToString(values);
        return codeVerifier;

    }

    public static String generateCodeChallenge(String codeVerifier) {
        String codeChallenge;
        if (codeVerifier == null) {
            throw new IllegalArgumentException("codeVerifier cannot be null");
        }
        if (codeVerifier.length() < 43 || codeVerifier.length() > 128) {
            throw new IllegalArgumentException("codeVerifier must be between 43 and 128 characters");
        }
        if (!SAFE_PATTERN.matcher(codeVerifier).matches()) {
            throw new IllegalArgumentException("codeVerifier contains invalid characters");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.UTF_8));
            codeChallenge = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
            return codeChallenge;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm is not available", e);
        }
    }
}
