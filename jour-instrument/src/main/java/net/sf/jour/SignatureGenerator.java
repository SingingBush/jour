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
package net.sf.jour;

import java.io.IOException;
import java.util.Properties;

import javassist.NotFoundException;
import net.sf.jour.signature.Generator;
import net.sf.jour.util.CmdArgs;

/**
 * @author vlads
 * 
 */
public class SignatureGenerator {

	public static void main(String[] args) throws IOException, NotFoundException {
		Properties argsp = CmdArgs.load(args);
		if ((args.length < 1) || argsp.getProperty("help") != null) {
			StringBuffer usage = new StringBuffer();
			usage.append("Usage:\n java ").append(SignatureGenerator.class.getName());
			usage
					.append(" --src classesDir|classes.jar (--systempath) (--jars jar1.jar;jar2.jar) (--dst api-signature.xml)\n");
			usage.append("  (--packages com.api;com.ext) (--level public|protected*|package|private)\n");
			System.out.println(usage);
			return;
		}
		Generator g = new Generator(argsp);
		g.process();
		System.out.println("Signature file " + g.getReportFile() + " created for " + g.getClassNames().size()
				+ " classe(s)");
	}

}
