package net.sf.jour.processor;

import java.io.File;
import java.util.Enumeration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.sf.jour.test.Utils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class TestJarFileInputSource {

	private static final Logger log = LoggerFactory.getLogger(TestJarFileInputSource.class);

    @Test
	public void testRecursion() throws Exception {
		final File jar = new File(Utils.getResourceAbsolutePath("/jarPreProcessorTest.jar"));
		log.debug(jar.getAbsolutePath());

		String resource = "uut.makeempty.MakeEmptyMethodCase".replace('.', '/') + ".class";

		InputSource<? extends Entry> inputSource = new JarFileInputSource(jar);

		boolean resourceFound = false;

		try {
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
		} finally {
			inputSource.close();
		}
		assertTrue(resourceFound, "Resource not found in recursion");
	}
}
