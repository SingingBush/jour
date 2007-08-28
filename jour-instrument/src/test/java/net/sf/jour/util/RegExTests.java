/*
 * Jour - java profiler and monitoring library
 *
 * Copyright (C) 2004 Jour team
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
package net.sf.jour.util;

import java.util.StringTokenizer;

import junit.framework.TestCase;

/**
 * TODO Add docs
 * Created on 03.12.2004
 *
 * Contributing Author(s):
 *
 *   Misha Lifschitz <mishalifschitz at users.sourceforge.net> (Inital implementation)
 *   Vlad Skarzhevskyy <vlads at users.sourceforge.net> (Inital implementation)
 *
 * @author vlads
 * @version $Revision$ ($Author$) $Date$
 */
public class RegExTests extends TestCase {
	
	void verify(String str, String expected, String regEx){
		String[] ar = RegExUtil.match(str, regEx);
		StringTokenizer st = new StringTokenizer(expected, "|");
		for(int i = 0; i < ar.length; i ++) {
			System.out.print("[" + ar[i] + "]");
			String expectedItem = st.nextToken();
			assertEquals(expectedItem, ar[i]);
		}
		System.out.println();
		assertFalse(st.hasMoreTokens()) ;
	}
	
	public void testJ14() throws Exception {
		//verify("(^[\\S]*)\\s*(\\S*)\\((\\S*)\\)\\s*$");
		
		verify("A B S", "A|B|S", "(\\S*)\\s*(\\S*)\\s*(\\S*)");
		verify("* set* (..) ", "*|set*|..", "(\\S*)\\s*(\\S*)\\s*\\((\\S*)\\)\\s*");
		//verify("* set*(..)", "(^[\\S]*)\\s*(\\S*)\\((\\S*)\\)\\s*$");
		
		verify("bob.bb->set*(..)", "bob|.bb|set*|..", "(\\S*)\\s*(\\.\\S*)->(\\S*)\\((\\S*)\\)");
	}
}
