/*
 * Jour - java profiler and monitoring library
 *
 * Copyright (C) 2007 Jour team
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 */
package net.sf.jour.log;

import java.util.WeakHashMap;

/**
 * Log4j wrapper
 * 
 * @author vlads
 *
 */
public class Logger {
	
	/** The fully qualified name of the Log class. */
    private static final String FQCN = Logger.class.getName();
    
    private static int log4jAvalable = 0;
    
    private static WeakHashMap instances = new WeakHashMap();
    
    protected Logger() {
    }
    
    private static int detectLog4j() {
    	try {
			Class.forName("org.apache.log4j.Logger");
			return 1;
		} catch (ClassNotFoundException e) {
			return -1;
		}
    }
    
    /**
     * We are in java 1.4 lets save some typing.
     */
    public static Logger getLogger() {
        String useName = null;
        StackTraceElement[] ste = new Throwable().getStackTrace();
        for (int i = 0; i < ste.length; i++ ) {
            if (ste[i].getClassName().equals(FQCN)) {
                i ++;
                useName = ste[i].getClassName();
                break;
            }
        }
        if (useName == null) {
            throw new Error("Can't find call origin");
        }
        return getLogger(useName);    
    }
    
    /**
     * Return the native Logger instance we are using.
     */
    public static Logger getLogger(String name) {
        return createLogger(name);
    }
    
    private static Logger createLogger(String name) {
    	if (log4jAvalable == 0) {
    		log4jAvalable = detectLog4j();
    	} 
    	if (log4jAvalable > 0) {
    		return createLoggerWrapper(name);
    	} else {
    		return new Logger();
    	}
    }
    
    private static Logger createLoggerWrapper(String name) {
    	org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(name);
        if (instances.containsKey(logger)) {
            return (Logger)instances.get(logger);
        } else {
            Logger log = new LoggerLog4j(logger);
            instances.put(logger, log);
            return log;
        }
    }
    
    public static Logger getLogger(Class clazz) {
        return getLogger(clazz.getName());
    }
    
    public static Logger getLog(Class clazz) {
        return getLogger(clazz.getName());
    }
    
    private static class LoggerLog4j extends Logger {

		private org.apache.log4j.Logger logger;

		protected LoggerLog4j(org.apache.log4j.Logger log4jLogger) {
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

	public void error(Object message) {
		System.err.println(message);
	}

	public void error(Object message, Throwable t) {
		System.err.println(message);
		if (t != null) {
			t.printStackTrace(System.err);
		}
	}
    
    public void warn(Object message) {
    }
    
    public void info(Object message) {
    }
    
    public boolean isDebugEnabled() {
   		return false;
    }

    public void debug(Object message) {
    }
}
