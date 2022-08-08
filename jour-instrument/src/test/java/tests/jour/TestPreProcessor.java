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
package tests.jour;

import java.io.File;

import net.sf.jour.PreProcessor;
import tests.jour.test.Utils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/*
 * Created on Dec 2, 2004
 *
 * Contributing Author(s):
 *
 * Misha Lifschitz <mishalifschitz at users.sourceforge.net> (Inital
 * implementation) Vlad Skarzhevskyy <vlads at users.sourceforge.net> (Inital
 * implementation)
 *
 * @author michaellif
 *
 * @version $Revision$ ($Author$)
 */
public class TestPreProcessor {

    @Test
	public void testPreProcessorDirectory() throws Exception {

		File srcDir = new File(Utils.getClassResourcePath(this.getClass().getName()));

		File dstDir = new File(srcDir.getParentFile(), "test-iclasses-dir");

		PreProcessor pp = new PreProcessor(new String[] { "--config",
				Utils.getResourceAbsolutePath("/preProcessorTest.jour.xml"), "--src", srcDir.getAbsolutePath(),
				"--dst", dstDir.getAbsolutePath(), "--systempath" });
		pp.process();

		assertTrue(pp.getCountMethods() > 0, "Nothing instrumented");
	}

    @Test
	public void testPreProcessorJar() throws Exception {

		File srcJar = new File(Utils.getResourceAbsolutePath("/jarPreProcessorTest.jar"));

		File dstDir = new File(srcJar.getParentFile().getParentFile(), "test-iclasses-jar");

		PreProcessor pp = new PreProcessor(new String[] { "--config",
				Utils.getResourceAbsolutePath("/preProcessorTest.jour.xml"), "--src", srcJar.getAbsolutePath(),
				"--dst", dstDir.getAbsolutePath(), "--classpath", srcJar.getParentFile().getAbsolutePath(),
				"--systempath" });
		pp.process();

		assertTrue(pp.getCountMethods() > 0, "Nothing instrumented");
	}
}
