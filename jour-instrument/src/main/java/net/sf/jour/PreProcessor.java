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
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import net.sf.jour.instrumentor.Instrumentor;
import net.sf.jour.instrumentor.InstrumentorResults;
import net.sf.jour.instrumentor.InstrumentorResultsImpl;
import net.sf.jour.log.Logger;
import net.sf.jour.processor.DirectoryInputSource;
import net.sf.jour.processor.DirectoryOutputWriter;
import net.sf.jour.processor.Entry;
import net.sf.jour.processor.EntryHelper;
import net.sf.jour.processor.InputSource;
import net.sf.jour.processor.InstrumentedCreatedEntry;
import net.sf.jour.processor.InstrumentedEntry;
import net.sf.jour.processor.JarFileInputSource;
import net.sf.jour.processor.OutputWriter;
import net.sf.jour.util.BuildVersion;
import net.sf.jour.util.CmdArgs;

/**
 * 
 * Created on 02.10.2004
 * 
 * Contributing Author(s):
 * 
 * Misha Lifschitz <mishalifschitz at users.sourceforge.net> (Inital
 * implementation) Vlad Skarzhevskyy <vlads at users.sourceforge.net> (Inital
 * implementation)
 * 
 * @author michaellif
 * @version $Revision$ ($Author$) $Date: 2007-10-10 18:49:54 -0400
 *          (Wed, 10 Oct 2007) $
 */
public class PreProcessor {

	protected static final Logger log = Logger.getLogger();

	public long savedClasses;

	public long countClasses;

	private long countMethods;

	private long countCounstructors;

	private File input;

	private File output;

	private boolean copyClasses = false;

	private boolean copyResources = false;

	private boolean useSystemClassPath = false;

	Config config;

	ClassPool classPool;

	public static void main(String[] args) {
		Properties argsp = CmdArgs.load(args);
		if ((args.length < 1) || argsp.getProperty("help") != null) {
			StringBuffer usage = new StringBuffer();
			usage.append("Usage:\n java ").append(PreProcessor.class.getName());
			usage.append(" --config jour.xml --src classesDir|classes.jar --dst outDir\n");
			usage.append("    (--classpath classpath) (--copy resource|classes|all) (--systempath)\n");
			System.out.println(usage);
			return;
		}

		try {
			PreProcessor pp = new PreProcessor(argsp);
			pp.process();

			System.out.println("Processed Classes     " + pp.countClasses);
			System.out.println("Altered Counstructors " + pp.countCounstructors);
			System.out.println("Altered Methods       " + pp.countMethods);
			System.out.println("Saved Classes         " + pp.savedClasses);

		} catch (Throwable e) {
			e.printStackTrace();
			throw new Error(e);
		}
	}

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
		String copy = properties.getProperty("copy");
		if ("all".equalsIgnoreCase(copy)) {
			setCopyClasses(true);
			setCopyResources(true);
		} else if ("classes".equalsIgnoreCase(copy)) {
			setCopyClasses(true);
		} else if ("resources".equalsIgnoreCase(copy)) {
			setCopyResources(true);
		}

		this.useSystemClassPath = "true".equals(properties.getProperty("systempath"));

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

		try {
			init(configFileName, new File(in).getCanonicalFile(), new File(out).getCanonicalFile(), classpath);
		} catch (IOException e) {
			throw new Error("Can't configure input/output path", e);
		}
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
		log.debug("input " + in.getAbsolutePath());
		log.debug("output " + out.getAbsolutePath());
		this.input = in;
		this.output = out;
		this.config = new Config(configFileName);
		this.classPool = new ClassPool();
		try {
			this.classPool.appendClassPath(this.input.getAbsolutePath());

			if (classpath != null) {
				for (Iterator i = classpath.iterator(); i.hasNext();) {
					String path = (String) i.next();
					log.debug("classPath " + path);
					this.classPool.appendPathList(path);
				}
			}
		} catch (NotFoundException e) {
			log.error("Can't setup class path", e);
			throw new ConfigException("Can't setup class path", e);
		}
	}

	public void process() throws Exception {

		log.info("Jour, version " + BuildVersion.getVersion());

		if (this.useSystemClassPath) {
			log.debug("useSystemClassPath");
			this.classPool.appendSystemPath();
		}

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
		int countResources = 0;
		int countNIClasses = 0;
		try {

			for (Enumeration en = inputSource.getEntries(); en.hasMoreElements();) {
				Entry e = (Entry) en.nextElement();
				log.debug(e.getName());
				if (outputWriter.needUpdate(e)) {
					if (!e.isClass()) {
						if (isCopyResources()) {
							outputWriter.write(e);
							countResources++;
						}
					} else {
						if (instrument(e, outputWriter) == InstrumentorResultsImpl.NOT_MODIFIED) {
							if (isCopyClasses()) {
								outputWriter.write(e);
								countNIClasses++;
							}
						}
					}
				}
				countEntry++;
			}
		} finally {
			inputSource.close();
			outputWriter.close();
		}
		log.debug("countEntry   " + countEntry);

		log.info("Processed Classes     " + this.countClasses);
		log.info("Altered Counstructors " + this.countCounstructors);
		log.info("Altered Methods       " + this.countMethods);
		log.info("Saved Classes         " + this.savedClasses);
		if (isCopyClasses()) {
			log.info("NotInstr Classes      " + countNIClasses);
		}
		if (isCopyResources()) {
			log.info("Resources             " + countResources);
		}
	}

	public InstrumentorResults instrument(Entry entry, OutputWriter outputWriter) throws IOException {
		this.countClasses++;
		String className = EntryHelper.getClassName(entry);
		Instrumentor[] instrumentors = config.getInstrumentors(className);
		if (instrumentors.length > 0) {
			log.debug("intercepting class " + className);
			Interceptor interceptor = new Interceptor(config, classPool, className, instrumentors);
			CtClass ctClass = interceptor.instrument();
			InstrumentorResults rc = interceptor.getInstrumentorResults();
			log.debug("intercepted methods " + rc.getCountMethods());
			if (rc.isModified()) {
				this.savedClasses++;
				this.countCounstructors += rc.getCountCounstructors();
				this.countMethods += rc.getCountMethods();
				outputWriter.write(new InstrumentedEntry(entry, ctClass));
				if (rc.getCreatedClasses() != null) {
					for (Iterator i = rc.getCreatedClasses().iterator(); i.hasNext();) {
						outputWriter.write(new InstrumentedCreatedEntry(entry, ctClass, (CtClass) i.next()));
						this.savedClasses++;
					}
				}
				return rc;
			}
		}
		return InstrumentorResultsImpl.NOT_MODIFIED;
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

	/**
	 * @return the copyClasses
	 */
	public boolean isCopyClasses() {
		return copyClasses;
	}

	/**
	 * @param copyClasses
	 *            the copyClasses to set
	 */
	public void setCopyClasses(boolean copyClasses) {
		this.copyClasses = copyClasses;
	}

	/**
	 * @return the copyResources
	 */
	public boolean isCopyResources() {
		return copyResources;
	}

	/**
	 * @param copyResources
	 *            the copyResources to set
	 */
	public void setCopyResources(boolean copyResources) {
		this.copyResources = copyResources;
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
