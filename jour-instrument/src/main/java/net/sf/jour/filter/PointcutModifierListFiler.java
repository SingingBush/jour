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
 * TODO Add docs
 * 
 * Created on 07.12.2004 Contributing Author(s):
 * 
 * Misha Lifschitz <mishalifschitz at users.sourceforge.net> (Inital
 * implementation) Vlad Skarzhevskyy <vlads at users.sourceforge.net> (Inital
 * implementation)
 * 
 * @author vlads
 * @version $Revision$ ($Author$) $Date: 2006-11-19 16:52:09 -0500
 *          (Sun, 19 Nov 2006) $
 */
public class PointcutModifierListFiler extends MatchListFilter {

	public PointcutModifierListFiler() {

	}

	private PointcutModifierListFiler(String andPattern) {
		this.matchAllInList = true;
		setAndPattern(andPattern);
		this.name = " & ";
	}

	public boolean addPatterns(String pattern) {
		if (pattern == null) {
			return false;
		}
		pattern = pattern.trim();
		if (pattern.length() == 0) {
			initialized();
			return false;
		}
		StringTokenizer tokenizer = new StringTokenizer(pattern, ";");
		boolean hasTokens = false;
		while (tokenizer.hasMoreTokens()) {
			String str = tokenizer.nextToken().trim();
			PointcutModifierListFiler pm = new PointcutModifierListFiler(str);
			if (pm.size() == 1) {
				merge(pm);
				hasTokens = true;
			} else {
				hasTokens = addInclude(new PointcutModifierListFiler(str)) || hasTokens;
			}
		}
		initialized();
		return hasTokens;
	}

	private boolean setAndPattern(String pattern) {
		if (pattern == null) {
			return false;
		}
		pattern = pattern.trim();
		if (pattern.length() == 0) {
			return false;
		}
		StringTokenizer tokenizer = new StringTokenizer(pattern, ",");
		boolean hasTokens = false;
		while (tokenizer.hasMoreTokens()) {
			String str = tokenizer.nextToken().trim();
			if (str.startsWith("!")) {
				str = str.substring(1);
				hasTokens = addExclude(new PointcutModifierFiler(str)) || hasTokens;
			} else {
				hasTokens = addInclude(new PointcutModifierFiler(str)) || hasTokens;
			}
		}
		return hasTokens;
	}

	public void initialized() {
		if (super.isEmptyInclude()) {
			addInclude(new MatchAnyFilter());
		}
	}

	public boolean match(int mod) {
		return isMatch(matchState(new Integer(mod)));
	}
}
