package net.sf.jour.processor;

import java.io.File;
import java.util.Enumeration;

import junit.framework.TestCase;
import net.sf.jour.log.Logger;
import net.sf.jour.test.Utils;
import org.junit.Test;

public class TestDirectoryInputSource extends TestCase {

	protected static final Logger log = Logger.getLogger();

    @Test
	public void testRecursion() throws Exception {

		String resourceName = this.getClass().getName();

		File dir = new File(Utils.getClassResourcePath(resourceName));

		String resource =  resourceName.replace('.', '/') + ".class";

		InputSource inputSource = new DirectoryInputSource(dir);

		boolean resourceFound = false;

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
		assertTrue("Resource not found in recursion", resourceFound);
	}

}
