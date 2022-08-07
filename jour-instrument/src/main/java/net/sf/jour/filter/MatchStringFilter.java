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
public class MatchStringFilter extends BasicFilter {

	private static final Logger log = LoggerFactory.getLogger(MatchStringFilter.class);

	String pattern;

	private Pattern rePattern;

	boolean negative;

	private static final boolean useRegexp = true;

	protected MatchStringFilter() {
	}

	public MatchStringFilter(String pattern) {
		setPattern(pattern);
	}

	public void debug() {
		if (negative) {
			log.debug("pattern:!" + pattern);
		} else {
			log.debug("pattern:" + pattern);
		}
	}

	public boolean setPattern(String pattern) {
		if ((pattern == null) || (pattern.length() == 0)) {
			return false;
		}
		if (pattern.startsWith("!")) {
			this.pattern = pattern.substring(1);
			negative = true;
		} else {
			this.pattern = pattern;
			negative = false;
		}
		if (useRegexp) {
			this.rePattern = getGlobPattern(this.pattern);
		}
		return true;
	}

	public int matchState(Object obj) {
		int rc;
		if (this.pattern == null) {
			return MATCH_NO;
		}
		if (useRegexp) {
			if (BasicFilter.accept(this.rePattern, (String) obj)) {
				rc = MATCH_YES;
			} else {
				rc = MATCH_NO;
			}
		} else {
			rc = matchSimple(this.pattern, (String) obj);
		}
		if (negative) {
			rc = notMatch(rc);
		}

		if (debug) {
			String s = "";
			if (negative) {
				s = "NOT ";
			}
			log.debug(s + this.pattern + "->" + match2String(rc) + " testing[" + obj + "]");
		}

		return rc;
	}

	public boolean match(String text) {
		return isMatch(matchState(text));
	}

	public static int matchSimple(String pattern, String text) {
		if (text == null) {
			return MATCH_NO;
		}
		// add sentinel so don't need to worry about *'s at end of pattern
		text += '\0';
		pattern += '\0';

		int N = pattern.length();

		boolean[] states = new boolean[N + 1];
		boolean[] old = new boolean[N + 1];
		old[0] = true;

		boolean wildcard = false;

		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			states = new boolean[N + 1]; // initialized to false
			for (int j = 0; j < N; j++) {
				char p = pattern.charAt(j);

				// hack to handle *'s that match 0 characters
				if (old[j] && (p == '*')) {
					old[j + 1] = true;
					wildcard = true;
				}

				if (old[j] && (p == c))
					states[j + 1] = true;

				if (old[j] && (p == '?')) {
					states[j + 1] = true;
					wildcard = true;
				}

				if (old[j] && (p == '*')) {
					states[j] = true;
					wildcard = true;
				}

				if (old[j] && (p == '*')) {
					states[j + 1] = true;
					wildcard = true;
				}
			}
			old = states;
		}

		if (!states[N]) {
			return MATCH_NO;
		} else if (wildcard) {
			return MATCH_YES;
		} else {
			return MATCH_EXACT;
		}
	}

}
