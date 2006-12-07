package net.sf.jour.maven;

import java.io.File;
import java.util.List;

import net.sf.jour.PreProcessor;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

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
	 * @readonly
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
	 * Output directory name or the name of the output JAR file. 
	 * 
	 * @parameter expression="iclasses"
	 * @required
	 */
	protected String output;

	public void execute() throws MojoExecutionException, MojoFailureException {
		log = getLog();
		MavenLogAppender.startPluginLog(this);
		try {

			log.info(jourConfig.getAbsolutePath());

			File out = new File(outputDirectory, output);

			List classpath = null;

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
}
