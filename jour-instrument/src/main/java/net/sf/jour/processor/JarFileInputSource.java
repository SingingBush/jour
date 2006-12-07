package net.sf.jour.processor;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarFileInputSource implements InputSource {

	JarFile jarFile;
	
	public JarFileInputSource(File file) throws IOException {
		jarFile = new JarFile(file);
	}
	
	public void close() {
		try {
			jarFile.close();
		} catch (IOException ignore) {
		}
	}

	
	public Enumeration getEntries() {
		return new JarEnumeration();
	}

	private class JarEnumeration implements Enumeration {

		Enumeration jarEnum;
		
		JarEnumeration() {
			jarEnum = jarFile.entries();
		}
		
		public boolean hasMoreElements() {
			return jarEnum.hasMoreElements();
		}

		public Object nextElement() {
			JarEntry jarEntry = (JarEntry)jarEnum.nextElement();
			return new JarFileEntry(jarFile, jarEntry);
		}
		
	}
}
