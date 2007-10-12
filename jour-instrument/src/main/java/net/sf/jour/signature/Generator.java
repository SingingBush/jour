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
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javassist.ClassPool;
import javassist.NotFoundException;

import net.sf.jour.log.Logger;
import net.sf.jour.processor.DirectoryInputSource;
import net.sf.jour.processor.Entry;
import net.sf.jour.processor.EntryHelper;
import net.sf.jour.processor.InputSource;
import net.sf.jour.processor.JarFileInputSource;

/**
 * @author vlads
 *
 */
public class Generator {

	protected static final Logger log = Logger.getLogger();
	
	private boolean useSystemClassPath = true;
	
	private String supportingJars;
	
	private String sources;
	
	private String packages;
	
	private String reportFile;

	private Set packageSet = new HashSet();
	
	private List classNames = new Vector();
	
	public Generator(Properties properties) {
		this(properties.getProperty("src"), properties.getProperty("packages"), properties.getProperty("dst"));
		this.useSystemClassPath = "true".equals(properties.getProperty("systempath")); 
		this.supportingJars = properties.getProperty("jars");
	}
	
	public Generator(String sources, String packages, String reportFile) {
		super();
		this.sources = sources;
		this.packages = packages;
		this.reportFile = reportFile;
		
		if (reportFile == null) {
			this.reportFile = "api-signature.xml";
		}
		if (packages != null) {
			StringTokenizer st = new StringTokenizer(packages, ";");
			if (st.hasMoreTokens()) {
				while (st.hasMoreTokens()) {
					packageSet.add(st.nextToken());
				}
			} else {
				packageSet.add(packages);
			}
		}

	}

	private boolean isSelectedPackage(String className) {
		if (packages == null) {
			return true;
		}
		StringBuffer packageName = new StringBuffer();
		StringTokenizer st = new StringTokenizer(className, ".");
		while (st.hasMoreTokens()) {
			if (packageName.length() > 0) {
				packageName.append(".");
			}
			packageName.append(st.nextToken());
			if (packageSet.contains(packageName.toString())) {
				return true;
			}
		}
		return false;
	}

	public void process() throws IOException, NotFoundException {
		
		File input = new File(sources).getCanonicalFile();
		
		InputSource inputSource;
		if (input.isDirectory()) {
			inputSource = new DirectoryInputSource(input);
		} else {
			inputSource = new JarFileInputSource(input);
		}
		
		ClassPool classPool = new ClassPool();
		classPool.appendClassPath(input.getAbsolutePath());
		if (this.supportingJars != null) {
			classPool.appendClassPath(this.supportingJars);
		}
		if (this.useSystemClassPath) {
			classPool.appendSystemPath();
		}
		
		List classes = new Vector();
		
		int countEntry = 0;

		try {

			for (Enumeration en = inputSource.getEntries(); en.hasMoreElements();) {
				Entry entry = (Entry) en.nextElement();
				if (!entry.isClass()) {
					continue;
				}
				String className = EntryHelper.getClassName(entry);
				if (!isSelectedPackage(className)) {
					continue;
				}

				log.debug(entry.getName());
				countEntry++;
				classes.add(classPool.get(className));
				classNames.add(className);
			}
		} finally {
			inputSource.close();
		}
		log.debug("countEntry   " + countEntry);
		
		ExportXML.export(reportFile, classes);
		
	}
	
	public void process(ClassPool classPool, List processClassNames) throws IOException, NotFoundException {
	    List classes = new Vector();
	    for (Iterator iterator = processClassNames.iterator(); iterator.hasNext();) {
            String className = (String) iterator.next();
            classes.add(classPool.get(className));
            this.classNames.add(className);
        }
	    ExportXML.export(reportFile, classes);
	}

	public List getClassNames() {
		return this.classNames;
	}
	
}
