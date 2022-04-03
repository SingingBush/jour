/*
 * Jour - bytecode instrumentation library
 *
 * Copyright (C) 2007 Vlad Skarzhevskyy
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
 *
 * @version $Id$
 *
 */
package net.sf.jour.signature;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.bytecode.ClassFile;
import net.sf.jour.util.FileUtil;

/**
 * @author vlads
 *
 */
public class ExportClasses {

	static final Map<String, int[]> javaVersion = new HashMap<>();

	static {
		javaVersion.put("1.0", new int[] { 45, 3 });
		javaVersion.put("1.1", new int[] { 45, 3 });
		javaVersion.put("1.2", new int[] { 46, 3 });
		javaVersion.put("1.3", new int[] { 47, 0 });
		javaVersion.put("1.4", new int[] { 48, 0 });
		javaVersion.put("1.5", new int[] { 49, 0 });
		javaVersion.put("1.6", new int[] { 50, 0 });
		javaVersion.put("1.7", new int[] { 51, 0 });
		javaVersion.put("1.8", new int[] { 52, 0 });
		javaVersion.put("9", new int[] { 53, 0 });
		javaVersion.put("10", new int[] { 54, 0 });
		javaVersion.put("11", new int[] { 55, 0 }); // Java 12 uses major version 56
		javaVersion.put("12", new int[] { 56, 0 }); // Java 12 uses major version 56
		javaVersion.put("13", new int[] { 57, 0 }); // Java 13 uses major version 57
		javaVersion.put("14", new int[] { 58, 0 }); // Java 14 uses major version 58
		javaVersion.put("15", new int[] { 59, 0 }); // Java 15 uses major version 59
		javaVersion.put("16", new int[] { 60, 0 }); // Java 16 uses major version 60
		javaVersion.put("17", new int[] { 61, 0 }); // Java 17 uses major version 61
		javaVersion.put("18", new int[] { 62, 0 }); // Java 18 uses major version 62
	}

	public static int export(String directoryName, CtClass klass, String classVersion) {
		List classes = new Vector();
		classes.add(klass);
		return export(directoryName, classes, classVersion);
	}

	public static int export(String directoryName, List classes, String classVersion) {
		File dir = new File(directoryName);
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				throw new RuntimeException("Can't create directory " + directoryName);
			}
		} else {
			FileUtil.deleteDir(new File(directoryName), false);
		}
		int count = 0;
		for (Iterator iterator = classes.iterator(); iterator.hasNext();) {
			CtClass klass = (CtClass) iterator.next();
			try {
				if (classVersion != null) {
					int[] majorMinor = (int[]) javaVersion.get(classVersion);
					if (majorMinor == null) {
						throw new RuntimeException("Unknown classVersion " + classVersion);
					}
					ClassFile cf = klass.getClassFile();
					cf.setMajorVersion(majorMinor[0]);
					cf.setMinorVersion(majorMinor[1]);
				}

				klass.writeFile(directoryName);
				count++;

			} catch (CannotCompileException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return count;
	}
}
