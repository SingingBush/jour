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
import java.util.Iterator;
import java.util.List;

import net.sf.jour.signature.APICompare;
import net.sf.jour.signature.APICompareConfig;
import net.sf.jour.signature.ChangeDetectedException;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import com.pyx4j.log4j.MavenLogAppender;

/**
 * The jour:signatureVerify will compare API descriptor and API classes.
 * 
 * @author vlads
 * 
 * @goal signatureVerify
 * 
 * @phase test
 * 
 * @requiresDependencyResolution compile
 * 
 * @description Verification of compatibility between Java APIs
 */
public class SignatureVerifyMojo extends AbstractMojo {

	/**
	 * The API descriptor XML.
	 * 
	 * @parameter
	 * @required
	 */
	private File signature;

	/**
	 * The directory or jar containing API classes.
	 * 
	 * @parameter expression="${project.build.outputDirectory}"
	 * @required
	 */
	private File classes;

	/**
	 * Appends the system search path to the end of the search path. The system
	 * search path usually includes the platform library, extension libraries,
	 * and the search path specified by the <code>-classpath</code> option or
	 * the <code>CLASSPATH</code> environment variable.
	 * 
	 * @parameter expression="true"
	 */
	private boolean useSystemClassPath;

	/**
	 * Generate error if new API member with access level public or protected
	 * has been added to class.
	 * 
	 * @parameter expression="true"
	 */
	private boolean allowAPIextension;

	/**
	 * Generate error if new API member throw less exception than declared in
	 * API
	 * 
	 * @parameter expression="true"
	 */
	private boolean allowThrowsLess;

	/**
	 * Generate error if new API member with access level package has been added
	 * to class. To include package level members use SignatureGenerator --level
	 * package
	 * 
	 * @parameter expression="false"
	 */
	private boolean allowPackageAPIextension;

	/**
	 * The Maven project reference where the plugin is currently being executed.
	 * Used for dependency resolution during compilation. The default value is
	 * populated from maven.
	 * 
	 * @parameter expression="${project}"
	 * @readonly
	 * @required
	 */
	protected MavenProject mavenProject;

	public void execute() throws MojoExecutionException, MojoFailureException {
		Log log = getLog();
		MavenLogAppender.startPluginLog(this);
		log.info("use signature: " + signature);

		APICompareConfig config = new APICompareConfig();

		config.allowAPIextension = allowAPIextension;
		config.allowThrowsLess = allowThrowsLess;
		config.allowPackageAPIextension = allowPackageAPIextension;

		StringBuffer supportingJars = new StringBuffer();

		List dependancy = this.mavenProject.getTestArtifacts();
		for (Iterator i = dependancy.iterator(); i.hasNext();) {
			Artifact artifact = (Artifact) i.next();
			File file = InstrumentationMojo.getClasspathElement(artifact, mavenProject);
			log.debug("dependancy:" + file.toString());
			if (supportingJars.length() < 0) {
				supportingJars.append(File.pathSeparatorChar);
			}
			supportingJars.append(file.toString());
		}

		try {
			APICompare.compare(classes.getAbsolutePath(), signature.getAbsolutePath(), config, useSystemClassPath,
					supportingJars.toString());
		} catch (ChangeDetectedException e) {
			log.error("API compare FAILED, processed " + APICompare.getClassesCount() + " classes");
			log.error(e.getMessage());
			throw new MojoExecutionException("API compare FAILED", e);
		}
		log.info("API compare PASSED in " + APICompare.getClassesCount() + " classes");
	}

}
