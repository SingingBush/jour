package net.sf.jour.processor;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.NoSuchElementException;

public class DirectoryInputSource implements InputSource<FileEntry> {

	private File dir;

	private String baseName;

	public DirectoryInputSource(File dir) throws IOException {
		this.dir = dir;
		baseName = dir.getCanonicalPath();
	}

	public Enumeration<FileEntry> getEntries() {
		return new DirectoryEnumeration(dir);
	}

	private class DirectoryEnumeration implements Enumeration<FileEntry> {

        private File[] files;

        private int processing;

        private Enumeration<FileEntry> child = null;

		DirectoryEnumeration(File dir) {
			files = dir.listFiles();
			if (files == null) {
				throw new Error(dir.getAbsolutePath() + " path does not denote a directory");
			}
			processing = 0;
		}

		public boolean hasMoreElements() {
			return ((child != null) && (child.hasMoreElements())) || (processing < files.length);
		}

		public FileEntry nextElement() {
			if (child != null) {
				try {
					return child.nextElement();
				} catch (NoSuchElementException e) {
					child = null;
				}
			}
			if (processing >= files.length) {
				throw new NoSuchElementException();
			}
			File next = files[processing++];
			if (next.isDirectory()) {
				child = new DirectoryEnumeration(next);
			}
			return new FileEntry(next, DirectoryInputSource.this.baseName);
		}

	}

	public void close() {

	}

}
