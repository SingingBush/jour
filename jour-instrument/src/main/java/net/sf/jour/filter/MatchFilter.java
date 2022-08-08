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
public abstract class MatchFilter implements Filter {

	public final static int MATCH_EXCLUDE = -2;

	public final static int MATCH_NO = -1;

	// when filte disable the result would depen on oth items in the list.
	public final static int MATCH_DONT_KNOW = 0;

	public final static int MATCH_YES = 1;

	public final static int MATCH_EXACT = 2;

	/**
     * conditional compilation set to false to allow compiler to identify and
     * eliminate unreachable code
     */
    public static final boolean debug = false;

	/**
     * a name use for debuging complex lists.
     */
	public String name = "n/a";

	public abstract int matchState(Object obj);

	public int notMatch(int state) {
		if (state >= MATCH_YES) {
			return MATCH_EXCLUDE;
		} else {
			return MATCH_YES;
		}
	}

	public int b2Match(boolean isMatch) {
		if (isMatch) {
			return MATCH_YES;
		} else {
			return MATCH_NO;
		}
	}

	public boolean isMatch(int state) {
		if (state >= MATCH_YES) {
			return true;
		} else {
			return false;
		}
	}

	public abstract void debug();

	public String match2String(int state) {
		switch (state) {
		case MATCH_EXCLUDE:
			return "MATCH_EXCLUDE";
		case MATCH_NO:
			return "MATCH_NO";
		case MATCH_DONT_KNOW:
			return "MATCH_DONT_KNOW";
		case MATCH_YES:
			return "MATCH_YES";
		case MATCH_EXACT:
			return "MATCH_EXACT";
		default:
			return "MATCH_??" + state;

		}
	}
}
