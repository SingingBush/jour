/*
 * Jour - bytecode instrumentation library
 *
 * Copyright (C) 2007 Vlad Skarzhevskyy
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
 * 
 * @version $Id$
 * 
 */
package net.sf.jour.util;

import java.net.URL;

/**
 * @author vlads
 *
 */
public class ClassPathUtil {

    public static String getClassResourceName(Class testClass) {
        return getClassResourceName(testClass.getName());
    }
    
    public static String getClassResourceName(String testClassName) {
        return "/" + testClassName.replace('.', '/') + ".class";
    }
    
    public static String getClassPath(Class testClass) {
        String resource = getClassResourceName(testClass);
        URL url = testClass.getResource(resource); 
        if (url == null) {
            throw new Error("Resource not found " + resource);
        }
        String path = url.toExternalForm();
        String prefix = "file:";
        int extraPathLen = 0;
        if (!path.startsWith(prefix)) {
            prefix = "jar:file:";
            extraPathLen = 1;
        }
        return path.substring(prefix.length(), path.length() - resource.length() - extraPathLen);
    }

}
