package net.sf.jour.processor;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarFileEntry implements Entry {

	JarFile jarFile;
	
	JarEntry jarEntry;
	
	public JarFileEntry(JarFile jarFile, JarEntry jarEntry) {
		this.jarFile = jarFile;
		this.jarEntry = jarEntry;
	}
	
	public InputStream getInputStream() throws IOException {
		return jarFile.getInputStream(this.jarEntry );
	}

	public Entry getOrigin() {
		return this;
	}
	
	public String getName() {
		return jarEntry.getName();
	}

	public long getSize() {
		return jarEntry.getSize();
	}

	public long getTime() {
		return jarEntry.getTime();
	}

	public boolean isClass() {
		return jarEntry.getName().endsWith(".class");
	}

	public boolean isDirectory() {
		return jarEntry.isDirectory();
	}

}
