package net.sf.jour.log;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoggerTest {

    @Test
    public void testGetLoggerCreatesLog4jWrapper() {
        final Logger logger = Logger.getLogger(LoggerTest.class);

        assertEquals("net.sf.jour.log.Log4jLogger", logger.getClass().getName());
    }

    @Test
    public void testGetLogCreatesLog4jWrapper() {
        final Logger logger = Logger.getLogger(LoggerTest.class);

        assertEquals("net.sf.jour.log.Log4jLogger", logger.getClass().getName());
    }

}

