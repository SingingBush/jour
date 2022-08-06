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

import javassist.CtClass;
import net.sf.jour.log.Logger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * @author vlads
 *
 */
public class GeneratorObjectTest {

	protected static final Logger log = Logger.getLogger();

    @Test
	public void testXMLGeneration() throws Exception {
		try {
			final String signatureFileName = "object-signature.xml";
			final String classpathTempDirectory = "target/test-object-signature-classes";

			SignatureImport im = new SignatureImport(false, null);
			im.load("/net/sf/jour/signature/" + signatureFileName, null);

			assertEquals("imported classes", 1, im.getClassNames().size());

			CtClass objectClass = (CtClass) im.getClasses().get(0);
			assertNull("Sper must be NULL", objectClass.getSuperclass());

			ExportClasses.export(classpathTempDirectory, im.getClasses(), "1.1");

		} catch (Throwable e) {
			log.error("test error", e);
			fail(e.getMessage());
		}
	}
}
