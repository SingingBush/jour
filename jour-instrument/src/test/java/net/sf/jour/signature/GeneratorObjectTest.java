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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

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

            final SignatureImport im = new SignatureImport(false, null);
			im.load("/net/sf/jour/signature/" + signatureFileName, null);

			assertEquals(1, im.getClassNames().size(), "imported classes");

            final CtClass objectClass = im.getClasses().get(0);
			assertNull(objectClass.getSuperclass(), "Sper must be NULL");

			ExportClasses.export(classpathTempDirectory, im.getClasses(), "1.1");

		} catch (final Throwable e) {
			log.error("test error", e);
			fail(e.getMessage());
		}
	}
}
