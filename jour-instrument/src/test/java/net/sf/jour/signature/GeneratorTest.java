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

import net.sf.jour.log.Logger;
import net.sf.jour.test.Utils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author vlads
 *
 */
public class GeneratorTest {

	protected static final Logger log = Logger.getLogger();

    @Test
	public void testXMLGeneration() throws Exception {
		try {
			final String fileName = "target/generatorTest.xml";
			final String testPackages = "uut.signature";
			final String classpathTempDirectory = "target/test-api-classes";

			String classpath = Utils.getClassResourcePath(this.getClass().getName());

			final Generator g = new Generator(classpath, testPackages, fileName, "package");
			g.setUseSystemClassPath(true);
			g.process();

			assertTrue(g.getClassNames().size() > 0, "exported classes");

			final SignatureImport im = new SignatureImport(true, null);
			im.load(fileName);

			assertEquals(g.getClassNames().size(), im.getClassNames().size(), "imported classes");

            final APICompareConfig compareConfig = new APICompareConfig();
			compareConfig.apiLevel = APIFilter.PACKAGE;

			APICompare.compare(classpath, fileName, compareConfig, true, null);

			ExportClasses.export(classpathTempDirectory, im.getClasses(), "1.1");

            final Generator g2 = new Generator(null, null, "target/generatorTestImported.xml", "package");
			g2.setUseSystemClassPath(true);
			g2.process(im.getClassPool(), im.getClassNames());

            final Generator g3 = new Generator(classpathTempDirectory, null, "target/generatorTestImportedClasses.xml",
					"package");
			g3.setUseSystemClassPath(true);
			g3.process();

			APICompare.compare(classpathTempDirectory, fileName, compareConfig, true, null);

		} catch (Throwable e) {
			log.error("test error", e);
			fail(e.getMessage());
		}
	}
}
