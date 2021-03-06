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
 */
package net.sf.jour.signature;

import net.sf.jour.util.ClassPathUtil;
import junit.framework.TestCase;

/**
 * @author vlads
 *
 */
public abstract class SignatureTestCase extends TestCase {

    public abstract String getAPIPath();
    
    public abstract String getSignatureXMLPath(); 
    
    public boolean isUeSystemClassPath() {
        return true;
    }
    
    public static String getClassPath(Class testClass) {
        return ClassPathUtil.getClassPath(testClass); 
    }
    
    public String getSupportingJarsPath() {
        return null;
    }
    
	public APICompareConfig getAPICompareConfig() {
	    return new APICompareConfig();
	}
	
	public void testAPIDeclaration() {
	    try {
            APICompare.compare(getAPIPath(), getSignatureXMLPath(), getAPICompareConfig(), isUeSystemClassPath(), getSupportingJarsPath());
        } catch (ChangeDetectedException e) {
            fail(e.getMessage());
        }
	}
}
