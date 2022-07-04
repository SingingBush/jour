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
import net.sf.jour.signature.APIFilter;
import net.sf.jour.signature.ChangeDetectedException;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import com.pyx4j.log4j.MavenLogAppender;

/*
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
@Mojo(name = "signatureVerify",
		defaultPhase = LifecyclePhase.TEST,
		requiresDependencyResolution = ResolutionScope.COMPILE)
public class SignatureVerifyMojo extends AbstractMojo {

	/**
	 * The API descriptor XML.
	 */
	@Parameter(name = "signature", readonly = true)
	private File signature;

	/**
	 * The directory or jar containing API classes.
	 */
	@Parameter(name = "classes", defaultValue = "${project.build.outputDirectory}", readonly = true)
	private File classes;

	/**
	 * Appends the system search path to the end of the search path. The system
	 * search path usually includes the platform library, extension libraries,
	 * and the search path specified by the <code>-classpath</code> option or
	 * the <code>CLASSPATH</code> environment variable.
	 */
	@Parameter(name = "useSystemClassPath", property = "true", defaultValue = "true")
	private boolean useSystemClassPath;

	/**
	 * Generate error if new API member with access level public or protected
	 * has been added to class.
	 */
	@Parameter(name = "allowAPIextension", property = "true", defaultValue = "true")
	private boolean allowAPIextension;

	/**
	 * Generate error if new API member throw less exception than declared in API
	 */
	@Parameter(name = "allowThrowsLess", property = "true", defaultValue = "true")
	private boolean allowThrowsLess;

	/**
	 * Compare API level public|[protected]|package|private
	 */
	@Parameter(name = "level", property = "protected", defaultValue = "protected")
	private String level;

	/**
	 * Compare Only selected packages
	 *
	 * @parameter
	 */
	@Parameter
	private String packages;

	/**
	 * The Maven project reference where the plugin is currently being executed.
	 * Used for dependency resolution during compilation. The default value is
	 * populated from maven.
	 */
	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	protected MavenProject mavenProject;

	public void execute() throws MojoExecutionException, MojoFailureException {
		final Log log = getLog();

		MavenLogAppender.startPluginLog(this);
		log.info("use signature: " + signature);

		APICompareConfig config = new APICompareConfig();

		config.allowAPIextension = allowAPIextension;
		config.allowThrowsLess = allowThrowsLess;

		config.packages = packages;
		config.apiLevel = APIFilter.getAPILevel(level);

		final StringBuffer supportingJars = new StringBuffer();

		final List<Artifact> dependencies = this.mavenProject.getTestArtifacts();

        for (final Artifact artifact : dependencies) {
            final File file = InstrumentationMojo.getClasspathElement(artifact, mavenProject);

            log.debug("dependency:" + file.toString());

            if (supportingJars.length() < 0) {
                supportingJars.append(File.pathSeparatorChar);
            }
            supportingJars.append(file.toString());
        }

		try {
			APICompare.compare(classes.getAbsolutePath(), signature.getAbsolutePath(), config, useSystemClassPath,
					(supportingJars.length() > 0) ? supportingJars.toString() : null);
		} catch (ChangeDetectedException e) {
			log.error("API compare FAILED, processed " + APICompare.getClassesCount() + " classes");
			log.error(e.getMessage());
			throw new MojoExecutionException("API compare FAILED", e);
		}
		log.info("API compare PASSED in " + APICompare.getClassesCount() + " classes");
	}

}
