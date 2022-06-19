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
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import net.sf.jour.processor.DirectoryInputSource;
import net.sf.jour.processor.Entry;
import net.sf.jour.processor.EntryHelper;
import net.sf.jour.processor.InputSource;
import net.sf.jour.processor.JarFileInputSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author vlads
 *
 */
public class Generator {

	protected static final Logger log = LoggerFactory.getLogger(Generator.class);

	private boolean useSystemClassPath = false;

	private String supportingJars;

	private String sources;

	private String packages;

	private String reportFile;

	private String filterLevel;

	private Set<String> packageSet = new HashSet<>();

	private List<String> classNames = new Vector<>();

	public Generator(Properties properties) {
		this(properties.getProperty("src"), properties.getProperty("packages"), properties.getProperty("dst"),
				properties.getProperty("level"));
		this.useSystemClassPath = "true".equals(properties.getProperty("systempath"));
		this.supportingJars = properties.getProperty("jars");
		this.filterLevel = properties.getProperty("level");
	}

	public Generator(String sources, String packages, String reportFile, String filterLevel) {
		super();
		this.sources = sources;
		this.packages = packages;
		this.reportFile = reportFile;
		this.filterLevel = filterLevel;

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
		classPool.appendPathList(input.getAbsolutePath());
		if (this.supportingJars != null) {
			classPool.appendPathList(this.supportingJars);
		}
		if (this.useSystemClassPath) {
			classPool.appendSystemPath();
		}

		List classes = new Vector();

		int countEntry = 0;

		APIFilter filter = new APIFilter(filterLevel);

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
				CtClass klass = classPool.get(className);
				if (filter.isAPIClass(klass)) {
					classes.add(klass);
					classNames.add(className);
				}
			}
		} finally {
			inputSource.close();
		}
		log.debug("countEntry   " + countEntry);

		Collections.sort(classes, new ClassSortComparator());

		ExportXML.export(reportFile, classes, filter);

	}

	private static class ClassSortComparator implements Comparator {

		public int compare(Object arg0, Object arg1) {
			return ((CtClass) (arg0)).getName().compareTo(((CtClass) (arg1)).getName());
		}

	}

	public void process(ClassPool classPool, List processClassNames) throws IOException, NotFoundException {
		APIFilter filter = new APIFilter(filterLevel);
		List classes = new Vector();
		for (Iterator iterator = processClassNames.iterator(); iterator.hasNext();) {
			String className = (String) iterator.next();
			CtClass klass = classPool.get(className);
			if (filter.isAPIClass(klass)) {
				classes.add(klass);
				classNames.add(className);
			}
		}
		ExportXML.export(reportFile, classes, filter);
	}

	public List getClassNames() {
		return this.classNames;
	}

	public String getReportFile() {
		return reportFile;
	}

	/**
	 * @return the useSystemClassPath
	 */
	public boolean isUseSystemClassPath() {
		return useSystemClassPath;
	}

	/**
	 * @param useSystemClassPath
	 *            the useSystemClassPath to set
	 */
	public void setUseSystemClassPath(boolean useSystemClassPath) {
		this.useSystemClassPath = useSystemClassPath;
	}

}
