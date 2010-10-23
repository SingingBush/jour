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

import net.sf.jour.signature.APIFilter;
import net.sf.jour.signature.ExportClasses;
import net.sf.jour.signature.SignatureImport;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import com.pyx4j.log4j.MavenLogAppender;

/**
 * The jour:generate will create API stub classes.
 * 
 * @author vlads
 * 
 * @goal generate
 * @phase compile
 * @requiresDependencyResolution test
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

	/**
	 * Export API level public|[protected]|package|private
	 * 
	 * @parameter expression="protected"
	 */
	private String level;

	/**
	 * Export Only selected packages
	 * 
	 * @parameter
	 */
	private String packages;

	/**
	 * API stub empty method/constructor body code may just throw Exception
	 * class name can be selected.
	 * 
	 * @parameter
	 */
	private String stubException;

	/**
	 * Exception class constructor String argument
	 * 
	 * @parameter
	 */
	private String stubExceptionMessage;

	/**
	 * Dependency artifacts scope: "compile", test", "runtime" or "system";
	 * 
	 * @parameter expression="compile" 
	 */
	private String scope;
	
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		Log log = getLog();
		MavenLogAppender.startPluginLog(this);
		log.info("use signature: " + signature);
		log.debug("packages: " + packages + " level:" + level);

		StringBuffer supportingJars = new StringBuffer();

		List dependancy;
		if (Artifact.SCOPE_COMPILE.equals(scope)) {
		    dependancy = this.mavenProject.getCompileArtifacts();
		} else if (Artifact.SCOPE_TEST.equals(scope)) {
		    dependancy = this.mavenProject.getTestArtifacts();
		} else if (Artifact.SCOPE_RUNTIME.equals(scope)) {
            dependancy = this.mavenProject.getRuntimeArtifacts();
		} else if (Artifact.SCOPE_SYSTEM.equals(scope)) {
            dependancy = this.mavenProject.getSystemArtifacts();
		} else {
            throw new MojoExecutionException("Unsupported scope " + scope);
        }
		for (Iterator i = dependancy.iterator(); i.hasNext();) {
			Artifact artifact = (Artifact) i.next();
			File file = InstrumentationMojo.getClasspathElement(artifact, mavenProject);
			log.debug("dependancy:" + file.toString());
			if (supportingJars.length() > 0) {
				supportingJars.append(File.pathSeparatorChar);
			}
			supportingJars.append(file.toString());
		}

		SignatureImport im = new SignatureImport(useSystemClassPath, (supportingJars.length() > 0) ? supportingJars.toString() : null);

		im.setStubException(stubException);
		im.setStubExceptionMessage(stubExceptionMessage);

		APIFilter apiFilter = new APIFilter(level, packages);

		im.load(signature.getAbsolutePath(), apiFilter);

		log.debug("loaded " + im.getClassNames().size() + " classe(s)");

		log.debug("output: " + output.getAbsolutePath());

		int count = ExportClasses.export(output.getAbsolutePath(), im.getClasses(), classVersion);
		log.info("Created " + count + " classe(s)");
	}

}
