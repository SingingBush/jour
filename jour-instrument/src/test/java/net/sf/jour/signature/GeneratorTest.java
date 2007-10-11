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
import net.sf.jour.signature.Generator;
import net.sf.jour.test.Utils;
import junit.framework.TestCase;

/**
 * @author vlads
 *
 */
public class GeneratorTest extends TestCase {
	
	protected static final Logger log = Logger.getLogger();
	
	public void testXMLGeneration() throws Exception {
		try {
			final String fileName = "target/generatorTest.xml";
			final String testPackages = "uut.signature";

			String classpath = Utils.getClassResourcePath(this.getClass().getName());
			
			Generator g = new Generator(classpath, testPackages, fileName);
			g.process();
			
			SignatureImport im = new SignatureImport();
			im.load(fileName);
			
			assertEquals("classes", im.getClassNames().size(), g.getClassNames().size());
			
			ExportClasses.export("target/test-api-classes", im.getClasses());
			
			Generator g2 = new Generator(null, null, "target/generatorTestImported.xml");
			g2.process(im.getClassPool(), im.getClassNames());
		} catch (Throwable e) {
			log.error("test error", e);
			fail(e.getMessage());
		}
	}
}
