package io.codeleaf.common.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.function.Supplier;

public class MethodReferencesTest {

    public interface TestInterface {

        String getName();

        static String getNameStatic() {
            return "abc";
        }

    }

    public static class TestImpl {

        public String getName() {
            return "abc";
        }

    }

    @Test
    public void testDereference() {
        // Given
        Supplier<?> methodReference = MethodReferences.getProxy(TestInterface.class)::getName;

        // When
        Method result = MethodReferences.derefence(methodReference);

        // Then
        Assertions.assertEquals("getName", result.getName());
    }

    @Test
    public void testDereference_InvalidType() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            // Given
            Supplier<?> methodReference = TestInterface::getNameStatic;

            // When
            MethodReferences.derefence(methodReference);
        });
    }

    @Test
    public void testReference_NonInterface() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {

            // When
            Supplier<?> result = MethodReferences.getProxy(TestImpl.class)::getName;
        });
    }

}
