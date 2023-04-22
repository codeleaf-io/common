package io.codeleaf.common.utils;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

public final class RSA {

    private RSA() {
    }

    public static final int DEFAULT_KEY_SIZE = 1_024;

    public static KeyPair createKeyPair() {
        return createKeyPair(DEFAULT_KEY_SIZE);
    }

    public static KeyPair createKeyPair(int keySize) {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(keySize);
            return generator.generateKeyPair();
        } catch (GeneralSecurityException cause) {
            throw new IllegalStateException("Failed to generate key with key size: " + keySize + ": " + cause, cause);
        }
    }

}