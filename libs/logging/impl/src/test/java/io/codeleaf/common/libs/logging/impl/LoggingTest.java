package io.codeleaf.common.libs.logging.impl;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingTest {

    @Test
    public void testLogging() {
        // Given
        Logger logger = LoggerFactory.getLogger(LoggingTest.class);

        // When
        logger.info("a");
    }
}
