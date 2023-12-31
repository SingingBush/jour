package net.sf.jour.log;

public class Slf4jLogger extends Logger {

    private org.slf4j.Logger logger;

    Slf4jLogger(org.slf4j.Logger slf4jLogger) {
        this.logger = slf4jLogger;
    }

    public void error(Object message) {
        this.logger.error(message.toString());
    }

    public void error(Object message, Throwable t) {
        this.logger.error(message.toString(), t);
    }

    public void warn(Object message) {
        this.logger.warn(message.toString());
    }

    public void info(Object message) {
        this.logger.info(message.toString());
    }

    public boolean isDebugEnabled() {
        return this.logger.isDebugEnabled();
    }

    public void debug(Object message) {
        this.logger.debug(message.toString());
    }
}
