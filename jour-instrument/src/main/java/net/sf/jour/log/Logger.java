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
 * Logging wrapper that attempts to use Log4j then Slf4j. This class was removed but had to be reinstated due to
 * some other projects that relied on it's existence. Please migrate away from using this class and just use
 * slf4j-api instead.
 *
 * @author vlads
 * @deprecated use slf4j Logger instead
 */
@Deprecated // todo: once targeting min JDK 11 use @Deprecated(since = "2.1.1", forRemoval = true)
public class Logger {

    /** The fully qualified name of the Log class. */
    protected static final String FQCN = Logger.class.getName();

    private static final WeakHashMap<String, Logger> instances = new WeakHashMap<>();

    protected Logger() {
    }

    private static boolean detectLog4j() {
        try {
            Class.forName("org.apache.log4j.Logger");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
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

    private static Logger createLogger(final String name) {
        if (instances.containsKey(name)) {
            return instances.get(name);
        } else if (detectLog4j()) {
            final Logger log4jLogger = new Log4jLogger(org.apache.log4j.Logger.getLogger(name));
            instances.put(name, log4jLogger);
            return log4jLogger;
        } else {
            final Logger slf4jLogger = new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(name));
            instances.put(name, slf4jLogger);
            return slf4jLogger;
        }
    }

    public static Logger getLogger(Class clazz) {
        return getLogger(clazz.getName());
    }

    public static Logger getLog(Class clazz) {
        return getLogger(clazz.getName());
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
