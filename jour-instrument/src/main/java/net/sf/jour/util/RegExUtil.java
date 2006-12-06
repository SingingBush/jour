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

//import java.util.ArrayList;
import java.util.Vector;

//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//Java1.3
import org.apache.regexp.RE;

/**
 * @author michaellif
 *
 */
public class RegExUtil {

	/*
    public static String[][] match(String input, String regExp) {
        ArrayList resultList = new ArrayList();

        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            int groupCount = matcher.groupCount();
            String[] s = new String[groupCount + 1];
            for (int i = 0; i <= groupCount; i++) {
                s[i] = matcher.group(i);
            }
            resultList.add(s);
        }

        return (resultList.size() == 0) ? new String[][] {}
        : (String[][]) resultList.toArray(new String[0][0]);
    }*/

	public static String[] match(String input, String regExp) {
			Vector resultList = new Vector();
			RE regex = new RE(regExp);

		if (regex.match(input)) {
			for( int i=1; i < regex.getParenCount() ; ++i ) {
				String s = regex.getParen(i);
				resultList.add(s);
			}
		}
		return (String[])resultList.toArray(new String[0]);
	}

}
