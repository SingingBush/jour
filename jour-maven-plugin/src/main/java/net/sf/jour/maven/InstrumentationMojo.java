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
 */
package net.sf.jour.maven;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import net.sf.jour.PreProcessor;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import com.pyx4j.log4j.MavenLogAppender;

/**
 * The jour:instrument will apply instrumentation during build.
 * 
 * @author vlads
 * 
 * @goal instrument
 * 
 * @pahse process-classes
 * 
 * @requiresDependencyResolution test
 * 
 * @description Instrument
 */
public class InstrumentationMojo extends AbstractMojo {

	private Log log;

	/**
	 * Reads configuration options from the given file. File or resource name.
	 * 
	 * @parameter default-value="${basedir}/jour.xml"
	 */
	private String jourConfig;

	/**
	 * The directory or jar containing original classes.
	 * 
	 * @parameter expression="${project.build.outputDirectory}"
	 * @required
	 */
	private File classesDirectory;

	/**
	 * Directory containing the generated JAR.
	 * 
	 * @parameter expression="${project.build.directory}"
	 * @required
	 */
	protected File outputDirectory;

	/**
	 * Output directory name relative to outputDirectory parameter.
	 * 
	 * @parameter expression="iclasses"
	 * @required
	 */
	protected String output;

	/**
	 * Copy not instrumented classes to "output"
	 * 
	 * @parameter expression="false"
	 */
	private boolean copyClasses;

	/**
	 * Copy resources to "output"
	 * 
	 * @parameter expression="false"
	 */
	private boolean copyResources;

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
		log = getLog();
		MavenLogAppender.startPluginLog(this);
		try {

			log.info("jourConfig: " + jourConfig);

			File out = new File(outputDirectory, output);

			List classpath = new Vector();

			List dependancy = this.mavenProject.getTestArtifacts();
			for (Iterator i = dependancy.iterator(); i.hasNext();) {
				Artifact artifact = (Artifact) i.next();
				File file = getClasspathElement(artifact, mavenProject);
				log.debug("dependancy:" + file.toString());
				classpath.add(file.toString());
			}

			PreProcessor pp = new PreProcessor(jourConfig, classesDirectory, out, classpath);

			pp.setUseSystemClassPath(useSystemClassPath);
			pp.setCopyClasses(copyClasses);
			pp.setCopyResources(copyResources);

			try {
				pp.process();
			} catch (Exception e) {
				log.error("PreProcessing error", e);
				throw new MojoExecutionException("PreProcessing error", e);
			}

		} finally {
			MavenLogAppender.endPluginLog(this);
		}
	}

	public static File getClasspathElement(Artifact artifact, MavenProject mavenProject) throws MojoExecutionException {
		String refId = artifact.getGroupId() + ":" + artifact.getArtifactId();
		MavenProject project = (MavenProject) mavenProject.getProjectReferences().get(refId);
		if (project != null) {
			return new File(project.getBuild().getOutputDirectory());
		} else {
			File file = artifact.getFile();
			if ((file == null) || (!file.exists())) {
				throw new MojoExecutionException("Dependency Resolution Required " + artifact);
			}
			return file;
		}
	}
}
