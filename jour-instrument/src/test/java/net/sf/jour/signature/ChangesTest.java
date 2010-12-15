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

import junit.framework.TestCase;
import net.sf.jour.test.Utils;

/**
 * @author vlads
 * 
 */
public class ChangesTest extends TestCase {

	private void verify(String signatureFileName, int ecpectedChanges) {
		APICompareConfig config = new APICompareConfig();
		config.apiLevel = APIFilter.PACKAGE;
		verify(signatureFileName, ecpectedChanges, config);
	}

	private void verify(String signatureFileName, int ecpectedChanges, APICompareConfig config) {
		List changes = APICompare.listChanges(Utils.getClassResourcePath("uut.signature.AChildClass"),
				"/net/sf/jour/signature/" + signatureFileName, config, true, null);
		String message = ChangeDetectedException.chageList(changes);
		// System.out.println(message);
		if (message.length() > 0) {
			message += "\n";
		}
		assertEquals("Changes:\n" + message, ecpectedChanges, changes.size());
	}

	public void testNoChange() {
		verify("base.xml", 0);
	}

	public void testHierarchyChange() {
		verify("hierarchyChange.xml", 1);
	}

	public void testInterfaceChange() {
		verify("interfaceChange.xml", 2);
	}

	public void testMethodMissing() {
		verify("methodMissing.xml", 2);
	}

	public void testMethodExtra() {
		verify("methodExtra.xml", 1);
	}

	public void testMethodSignature() {
		verify("methodSignature.xml", 1);
	}

	public void testMethodSignatureReturn() {
		verify("methodSignatureReturn.xml", 1);
	}

	public void testMethodSignatureModifiers() {
		verify("methodSignatureModifiers.xml", 1);
	}

	public void testConstructorMissing() {
		verify("constructorMissing.xml", 1);
	}

	public void testConstructorExtra() {
		verify("constructorExtra.xml", 1);
	}

	public void testConstructorThrow() {
		verify("constructorThrow.xml", 3);
	}

	public void testConstantValue() {
		verify("constantValue.xml", 2);
	}

	public void testMemberWithModifiers() {
	    verify("modifiers/memberWithModifiers.xml", 0);
	}
	
	public void testMemberWithModifiersTransientMissing() {
        verify("modifiers/memberWithModifiers-transient.xml", 1);
    }
	
	public void testMemberWithModifiersVolatileMissing() {
        verify("modifiers/memberWithModifiers-volatile.xml", 1);
    }
	
	public void testPackageAPIextension() {
		APICompareConfig config = new APICompareConfig();
		config.apiLevel = APIFilter.PROTECTED;
		verify("packageAPIextension.xml", 0, config);
	}

}
