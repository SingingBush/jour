package net.sf.jour.log;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LoggerTest {

    @Test
    public void testGetLoggerCreatesLog4jWrapper() {
        final Logger logger = Logger.getLogger(LoggerTest.class);

        assertEquals("net.sf.jour.log.Logger$LoggerLog4j", logger.getClass().getName());
    }

    @Test
    public void testGetLogCreatesLog4jWrapper() {
        final Logger logger = Logger.getLogger(LoggerTest.class);

        assertEquals("net.sf.jour.log.Logger$LoggerLog4j", logger.getClass().getName());
    }

}
