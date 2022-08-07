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
package net.sf.jour.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;


/**
 * @author michaellif
 */
public abstract class BasicFilter extends MatchFilter {

	private static final Logger log = LoggerFactory.getLogger(BasicFilter.class);

	public static Pattern getGlobPattern(String globPattern) {
		char[] gPat = globPattern.toCharArray();
		char[] rPat = new char[gPat.length * 2 + 2];
		boolean inBrackets = false;

		int j = 0;

		// Should match full string
		// "foo" should not match "1foo".
		rPat[j++] = '^';

		for (int i = 0; i < gPat.length; i++) {
			switch (gPat[i]) {
			case '*':

				if (!inBrackets) {
					rPat[j++] = '.';
				}

				rPat[j++] = '*';

				break;

			case '?':
				rPat[j++] = inBrackets ? '?' : '.';

				break;

			case '[':
				inBrackets = true;
				rPat[j++] = gPat[i];

				if (i < (gPat.length - 1)) {
					switch (gPat[i + 1]) {
					case '!':
					case '^':
						rPat[j++] = '^';
						i++;

						break;

					case ']':
						// vlads: [] should be escaped for arrays
						rPat[j - 1] = '\\';
						rPat[j++] = '[';
						rPat[j++] = '\\';

						rPat[j++] = gPat[++i];

						break;
					}
				}

				break;

			case ']':
				rPat[j++] = gPat[i];
				inBrackets = false;

				break;

			case '\\':
				rPat[j++] = '\\';

				if ((i < (gPat.length - 1)) && ("*?[]".indexOf(gPat[i + 1]) >= 0)) {
					rPat[j++] = gPat[++i];
				} else {
					rPat[j++] = '\\';
				}

				break;

			default:

				if (!Character.isLetterOrDigit(gPat[i])) {
					rPat[j++] = '\\';
				}

				rPat[j++] = gPat[i];

				break;
			}
		}

		// Should match full string
		// "foo" should not match "foo2".
		rPat[j++] = '$';

		// System.out.println(new String(rPat, 0, j));

		return Pattern.compile(new String(rPat, 0, j),  Pattern.CASE_INSENSITIVE);
	}

	protected static boolean accept(Pattern  pattern, String str) {
		if (str == null) {
			return false;
		}
		return pattern.matcher(str).matches();
	}

}
