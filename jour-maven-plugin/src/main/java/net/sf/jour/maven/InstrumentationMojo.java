package net.sf.jour.maven;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

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
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		log = getLog();
		
		log.info(jourConfig.getAbsolutePath());
	}
}
