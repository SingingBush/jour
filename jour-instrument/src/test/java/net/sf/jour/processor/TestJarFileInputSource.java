package net.sf.jour.processor;

import java.io.File;
import java.util.Enumeration;
import net.sf.jour.log.Logger;
import net.sf.jour.test.Utils;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestJarFileInputSource {

	protected static final Logger log = Logger.getLogger();

    @Test
	public void testRecursion() throws Exception {

		String testJar = "/jarPreProcessorTest.jar";

		File jar = new File(Utils.getResourceAbsolutePath(testJar));
		log.debug(jar.getAbsolutePath());

		String resourceName = "uut.makeempty.MakeEmptyMethodCase";

		String resource = resourceName.replace('.', '/') + ".class";

		InputSource inputSource = new JarFileInputSource(jar);

		boolean resourceFound = false;

		try {
			for (Enumeration en = inputSource.getEntries(); en.hasMoreElements();) {
				Entry e = (Entry) en.nextElement();
				log.debug(e.getName());
				if (e.getName().equals(resource)) {
					if (resourceFound) {
						fail("Duplicate resource found");
					}
					resourceFound = true;
				}
			}
		} finally {
			inputSource.close();
		}
		assertTrue("Resource not found in recursion", resourceFound);
	}
}
