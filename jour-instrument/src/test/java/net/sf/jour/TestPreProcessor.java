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
package net.sf.jour;

import junit.framework.TestCase;

/*
 * Created on Dec 2, 2004
 *
 * Contributing Author(s):
 *
 *   Misha Lifschitz <mishalifschitz at users.sourceforge.net> (Inital implementation)
 *   Vlad Skarzhevskyy <vlads at users.sourceforge.net> (Inital implementation)
 *
 * @author michaellif
 * @version $Revision$ ($Author$)
 */
public class TestPreProcessor extends TestCase {
    
    public void xtestPreProcessor() throws Exception {
        
        PreProcessor.main(new String[]{
			"-src", "./target/test-classes/", 
			"-dst", "./target/test-iclasses"});       
    }

	public void testPreProcessor01() throws Exception {
        
		PreProcessor.main(new String[]{
			"-classlist", "./src/test/resources/case01.list.txt", 
			"-dst", "./target/test-iclasses"});       
	}    

}
