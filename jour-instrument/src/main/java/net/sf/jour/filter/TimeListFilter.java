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

import java.util.StringTokenizer;

public class TimeListFilter extends MatchListFilter {

	protected static final Logger log = LoggerFactory.getLogger(TimeListFilter.class);

	public TimeListFilter() {
		emptyListMath = true;
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
			boolean exclude = false;
			if (str.startsWith("!")) {
				str = str.substring(1);
				exclude = true;
			}
			TimeMatchFilter tm = new TimeMatchFilter(str);
			if (!tm.isValid()) {
				log.warn("Invalid time interval [" + str + "]");
				continue;
			}
			if (exclude) {
				hasTokens = addExclude(tm) || hasTokens;
			} else {
				hasTokens = addInclude(tm) || hasTokens;
			}
		}
		return hasTokens;
	}

	// public void readConfig(List times) {
	// if (times == null) {
	// return;
	// }
	// for (Iterator i = times.iterator(); i.hasNext();) {
	// TimeType def = (TimeType) i.next();
	// if ((def.getValue() != null) && (def.getValue().indexOf(";") > 0)) {
	// addPatterns(def.getValue());
	// continue;
	// }
	// TimeMatchFilter tm = new TimeMatchFilter(def.getFrom(), def.getTo(),
    // def.getValue());
	// if (!tm.isValid()) {
	// log.warn("Invalid time [" + def.getValue() + "] or ["+ def.getFrom() + "
    // - " + def.getTo());
	// continue;
	// }
	// tm.setEnabled(def.isEnabled());
	// if (def.isExclude()) {
	// addExclude(tm);
	// } else {
	// addInclude(tm);
	// }
	// }
	// }

	// Single thread optimization to avoid new object creation.
	double time;

	protected double doubleValue() {
		return this.time;
	}

	public boolean match(double time) {
		this.time = time;
		// return isMatch(super.matchState(new Double(time)));
		return isMatch(super.matchState(this));
	}
}
