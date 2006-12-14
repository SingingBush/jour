package net.sf.jour.processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileEntry implements Entry {

	private File file;
	
	private String name;
	
	public FileEntry(File file, String baseName) {
		this.file = file;
		name = file.getAbsolutePath().substring(baseName.length() + 1).replace('\\', '/');
	}
	
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(file);
	}

	public Entry getOrigin() {
		return this;
	}
	
	public String getName() {
		return name;
	}

	public long getSize() {
		return file.length();
	}

	public long getTime() {
		return file.lastModified();
	}

	public boolean isClass() {
		return file.getName().endsWith(".class");
	}

	public boolean isDirectory() {
		return file.isDirectory();
	}

	public boolean equals(Object o) {
		 if ((o == null) || (!(o instanceof FileEntry))) {
			 return false;
		 }
		 return file.equals(((FileEntry)o).file);
	}
	 
}
