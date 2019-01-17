package io.codeleaf.common.utils;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.function.Supplier;

public class MethodReferencesTest {

    public interface TestInterface {

        String getName();

        static String getNameStatic() {
            return "abc";
        }

    }

    public class TestImpl {

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
        Assert.assertEquals("getName", result.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDereference_InvalidType() {
        // Given
        Supplier<?> methodReference = TestInterface::getNameStatic;

        // When
        Method result = MethodReferences.derefence(methodReference);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReference_NonInterface() {
        // When
        Supplier<?> result = MethodReferences.getProxy(TestImpl.class)::getName;
    }

}
