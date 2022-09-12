/*
 * Jour - bytecode instrumentation library
 *
 * Copyright (C) 2008 Vlad Skarzhevskyy
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
 *
 * @version $Id$
 *
 */
package net.sf.jour;

import java.util.Properties;

import net.sf.jour.signature.APIFilter;
import net.sf.jour.signature.ExportClasses;
import net.sf.jour.signature.SignatureImport;
import net.sf.jour.util.CmdArgs;

/**
 * @author vlads
 *
 */
public class SignatureExport {

    private final String destinationDir;

    private final String classVersion;

    private final SignatureImport signatureImport;

	public static void main(String[] args) {
		final Properties argsp = CmdArgs.load(args);

		if ((args.length < 2) || argsp.getProperty("help") != null) {
			final StringBuffer usage = new StringBuffer();
			usage.append("Usage:\n java ").append(SignatureExport.class.getName());
			usage.append("--signature api-signature.xml --dst classesDir");

			usage.append("\t (--packages org.api2;org.api2)\n");
			usage.append("\t (--systempath)\n");
			usage.append("\t (--jars jar1.jar;jar2.jar)\n");
			usage.append("\t (--level public|[protected]|package|private)\n");
			usage.append("\t (--classVersion 1.3|1.4|1.5|1.6|1.7|1.8|9|10|11|12|13|14|15|16|17|18|19|20)\n");
			usage.append("\t (--stubException <ExceptionClassName>)\n");
			usage.append("\t (--stubExceptionMessage <ExceptionMessage>)\n");

			System.out.println(usage);
			return;
		}

        final SignatureExport signatureExport = new SignatureExport(argsp);
        signatureExport.export();
    }

    /**
     *
     * @param argsp properties matching the args of the main function
     * @since 2.1.1
     */
    public SignatureExport(final Properties argsp) {
        this("true".equalsIgnoreCase(argsp.getProperty("systempath", "false")),
            argsp.getProperty("jars"),
            argsp.getProperty("stubException"),
            argsp.getProperty("stubExceptionMessage"),
            argsp.getProperty("level", "protected"),
            argsp.getProperty("packages"),
            argsp.getProperty("signature"),
            argsp.getProperty("dst"),
            argsp.getProperty("classVersion")
        );
    }

    /**
     *
     * @param useSystemClassPath boolean of the systempath arg
     * @param jars semicolon delimited collection of jar files
     * @param stubException the ExceptionClassName
     * @param stubExceptionMessage the ExceptionMessage
     * @param level one of public|protected|package|private. The default should be protected
     * @param packages semicolon delimited collection of package names
     * @param signature the xml files to process.eg: api-signature.xml
     * @param destinationDir the directory path to output the files to. Must not end with a separator
     * @param classVersion the desired JDK version. eg: 1.8
     * @since 2.1.1
     */
    public SignatureExport(final boolean useSystemClassPath,
                           final String jars, // semicolon delimited collection of strings
                           final String stubException,
                           final String stubExceptionMessage,
                           final String level,
                           final String packages,
                           final String signature,
                           final String destinationDir,
                           final String classVersion
    ) {
        this.destinationDir = destinationDir;
        this.classVersion = classVersion;

        this.signatureImport = new SignatureImport(useSystemClassPath, jars);
        signatureImport.setStubException(stubException);
        signatureImport.setStubExceptionMessage(stubExceptionMessage);

        final APIFilter apiFilter = new APIFilter(level, packages);
        signatureImport.load(signature, apiFilter);
    }

    private void export() {
        int count = ExportClasses.export(destinationDir, signatureImport.getClasses(), classVersion);

        System.out.println("Exported " + count + " of " + signatureImport.getClasses().size() + " classes");
    }
}
