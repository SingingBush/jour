/*
 * Jour - java profiler and monitoring library
 *
 * Copyright (C) 2004-2007 Jour team
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
package net.sf.jour.util;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author michaellif
 *
 */
public class RegExUtil {

    public static String[] match(String input, String regExp) {
		final Vector<String> resultList = new Vector<>();

		final Pattern pattern = Pattern.compile(regExp);
		final Matcher matcher = pattern.matcher(input);

		if (matcher.matches()) {
			int groupCount = matcher.groupCount();
			for (int i = 1; i <= groupCount; i++) {
				resultList.add(matcher.group(i));
			}
		}

		return resultList.toArray(new String[0]);
	}

//	public static String[] match(String input, String regExp) {
//		Vector resultList = new Vector();
//		org.apache.regexp.RE regex = new org.apache.regexp.RE(regExp);
//
//		if (regex.match(input)) {
//			for (int i = 1; i < regex.getParenCount(); ++i) {
//				String s = regex.getParen(i);
//				resultList.add(s);
//			}
//		}
//		return (String[]) resultList.toArray(new String[0]);
//	}

    public static String getParen(String input, String regExp) {
    	Pattern pattern = Pattern.compile(regExp);
		Matcher matcher = pattern.matcher(input);
		if (matcher.find()) {
			return matcher.group(0);
		} else {
			return null;
		}
    }

//    public static String getParen(String input, String regExp) {
//    	org.apache.regexp.RE regex = new org.apache.regexp.RE(regExp);
//		if (regex.match(input)) {
//			return regex.getParen(0);
//		}
//		return null;
//    }
}
