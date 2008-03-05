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

	public static void main(String[] args) {
		Properties argsp = CmdArgs.load(args);
		if ((args.length < 2) || argsp.getProperty("help") != null) {
			StringBuffer usage = new StringBuffer();
			usage.append("Usage:\n java ").append(SignatureExport.class.getName());
			usage.append("--signature api-signature.xml --dst classesDir");

			usage.append("\t (--packages org.api2;org.api2)\n");
			usage.append("\t (--systempath)\n");
			usage.append("\t (--jars jar1.jar;jar2.jar)\n");
			usage.append("\t (--level public|[protected]|package|private)\n");
			usage.append("\t (--classVersion 1.3|1.4|1.5)\n");
			usage.append("\t (--stubException <ExceptionClassName>)\n");
			usage.append("\t (--stubExceptionMessage <ExceptionMessage>)\n");

			System.out.println(usage);
			return;
		}

		SignatureImport im = new SignatureImport("true".equals(argsp.getProperty("systempath")), argsp
				.getProperty("jars"));

		im.setStubException(argsp.getProperty("stubException"));
		im.setStubExceptionMessage(argsp.getProperty("stubExceptionMessage"));

		APIFilter apiFilter = new APIFilter(argsp.getProperty("level", "protected"), argsp.getProperty("packages"));

		im.load(argsp.getProperty("signature"), apiFilter);

		ExportClasses.export(argsp.getProperty("dst"), im.getClasses(), argsp.getProperty("classVersion"));

		System.out.println("Exported " + im.getClasses().size() + " classes");

	}
}
