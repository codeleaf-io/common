package io.codeleaf.common.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.crypto.spec.SecretKeySpec;

public class AES1Test {

    @Test
    public void testDecrypt() {
        // Given
        SecretKeySpec secretKey = AES1.createSecretKey("test");
        String encryptedMessage = AES1.encrypt("message", secretKey);

        // When
        String result = AES1.decrypt(encryptedMessage, secretKey);

        // Then
        Assertions.assertEquals("message", result);
    }
}
