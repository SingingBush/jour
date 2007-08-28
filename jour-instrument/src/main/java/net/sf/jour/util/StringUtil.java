/*
 * Jour - java profiler and monitoring library
 *
 * Copyright (C) 2004 Jour team
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

import java.text.DecimalFormat;
import java.util.StringTokenizer;

/**
 *
 * Created on 04.12.2004
 *
 * Contributing Author(s):
 *
 *   Misha Lifschitz <mishalifschitz at users.sourceforge.net> (Inital implementation)
 *   Vlad Skarzhevskyy <vlads at users.sourceforge.net> (Inital implementation)
 *
 * @author vlads
 * @version $Revision$ ($Author$) $Date$
 */
public class StringUtil {
	/**
	 * jre 1.3 compatible replaceAll.
	 * @param str text to search and replace in
	 * @param replace the String to search for
	 * @param replacement the String to replace with
	 * @return the text with any replacements processed
	 */
	public static String replaceAll(String str, String replace, String replacement)
	{
		StringBuffer sb = new StringBuffer(str);
		int firstOccurrence = str.indexOf(replace);

		while (firstOccurrence != -1)
		{
			sb.replace(firstOccurrence, firstOccurrence + replace.length(), replacement);
			firstOccurrence = sb.toString().indexOf(replace);
		}

		return sb.toString();
	}

    public String[] stringToArray(String str, String delim) {
        StringTokenizer paramStrokenizer = new StringTokenizer(str, delim);
    
        String[] retVal = new String[paramStrokenizer.countTokens()];
        int i = 0;
    
        while (paramStrokenizer.hasMoreTokens()) {
            retVal[i++] = paramStrokenizer.nextToken();
        }
    
        return retVal;
    }
    
    public static String percent(double duration, double durationTotal) {
		if (durationTotal == 0.0) {
			return "n/a";
		}
		final DecimalFormat format = new DecimalFormat("##0");
		format.setMinimumIntegerDigits(1);
		format.setMaximumFractionDigits(0);
				
		int prc = (new Double(Math.floor(100 * duration/durationTotal))).intValue();
		return format.format(prc);
	}
}
