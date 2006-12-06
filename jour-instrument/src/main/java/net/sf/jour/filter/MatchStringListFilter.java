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

import java.util.StringTokenizer;

/**
 * TODO Add docs Created on 04.12.2004
 * 
 * Contributing Author(s):
 * 
 * Misha Lifschitz <mishalifschitz at users.sourceforge.net> (Inital
 * implementation) Vlad Skarzhevskyy <vlads at users.sourceforge.net> (Inital
 * implementation)
 * 
 * @author vlads
 * @version $Revision$ ($Author$) $Date: 2006-11-19 16:52:09 -0500
 *          (Sun, 19 Nov 2006) $
 */
public class MatchStringListFilter extends MatchListFilter {

	public MatchStringListFilter() {
	}

	public MatchStringListFilter(String pattern) {
		addPatterns(pattern);
	}

	public boolean match(String text) {
		return isMatch(matchState(text));
	}

	public boolean addPatterns(String pattern) {
		if (pattern == null) {
			return false;
		}
		pattern = pattern.trim();
		if (pattern.length() == 0) {
			return false;
		}
		StringTokenizer tokenizer = new StringTokenizer(pattern, ";");
		boolean hasTokens = false;
		while (tokenizer.hasMoreTokens()) {
			String str = tokenizer.nextToken().trim();
			if (str.startsWith("!")) {
				str = str.substring(1);
				hasTokens = addExclude(str, false) || hasTokens;
			} else {
				hasTokens = addInclude(str, false) || hasTokens;
			}
		}
		return hasTokens;
	}

	public MatchFilter newStringPattern(String pattern, boolean isList) {
		if ((pattern == null) || (pattern.length() == 0)) {
			return null;
		}
		if (isList) {
			MatchStringListFilter l = new MatchStringListFilter();
			if (l.addPatterns(pattern)) {
				return l;
			} else {
				return null;
			}
		} else {
			return new MatchStringFilter(pattern);
		}
	}

	public boolean addInclude(String pattern, boolean isList) {
		return addInclude(newStringPattern(pattern, isList));
	}

	public boolean addExclude(String pattern, boolean isList) {
		return addExclude(newStringPattern(pattern, isList));
	}

	// public boolean readContext(Object o) {
	// if (o instanceof Include) {
	// Include inc = (Include) o;
	// if (inc.isEnabled()) {
	// addInclude(inc.getNames(), false);
	// addInclude(inc.getValue(), true);
	// }
	// return true;
	// } else if (o instanceof Exclude) {
	// Exclude exc = (Exclude) o;
	// if (exc.isEnabled()) {
	// addExclude(exc.getNames(), false);
	// addExclude(exc.getValue(), true);
	// }
	// return true;
	// } else if (o instanceof String) {
	// addInclude((String) o, true);
	// return true;
	// } else {
	// return super.readContext(o);
	// }
	// }
}
