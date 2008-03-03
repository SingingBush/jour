/*
 * Jour - bytecode instrumentation library
 *
 * Copyright (C) 2007-2008 Vlad Skarzhevskyy
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
package net.sf.jour.maven;

import java.io.File;

import net.sf.jour.signature.ExportClasses;
import net.sf.jour.signature.SignatureImport;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import com.pyx4j.log4j.MavenLogAppender;

/**
 * The jour:generate will create API stub classes.
 * 
 * @author vlads
 * 
 * @goal generate
 * 
 * @phase compile
 * 
 * @description Export API descriptor XML to classes
 */
public class GenerateMojo extends AbstractMojo {

	/**
	 * The directory containing project classes.
	 * 
	 * @parameter expression="${project.build.outputDirectory}"
	 * @required
	 */
	private File output;

	/**
	 * The API descriptor XML.
	 * 
	 * @parameter
	 * @required
	 */
	private File signature;

	/**
	 * Appends the system search path to the end of the search path. The system
	 * search path usually includes the platform library, extension libraries,
	 * and the search path specified by the <code>-classpath</code> option or
	 * the <code>CLASSPATH</code> environment variable.
	 * 
	 * @parameter expression="false"
	 */
	private boolean useSystemClassPath;

	/**
	 * Java platform version for created classes. e.g. 1.1, 1.3, 1.4, 1.5 or 1.6
	 * 
	 * @parameter
	 */
	private String classVersion;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		Log log = getLog();
		MavenLogAppender.startPluginLog(this);
		log.info("use signature: " + signature);

		SignatureImport im = new SignatureImport(useSystemClassPath, null);

		im.load(signature.getAbsolutePath());

		ExportClasses.export(output.getAbsolutePath(), im.getClasses(), classVersion);
	}

}
