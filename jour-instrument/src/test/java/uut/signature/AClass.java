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
package uut.signature;

/**
 * @author vlads
 *
 */
public class AClass implements AnInerface {

	public final static int constData = 10;
	
	private static int internalData;
	
	protected static short data;
	
	public AClass() {
		
	}
	
	AClass(int[] i) {
		
	}

	public int getInt() {
		return internalData;
	}

	protected native void getProt();
	
	public native void getVoid();
	
	private native void getData();

	protected AnInerface getAnInerface() {
		return this;
	}
	
	protected AClass getAClass() {
		return this;
	}
}
