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
package net.sf.jour.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {

	private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

	public static URL getFileLocation(String resource) {
		return getFileLocation(resource, null);
	}

	public static URL getFileLocation(String resource, ClassLoader loader) {
		URL url = null;
		if (loader != null) {
			url = loader.getResource(resource);
		}
		if (url == null) {
			ClassLoader loader2 = Thread.currentThread().getContextClassLoader();
			if (loader2 != null) {
				url = loader2.getResource(resource);
			}
		}
		if (url == null) {
			ClassLoader loader3 = FileUtil.class.getClassLoader();
			url = loader3.getResource(resource);
		}
		if (url == null) {
			url = FileUtil.class.getResource(resource);
		}
		if (url != null) {
			log.debug("rc[" + resource + "] -> " + url.getFile());
		}
		return url;
	}

	public static URL getFile(String fileName) {
		return getFile(fileName, FileUtil.class.getClassLoader());
	}

	public static URL getFile(String fileName, Object owner) {
		URL url = owner.getClass().getResource(fileName);
		if (url != null) {
			return url;
		}
		return getFile(fileName, owner.getClass().getClassLoader());
	}

	public static URL getFile(String fileName, ClassLoader loader) {
		File file = new File(fileName);
		if (!file.canRead()) {
			URL location = getFileLocation(fileName, loader);
			if (location != null) {
				return location;
			} else {
				log.debug("[" + fileName + "] -> not found");
				return null;
			}
		} else {
			log.debug(fileName + "->" + file.getAbsolutePath());
			try {
				return new URL("file:" + file.getAbsolutePath());
			} catch (MalformedURLException e) {
				log.error("Error", e);
				return null;
			}
		}
	}

	public static boolean deleteDir(File dir, boolean delSelf) {
		if (dir.isDirectory()) {
			final String[] children = Objects.requireNonNull(dir.list());
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child), true);
                if (!success) {
                    return false;
                }
            }
		}
		if (delSelf) {
			return dir.delete();
		} else {
			return true;
		}
	}

    // Nullable
	public static HashSet<String> readTextFile(File file) {
		final HashSet<String> list = new HashSet<>();
		if (!readTextFile(file, list)) {
			return null;
		}
		return list;
	}

	public static boolean readTextFile(File file, HashSet<String> list) {
		final String filename = file.getName();

		try (BufferedReader in = new BufferedReader(new FileReader(file))) {
			final String directiveInclude = "#include";

			while (true) {
				String l = in.readLine();
				if (l == null)
					break;
				l = l.trim();
				if ((l.length() == 0) || (l.startsWith("#"))) {

					if (l.startsWith(directiveInclude)) {
						String fileName = l.substring(directiveInclude.length()).trim();
						if (!readTextFile(new File(fileName), list)) {
							return false;
						}
					}

					continue;
				}
				if (!list.contains(l)) {
					list.add(l);
				}
			}

		} catch (Exception e) {
			log.error("Read error " + filename, e);
			return false;
		}
        log.debug("Read list of {} classes in {}", list.size(), filename);
		return true;
	}

	static public File[] sortFileListByDate(File[] children) {
		Arrays.sort(children, new FileListByDateComparator());
		return children;
	}

	static public File[] sortFileListByName(File[] children) {
		Arrays.sort(children, new FileListByNameComparator());
		return children;
	}

	public static class FileListByDateComparator implements Comparator {

		public FileListByDateComparator() {
		}

		public int compare(Object o1, Object o2) {
			File f1 = (File) o1;
			File f2 = (File) o2;
			// keep folders at the top of the list
			if (f1.isDirectory() && !f2.isDirectory()) {
				return -1;
			} else if (!f1.isDirectory() && f2.isDirectory()) {
				return 1;
			}

			if (f1.lastModified() < f2.lastModified()) {
				return -1;
			} else if (f1.lastModified() > f2.lastModified()) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	public static class FileListByNameComparator implements Comparator<File> {

		public FileListByNameComparator() {
		}

        @Override
        public int compare(File f1, File f2) {
			// keep folders at the top of the list
			if (f1.isDirectory() && !f2.isDirectory()) {
				return -1;
			} else if (!f1.isDirectory() && f2.isDirectory()) {
				return 1;
			}

			return f1.getName().compareTo(f2.getName());
		}
    }

}
