package io.codeleaf.common.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class ClassPathResources {

    private ClassPathResources() {
    }

    private static final ClassLoader DEFAULT = ClassPathResources.class.getClassLoader();

    public static byte[] getBytes(String path) throws IOException {
        return getBytes(DEFAULT, path);
    }

    public static byte[] getBytes(ClassLoader classLoader, String path) throws IOException {
        Objects.requireNonNull(classLoader);
        Objects.requireNonNull(path);
        return InputStreams.readAllBytes(classLoader.getResourceAsStream(path));
    }

    public static String getUTF8(String path) throws IOException {
        return getUTF8(DEFAULT, path);
    }

    public static String getUTF8(ClassLoader classLoader, String path) throws IOException {
        return getString(classLoader, path, StandardCharsets.UTF_8);
    }

    public static String getString(String path, Charset charset) throws IOException {
        return getString(DEFAULT, path, charset);
    }

    public static String getString(ClassLoader classLoader, String path, Charset charset) throws IOException {
        return new String(getBytes(classLoader, path), charset);
    }

}
