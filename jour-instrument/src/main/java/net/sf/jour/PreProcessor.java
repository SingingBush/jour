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
package net.sf.jour;

import java.io.File;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import net.sf.jour.instrumentor.Instrumentor;
import net.sf.jour.processor.DirectoryInputSource;
import net.sf.jour.processor.DirectoryOutputWriter;
import net.sf.jour.processor.Entry;
import net.sf.jour.processor.InputSource;
import net.sf.jour.processor.InstrumentedEntry;
import net.sf.jour.processor.JarFileInputSource;
import net.sf.jour.processor.OutputWriter;
import net.sf.jour.util.CmdArgs;

import org.apache.log4j.Logger;

/**
 *
 * Created on 02.10.2004
 *
 * Contributing Author(s):
 *
 *   Misha Lifschitz <mishalifschitz at users.sourceforge.net> (Inital implementation)
 *   Vlad Skarzhevskyy <vlads at users.sourceforge.net> (Inital implementation)
 * 
 * @author michaellif
 * @version $Revision$ ($Author$) $Date$
 */
public class PreProcessor {

	protected static final Logger log = Logger.getLogger(PreProcessor.class);

	public long savedClasses;

	public long countClasses;

	private long countMethods;

	private long countCounstructors;

	private File input;

	private File output;

	Config config;

	ClassPool classPool;

	public PreProcessor(String[] args) throws NotFoundException {
		this(CmdArgs.load(args));
	}

	public PreProcessor(Properties properties) throws NotFoundException {
		String configFileName = properties.getProperty("config");
		String out = properties.getProperty("dst");
		String in = properties.getProperty("src");
		if (out == null) {
			out = in;
		}

		List classpath = new Vector();

		Object cp = properties.get("cp");
		if (cp != null) {
			if (cp instanceof List) {
				classpath.addAll((List) cp);
			} else {
				classpath.add(cp);
			}
		}
		cp = properties.get("classpath");
		if (cp != null) {
			if (cp instanceof List) {
				classpath.addAll((List) cp);
			} else {
				classpath.add(cp);
			}
		}

		init(configFileName, new File(in), new File(out), classpath);
	}

	public PreProcessor(String configFileName, File in, File out, List classpath) {
		init(configFileName, in, out, classpath);
	}

	private void init(String configFileName, File in, File out, List classpath) {
		if ((in == null) || (!in.exists())) {
			throw new Error("Input jar or folder expected");
		}
		if (in.isFile() && (!in.getName().endsWith(".jar"))) {
			throw new Error("Input file should be .jar");
		}
		this.input = in;
		this.output = out;
		this.config = new Config(configFileName);
		this.classPool = new ClassPool();
		this.classPool.appendSystemPath();
		try {
			this.classPool.appendClassPath(this.input.getAbsolutePath());

			if (classpath != null) {
				for (Iterator i = classpath.iterator(); i.hasNext();) {
					this.classPool.appendPathList((String) i.next());
				}
			}
		} catch (NotFoundException e) {
			log.error("Can't setup class path", e);
			throw new ConfigException("Can't setup class path", e);
		}
	}

	public void process() throws Exception {

		OutputWriter outputWriter;
		if (!output.isFile()) {
			outputWriter = new DirectoryOutputWriter(output);
		} else {
			throw new ConfigException("jar output not supported yet");
		}

		InputSource inputSource;
		if (input.isDirectory()) {
			inputSource = new DirectoryInputSource(input);
		} else {
			inputSource = new JarFileInputSource(input);
		}

		int countEntry = 0;

		try {

			for (Enumeration en = inputSource.getEntries(); en.hasMoreElements();) {
				Entry e = (Entry) en.nextElement();
				log.debug(e.getName());
				if (outputWriter.needUpdate(e)) {
					outputWriter.write(instrument(e));
				}
				countEntry++;
			}
		} finally {
			inputSource.close();
			outputWriter.close();
		}
		log.debug("countEntry   " + countEntry);
		log.debug("countClasses " + countClasses);

		log.info("Altered Counstructors " + this.countCounstructors);
		log.info("Altered Methods       " + this.countMethods);
		log.info("Saved Classes         " + this.savedClasses);
	}

	public Entry instrument(Entry entry) {
		if (!entry.isClass()) {
			return entry;
		}
		this.countClasses++;
		String className = entry.getName().replace('/', '.');
		className = className.substring(0, className.lastIndexOf('.'));
		Instrumentor[] instrumentors = config.getInstrumentors(className);
		if (instrumentors.length > 0) {
			log.debug("intercepting class " + className);
			Interceptor interceptor = new Interceptor(config, classPool, className, instrumentors);
			CtClass ctClass = interceptor.instrument();
			log.debug("intercepted methods " + interceptor.getCountMethods());
			if (interceptor.isModified()) {
				this.savedClasses++;
				this.countCounstructors += interceptor.getCountCounstructors();
				this.countMethods += interceptor.getCountMethods();
				return new InstrumentedEntry(entry, ctClass);
			}
		}
		return entry;
	}

	public long getCountCounstructors() {
		return countCounstructors;
	}

	public long getCountMethods() {
		return countMethods;
	}

	public long getSavedClasses() {
		return savedClasses;
	}

	public long getCountClasses() {
		return countClasses;
	}

	public static void main(String[] args) {
		Properties argsp = CmdArgs.load(args);
		if ((args.length < 1) || argsp.getProperty("help") != null) {
			StringBuffer usage = new StringBuffer();
			usage.append("Usage:java ").append(PreProcessor.class.getName());
			usage.append("--config jour.xml --src classesDir|classes.jar --dst outDir|out.jar\n");
			usage.append("    (--classpath classpath)\n");
			return;
		}

		try {
			PreProcessor pp = new PreProcessor(argsp);
			pp.process();
		} catch (Throwable e) {
			e.printStackTrace();
			throw new Error(e);
		}
	}

}
