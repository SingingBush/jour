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

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.bytecode.ClassFile;
import net.sf.jour.util.FileUtil;

/**
 * @author vlads
 * 
 */
public class ExportClasses {

	static final Map javaVersion = new HashMap();

	static {
		javaVersion.put("1.0", new int[] { 45, 3 });
		javaVersion.put("1.1", new int[] { 45, 3 });
		javaVersion.put("1.2", new int[] { 46, 3 });
		javaVersion.put("1.3", new int[] { 47, 0 });
		javaVersion.put("1.4", new int[] { 48, 0 });
		javaVersion.put("1.5", new int[] { 49, 0 });
		javaVersion.put("1.6", new int[] { 50, 0 });
	}

	public static void export(String directoryName, List classes, String classVersion) {
		FileUtil.deleteDir(new File(directoryName), false);
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
			} catch (CannotCompileException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
