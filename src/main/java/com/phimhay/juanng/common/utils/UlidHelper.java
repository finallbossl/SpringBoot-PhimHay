package com.phimhay.juanng.common.utils;

import java.security.SecureRandom;

public class UlidHelper {
    private static final char[] CROCKFORD_BASE32 = "0123456789ABCDEFGHJKMNPQRSTVWXYZ".toCharArray();
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String nextUlid() {
        long timestamp = System.currentTimeMillis();
        char[] buffer = new char[26];

        // Mã hóa Timestamp (48 bits -> 10 ký tự Crockford Base32)
        for (int i = 9; i >= 0; i--) {
            buffer[i] = CROCKFORD_BASE32[(int) (timestamp % 32)];
            timestamp /= 32;
        }

        // Mã hóa Randomness (80 bits -> 16 ký tự Crockford Base32)
        for (int i = 10; i < 26; i++) {
            buffer[i] = CROCKFORD_BASE32[RANDOM.nextInt(32)];
        }

        return new String(buffer);
    }
}
