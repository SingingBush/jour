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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Unmarshaller;
import org.xml.sax.InputSource;

/**
 * TODO Add docs
 * Need this Class so runtime would not depend on JAXB.
 * 
 * Created on 04.12.2004
 *
 * Contributing Author(s):
 *
 *   Misha Lifschitz <mishalifschitz at users.sourceforge.net> (Inital implementation)
 *   Vlad Skarzhevskyy <vlads at users.sourceforge.net> (Inital implementation)
 *
 * @author vlads
 * @version $Revision$ ($Author$) $Date$
 */

public class ConfigFileUtil extends FileUtil {

	protected static final Logger log = Logger.getLogger(ConfigFileUtil.class);
	
    public static Object unmarshal(InputStream configStream, String castorMapingResource) {
        InputStream mappingStream = null;
        try {
            mappingStream = ConfigFileUtil.class.getResourceAsStream(castorMapingResource);
            if (mappingStream == null) {
                throw new FileNotFoundException(castorMapingResource);
            }
            Mapping mapping = new Mapping();
            mapping.loadMapping(new InputSource(mappingStream));
            Unmarshaller unmar = new Unmarshaller(mapping);
            return unmar.unmarshal(new InputSource(configStream));
        } catch (Throwable e) {
        	log.error("Can't configure processor", e);
            throw new Error("Can't configure processor " + e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(mappingStream);
        }
    }
    
    public static Object unmarshal(String resourceXML, String castorMapingResource) {
        InputStream configStream = null;
        try {
            File config = new File(resourceXML);
            if (!config.exists()) {
                configStream = ConfigFileUtil.class.getResourceAsStream(resourceXML);
                if (configStream == null) {
                    throw new Error("resource or file not found " + resourceXML);
                }
            } else {
                try {
                    configStream = new FileInputStream(config);
                } catch (FileNotFoundException e) {
                    throw new Error("Can't configure processor " + e.getMessage(), e);
                }
            }
            return unmarshal(configStream, castorMapingResource);
        } finally {
            IOUtils.closeQuietly(configStream);
        }
    }
    
    public static Object unmarshalConfigFile(String fileName, String castorMapingResource) {
    	InputStream configStream = null;
    	try {
    		configStream = loadConfig(fileName);
    		if (configStream == null) {
    			throw new Error("resource or file not found " + fileName);
    		}
    		return unmarshal(configStream, castorMapingResource);
        } finally {
            IOUtils.closeQuietly(configStream);
        }
    }

    public static Object unmarshalConfigFile(URL location, String castorMapingResource) {
    	InputStream configStream = null;
    	try {
    		try {
    			configStream = location.openStream();
            } catch (IOException e) {
            	throw new Error("resource not found " + location);
    		} 
    		return unmarshal(configStream, castorMapingResource);
		} finally {
            IOUtils.closeQuietly(configStream);
        }
    }
    
    public static InputStream loadConfig(String fileName) {
        URL location = FileUtil.getFile(fileName);
        if (location != null) {
            try {
                FileUtil.log.info("Using config file " + location);
                return location.openStream(); 
            } catch (Exception e) {
                FileUtil.log.error("Error reading " + fileName, e);
                return null;
            }
        } else {
            FileUtil.log.error("Config file not found: " + fileName);
            return null;
        }
    }
}
