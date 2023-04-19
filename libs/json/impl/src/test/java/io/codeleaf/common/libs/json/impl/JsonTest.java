package io.codeleaf.common.libs.json.impl;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JsonTest {

    @Test
    public void testJson() {
        // Given
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("f1", "bar")
                .add("f2", false)
                .build();

        // When
        String result = jsonObject.toString();

        // Then
        Assertions.assertEquals("{\"f1\":\"bar\",\"f2\":false}", result);
    }
}
