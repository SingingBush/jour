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
package net.sf.jour.signature;

import java.util.List;
import java.util.Vector;

/**
 * @author vlads
 *
 */
class APICompareChangeHelper {

    List changes;

    APICompareChangeHelper() {
        changes = new Vector();
    }

    public void fail(String message) {
        changes.add(message);
    }

    public void assertTrue(String message, boolean condition) {
        if (!condition) {
            fail(message);
        }
    }

    public void assertFalse(String message, boolean condition) {
        if (condition) {
            fail(message);
        }
    }
    
    public void assertNull(String message, Object object) {
        assertTrue(message, object == null);
    }
    
    public void assertNotNull(String message, Object object) {
        assertTrue(message, object != null);
    }
    
    public void assertEquals(String message, int expected, int actual) {
        assertEquals(message, new Integer(expected), new Integer(actual));
    }
    
    public void assertEquals(String message, boolean expected, boolean actual) {
        assertEquals(message, new Boolean(expected), new Boolean(actual));
    }

    public void assertEquals(String message, Object expected, Object actual) {
        if (expected == null && actual == null)
            return;
        if (expected != null && expected.equals(actual))
            return;
        failNotEquals(message, expected, actual);
    }
    
    private void failNotEquals(String message, Object expected, Object actual) {
        fail(format(message, expected, actual));
    }

    String format(String message, Object expected, Object actual) {
        String formatted = "";
        if (message != null) {
            formatted = message + " ";
        }
        return formatted + "expected:<" + expected + "> but was:<" + actual + ">";
    }
}
