package io.codeleaf.common.utils;

import java.util.Base64;

public final class PublicKeys {

    private static final String BEGIN_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----";
    private static final String END_PUBLIC_KEY = "-----END PUBLIC KEY-----";
    private static final String PEM_CRLF = "\r\n";
    private static final String SYSTEM_CRLF = System.lineSeparator();

    private PublicKeys() {
    }

    public static byte[] getBytes(String pem) {
        return Base64.getDecoder().decode(unwrapPublicKey(pem));
    }

    public static String wrapPublicKey(String encodedBytes) {
        return BEGIN_PUBLIC_KEY + PEM_CRLF + wrapBase64(encodedBytes) + PEM_CRLF + END_PUBLIC_KEY;
    }

    private static String wrapBase64(String encodedBytes) {
        StringBuilder sb = new StringBuilder();
        int pos = 0;
        for (byte b : encodedBytes.getBytes()) {
            if (pos > 0 && pos % 64 == 0) {
                sb.append(PEM_CRLF);
            }
            sb.append((char) b);
            pos++;
        }
        return sb.toString();
    }

    public static String unwrapPublicKey(String pem) {
        return pem
                .replaceAll(PEM_CRLF, "")
                .replaceAll(SYSTEM_CRLF, "")
                .replaceAll(BEGIN_PUBLIC_KEY, "")
                .replaceAll(END_PUBLIC_KEY, "");
    }

    public static String toPEM(byte[] bytes) {
        return wrapPublicKey(Base64.getEncoder().encodeToString(bytes));
    }

    public static String toPEM(byte[] bytes, boolean systemNewLines) {
        String pem = toPEM(bytes);
        return systemNewLines ? pem.replaceAll(PEM_CRLF, SYSTEM_CRLF) : pem;
    }

}
