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
package uut.signature;

/**
 * Do not change this class it is used in ChangesTest.
 * 
 * @author vlads
 *
 */
class AChildClass extends AClass {

	//public static final boolean booleanField = true;
	
	//public static final float floatField = (float)3.14;
	
	public static final double doubleField = 3.1415;
	
	public static final String stringField = "AString";
	   
	protected AChildClass() {
		
	}
	
	AChildClass(int[] data) {
        
    }

	public AChildClass(int a, char b) throws IllegalAccessException {
        
    }

	public void run() {
	}
	
	public Long run(byte b) throws Error {
		return null;
	}
	
	void runImpl() {
    }
}
