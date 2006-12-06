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

import java.io.*;
import java.util.*;

import javassist.*;
import net.sf.jour.instrumentor.*;

import org.apache.log4j.Logger;

//import net.sf.jour.util.PropertiesBase;
import net.sf.jour.util.FileUtil;

/**
 * TODO add docs.
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
	
    public static long startTime = new Date().getTime();
	
    public long savedClasses;
    private long countMethods;
    private long countCounstructors;
    
    private static final char FILE_SEPARATOR = System.getProperty("file.separator").charAt(0);

	//private PropertiesBase properties;
	
    private File startFile;
    
	private File classList;

    private String outDir;
    
    Config config;

    public PreProcessor(/*PropertiesBase properties*/) {
//    	//this.properties = properties;
//		
//    	log.info("java.class.path = " + System.getProperty("java.class.path"));
//    	
//    	File out;// = properties.getFolder("dst", true);
//		if (out == null) {
//			throw new Error("-dst DIR Output folder expected");
//		}
//		
//        String classlist = properties.getProperty("classlist");
//        if (classlist != null) {
//            this.classList = properties.getFile("classlist", true);
//            if (this.classList == null) {
//                throw new Error("-classlist fileName Input file expected");
//            }
//        } else {
//            File f = properties.getFolder("src", false);
//            if (f == null) {
//                throw new Error("-src DIR Input folder expected");
//            }
//            this.startFile = f;
//        }
//
//        System.out.println("Start Processor");
//
//		this.outDir = out.getAbsolutePath();
		
		config = new Config(); 
    }

    public void process() throws Exception {
		
//		InstrumentationMap map = InstrumentationMap.instance();
//		String mapFile = properties.getProperty("imap", 
//			outDir + File.separator + InstrumentationMap.DEFULAT_FILE_NAME);
//		
//		if (new File(mapFile).canRead()) {
//			map.load(new File(mapFile));
//    	}	
//		
//    	if (this.startFile != null) {
//	        process(this.startFile);
//    	} else if (this.classList != null) {
//			processList(this.classList);
//    	}
//    	
//    	if (map.isUpdated()) {
//    		if (map.save(mapFile)) {
//    			log.info("Map saved " + mapFile);
//    		}
//    	}
//    	log.info("Map size " + map.size());
//		log.info("Altered Counstructors " + this.countCounstructors);
//		log.info("Altered Methods " + this.countMethods);
//		log.info("Saved Classes " + savedClasses);
    }

    public void process(File f) throws Exception {
        if (f.isFile()) {
            processFile(f);
        } else if (f.isDirectory()) {
            processDirectory(f);
        }
    }

    void processDirectory(File f) throws Exception {
        String[] flist = f.list();
        String initPath = f.getCanonicalPath();
		log.debug(f);
        for (int i = 0; i < flist.length; i++) {
            String path = initPath;

            if (path.charAt(path.length() - 1) != FILE_SEPARATOR) {
                path += FILE_SEPARATOR;
            }

            path += flist[i];

            File f1 = new File(path);
            process(f1);
        }
    }

    void processFile(File f) throws Exception {
        //log.debug(f);
        String className = fileName2Class(f);
        if (className != null) {
			processClass(className);
        }
    }
    
	private void processList(File f) throws Exception {
		HashSet list = FileUtil.readTextFile(f);
		if (list == null) {
			return;
		}
		for (Iterator i = list.iterator(); i.hasNext(); ) {
			String className = (String) i.next();
			processClass(className);
		}
	}

    void processClass(String className) throws Exception {
        log.debug("className " + className);
        ClassPool pool = ClassPool.getDefault();
        Instrumentor[] instrumentors = config.getInstrumentors(className);
        if (instrumentors.length > 0) {
            log.debug("intercepting class " + className);
            Interceptor interceptor = new Interceptor(pool, className, instrumentors);
            CtClass cc = interceptor.instrument();
            log.debug("intercepted methods " + interceptor.getCountMethods());
            if (interceptor.isModified()) {
	            cc.writeFile(outDir);
    	        this.savedClasses ++;
    	        this.countCounstructors += interceptor.getCountCounstructors();
    	        this.countMethods += interceptor.getCountMethods();
            }
        }
    }

    String fileName2Class(File f) {
        String className = null;

        try {
            if (f.getName().endsWith(".class")) {
            	String baseName = startFile.getCanonicalPath();
            	String fileName = f.getCanonicalPath();
                int idx = fileName.indexOf(baseName);
                if (idx == -1) {
            		log.debug("base:" + baseName);
            		log.debug("file:" + fileName);
                	return null;
                }
            	idx = baseName.length() + 1;
                    
                className = fileName.substring(idx).replace(FILE_SEPARATOR, '.');
                className = className.substring(0,
                        className.length() - ".class".length());
            }
        } catch (IOException e) {
            log.error("File name rrror ", e);
            return null;
        }

        return className;
    }

    String class2FileName(String classname) {
        return startFile.getAbsolutePath() + FILE_SEPARATOR +
        classname.replace('.', FILE_SEPARATOR) + ".class";
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage:java net.sf.jour.PreProcessor " 
            + " -dst outDir \n"
            + " (-src targetDir | -classlist fileName.txt)"
            + " [-imap mapFileName]");
            System.exit(1);
        }

        try {
			//PropertiesBase pargs = new PropertiesBase();
			//pargs.loadArgs(args);
			
            //PreProcessor pp = new PreProcessor(pargs);
            //pp.process();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
