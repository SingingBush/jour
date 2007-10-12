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
import net.sf.jour.signature.APICompare;
import net.sf.jour.signature.APICompareConfig;
import net.sf.jour.signature.ChangeDetectedException;
import net.sf.jour.util.CmdArgs;

/**
 * @author vlads
 *
 */
public class SignatureVerify {
	
	public static void main(String[] args) throws IOException, NotFoundException {
		Properties argsp = CmdArgs.load(args);
		if ((args.length < 2) || argsp.getProperty("help") != null) {
			StringBuffer usage = new StringBuffer();
			usage.append("Usage:\n java ").append(SignatureVerify.class.getName());
			usage.append(" --src classesDir|classes.jar --signature api-signature.xml (--systempath) (--jars jar1.jar;jar2.jar)\n");
			System.out.println(usage);
			return;
		}
		
		APICompareConfig config = new APICompareConfig();
		
		try {
            APICompare.compare(argsp.getProperty("src"), argsp.getProperty("signature"), config, "true".equals(argsp.getProperty("systempath")), argsp.getProperty("jars"));
        } catch (ChangeDetectedException e) {
            System.out.println("API compare FAILED");
            System.out.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("API compare PASSED");
        System.exit(0);
		
	}
}
