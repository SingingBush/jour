package net.sf.jour.processor;

import java.io.File;
import java.util.Enumeration;

import net.sf.jour.log.Logger;
import net.sf.jour.test.Utils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class TestDirectoryInputSource {

	protected static final Logger log = Logger.getLogger();

    @Test
	public void testRecursion() throws Exception {

		String resourceName = this.getClass().getName();

		File dir = new File(Utils.getClassResourcePath(resourceName));

		String resource =  resourceName.replace('.', '/') + ".class";

		InputSource<? extends Entry> inputSource = new DirectoryInputSource(dir);

		boolean resourceFound = false;

		for (Enumeration<? extends Entry> en = inputSource.getEntries(); en.hasMoreElements();) {
			final Entry e = en.nextElement();

			log.debug(e.getName());
			if (e.getName().equals(resource)) {
				if (resourceFound) {
					fail("Duplicate resource found");
				}
				resourceFound = true;
			}
		}
		assertTrue(resourceFound, "Resource not found in recursion");
	}

}
