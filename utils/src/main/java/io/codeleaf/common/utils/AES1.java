package io.codeleaf.common.utils;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public final class AES1 {

    private AES1() {
    }

    private static final MessageDigest SHA256;

    static {
        try {
            SHA256 = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException cause) {
            throw new ExceptionInInitializerError(cause);
        }
    }

    public static SecretKeySpec createSecretKey(String secret) {
        byte[] key = secret.getBytes(StandardCharsets.UTF_8);
        key = SHA256.digest(key);
        key = Arrays.copyOf(key, 16);
        return new SecretKeySpec(key, "AES");
    }

    public static String encrypt(String stringToEncrypt, SecretKeySpec secretKey) {
        return Base64.getEncoder().encodeToString(encrypt(stringToEncrypt.getBytes(StandardCharsets.UTF_8), secretKey));
    }

    public static byte[] encrypt(byte[] bytesToEncrypt, SecretKeySpec secretKey) {
        try {
            Cipher cipher = getCipher();
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(bytesToEncrypt);
        } catch (GeneralSecurityException cause) {
            throw new IllegalArgumentException("Error while encrypting: " + cause.toString());
        }
    }

    public static String decrypt(String stringToDecrypt, SecretKeySpec secretKey) {
        return new String(decrypt(Base64.getDecoder().decode(stringToDecrypt), secretKey), StandardCharsets.UTF_8);
    }

    public static byte[] decrypt(byte[] bytesToDecrypt, SecretKeySpec secretKey) {
        try {
            Cipher cipher = getCipher();
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(bytesToDecrypt);
        } catch (GeneralSecurityException cause) {
            throw new IllegalArgumentException("Error while encrypting: " + cause.toString());
        }
    }

    private static Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        return Cipher.getInstance("AES/ECB/PKCS5Padding");
    }
}