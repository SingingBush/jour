package net.sf.jour.log;

public class Log4jLogger extends Logger {

    private org.apache.log4j.Logger logger;

    Log4jLogger(org.apache.log4j.Logger log4jLogger) {
        this.logger = log4jLogger;
    }

    public void error(Object message) {
        this.logger.log(FQCN, org.apache.log4j.Level.ERROR, message, null);
    }

    public void error(Object message, Throwable t) {
        this.logger.log(FQCN, org.apache.log4j.Level.ERROR, message, t);
    }

    public void warn(Object message) {
        this.logger.log(FQCN, org.apache.log4j.Level.WARN, message, null);
    }

    public void info(Object message) {
        this.logger.log(FQCN, org.apache.log4j.Level.INFO, message, null);
    }

    public boolean isDebugEnabled() {
        return this.logger.isDebugEnabled();
    }

    public void debug(Object message) {
        this.logger.log(FQCN, org.apache.log4j.Level.DEBUG, message, null);
    }
}
