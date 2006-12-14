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
	 * Reads configuration options from the given file.
	 * 
	 * @parameter default-value="${basedir}/jour.xml"
	 */
	private File jourConfig;

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
	 * Output directory name or the name of the output JAR file relative to outputDirectory parameter. 
	 * 
	 * @parameter expression="iclasses"
	 * @required
	 */
	protected String output;
	
	/**
	 * The Maven project reference where the plugin is currently being executed.
	 * Used for dependancy resolution during compilation.
	 * The default value is populated from maven.
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

			log.info(jourConfig.getAbsolutePath());

			File out = new File(outputDirectory, output);

			List classpath = new Vector();

			List dependancy = this.mavenProject.getTestArtifacts();
			for (Iterator i = dependancy.iterator(); i.hasNext();) {
				Artifact artifact = (Artifact) i.next();
				File file = getClasspathElement(artifact, mavenProject);
				log.debug("dependancy:" + file.toString());
				classpath.add(file.toString());
			}
			
			PreProcessor pp = new PreProcessor(jourConfig.getAbsolutePath(), classesDirectory, out, classpath);

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
        MavenProject project = (MavenProject) mavenProject.getProjectReferences().get( refId );
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
