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
package tests.jour.signature;

import java.util.List;

import net.sf.jour.signature.APICompare;
import net.sf.jour.signature.APICompareConfig;
import net.sf.jour.signature.APIFilter;
import net.sf.jour.signature.ChangeDetectedException;
import tests.jour.test.Utils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author vlads
 *
 */
public class ChangesTest {

	private void verify(String signatureFileName, int ecpectedChanges) {
		APICompareConfig config = new APICompareConfig();
		config.apiLevel = APIFilter.PACKAGE;
		verify(signatureFileName, ecpectedChanges, config);
	}

	private void verify(String signatureFileName, int ecpectedChanges, APICompareConfig config) {
		final List<String> changes = APICompare.listChanges(Utils.getClassResourcePath("uut.signature.AChildClass"),
				"/net/sf/jour/signature/" + signatureFileName, config, true, null);

		String message = ChangeDetectedException.chageList(changes);
		// System.out.println(message);
		if (message.length() > 0) {
			message += "\n";
		}
		assertEquals(ecpectedChanges, changes.size(), "Changes:\n" + message);
	}

    @Test
	public void testNoChange() {
		verify("base.xml", 0);
	}

    @Test
	public void testHierarchyChange() {
		verify("hierarchyChange.xml", 1);
	}

    @Test
	public void testInterfaceChange() {
		verify("interfaceChange.xml", 2);
	}

    @Test
	public void testMethodMissing() {
		verify("methodMissing.xml", 2);
	}

    @Test
	public void testMethodExtra() {
		verify("methodExtra.xml", 1);
	}

    @Test
	public void testMethodSignature() {
		verify("methodSignature.xml", 1);
	}

    @Test
	public void testMethodSignatureReturn() {
		verify("methodSignatureReturn.xml", 1);
	}

    @Test
	public void testMethodSignatureModifiers() {
		verify("methodSignatureModifiers.xml", 1);
	}

    @Test
	public void testConstructorMissing() {
		verify("constructorMissing.xml", 1);
	}

    @Test
	public void testConstructorExtra() {
		verify("constructorExtra.xml", 1);
	}

    @Test
	public void testConstructorThrow() {
		verify("constructorThrow.xml", 3);
	}

    @Test
	public void testConstantValue() {
		verify("constantValue.xml", 2);
	}

    @Test
	public void testMemberWithModifiers() {
	    verify("modifiers/memberWithModifiers.xml", 0);
	}

    @Test
	public void testMemberWithModifiersTransientMissing() {
        verify("modifiers/memberWithModifiers-transient.xml", 1);
    }

    @Test
	public void testMemberWithModifiersVolatileMissing() {
        verify("modifiers/memberWithModifiers-volatile.xml", 1);
    }

    @Test
	public void testPackageAPIextension() {
		APICompareConfig config = new APICompareConfig();
		config.apiLevel = APIFilter.PROTECTED;
		verify("packageAPIextension.xml", 0, config);
	}

}
