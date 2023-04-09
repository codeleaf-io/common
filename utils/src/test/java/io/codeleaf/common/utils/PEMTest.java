package io.codeleaf.common.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class PEMTest {

    @Test
    public void testToPEM() throws IOException {
        // Given
        String pem = ClassPathResources.getUTF8("pub-dsa.pem");
        byte[] bytes = PublicKeys.getBytes(pem);

        // When
        String result = PublicKeys.toPEM(bytes, true);

        // Then
        Assertions.assertEquals(pem, result);
    }

}
