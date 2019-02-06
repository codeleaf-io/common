package io.codeleaf.common.utils;


import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.Map.Entry;

public final class Strings {
    public static final char ESCAPE_CHAR = '\\';
    public static final char FIELD_DELIMITER = ',';

    private Strings() {
    }

    public static String encodeString(Map<String, String> stringMap) {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator var2 = stringMap.entrySet().iterator();

        while (var2.hasNext()) {
            Entry<String, String> entry = (Entry) var2.next();
            stringBuilder.append(escapeField(entry.getKey())).append(',').append(escapeField(entry.getValue())).append(',');
        }
        //removing last char
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    public static Map<String, String> decodeString(String encodedString) {
        if (encodedString != null && !encodedString.isEmpty()) {
            int nextDelPos = -1;
            LinkedHashMap stringMap = new LinkedHashMap();

            do {
                int lastDelPos = nextDelPos;
                nextDelPos = mustFindNextDelimiter(encodedString, nextDelPos);
                String key = extractField(encodedString, lastDelPos, nextDelPos);
                lastDelPos = nextDelPos;
                nextDelPos = findNextDelimiter(encodedString, nextDelPos);
                String value = extractField(encodedString, lastDelPos, nextDelPos);
                stringMap.put(key, value);
            } while (nextDelPos >= 0);

            return stringMap;
        } else {
            return Collections.emptyMap();
        }
    }

    public static String encodeString(String... fields) {
        return encodeString(asMap(fields));
    }

    public static Map<String, String> asMap(String... fields) {
        if (fields != null && fields.length != 0) {
            if (fields.length % 2 != 0) {
                throw new IllegalArgumentException("Must be even number of fields!");
            } else {
                Map<String, String> stringMap = new LinkedHashMap();

                for (int i = 0; i < fields.length; i += 2) {
                    stringMap.put(fields[i], fields[i + 1]);
                }

                return stringMap;
            }
        } else {
            return Collections.emptyMap();
        }
    }

    public static String toEncodeBase64UTF8(String text) throws UnsupportedEncodingException {
        return new String(Base64.getEncoder().encode(text.getBytes("UTF-8")));
    }

    public static String toDecodeBase64UTF8(String text) throws UnsupportedEncodingException {
        return new String(Base64.getDecoder().decode(text.getBytes("UTF-8")));
    }

    private static String escapeField(String string) {
        return string == null ? "\\0" : escapeChars(string);
    }

    private static String escapeChars(String string) {
        StringBuilder escapedField = new StringBuilder();

        for (int index = 0; index < string.length(); ++index) {
            char c = string.charAt(index);
            switch (c) {
                case ',':
                case '\\':
                    escapedField.append('\\').append(c);
                    break;
                default:
                    escapedField.append(c);
            }
        }

        return escapedField.toString();
    }

    private static String unescapeField(String string) {
        return string.equals("\\0") ? null : unescapeChars(string);
    }

    private static String unescapeChars(String escapedString) {
        StringBuilder field = new StringBuilder();

        for (int index = 0; index < escapedString.length(); ++index) {
            char c = escapedString.charAt(index);
            if (c == '\\') {
                ++index;
                if (index > escapedString.length() - 1) {
                    throw new IllegalArgumentException("Invalid escaped String!");
                }

                c = escapedString.charAt(index);
            }

            field.append(c);
        }

        return field.toString();
    }

    public static String extractHostName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String hostname = uri.getHost();
        if (hostname != null) {
            return hostname.startsWith("www.") ? hostname.substring(4) : hostname;
        }
        return hostname;
    }

    private static String extractField(String encodedString, int lastDelPos, int nextDelPos) {
        String escapedString = encodedString.substring(lastDelPos < 0 ? 0 : lastDelPos + 1, nextDelPos < 0 ? encodedString.length() : nextDelPos);
        return unescapeField(escapedString);
    }

    private static int mustFindNextDelimiter(String encodedString, int lastDelPos) {
        int nextDelPos = findNextDelimiter(encodedString, lastDelPos);
        if (nextDelPos < 0) {
            throw new IllegalArgumentException("Could not decode String Map from String: Only 1 field found!");
        } else {
            return nextDelPos;
        }
    }

    private static int findNextDelimiter(String encodedString, int lastDelPos) {
        int index = lastDelPos < 0 ? 0 : lastDelPos + 1;

        while (index < encodedString.length()) {
            switch (encodedString.charAt(index)) {
                case ',':
                    return index;
                case '\\':
                    ++index;
                default:
                    ++index;
            }
        }

        return -1;
    }
}