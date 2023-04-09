package io.codeleaf.common.utils;


import java.nio.charset.StandardCharsets;
import java.util.*;

public final class StringEncodings {

    public static final char ESCAPE_CHAR = '\\';
    public static final char FIELD_DELIMITER = ',';

    private StringEncodings() {
    }

    public static String encodeMap(Map<String, String> stringMap) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : stringMap.entrySet()) {
            builder.append(escapeField(entry.getKey())).append(',').append(escapeField(entry.getValue())).append(',');
        }
        removeLastChar(builder);
        return builder.toString();
    }

    public static Map<String, String> decodeMap(String encodedStringMap) {
        if (encodedStringMap == null || encodedStringMap.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> stringMap = new LinkedHashMap<>();
        int nextDelPos = -1;
        do {
            int lastDelPos = nextDelPos;
            nextDelPos = mustFindNextDelimiter(encodedStringMap, nextDelPos);
            String key = extractField(encodedStringMap, lastDelPos, nextDelPos);
            lastDelPos = nextDelPos;
            nextDelPos = findNextDelimiter(encodedStringMap, nextDelPos);
            String value = extractField(encodedStringMap, lastDelPos, nextDelPos);
            stringMap.put(key, value);
        } while (nextDelPos >= 0);
        return stringMap;
    }

    public static String encodeSet(Set<String> stringSet) {
        return encodeList(new ArrayList<>(stringSet));
    }

    public static String encodeList(List<String> stringList) {
        StringBuilder builder = new StringBuilder();
        for (String item : stringList) {
            builder.append(escapeField(item)).append(',');
        }
        removeLastChar(builder);
        return builder.toString();
    }

    private static void removeLastChar(StringBuilder builder) {
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
    }

    public static Set<String> decodeSet(String encodedStringSet) {
        return new LinkedHashSet<>(decodeList(encodedStringSet));
    }

    public static List<String> decodeList(String encodedStringList) {
        if (encodedStringList == null || encodedStringList.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> stringList = new LinkedList<>();
        int nextDelPos = -1;
        do {
            int lastDelPos = nextDelPos;
            nextDelPos = findNextDelimiter(encodedStringList, nextDelPos);
            String value = extractField(encodedStringList, lastDelPos, nextDelPos);
            stringList.add(value);
        } while (nextDelPos >= 0);
        return stringList;
    }

    public static String toEncodedBase64UTF8(String text) {
        return new String(Base64.getEncoder().encode(text.getBytes(StandardCharsets.UTF_8)));
    }

    public static String toDecodedBase64UTF8(String text) {
        return new String(Base64.getDecoder().decode(text.getBytes(StandardCharsets.UTF_8)));
    }

    private static String escapeField(String string) {
        return string == null ? "\\0" : escapeChars(string);
    }

    private static String escapeChars(String string) {
        StringBuilder escapedField = new StringBuilder();

        for (int index = 0; index < string.length(); ++index) {
            char c = string.charAt(index);
            switch (c) {
                case FIELD_DELIMITER:
                case ESCAPE_CHAR:
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
                case FIELD_DELIMITER:
                    return index;
                case ESCAPE_CHAR:
                    ++index;
                default:
                    ++index;
            }
        }
        return -1;
    }
}