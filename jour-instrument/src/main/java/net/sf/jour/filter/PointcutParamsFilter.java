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

/**
 * TODO Add docs
 *
 * Created on 05.12.2004 Contributing Author(s):
 *
 * Misha Lifschitz <mishalifschitz at users.sourceforge.net> (Inital
 * implementation) Vlad Skarzhevskyy <vlads at users.sourceforge.net> (Inital
 * implementation)
 *
 * @author vlads
 * @version $Revision$ ($Author$) $Date: 2006-11-19 16:52:09 -0500
 *          (Sun, 19 Nov 2006) $
 */
public class PointcutParamsFilter extends MatchStringListFilter {

	private static final Logger log = LoggerFactory.getLogger(PointcutParamsFilter.class);

	boolean matchAny = false;

	PointcutParamsFilter nextParam = null;

	public PointcutParamsFilter() {

	}

	public PointcutParamsFilter(String params) {
		setParams(params);
	}

	public void debug() {
		log.debug("matchAny " + matchAny);
		log.debug("has nextParam " + (nextParam != null));
		super.debug();
	}

	public void setParams(String params) {
		params = params.trim();
		if (params.equals("..")) {
			this.matchAny = true;
			return;
		}
		int idx = params.indexOf(",");
		if (idx == -1) {
			super.addPatterns(params);
			return;
		}

		String firstParam = params.substring(0, idx);
		// (int,..) should work now.
		// TODO add params like this: (..,int)

		super.addPatterns(firstParam);
		this.nextParam = new PointcutParamsFilter(params.substring(idx + 1));
	}

	public int matchState(Object obj) {

		if (debug) {
			debug();
		}

		if (obj instanceof String) {
			return super.matchState(obj);
		}
		if (!(obj instanceof String[])) {
			return MATCH_NO;
		}
		if (matchAny) {
			return MATCH_YES;
		}

		String[] params = (String[]) obj;

		if (params.length == 0) {
			if (isEmpty()) {
				return MATCH_YES;
			} else {
				return MATCH_NO;
			}
		}

		if (!super.match(params[0])) {
			return MATCH_NO;
		}

		if (this.nextParam == null) {
			return MATCH_YES;
		}

		String[] nextParams = new String[params.length - 1];
		for (int i = 1; i < params.length; i++) {
			nextParams[i - 1] = params[i];
		}

		return this.nextParam.matchState(nextParams);
	}

}
