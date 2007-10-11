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
import java.util.Iterator;
import java.util.List;

import net.sf.jour.util.FileUtil;

import javassist.CannotCompileException;
import javassist.CtClass;

/**
 * @author vlads
 *
 */
public class ExportClasses {

	public static void export(String directoryName, List classes) {
		FileUtil.deleteDir(new File(directoryName), false);
		for (Iterator iterator = classes.iterator(); iterator.hasNext();) {
			CtClass klass = (CtClass) iterator.next();
			try {
				klass.writeFile(directoryName);
			} catch (CannotCompileException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
